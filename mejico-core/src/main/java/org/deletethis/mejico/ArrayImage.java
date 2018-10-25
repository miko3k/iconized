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

import java.util.Arrays;

/**
 * A rectangular array of pixels stored as <code>int []</code>.
 *
 * It's obviously completely platform independent!
 */
public class ArrayImage implements WritableImage {
    private final int width, height;
    private int [] data;

    public static final WritableImageFactory<ArrayImage> FACTORY = new WritableImageFactory<ArrayImage>() {
        @Override
        public ArrayImage createWritableImage(int width, int height) {
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
        ArrayImage other = (ArrayImage) o;
        return width == other.width &&
                height == other.height &&
                Arrays.equals(data, other.data);
    }

    /**
     * Returns a hashcode. Currently only dimensions are used to calculate hash value, because they
     * are only immutable properties. In addition, one must be quite insane in order use use this as a key
     * in hash table.
     */
    @Override
    public int hashCode() {
        return width + 31 * height;
    }

    @Override
    public String toString() {
        return "ArrayImage(" + width + 'x' + height + ')';
    }

    public int [] getData() {
        return data;
    }
}
