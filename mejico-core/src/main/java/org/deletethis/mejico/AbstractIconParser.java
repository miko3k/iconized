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
import java.util.*;

/**
 * Main icon loader.
 *
 * High level interface consists of various {@code getIcons} methods. All of them
 * automatically close the stream which is passed in. Always, also in a case of exception.
 *
 * One might open an {@link IconReader}, which allows more fine grained control which images will get decoded.
 *
 * This class is expected to be overridden by platform implementation.
 * Only overridable method is {@link #openReader(InputStream)} which does the core work.
 *
 * @param <T>
 */
abstract public class AbstractIconParser<T>  {
    /**
     * Returns an instance of {@link IconReader} which enables to read file metadata and individual images.
     *
     * @param stream the input stream
     * @return an instance of icon reader
     */
    abstract public IconReader<T> openReader(InputStream stream);

    final public IconReader<T> openReader(byte[] data) {
        return openReader(new ByteArrayInputStream(data));
    }

    final public List<T> getIcons(byte [] data) throws IOException {
        return getIcons(new ByteArrayInputStream(data), IconErrorListener.BLACKHOLE);
    }

    final public List<T> getIcons(byte [] data, IconErrorListener errorListener) throws IOException {
        return getIcons(new ByteArrayInputStream(data), errorListener);
    }

    final public List<T> getIcons(InputStream inputStream) throws IOException {
        return getIcons(inputStream, IconErrorListener.BLACKHOLE);
    }

    final public List<T> getIcons(InputStream inputStream, IconErrorListener errorListener) throws IOException {
        Objects.requireNonNull(inputStream, "input stream is null");
        Objects.requireNonNull(errorListener, "error listener is null");
        try {
            IconReader<T> iconReader = openReader(inputStream);
            FileMetadata fileMetadata = iconReader.readFileMetadata();

            Map<Integer, T> data = new TreeMap<>();

            for(ImageMetadata item: fileMetadata.getImageMetadata()) {
                try {
                    T t = iconReader.readImage(item);
                    data.put(item.getIndex(), t);
                } catch(IconFormatException ex) {
                    errorListener.onIconError(item, ex);
                }
            }
            ArrayList<T> result = new ArrayList<>(data.values());
            if(result.isEmpty()) {
                throw new IconFormatException("No icons successfully read");
            }
            return result;
        } finally {
            inputStream.close();
        }
    }
}


