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
 * <p>
 * High level interface consists of various {@code getIcons} methods. All of them
 * always automatically close the stream which is passed in. The stream is closed even if an exception
 * is thrown.
 * <p>
 * Lower level interface is provided by various overloads of {@code openReader}, which
 * return {@link IconReader}. This enables more fine grained control over which images get decoded.
 * These methods ignore non-fatal errors by default, an {@link IconErrorListener} might be used to alter
 * this behaviour.
 * <p>
 * This class is expected to be overridden by platform implementation.
 * Only overridable method is {@link #openReader(InputStream)}.
 *
 * @param <T> The type of image produced
 *
 */
abstract public class AbstractIconParser<T>  {
    /**
     * Returns an instance of {@link IconReader} which enables to read file metadata and individual images.
     * <p>
     * Icon reader is not closed automatically and should be used with {@code try} with resources.
     *
     * @param stream the input stream
     * @return an instance of the icon reader
     */
    abstract public IconReader<T> openReader(InputStream stream);

    /**
     * Returns an {@link IconReader} to read the byte array. See {@link #openReader(InputStream)} for further detail.
     *
     * @param data byte array to be read
     * @return an instance of the icon reader
     */
    final public IconReader<T> openReader(byte[] data) {
        return openReader(new ByteArrayInputStream(data));
    }

    /**
     * Returns images from the icon file contained in specified byte array. Non-fatal errors
     * are silently ignored. See {@link #getIcons(InputStream, IconErrorListener)} for further details.
     * <p>
     * To read only a part of the byte array, construct {@link ByteArrayInputStream} yourself.
     *
     * @param data {@code .ico} file as an byte array
     * @return the list of images from icon file
     * @throws IOException when IO error occurs
     */
    final public List<T> getIcons(byte [] data) throws IOException {
        return getIcons(new ByteArrayInputStream(data), IconErrorListener.BLACKHOLE);
    }

    /**
     * Returns images from the icon file contained in specified byte array.
     * See {@link #getIcons(InputStream, IconErrorListener)} for further details.
     * <p>
     * To read only a part of the byte array, construct {@link ByteArrayInputStream} yourself.
     *
     * @param data {@code .ico} file as an byte array
     * @param errorListener callback to be called when an error occurs
     * @return the list of images from icon file
     * @throws IOException when IO error occurs
     */
    final public List<T> getIcons(byte [] data, IconErrorListener errorListener) throws IOException {
        return getIcons(new ByteArrayInputStream(data), errorListener);
    }

    /**
     * Returns icon images from specified {@link InputStream}. Non-fatal errors
     * are ignored. See {@link #getIcons(InputStream, IconErrorListener)} for further details.
     * <p>
     * To read only a part of the byte array, construct {@link ByteArrayInputStream} yourself.
     *
     * @param inputStream the stream to read from
     * @return the list of images from icon file
     * @throws IOException when IO error occurs
     */
    final public List<T> getIcons(InputStream inputStream) throws IOException {
        return getIcons(inputStream, IconErrorListener.BLACKHOLE);
    }

    /**
     * Returns icons from specified {@link InputStream}. An error listener is notified, when a problem occurs.
     * <p>
     * The icons are returned in the same order as specified in icon directory, rather than ordering
     * of {@link ImageMetadata} in {@link FileMetadata}.
     * <p>
     * Errors can be suppressed by passing {@link IconErrorListener#BLACKHOLE} to error listener or made
     * fatal by using {@link IconErrorListener#MAKE_FATAL}. Listener might also simply log errors
     * for further diagnosis.
     * <p>
     * In case no image is read, an {@link IconFormatException} is thrown, rather than an empty list.
     *
     * @param inputStream the stream to read from
     * @param errorListener callback to notify in case recoverable error
     *
     * @return the list of images in platform format
     * @throws IOException when IO error occurs
     */
    final public List<T> getIcons(InputStream inputStream, IconErrorListener errorListener) throws IOException {
        Objects.requireNonNull(inputStream, "input stream is null");
        Objects.requireNonNull(errorListener, "error listener is null");
        try {
            IconReader<T> iconReader = openReader(inputStream);
            FileMetadata fileMetadata = iconReader.readMetadata();

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


