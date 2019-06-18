/*
 * mejico - an .ico parser in Java
 *
 * Copyright (c) 2018-2019 Peter Hanula
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
 * First call should be the call to {@link #readMetadata()} followed by one or
 * more calls to {@link #readImage(ImageMetadata)}. This calls should be issued
 * in the same order as returned in {@link FileMetadata}.
 * <p>
 * This class seeks balance between usable and low level API. We could also
 * provide way to read individual icon directory entries one by one, but
 * such an API would be harder to use as user would have to sort the entries
 * by hand. In addition, cost of reading all the entries is not significant compared
 * to cost of reading images themselves.
 *
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

    private static ImageMetadata readImageMetadata(SimpleDataStream data, boolean isCursor, int n) throws IOException {

        int width = data.readUnsignedByte();
        int height = data.readUnsignedByte();
        int colorCount = data.readUnsignedByte();
        data.skipFully(1);
        int colorPlanes = data.readUnsignedShort();
        int bpp = data.readUnsignedShort();

        int dataSize = data.readIntelInt();
        int dataOffset = data.readIntelInt();

        return new ImageMetadata(n, dataSize, dataOffset,
                width==0 ? null : width,
                height==0 ? null : height,
                colorCount==0 ? null : colorCount,
                isCursor ? null : colorPlanes,
                isCursor ? null : bpp,
                isCursor ? colorPlanes : null,
                isCursor ? bpp : null);
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

    public FileMetadata readMetadata() throws IOException {
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
        boolean isCursor = type == CURSOR;
        for (int currentImage = 0; currentImage < numberOfImages; ++currentImage) {
            ImageMetadata imageMetadata = readImageMetadata(data, isCursor, currentImage);
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

        return new FileMetadata(isCursor, Collections.unmodifiableList(items));
    }

    /**
     * Decodes a single images.
     * <p>
     * It seeks forward in the stream in order to find it first and then delegates reading to {@link ImageDecoder}.
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


