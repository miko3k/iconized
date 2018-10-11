/*
 * Decodes a BMP image from an <tt>InputStream</tt> to a <tt>BufferedImage</tt>
 *
 * @author Ian McDonagh
 */

package org.deletethis.iconized.codec.bmp;

import org.deletethis.iconized.Buffer;
import org.deletethis.iconized.Colors;
import org.deletethis.iconized.IndexedPixmap;
import org.deletethis.iconized.Pixmap;


/**
 * Decodes images in BMP format.
 *
 * @author Ian McDonagh
 */
public class BMPDecoder {

    /**
     * Retrieves a bit from the lowest order byte of the given integer.
     *
     * @param bits  the source integer, treated as an unsigned byte
     * @param index the index of the bit to retrieve, which must be in the range <tt>0..7</tt>.
     * @return the bit at the specified index, which will be either <tt>0</tt> or <tt>1</tt>.
     */
    private static int getBit(int bits, int index) {
        return (bits >> (7 - index)) & 1;
    }

    /**
     * Retrieves a nibble (4 bits) from the lowest order byte of the given integer.
     *
     * @param nibbles the source integer, treated as an unsigned byte
     * @param index   the index of the nibble to retrieve, which must be in the range <tt>0..1</tt>.
     * @return the nibble at the specified index, as an unsigned byte.
     */
    private static int getNibble(int nibbles, int index) {
        return (nibbles >> (4 * (1 - index))) & 0xF;
    }

    /**
     * /**
     * Reads the BMP info header structure from the given <tt>InputStream</tt>.
     *
     * @param lis the <tt>InputStream</tt> to read
     * @return the <tt>InfoHeader</tt> structure
     */
    public static InfoHeader readInfoHeader(Buffer lis, int infoSize) {
        return new InfoHeader(lis, infoSize);
    }

    /**
     * Reads the BMP data from the given <tt>InputStream</tt> using the information
     * contained in the <tt>InfoHeader</tt>.
     *
     * @param lis        the source input
     * @param infoHeader an <tt>InfoHeader</tt> that was read by a call to
     *                   {@link #readInfoHeader readInfoHeader()}.
     * @return the decoded image read from the source input
     */
    public static Pixmap read(InfoHeader infoHeader, Buffer lis) {
        /* Color table (palette) */
        IndexedPixmap.Palette colorTable = null;

        //color table is only present for 1, 4 or 8 bit (indexed) images
        if (infoHeader.sBitCount <= 8) {
            colorTable = readColorTable(infoHeader, lis);
        }

        return read(infoHeader, lis, colorTable);
    }

    /**
     * Reads the BMP data from the given <tt>InputStream</tt> using the information
     * contained in the <tt>InfoHeader</tt>.
     *
     * @param colorTable <tt>ColorEntry</tt> array containing palette
     * @param infoHeader an <tt>InfoHeader</tt> that was read by a call to
     *                   {@link #readInfoHeader readInfoHeader()}.
     * @param lis        the source input
     * @return the decoded image read from the source input
     */
    public static Pixmap read(InfoHeader infoHeader, Buffer lis,
                              IndexedPixmap.Palette colorTable) {

        Pixmap img;

        //1-bit (monochrome) uncompressed
        if (infoHeader.sBitCount == 1 && infoHeader.iCompression == BMPConstants.BI_RGB) {

            img = read1(infoHeader, lis, colorTable);

        }
        //4-bit uncompressed
        else if (infoHeader.sBitCount == 4 && infoHeader.iCompression == BMPConstants.BI_RGB) {

            img = read4(infoHeader, lis, colorTable);

        }
        //8-bit uncompressed
        else if (infoHeader.sBitCount == 8 && infoHeader.iCompression == BMPConstants.BI_RGB) {

            img = read8(infoHeader, lis, colorTable);

        }
        //24-bit uncompressed
        else if (infoHeader.sBitCount == 24 && infoHeader.iCompression == BMPConstants.BI_RGB) {

            img = read24(infoHeader, lis);

        }
        //32bit uncompressed
        else if (infoHeader.sBitCount == 32 && infoHeader.iCompression == BMPConstants.BI_RGB) {

            img = read32(infoHeader, lis);

        } else {
            throw new IllegalArgumentException("Unrecognized bitmap format: bit count=" + infoHeader.sBitCount + ", compression=" +
                    infoHeader.iCompression);
        }

        return img;
    }

    /**
     * Reads the <tt>ColorEntry</tt> table from the given <tt>InputStream</tt> using
     * the information contained in the given <tt>infoHeader</tt>.
     *
     * @param infoHeader the <tt>InfoHeader</tt> structure, which was read using
     *                   {@link #readInfoHeader readInfoHeader()}
     * @param lis        the <tt>InputStream</tt> to read
     * @return the decoded image read from the source input
     */
    private static IndexedPixmap.Palette readColorTable(InfoHeader infoHeader, Buffer lis) {
        int[] colors = new int[infoHeader.iNumColors];

        for (int i = 0; i < infoHeader.iNumColors; i++) {
            int bBlue = lis.int8();
            int bGreen = lis.int8();
            int bRed = lis.int8();
            lis.skip(1);

            colors[i] = Colors.create(bRed, bGreen, bBlue);
        }
        return new IndexedPixmap.Palette(colors);
    }

    /**
     * Reads 1-bit uncompressed bitmap raster data, which may be monochrome depending on the
     * palette entries in <tt>colorTable</tt>.
     *
     * @param infoHeader the <tt>InfoHeader</tt> structure, which was read using
     *                   {@link #readInfoHeader readInfoHeader()}
     * @param lis        the source input
     * @param colorTable <tt>ColorEntry</tt> array specifying the palette, which
     *                   must not be <tt>null</tt>.
     * @return the decoded image read from the source input
     */
    private static Pixmap read1(InfoHeader infoHeader, Buffer lis, IndexedPixmap.Palette colorTable) {
        //1 bit per pixel or 8 pixels per byte
        //each pixel specifies the palette index


        // Create indexed image
        IndexedPixmap img = new IndexedPixmap(
                infoHeader.iWidth, infoHeader.iHeight,
                colorTable);

        //padding

        int bitsPerLine = infoHeader.iWidth;
        if (bitsPerLine % 32 != 0) {
            bitsPerLine = (bitsPerLine / 32 + 1) * 32;
        }

        int bytesPerLine = bitsPerLine / 8;
        int[] line = new int[bytesPerLine];

        for (int y = infoHeader.iHeight - 1; y >= 0; y--) {
            for (int i = 0; i < bytesPerLine; i++) {
                line[i] = lis.int8();
            }

            for (int x = 0; x < infoHeader.iWidth; x++) {
                int i = x / 8;
                int v = line[i];
                int b = x % 8;
                int index = getBit(v, b);
                img.setColor(x, y, index);
            }
        }

        return img.getPixmap();
    }

    /**
     * Reads 4-bit uncompressed bitmap raster data, which is interpreted based on the colours
     * specified in the palette.
     *
     * @param infoHeader the <tt>InfoHeader</tt> structure, which was read using
     *                   {@link #readInfoHeader readInfoHeader()}
     * @param lis        the source input
     * @param colorTable <tt>ColorEntry</tt> array specifying the palette, which
     *                   must not be <tt>null</tt>.
     * @return the decoded image read from the source input
     */
    private static Pixmap read4(InfoHeader infoHeader,
                                Buffer lis,
                                IndexedPixmap.Palette colorTable) {

        // 2 pixels per byte or 4 bits per pixel.
        // Colour for each pixel specified by the color index in the pallette.

        IndexedPixmap img = new IndexedPixmap(
                infoHeader.iWidth, infoHeader.iHeight,
                colorTable);

        //padding
        int bitsPerLine = infoHeader.iWidth * 4;
        if (bitsPerLine % 32 != 0) {
            bitsPerLine = (bitsPerLine / 32 + 1) * 32;
        }
        int bytesPerLine = bitsPerLine / 8;

        int[] line = new int[bytesPerLine];

        for (int y = infoHeader.iHeight - 1; y >= 0; y--) {
            //scan line
            for (int i = 0; i < bytesPerLine; i++) {
                int b = lis.int8();
                line[i] = b;
            }

            //get pixels
            for (int x = 0; x < infoHeader.iWidth; x++) {
                //get byte index for line
                int b = x / 2; // 2 pixels per byte
                int i = x % 2;
                int n = line[b];
                int index = getNibble(n, i);
                img.setColor(x, y, index);
            }
        }

        return img.getPixmap();
    }

    /**
     * Reads 8-bit uncompressed bitmap raster data, which is interpreted based on the colours
     * specified in the palette.
     *
     * @param infoHeader the <tt>InfoHeader</tt> structure, which was read using
     *                   {@link #readInfoHeader readInfoHeader()}
     * @param lis        the source input
     * @param colorTable <tt>ColorEntry</tt> array specifying the palette, which
     *                   must not be <tt>null</tt>.
     * @return the decoded image read from the source input
     */
    private static Pixmap read8(InfoHeader infoHeader,
                                Buffer lis,
                                IndexedPixmap.Palette colorTable) {
        //1 byte per pixel
        //  color index 1 (index of color in palette)
        //lines padded to nearest 32bits
        //no alpha

        IndexedPixmap img = new IndexedPixmap(
                infoHeader.iWidth, infoHeader.iHeight,
                colorTable);
    
      /*
      //create color pallette
      int[] c = new int[infoHeader.iNumColors];
      for (int i = 0; i < c.length; i++) {
        int r = colorTable[i].bRed;
        int g = colorTable[i].bGreen;
        int b = colorTable[i].bBlue;
        c[i] = (r << 16) | (g << 8) | (b);
      }
       */

        //padding
        int dataPerLine = infoHeader.iWidth;
        int bytesPerLine = dataPerLine;
        if (bytesPerLine % 4 != 0) {
            bytesPerLine = (bytesPerLine / 4 + 1) * 4;
        }
        int padBytesPerLine = bytesPerLine - dataPerLine;

        for (int y = infoHeader.iHeight - 1; y >= 0; y--) {
            for (int x = 0; x < infoHeader.iWidth; x++) {
                int b = lis.int8();
                //int clr = c[b];
                //img.setRGB(x, y, clr);
                //set sample (colour index) for pixel
                img.setColor(x, y, b);
            }

            lis.skip(padBytesPerLine);
        }

        return img.getPixmap();
    }

    /**
     * Reads 24-bit uncompressed bitmap raster data.
     *
     * @param lis        the source input
     * @param infoHeader the <tt>InfoHeader</tt> structure, which was read using
     *                   {@link #readInfoHeader readInfoHeader()}
     * @return the decoded image read from the source input
     */
    private static Pixmap read24(InfoHeader infoHeader,
                                 Buffer lis) {
        //3 bytes per pixel
        //  blue 1
        //  green 1
        //  red 1
        // lines padded to nearest 32 bits
        // no alpha

        Pixmap img = new Pixmap(
                infoHeader.iWidth, infoHeader.iHeight);

        //padding to nearest 32 bits
        int dataPerLine = infoHeader.iWidth * 3;
        int bytesPerLine = dataPerLine;
        if (bytesPerLine % 4 != 0) {
            bytesPerLine = (bytesPerLine / 4 + 1) * 4;
        }
        int padBytesPerLine = bytesPerLine - dataPerLine;

        for (int y = infoHeader.iHeight - 1; y >= 0; y--) {
            for (int x = 0; x < infoHeader.iWidth; x++) {
                int b = lis.int8();
                int g = lis.int8();
                int r = lis.int8();

                img.setRGB(x, y, Colors.create(r, g, b));
            }
            lis.skip(padBytesPerLine);
        }

        return img;
    }


    /**
     * Reads 32-bit uncompressed bitmap raster data, with transparency.
     *
     * @param lis        the source input
     * @param infoHeader the <tt>InfoHeader</tt> structure, which was read using
     *                   {@link #readInfoHeader readInfoHeader()}
     * @return the decoded image read from the source input
     */
    private static Pixmap read32(InfoHeader infoHeader,
                                 Buffer lis) {
        //4 bytes per pixel
        // blue 1
        // green 1
        // red 1
        // alpha 1
        //No padding since each pixel = 32 bits

        Pixmap img = new Pixmap(
                infoHeader.iWidth, infoHeader.iHeight);

        for (int y = infoHeader.iHeight - 1; y >= 0; y--) {
            for (int x = 0; x < infoHeader.iWidth; x++) {
                int b = lis.int8();
                int g = lis.int8();
                int r = lis.int8();
                int a = lis.int8();
                img.setRGB(x, y, Colors.create(r, g, b, a));
            }
        }

        return img;
    }
}
