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
 * Metadata about a single image or cursor.
 */
public class ImageMetadata {
    private final int index;
    private final int dataSize;
    private final int dataOffset;
    private final Integer width;
    private final Integer height;
    private final Integer colorCount;
    private final Integer colorPlanes;
    private final Integer bpp;
    private final Integer hotspotX;
    private final Integer hotspotY;

    public ImageMetadata(int index, int dataSize, int dataOffset, Integer width, Integer height,
                         Integer colorCount, Integer colorPlanes, Integer bpp, Integer hotspotX, Integer hotspotY) {
        this.index = index;
        this.dataSize = dataSize;
        this.dataOffset = dataOffset;
        this.width = width;
        this.height = height;
        this.colorCount = colorCount;
        this.colorPlanes = colorPlanes;
        this.bpp = bpp;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
    }

    /** Index of current entry in file.
     * <p>
     * List of {@link ImageMetadata} structures will be returned by increasing data offset,
     * so this may be used to reconstruct original order as declared in {@code .ico} file */
    public int getIndex() {
        return index;
    }

    public int getDataSize() {
        return dataSize;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getColorCount() {
        return colorCount;
    }

    public Integer getColorPlanes() {
        return colorPlanes;
    }

    public Integer getBpp() {
        return bpp;
    }

    public Integer getHotspotX() {
        return hotspotX;
    }

    public Integer getHotspotY() {
        return hotspotY;
    }
}
