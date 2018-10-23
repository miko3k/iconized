/*
 * Iconized - an .ico parser in Java
 *
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

import java.util.Arrays;

public class ArrayImage implements Image {
    private final int width, height;
    private int [] data;

    public static final ImageFactory<ArrayImage> FACTORY = new ImageFactory<ArrayImage>() {
        @Override
        public ArrayImage createImage(int width, int height) {
            return new ArrayImage(width, height);
        }
    };

    public ArrayImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new int[width*height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getARGB(int x, int y) {
        int w = width;
        if(x < 0 || x >= w)
            throw new IllegalArgumentException("not withing the buffer");

        if(y < 0 || y >= height)
            throw new IllegalArgumentException("not withing the buffer");

        return data[w*y+x];
    }

    public void setARGB(int x, int y, int rgb) {
        int w = width;
        if(x < 0 || x >= w)
            throw new IllegalArgumentException("not withing the buffer");

        if(y < 0 || y >= height)
            throw new IllegalArgumentException("not withing the buffer");

        data[w*y+x] = rgb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayImage)) return false;
        ArrayImage pixmap = (ArrayImage) o;
        return width == pixmap.width &&
                height == pixmap.height &&
                Arrays.equals(data, pixmap.data);
    }

    @Override
    public int hashCode() {
        int result = width + 31 * height;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "ArrayImage(" + width + 'x' + height + ')';
    }

    public int [] getData() {
        return data;
    }
}
