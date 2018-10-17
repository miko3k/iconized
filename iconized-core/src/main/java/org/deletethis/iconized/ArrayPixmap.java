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
import java.util.Objects;

final public class ArrayPixmap implements Pixmap {
    private final int width, height;
    private int [] data;

    public static PixmapFactory<ArrayPixmap> FACTORY = new PixmapFactory<ArrayPixmap>() {
        @Override
        public ArrayPixmap createPixmap(int width, int height) {
            return new ArrayPixmap(width, height);
        }
    };

    public ArrayPixmap(int width, int height) {
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

    public int[] getData() {
        return data;
    }

    public int getRGB(int x, int y) {
        int w = width;
        if(x < 0 || x >= w)
            throw new IllegalArgumentException("not withing the buffer");

        if(y < 0 || y >= height)
            throw new IllegalArgumentException("not withing the buffer");

        return data[w*y+x];
    }

    public void setRGB(int x, int y, int rgb) {
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
        if (!(o instanceof ArrayPixmap)) return false;
        ArrayPixmap pixmap = (ArrayPixmap) o;
        return width == pixmap.width &&
                height == pixmap.height &&
                Arrays.equals(data, pixmap.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(width, height);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + '{' + width + 'x' + height + '}';
    }
}
