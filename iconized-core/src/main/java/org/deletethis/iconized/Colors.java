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

public class Colors {
    private static void range(int b) {
        if((b & 0xFF) != b)
            throw new IllegalArgumentException("not a color: " + b);
    }


    public static int create(int red, int green, int blue, int alpha) {
        return (alpha&0xFF) << 24 | (red&0xFF) << 16 | (green&0xFF) << 8 | (blue&0xFF);
    }

    public static int create(int red, int green, int blue) {
        return 0xFF << 24 | (red&0xFF) << 16 | (green&0xFF) << 8 | (blue&0xFF);
    }

    static int getRed(int color) {
        return color & 0xFF;
    }

    static int getGreen(int color) {
        return (color >>> 8) & 0xFF;
    }

    static int getBlue(int color) {
        return (color >>> 16) & 0xFF;
    }

    static int getAlpha(int color) {
        return color >>> 24;
    }

    static int setAlpha(int color, int alpha) {
        range(alpha);
        return (color & 0xFFFFFF) | alpha << 24;
    }

    static int setAlpha(int color, byte alpha) {
        range(alpha);
        return (color & 0xFFFFFF) | (alpha&0xFF) << 24;
    }

    public static String toString(int color) {
        return String.format("%08x", color);
    }
}
