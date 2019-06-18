/*
 * mejico - an .ico parser in Java
 *
 * Copyright (c) 2018 Peter Hanula
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
package org.deletethis.mejico;

/**
 * A write-only rectangular array of pixels.
 */
public interface WritableImage {
    /**
     * Determines width in pixels
     *
     * @return width in pixels
     */
    int getWidth();

    /**
     * Determines height in pixels
     *
     * @return height in pixels
     */
    int getHeight();

    /**
     * Sets color value of one pixel
     *
     * @param x x-coordinate from 0 to {@link #getWidth}()-1
     * @param y y-coordinate from 0 to {@link #getHeight}()-1
     * @param rgb ARGB 32-bit color value, alpha value 0 means completely transparent pixel
     */
    void setARGB(int x, int y, int rgb);
}
