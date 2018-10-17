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

/**
 * Represents a bitmap <tt>InfoHeader</tt> structure, which provides header information.
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

    int getWidth() {
        return width;
    }

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
