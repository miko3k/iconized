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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class AbstractIcoParser<T> {
    protected final static int BMP_MAGIC = 40;
    protected final static int PNG_MAGIC = 0x474E5089;

    private final static int ICON = 1, CURSOR = 2;

    abstract protected ImageDecoder<T> getImageDecoder(int magic);

    final public List<T> getIcons(byte [] buffer) throws IOException {
        return getIcons(buffer, 0, buffer.length);
    }

    final public List<T> getIcons(byte [] buffer, int offset, int length) throws IOException {
        return getIcons(new ByteArrayInputStream(buffer, offset, length));
    }

    final public List<T> getIcons(InputStream inputStream) throws IOException {
        return getIcons(new IconInputStream(inputStream));
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
            if(dataOffset != o.dataOffset) {
                return Integer.signum(dataOffset - o.dataOffset);
            }
            return Integer.signum(dataSize - o.dataSize);
        }
    }

    private void removeNulls(List<T> list) {
        int notNullCount = 0;
        for(int i=0; i < list.size(); ++i) {
            T t = list.get(i);
            if(t != null) {
                list.set(notNullCount, t);
                ++notNullCount;
            }
        }
        if(notNullCount < list.size()) {
            list.subList(notNullCount, list.size()).clear();
        }

    }

    private List<T> getIcons(IconInputStream stream) throws IOException {
        SimpleDataStream data = new SimpleDataStream(stream);

        if(data.readIntelShort() != 0) {
            throw new IcoFormatException("first WORD must be 0");
        }

        int type = data.readIntelShort();
        if(type != ICON && type != CURSOR)
            throw new IcoFormatException("second WORD must be 0 or 1");

        int numberOfImages = data.readIntelShort();
        if(numberOfImages == 0){
            throw new IcoFormatException("no icons in this file");
        }

        List<IconInfo> icons = new ArrayList<>(numberOfImages);

        for (int currentImage = 0; currentImage < numberOfImages; ++currentImage) {
            // information about colors etc are quite unreliable, coz they tend to overflow with big values,
            // we will just ignore them
            // discard:
            //    - width, height, number of colors, and reserved
            //    - (for ICO) planes and bpp information
            //    - (for CUR) hotspot coordinates
            data.skipFully(8);

            int dataSize = data.readIntelInt();
            int dataOffset = data.readIntelInt();

            icons.add(new IconInfo(currentImage, dataSize, dataOffset));
        }

        Collections.sort(icons);

        ArrayList<T> result = new ArrayList<>(Collections.<T>nCopies(numberOfImages, null));

        for(IconInfo iconInfo: icons) {
            int imageNumber = iconInfo.imageNumber;
            int dataOffset = iconInfo.dataOffset;

            long streamOffset = stream.getOffset();
            long skip = dataOffset - streamOffset;
            if(skip < 0) {
                // ignore this icon, we probably read too much during last image
                continue;
            }
            if(skip > 0) {
                data.skipFully((int)skip);
            }

            try {
                int magic = data.readIntelInt();

                stream.pushBack((byte)(magic >>> 24));
                stream.pushBack((byte)(magic >>> 16));
                stream.pushBack((byte)(magic >>> 8));
                stream.pushBack((byte)magic);

                ImageDecoder<T> imageDecoder = getImageDecoder(magic);

                if (imageDecoder == null) {
                    continue;
                }

                T image = imageDecoder.decodeImage(stream);

                result.set(imageNumber, image);
            } catch(IcoFormatException ex) {
                throw new IcoFormatException("Icon #" + imageNumber + ": " + ex.getMessage(), ex);
            }
        }
        removeNulls(result);
        if(result.isEmpty()){
            throw new IcoFormatException("no icon was successfully decoded");
        }

        return result;
    }
}
