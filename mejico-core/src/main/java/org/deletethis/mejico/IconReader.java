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
 * <p>
 * It reads file header and decodes individual images using {@link ImageDecoder}.
 * <p>
 * @param <T> Platform dependent image type
 */
public class IconReader<T>  {
    private final static int ICON = 1, CURSOR = 2;
    private final ImageDecoder<T> imageDecoder;
    private final IconInputStream stream;

    public IconReader(ImageDecoder<T> imageDecoder, InputStream stream) {
        this.imageDecoder = imageDecoder;
        this.stream = new IconInputStream(stream);
    }

    private ImageMetadata readImageMetadata(int n) throws IOException {
        SimpleDataStream data = new SimpleDataStream(stream);
        // information about colors etc are quite unreliable, coz they tend to overflow with big values,
        // we will just ignore them
        // discard:
        //    - width, height, number of colors, and reserved
        //    - (for ICO) planes and bpp information
        //    - (for CUR) hotspot coordinates
        data.skipFully(8);

        int dataSize = data.readIntelInt();
        int dataOffset = data.readIntelInt();

        return new ImageMetadata(n, dataSize, dataOffset);
    }

    /**
     * Returns information about images in a stream.
     * <p>
     * Can be called only once and should be the first call.
     * <p>
     * Images are ordered by they order in file, so then can be easily sequentially read.
     *
     * @return file meta data
     * @throws IOException when IO error occurs
     */

    public FileMetadata readFileMetadata() throws IOException {
        SimpleDataStream data = new SimpleDataStream(stream);

        if (data.readIntelShort() != 0) {
            throw new IconFormatException("first WORD must be 0");
        }

        final int type = data.readIntelShort();
        if (type != ICON && type != CURSOR)
            throw new IconFormatException("second WORD must be 0 or 1");

        final int numberOfImages = data.readIntelShort();
        if (numberOfImages == 0) {
            throw new IconFormatException("no icons in this file");
        }

        final List<ImageMetadata> items = new ArrayList<>(numberOfImages);

        for (int currentImage = 0; currentImage < numberOfImages; ++currentImage) {
            ImageMetadata imageMetadata = readImageMetadata(currentImage);
            items.add(imageMetadata);
        }
        Collections.sort(items, new Comparator<ImageMetadata>() {
            @Override
            public int compare(ImageMetadata e, ImageMetadata f) {

                if (e.getDataOffset() != f.getDataOffset()) {
                    return Integer.signum(e.getDataOffset() - f.getDataOffset());
                }
                return Integer.signum(e.getDataSize() - f.getDataOffset());
            }
        });

        return new FileMetadata(type == CURSOR, Collections.unmodifiableList(items));
    }

    /**
     * Decodes a single image and returns the image data.
     * <p>
     * It seeks forward in the stream in order to find it first and then delegates decoding to {@link ImageDecoder}.
     *
     * @param meta identifies image to be decoded
     *
     * @return Decoded image in platform format
     * @throws IOException when IO error occurs
     */
    public T readImage(ImageMetadata meta) throws IOException {
        int dataOffset = meta.getDataOffset();

        long streamOffset = stream.getOffset();
        long skip = dataOffset - streamOffset;
        if (skip < 0) {
            throw new IconFormatException("current stream offset is " + streamOffset + " but icon begins at " + dataOffset);
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

        T image = imageDecoder.decodeImage(magic, stream);
        return Objects.requireNonNull(image);
    }

    /**
     * Closes underlying stream
     * @throws IOException when IO error occurs
     */
    public void close() throws IOException {
        stream.close();
    }
}


