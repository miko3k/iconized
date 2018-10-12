package org.deletethis.iconized;

import org.deletethis.iconized.codec.bmp.BMPDecoder;
import org.deletethis.iconized.codec.bmp.InfoHeader;

public class IconBmpDecoder implements BufferDecoder<Pixmap> {
    private IconBmpDecoder() {
    }

    private static IconBmpDecoder INSTANCE = new IconBmpDecoder();

    public static IconBmpDecoder getInstance() {
        return INSTANCE;
    }

    private static final IndexedPixmap.Palette andColorTable = new IndexedPixmap.Palette(new int[]{
            0xFFFFFFFF,
            0x00000000
    });

    @Override
    public Pixmap decodeImage(Buffer in, Params params) {
        int infoHeaderSize = in.int32();
        if (infoHeaderSize != BufferDecoder.BMP_MAGIC) {
            throw new IllegalArgumentException("not a bitmap, magic = " + Integer.toHexString(infoHeaderSize));
        }

        // read XOR bitmap
        // BMPDecoder bmp = new BMPDecoder(is);
        InfoHeader infoHeader;
        infoHeader = BMPDecoder.readInfoHeader(in);

        InfoHeader xorHeader = infoHeader.halfHeight();
        InfoHeader andHeader = xorHeader.mono();


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

        Pixmap img = new Pixmap(xorHeader.width,
                xorHeader.height);

        if (infoHeader.bpp == 32) {
            // transparency from alpha
            // ignore bytes after XOR bitmap

            // data size = w * h * 4
            int dataSize = xorHeader.width * xorHeader.height * 4;
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

            for (int y = 0; y < xorHeader.height; y++) {
                for (int x = 0; x < xorHeader.width; x++) {
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
