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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class BaseIcoReader<T> {
    private final static int ICON = 1, CURSOR = 2;

    abstract protected ImageDecoder<T> getImageDecoder(int magic);

    final public List<T> decode(byte [] buffer) throws IOException {
        return decode(buffer, 0, buffer.length);
    }

    final public List<T> decode(byte [] buffer, int offset, int length) throws IOException {
        return decode(new IconInputStream(new ByteArrayInputStream(buffer, offset, length)));
    }

    private static class IconInfo implements Comparable<IconInfo> {
        final private int imageNumber;
        final private int dataSize;
        final private int dataOffset;

        public IconInfo(int imageNumber, int dataSize, int dataOffset) {
            this.imageNumber = imageNumber;
            this.dataSize = dataSize;
            this.dataOffset = dataOffset;
        }

        @Override
        public int compareTo(IconInfo o) {
            return dataOffset - o.dataOffset;
        }
    }

    private List<T> decode(IconInputStream iconInputStream) throws IOException {

        if(iconInputStream.readShortLE() != 0) {
            throw new IllegalArgumentException("first WORD must be 0");
        }

        int type = iconInputStream.readShortLE();
        if(type != ICON && type != CURSOR)
            throw new IllegalArgumentException("second WORD must be 0 or 1");

        int numberOfImages = iconInputStream.readShortLE();

        List<IconInfo> icons = new ArrayList<>(numberOfImages);

        for (int currentImage = 0; currentImage < numberOfImages; ++currentImage) {
            // information about colors etc are quite unreliable, coz they tend to overflow with big values,
            // we will just ignore them
            // discard:
            //    - width, height, number of colors, and reserved
            //    - (for ICO) planes and bpp information
            //    - (for CUR) hotspot coordinates
            iconInputStream.skipFully(8);

            int dataSize = iconInputStream.readIntLE();
            int dataOffset = iconInputStream.readIntLE();

            icons.add(new IconInfo(currentImage, dataSize, dataOffset));
        }

        Collections.sort(icons);
        List<T> result = new ArrayList<>(Collections.<T>nCopies(numberOfImages, null));


        for(IconInfo iconInfo: icons) {
            int currentImage = iconInfo.imageNumber;
            int dataOffset = iconInfo.dataOffset;
            int dataSize = iconInfo.dataSize;

            int skip = dataOffset - iconInputStream.getOffset();
            if(skip < 0) {
                throw new IOException("past start of the icon");
            }
            if(skip > 0) {
                iconInputStream.skipFully(skip);
            }

            try {
                System.out.println("IMG" + currentImage + ": offset = " + dataOffset + ", size: " + dataSize);

                int magic = iconInputStream.readIntLE();
                iconInputStream.unreadIntLE(magic);

                ImageDecoder<T> imageDecoder = getImageDecoder(magic);

                if (imageDecoder == null) {
                    throw new IllegalArgumentException("unknown magic: " + Integer.toHexString(magic));
                }
                result.set(currentImage, imageDecoder.decodeImage(iconInputStream));
            } catch(IllegalArgumentException ex) {
                throw new IllegalArgumentException("Pixmap #" + currentImage + ": " + ex.getMessage(), ex);
            }
        }
        iconInputStream.close();
        return result;
    }
}
