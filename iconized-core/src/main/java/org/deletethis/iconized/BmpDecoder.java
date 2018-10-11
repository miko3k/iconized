package org.deletethis.iconized;

import org.deletethis.iconized.codec.bmp.BMPDecoder;
import org.deletethis.iconized.codec.bmp.InfoHeader;

public class BmpDecoder implements BufferDecoder<Pixmap> {
    private BmpDecoder() {
    }

    private static BmpDecoder INSTANCE = new BmpDecoder();

    public static BmpDecoder getInstance() {
        return INSTANCE;
    }

    private static final IndexedPixmap.Palette andColorTable = new IndexedPixmap.Palette(new int[]{
            0xFFFFFFFF,
            0x00000000
    });

    @Override
    public Pixmap decodeImage(Buffer in, Params params) {
        int info = in.int32();
        if (info != BufferDecoder.BMP_MAGIC) {
            throw new IllegalArgumentException("not a bitmap, magic = " + Integer.toHexString(info));
        }

        // read XOR bitmap
        // BMPDecoder bmp = new BMPDecoder(is);
        InfoHeader infoHeader;
        infoHeader = BMPDecoder.readInfoHeader(in, info);
        InfoHeader andHeader = new InfoHeader(infoHeader);
        andHeader.iHeight = infoHeader.iHeight / 2;
        andHeader.sBitCount = 1;
        andHeader.iNumColors = 2;
        InfoHeader xorHeader = new InfoHeader(infoHeader);
        xorHeader.iHeight = andHeader.iHeight;


        // for now, just read all the raster data (xor + and)
        // and store as separate images

        Pixmap xor = BMPDecoder.read(xorHeader, in);
        // If we want to be sure we've decoded the XOR mask
        // correctly,
        // we can write it out as a PNG to a temp file here.
        // try {
        // File temp = File.createTempFile("image4j", ".png");
        // ImageIO.write(xor, "png", temp);
        // log.info("Wrote xor mask for image #" + i + " to "
        // + temp.getAbsolutePath());
        // } catch (Throwable ex) {
        // }
        // Or just add it to the output list:
        // img.add(xor);

        Pixmap img = new Pixmap(xorHeader.iWidth,
                xorHeader.iHeight);

        if (infoHeader.sBitCount == 32) {
            // transparency from alpha
            // ignore bytes after XOR bitmap
            int infoHeaderSize = infoHeader.iSize;
            // data size = w * h * 4
            int dataSize = xorHeader.iWidth * xorHeader.iHeight * 4;
            int skip = in.size() - infoHeaderSize - dataSize;

            // ignore AND bitmap since alpha channel stores
            // transparency

            // If we skipped less bytes than expected, the AND mask
            // is probably badly formatted.
            // If we're at the last/only entry in the file, silently
            // ignore and continue processing...
            if (!params.isLastImage()) {
                in.skip(skip);
            }
            // nothing needs to be done at this point
        } else {
            Pixmap and = BMPDecoder.read(andHeader, in,
                    andColorTable);

            for (int y = 0; y < xorHeader.iHeight; y++) {
                for (int x = 0; x < xorHeader.iWidth; x++) {
                    int c = xor.getRGB(x, y);
                    int a = and.getRGB(x, y);
                    c = Colors.setAlpha(c, Colors.getAlpha(a));
                    xor.setRGB(x, y, c);
                }
            }
        }
        return xor;
    }
}
