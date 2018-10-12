/*
 * InfoHeader.java
 *
 * Created on 10 May 2006, 08:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.deletethis.iconized.codec.bmp;

/**
 * Represents a bitmap <tt>InfoHeader</tt> structure, which provides header information.
 *
 * @author Ian McDonagh
 */
public class InfoHeader {
    /**
     * Specifies no compression.
     *
     * @see InfoHeader#compression InfoHeader
     */
    public static final int BI_RGB = 0; // no compression


    /**
     * The width in pixels of the bitmap represented by this <tt>InfoHeader</tt>.
     */
    final public int width;
    /**
     * The height in pixels of the bitmap represented by this <tt>InfoHeader</tt>.
     */
    final public int height;
    /**
     * The bit count, which represents the colour depth (bits per pixel).
     * This should be either <tt>1</tt>, <tt>4</tt>, <tt>8</tt>, <tt>24</tt> or <tt>32</tt>.
     */
    final public int bpp;

    /**
     * The compression type, which should be one of the following:
     * <ul>
     * <li>{@link #BI_RGB BI_RGB} - no compression</li>
     * </ul>
     */
    final public int compression;


    public int getColorCount() {
        return 2 << (bpp -1);
    }


    public InfoHeader(int width, int height, int bpp, int compression) {
        this.width = width;
        this.height = height;
        this.bpp = bpp;
        this.compression = compression;
    }

    public InfoHeader halfHeight() {
        return new InfoHeader(width, height/2, bpp, compression);
    }

    public InfoHeader mono() {
        return new InfoHeader(width, height, 1, compression);
    }
}
