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
import java.io.InputStream;
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
        return decode(new ByteArrayInputStream(buffer, offset, length));
    }

    final public List<T> decode(InputStream inputStream) throws IOException {
        return decode(new IconInputStream(inputStream));
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

    private List<T> decode(IconInputStream stream) throws IOException {

        if(stream.readIntelShort() != 0) {
            throw new BadIconFormatException("first WORD must be 0");
        }

        int type = stream.readIntelShort();
        if(type != ICON && type != CURSOR)
            throw new BadIconFormatException("second WORD must be 0 or 1");

        int numberOfImages = stream.readIntelShort();

        List<IconInfo> icons = new ArrayList<>(numberOfImages);

        for (int currentImage = 0; currentImage < numberOfImages; ++currentImage) {
            // information about colors etc are quite unreliable, coz they tend to overflow with big values,
            // we will just ignore them
            // discard:
            //    - width, height, number of colors, and reserved
            //    - (for ICO) planes and bpp information
            //    - (for CUR) hotspot coordinates
            stream.skipFully(8);

            int dataSize = stream.readIntelInt();
            int dataOffset = stream.readIntelInt();

            icons.add(new IconInfo(currentImage, dataSize, dataOffset));
        }

        Collections.sort(icons);
        List<T> result = new ArrayList<>(Collections.<T>nCopies(numberOfImages, null));

        for(IconInfo iconInfo: icons) {
            int imageNumber = iconInfo.imageNumber;
            int dataOffset = iconInfo.dataOffset;
            int dataSize = iconInfo.dataSize;

            int streamOffset = stream.getOffset();
            int skip = dataOffset - streamOffset;
            if(skip < 0) {
                throw new IOException("icon starts at " + dataOffset + ", but " + streamOffset + " bytes have bean read");
            }
            if(skip > 0) {
                stream.skipFully(skip);
            }

            try {
                int magic = stream.readIntelInt();
                stream.unreadIntLE(magic);

                ImageDecoder<T> imageDecoder = getImageDecoder(magic);

                if (imageDecoder == null) {
                    throw new BadIconFormatException("unknown magic: " + Integer.toHexString(magic));
                }

                T image = imageDecoder.decodeImage(stream);

                result.set(imageNumber, image);
            } catch(BadIconFormatException ex) {
                throw new BadIconFormatException("Icon #" + imageNumber + ": " + ex.getMessage(), ex);
            }
        }
        stream.close();
        return result;
    }
}
