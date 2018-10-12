/*
 * InfoHeader.java
 *
 * Created on 10 May 2006, 08:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.deletethis.iconized;

/**
 * Represents a bitmap <tt>InfoHeader</tt> structure, which provides header information.
 *
 * @author Ian McDonagh
 * @author Peter Hanula
 */
class InfoHeader {
    /**
     * Specifies no compression.
     *
     * @see InfoHeader#compression InfoHeader
     */
    static final int BI_RGB = 0; // no compression

    private final int width;
    private final int height;
    private final int bpp;
    private final int compression;


    InfoHeader(int width, int height, int bpp, int compression) {
        this.width = width;
        this.height = height;
        this.bpp = bpp;
        this.compression = compression;
    }

    InfoHeader halfHeight() {
        return new InfoHeader(getWidth(), getHeight() /2, getBpp(), getCompression());
    }

    InfoHeader mono() {
        return new InfoHeader(getWidth(), getHeight(), 1, getCompression());
    }

    /**
     * The width in pixels of the bitmap represented by this <tt>InfoHeader</tt>.
     */
    int getWidth() {
        return width;
    }

    /**
     * The height in pixels of the bitmap represented by this <tt>InfoHeader</tt>.
     */
    int getHeight() {
        return height;
    }

    /**
     * The bit count, which represents the colour depth (bits per pixel).
     * This should be either <tt>1</tt>, <tt>4</tt>, <tt>8</tt>, <tt>24</tt> or <tt>32</tt>.
     */
    int getBpp() {
        return bpp;
    }

    /**
     * The compression type, which should be one of the following:
     * <ul>
     * <li>{@link #BI_RGB BI_RGB} - no compression</li>
     * </ul>
     */
    int getCompression() {
        return compression;
    }

    int getColorCount() {
        return 2 << (getBpp() -1);
    }

}
