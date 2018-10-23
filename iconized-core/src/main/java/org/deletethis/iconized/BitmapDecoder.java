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

import java.io.IOException;
import java.io.InputStream;

public class BitmapDecoder<T extends Image> implements ImageDecoder<T> {
    private final ImageFactory<T> imageFactory;

    public BitmapDecoder(ImageFactory<T> imageFactory) {
        this.imageFactory = imageFactory;
    }

    private final static int AND_0 = 0xFFFFFFFF;
    private final static int AND_1 = 0x00000000;

    private static final int [] andColorTable = new int[]{
            AND_0,
            AND_1
    };

    private static int createColor(int red, int green, int blue, int alpha) {
        return (alpha&0xFF) << 24 | (red&0xFF) << 16 | (green&0xFF) << 8 | (blue&0xFF);
    }

    private static int createColor(int red, int green, int blue) {
        return 0xFF << 24 | (red&0xFF) << 16 | (green&0xFF) << 8 | (blue&0xFF);
    }

    public T decodeImage(InputStream inStream) throws IOException {
        SimpleDataStream in = new SimpleDataStream(inStream);

        int infoHeaderSize = in.readIntelInt();
        if (infoHeaderSize != AbstractIcoParser.BMP_MAGIC) {
            throw new IcoFormatException("not a bitmap, magic = " + Integer.toHexString(infoHeaderSize));
        }

        // read header
        InfoHeader header;
        header = readInfoHeader(in).halfHeight();

        // for now, just read xor raster data
        // and store as separate images

        T xor = read(header, in);

        // 32 bit icon has transparency from alpha so doing nothing is fine
        if (header.getBpp() != 32) {
            ArrayImage andMask = new ArrayImage(header.getWidth(), header.getHeight());
            read1(andMask, in, andColorTable);

            for (int y = 0; y < header.getHeight(); y++) {
                for (int x = 0; x < header.getWidth(); x++) {
                    int a = andMask.getARGB(x, y);
                    if(a == AND_1) {
                        xor.setARGB(x, y, 0);
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
    private InfoHeader readInfoHeader(SimpleDataStream in) throws IOException {
        //Width
        int width = in.readIntelInt();

        //Height
        int height = in.readIntelInt();

        //Planes (=1)
        in.skipFully(2);

        //Bit count
        int bpp = in.readIntelShort();

        //Compression
        int compression = in.readIntelInt();
        //Image size - compressed size of image or 0 if Compression = 0
        in.skipFully(4);

        //horizontal resolution pixels/meter
        in.skipFully(4);

        //vertical resolution pixels/meter
        in.skipFully(4);

        //Colors used - number of colors actually used
        in.skipFully(4);

        //Colors important - number of important colors 0 = all
        in.skipFully(4);

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
    private T read(InfoHeader infoHeader, SimpleDataStream lis) throws IOException {
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
    private T read(InfoHeader infoHeader, SimpleDataStream lis,
                int[] colorTable) throws IOException {

        T pm = imageFactory.createImage(infoHeader.getWidth(), infoHeader.getHeight());
        int bpp = infoHeader.getBpp();
        int compression = infoHeader.getCompression();

        if (bpp == 1 && compression == InfoHeader.BI_RGB) {
            //1-bit (monochrome) uncompressed
            read1(pm, lis, colorTable);
            return pm;
        }

        if (bpp == 4 && compression == InfoHeader.BI_RGB) {
            //4-bit uncompressed
            read4(pm, lis, colorTable);
            return pm;
        }

        if (bpp == 8 && compression == InfoHeader.BI_RGB) {
            //8-bit uncompressed
            read8(pm, lis, colorTable);
            return pm;
        }

        if (bpp == 24 && compression == InfoHeader.BI_RGB) {
            //24-bit uncompressed
            read24(pm, lis);
            return pm;
        }

        if (bpp == 32 && compression == InfoHeader.BI_RGB) {
            //32bit uncompressed
            read32(pm, lis);
            return pm;
        }

        throw new IcoFormatException("Unrecognized bitmap format: bpp = " + bpp + ", compression = " + compression);
    }

    /**
     * Reads the <tt>ColorEntry</tt> table from the given <tt>InputStream</tt> using
     * the information contained in the given <tt>infoHeader</tt>.
     *
     * @param numColors  the number of colors
     * @param lis        the <tt>InputStream</tt> to read
     * @return the decoded image read from the source input
     */
    private int[] readColorTable(int numColors, SimpleDataStream lis) throws IOException {

        int[] colors = new int[numColors];

        for (int i = 0; i < numColors; i++) {
            byte bBlue = lis.readByte();
            byte bGreen = lis.readByte();
            byte bRed = lis.readByte();
            lis.skipFully(1);

            colors[i] = createColor(bRed, bGreen, bBlue, (byte)0xFF);
        }
        return colors;
    }

    private void read1(Image img, SimpleDataStream lis, int[] colorTable) throws IOException {
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
        byte[] line = new byte[bytesPerLine];

        for (int y = height - 1; y >= 0; y--) {
            for (int i = 0; i < bytesPerLine; i++) {
                line[i] = lis.readByte();
            }

            for (int x = 0; x < width; x++) {
                int i = x / 8;
                int v = line[i];
                int b = x % 8;
                int index = getBit(v, b);
                img.setARGB(x, y, colorTable[index]);
            }
        }
    }

    private void read4(Image img, SimpleDataStream lis, int [] colorTable) throws IOException {

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

        byte[] line = new byte[bytesPerLine];

        for (int y = height - 1; y >= 0; y--) {
            //scan line
            for (int i = 0; i < bytesPerLine; i++) {
                byte b = lis.readByte();
                line[i] = b;
            }

            //get pixels
            for (int x = 0; x < width; x++) {
                //get byte index for line
                int b = x / 2; // 2 pixels per byte
                int i = x % 2;
                int n = line[b];
                int index = getNibble(n, i);
                img.setARGB(x, y, colorTable[index]);
            }
        }
    }

    private void read8(Image img, SimpleDataStream lis, int[] colorTable) throws IOException {
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
                int b = lis.readByte();
                //int clr = c[b];
                //img.setARGB(x, y, clr);
                //set sample (colour index) for pixel
                img.setARGB(x, y, colorTable[b&0xFF]);
            }

            lis.skipFully(padBytesPerLine);
        }
    }

    private void read24(Image img, SimpleDataStream lis) throws IOException {
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
                byte b = lis.readByte();
                byte g = lis.readByte();
                byte r = lis.readByte();

                int color = createColor(r, g, b);

                img.setARGB(x, y, color);
            }
            lis.skipFully(padBytesPerLine);
        }
    }

    private void read32(Image img, SimpleDataStream lis) throws IOException {
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
                byte b = lis.readByte();
                byte g = lis.readByte();
                byte r = lis.readByte();
                byte a = lis.readByte();
                img.setARGB(x, y, createColor(r, g, b, a));
            }
        }
    }
}
