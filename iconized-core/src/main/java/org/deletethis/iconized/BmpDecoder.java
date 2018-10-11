package org.deletethis.iconized;

import org.deletethis.iconized.codec.bmp.BMPDecoder;
import org.deletethis.iconized.codec.bmp.ColorEntry;
import org.deletethis.iconized.codec.bmp.InfoHeader;
import org.deletethis.iconized.io.CountingInputStream;
import org.deletethis.iconized.io.LittleEndianInputStream;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;

public class BmpDecoder implements BufferDecoder<BufferedImage> {
    private BmpDecoder() {}

    public static BmpDecoder INSTANCE = new BmpDecoder();

    @Override
    public BufferedImage decodeImage(Buffer buffer, Params params) {
        try {
            LittleEndianInputStream in = new LittleEndianInputStream(new CountingInputStream(buffer.toInputStream()));

            int info = in.readIntLE();
            if (info != BufferDecoder.BMP_MAGIC) {
                throw new IllegalArgumentException("not a bitmap, magic = " + Integer.toHexString(info));
            }

            // read XOR bitmap
            // BMPDecoder bmp = new BMPDecoder(is);
            InfoHeader infoHeader = null;
            infoHeader = BMPDecoder.readInfoHeader(in, info);
            InfoHeader andHeader = new InfoHeader(infoHeader);
            andHeader.iHeight = (int) (infoHeader.iHeight / 2);
            InfoHeader xorHeader = new InfoHeader(infoHeader);
            xorHeader.iHeight = andHeader.iHeight;

            andHeader.sBitCount = 1;
            andHeader.iNumColors = 2;

            // for now, just read all the raster data (xor + and)
            // and store as separate images

            BufferedImage xor = BMPDecoder.read(xorHeader, in);
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

            BufferedImage img = new BufferedImage(xorHeader.iWidth,
                    xorHeader.iHeight, BufferedImage.TYPE_INT_ARGB);

            ColorEntry[] andColorTable = new ColorEntry[]{
                    new ColorEntry(255, 255, 255, 255),
                    new ColorEntry(0, 0, 0, 0)};

            if (infoHeader.sBitCount == 32) {
                // transparency from alpha
                // ignore bytes after XOR bitmap
                int infoHeaderSize = infoHeader.iSize;
                // data size = w * h * 4
                int dataSize = xorHeader.iWidth * xorHeader.iHeight * 4;
                int skip = buffer.getSize() - infoHeaderSize - dataSize;

                // ignore AND bitmap since alpha channel stores
                // transparency

                if (in.skip(skip, false) < skip && params.isLastImage()) {
                    throw new EOFException("Unexpected end of input");
                }
                // If we skipped less bytes than expected, the AND mask
                // is probably badly formatted.
                // If we're at the last/only entry in the file, silently
                // ignore and continue processing...

                // //read AND bitmap
                // BufferedImage and = BMPDecoder.read(andHeader, in,
                // andColorTable);
                // this.img.add(and);

                WritableRaster srgb = xor.getRaster();
                WritableRaster salpha = xor.getAlphaRaster();
                WritableRaster rgb = img.getRaster();
                WritableRaster alpha = img.getAlphaRaster();

                for (int y = xorHeader.iHeight - 1; y >= 0; y--) {
                    for (int x = 0; x < xorHeader.iWidth; x++) {
                        int r = srgb.getSample(x, y, 0);
                        int g = srgb.getSample(x, y, 1);
                        int b = srgb.getSample(x, y, 2);
                        int a = salpha.getSample(x, y, 0);
                        rgb.setSample(x, y, 0, r);
                        rgb.setSample(x, y, 1, g);
                        rgb.setSample(x, y, 2, b);
                        alpha.setSample(x, y, 0, a);
                    }
                }

            } else {
                BufferedImage and = BMPDecoder.read(andHeader, in,
                        andColorTable);
                // img.add(and);

                // copy rgb
                WritableRaster srgb = xor.getRaster();
                WritableRaster rgb = img.getRaster();
                // copy alpha
                WritableRaster alpha = img.getAlphaRaster();
                WritableRaster salpha = and.getRaster();

                for (int y = 0; y < xorHeader.iHeight; y++) {
                    for (int x = 0; x < xorHeader.iWidth; x++) {
                        int r, g, b;
                        int c = xor.getRGB(x, y);
                        r = (c >> 16) & 0xFF;
                        g = (c >> 8) & 0xFF;
                        b = (c) & 0xFF;
                        // red
                        rgb.setSample(x, y, 0, r);
                        // green
                        rgb.setSample(x, y, 1, g);
                        // blue
                        rgb.setSample(x, y, 2, b);
                        // System.out.println(x+","+y+"="+Integer.toHexString(c));
                        // img.setRGB(x, y, c);

                        // alpha
                        int a = and.getRGB(x, y);
                        alpha.setSample(x, y, 0, a);
                    }
                }
            }
            return img;
            /*
            Pixmap result = new Pixmap(img.getWidth(), img.getHeight());
            for (int j = 0; j < img.getHeight(); j++) {
                for (int i = 0; i < img.getWidth(); i++) {
                    result.setRGB(i, j, img.getRGB(i, j));
                }
            }
            return result;
            */
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
