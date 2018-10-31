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
 * Metadata about a single icon or cursor image.
 */
public class ImageMetadata {
    /*int getWidth();
    int getHeight();
    int getColorCount();
    int getColorsPlansOrHotspotX();
    int getBppOrHotspotY();*/
    private final int index;
    private final int dataSize;
    private final int dataOffset;

    public ImageMetadata(int index, int dataSize, int dataOffset) {
        this.index = index;
        this.dataSize = dataSize;
        this.dataOffset = dataOffset;
    }

    public int getIndex() {
        return index;
    }

    public int getDataSize() {
        return dataSize;
    }

    public int getDataOffset() {
        return dataOffset;
    }
}
