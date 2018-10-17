/*
 * Iconized - an .ico parser in Java
 *
 * Copyright (c) 2015-2017 Ian McDonagh
 * Copyright (c) 2018, Peter Hanula
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.deletethis.iconized;

public class IconBmpDecoder<T extends Pixmap> implements BufferDecoder<T> {
    private final PixmapFactory<T> pixmapFactory;

    public IconBmpDecoder(PixmapFactory<T> pixmapFactory) {
        this.pixmapFactory = pixmapFactory;
    }

    private final static int AND_0 = 0xFFFFFFFF;
    private final static int AND_1 = 0x00000000;

    private static final int [] andColorTable = new int[]{
            AND_0,
            AND_1
    };

    public T decodeImage(Buffer in) {
        int infoHeaderSize = in.int32();
        if (infoHeaderSize != BufferDecoder.BMP_MAGIC) {
            throw new IllegalArgumentException("not a bitmap, magic = " + Integer.toHexString(infoHeaderSize));
        }

        // read header
        InfoHeader header;
        header = readInfoHeader(in).halfHeight();

        // for now, just read xor raster data
        // and store as separate images

        T xor = read(header, in);

        // 32 bit icon has transparency from alpha so doing nothing is fine
        if (header.getBpp() != 32) {
            ArrayPixmap andMask = new ArrayPixmap(header.getWidth(), header.getHeight());
            read1(andMask, in, andColorTable);

            for (int y = 0; y < header.getHeight(); y++) {
                for (int x = 0; x < header.getWidth(); x++) {
                    int a = andMask.getRGB(x, y);
                    if(a == AND_1) {
                        xor.setRGB(x, y, 0);
                    }
                }
            }
        }
        return xor;
    }

    /**
     * Retrieves a bit from the lowest order byte of the given integer.
     *
     * @param bits  the source integer, treated as an unsigned byte
     * @param index the index of the bit to retrieve, which must be in the range <tt>0..7</tt>.
     * @return the bit at the specified index, which will be either <tt>0</tt> or <tt>1</tt>.
     */
    private int getBit(int bits, int index) {
        return (bits >> (7 - index)) & 1;
    }

    /**
     * Retrieves a nibble (4 bits) from the lowest order byte of the given integer.
     *
     * @param nibbles the source integer, treated as an unsigned byte
     * @param index   the index of the nibble to retrieve, which must be in the range <tt>0..1</tt>.
     * @return the nibble at the specified index, as an unsigned byte.
     */
    private int getNibble(int nibbles, int index) {
        return (nibbles >> (4 * (1 - index))) & 0xF;
    }

    /**
     * /**
     * Reads the BMP info header structure from the given <tt>InputStream</tt>.
     *
     * @param in the <tt>InputStream</tt> to read
     * @return the <tt>InfoHeader</tt> structure
     */
    private InfoHeader readInfoHeader(Buffer in) {
        //Width
        int width = in.int32();

        //Height
        int height = in.int32();

        //Planes (=1)
        in.skip(2);

        //Bit count
        int bpp = in.int16();

        //Compression
        int compression = in.int32();
        //Image size - compressed size of image or 0 if Compression = 0
        in.skip(4);

        //horizontal resolution pixels/meter
        in.skip(4);

        //vertical resolution pixels/meter
        in.skip(4);

        //Colors used - number of colors actually used
        in.skip(4);

        //Colors important - number of important colors 0 = all
        in.skip(4);

        return new InfoHeader(width, height, bpp, compression);
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
    private T read(InfoHeader infoHeader, Buffer lis) {
        /* Color table (palette) */
        int[] colorTable = null;

        //color table is only present for 1, 4 or 8 bit (indexed) images
        if (infoHeader.getBpp() <= 8) {
            colorTable = readColorTable(infoHeader.getColorCount(), lis);
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
    private T read(InfoHeader infoHeader, Buffer lis,
                int[] colorTable) {

        T pm = pixmapFactory.createPixmap(infoHeader.getWidth(), infoHeader.getHeight());

        if (infoHeader.getBpp() == 1 && infoHeader.getCompression() == InfoHeader.BI_RGB) {
            //1-bit (monochrome) uncompressed
            read1(pm, lis, colorTable);
            return pm;
        }

        if (infoHeader.getBpp() == 4 && infoHeader.getCompression() == InfoHeader.BI_RGB) {
            //4-bit uncompressed
            read4(pm, lis, colorTable);
            return pm;
        }

        if (infoHeader.getBpp() == 8 && infoHeader.getCompression() == InfoHeader.BI_RGB) {
            //8-bit uncompressed
            read8(pm, lis, colorTable);
            return pm;
        }

        if (infoHeader.getBpp() == 24 && infoHeader.getCompression() == InfoHeader.BI_RGB) {
            //24-bit uncompressed
            read24(pm, lis);
            return pm;
        }

        if (infoHeader.getBpp() == 32 && infoHeader.getCompression() == InfoHeader.BI_RGB) {
            //32bit uncompressed
            read32(pm, lis);
            return pm;
        }

        throw new IllegalArgumentException("Unrecognized bitmap format: bit count=" + infoHeader.getBpp() + ", compression=" +
                    infoHeader.getCompression());
    }

    /**
     * Reads the <tt>ColorEntry</tt> table from the given <tt>InputStream</tt> using
     * the information contained in the given <tt>infoHeader</tt>.
     *
     * @param numColors  the number of colors
     * @param lis        the <tt>InputStream</tt> to read
     * @return the decoded image read from the source input
     */
    private int[] readColorTable(int numColors, Buffer lis) {

        int[] colors = new int[numColors];

        for (int i = 0; i < numColors; i++) {
            byte bBlue = lis.byte8();
            byte bGreen = lis.byte8();
            byte bRed = lis.byte8();
            lis.skip(1);

            colors[i] = Colors.create(bRed, bGreen, bBlue, (byte)0xFF);
        }
        return colors;
    }

    private void read1(Pixmap img, Buffer lis, int[] colorTable) {
        //1 bit per pixel or 8 pixels per byte
        //each pixel specifies the palette index

        int width = img.getWidth();
        int height = img.getHeight();

        //padding
        int bitsPerLine = width;
        if (bitsPerLine % 32 != 0) {
            bitsPerLine = (bitsPerLine / 32 + 1) * 32;
        }

        int bytesPerLine = bitsPerLine / 8;
        int[] line = new int[bytesPerLine];

        for (int y = height - 1; y >= 0; y--) {
            for (int i = 0; i < bytesPerLine; i++) {
                line[i] = lis.int8();
            }

            for (int x = 0; x < width; x++) {
                int i = x / 8;
                int v = line[i];
                int b = x % 8;
                int index = getBit(v, b);
                img.setRGB(x, y, colorTable[index]);
            }
        }
    }

    private void read4(Pixmap img, Buffer lis, int [] colorTable) {

        // 2 pixels per byte or 4 bits per pixel.
        // Colour for each pixel specified by the color index in the pallette.

        int width = img.getWidth();
        int height = img.getHeight();

        //padding
        int bitsPerLine = width * 4;
        if (bitsPerLine % 32 != 0) {
            bitsPerLine = (bitsPerLine / 32 + 1) * 32;
        }
        int bytesPerLine = bitsPerLine / 8;

        int[] line = new int[bytesPerLine];

        for (int y = height - 1; y >= 0; y--) {
            //scan line
            for (int i = 0; i < bytesPerLine; i++) {
                int b = lis.int8();
                line[i] = b;
            }

            //get pixels
            for (int x = 0; x < width; x++) {
                //get byte index for line
                int b = x / 2; // 2 pixels per byte
                int i = x % 2;
                int n = line[b];
                int index = getNibble(n, i);
                img.setRGB(x, y, colorTable[index]);
            }
        }
    }

    private void read8(Pixmap img, Buffer lis, int[] colorTable) {
        //1 byte per pixel
        //  color index 1 (index of color in palette)
        //lines padded to nearest 32bits
        //no alpha

        int width = img.getWidth();
        int height = img.getHeight();

        //padding
        int dataPerLine = width;
        int bytesPerLine = dataPerLine;
        if (bytesPerLine % 4 != 0) {
            bytesPerLine = (bytesPerLine / 4 + 1) * 4;
        }
        int padBytesPerLine = bytesPerLine - dataPerLine;

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int b = lis.int8();
                //int clr = c[b];
                //img.setRGB(x, y, clr);
                //set sample (colour index) for pixel
                img.setRGB(x, y, colorTable[b]);
            }

            lis.skip(padBytesPerLine);
        }
    }

    private void read24(Pixmap img, Buffer lis) {
        //3 bytes per pixel
        //  blue 1
        //  green 1
        //  red 1
        // lines padded to nearest 32 bits
        // no alpha

        int width = img.getWidth();
        int height = img.getHeight();

        //padding to nearest 32 bits
        int dataPerLine = width * 3;
        int bytesPerLine = dataPerLine;
        if (bytesPerLine % 4 != 0) {
            bytesPerLine = (bytesPerLine / 4 + 1) * 4;
        }
        int padBytesPerLine = bytesPerLine - dataPerLine;

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                byte b = lis.byte8();
                byte g = lis.byte8();
                byte r = lis.byte8();

                int color = Colors.create(r, g, b);

                img.setRGB(x, y, color);
            }
            lis.skip(padBytesPerLine);
        }
    }

    private void read32(Pixmap img,  Buffer lis) {
        //4 bytes per pixel
        // blue 1
        // green 1
        // red 1
        // alpha 1
        //No padding since each pixel = 32 bits

        int width = img.getWidth();
        int height = img.getHeight();

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                byte b = lis.byte8();
                byte g = lis.byte8();
                byte r = lis.byte8();
                byte a = lis.byte8();
                img.setRGB(x, y, Colors.create(r, g, b, a));
            }
        }
    }
}
