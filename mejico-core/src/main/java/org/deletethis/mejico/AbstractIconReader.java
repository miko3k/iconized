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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Core parser of ICO file.
 *
 * It reads file header and decodes individual images using {@link #decodeImage}.
 *
 * Abstract method {@link #decodeImage} is meant to be overridden by subclasses.
 *
 * @param <T> Platform dependent image type
 */
abstract public class AbstractIconReader<T> implements IconReader<T> {
    private final static int ICON = 1, CURSOR = 2;
    private final IconInputStream stream;
    private boolean metadataRead = false;

    public AbstractIconReader(InputStream stream) {
        this.stream = new IconInputStream(stream);
    }

    protected abstract T decodeImage(int magic, InputStream stream) throws IOException;

    private static class IconInfo implements IconMeta {
        final private int imageNumber;
        final private int dataSize;
        final private int dataOffset;

        public IconInfo(int imageNumber, int dataSize, int dataOffset) {
            this.imageNumber = imageNumber;
            this.dataSize = dataSize;
            this.dataOffset = dataOffset;
        }

        @Override
        public int getImageNumber() {
            return imageNumber;
        }
    }

    @Override
    public List<IconMeta> readMetadata() throws IOException {
        SimpleDataStream data = new SimpleDataStream(stream);

        if(metadataRead) {
            throw new IOException("Metadata already read");
        }
        if (data.readIntelShort() != 0) {
            throw new IcoFormatException("first WORD must be 0");
        }

        int type = data.readIntelShort();
        if (type != ICON && type != CURSOR)
            throw new IcoFormatException("second WORD must be 0 or 1");

        int numberOfImages = data.readIntelShort();
        if (numberOfImages == 0) {
            throw new IcoFormatException("no icons in this file");
        }

        List<IconMeta> icons = new ArrayList<>(numberOfImages);

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

        Collections.sort(icons, new Comparator<IconMeta>() {
            @Override
            public int compare(IconMeta a, IconMeta b) {
                IconInfo e = (IconInfo)a;
                IconInfo f = (IconInfo)b;

                if (e.dataOffset != f.dataOffset) {
                    return Integer.signum(e.dataOffset - f.dataOffset);
                }
                return Integer.signum(e.dataSize - f.dataSize);
            }
        });

        return icons;
    }

    @Override
    public T readImage(IconMeta meta) throws IOException {
        IconInfo iconInfo = (IconInfo) meta;

        int imageNumber = iconInfo.imageNumber;
        int dataOffset = iconInfo.dataOffset;

        long streamOffset = stream.getOffset();
        long skip = dataOffset - streamOffset;
        if (skip < 0) {
            throw new IcoFormatException("current stream offset is " + streamOffset + " but icon begins at " + dataOffset);
        }

        SimpleDataStream data = new SimpleDataStream(stream);

        if (skip > 0) {
            data.skipFully((int) skip);
        }

        int magic = data.readIntelInt();

        stream.pushBack((byte) (magic >>> 24));
        stream.pushBack((byte) (magic >>> 16));
        stream.pushBack((byte) (magic >>> 8));
        stream.pushBack((byte) magic);

        T image = decodeImage(magic, stream);
        return Objects.requireNonNull(image);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}


