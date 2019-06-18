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

/**
 * A simple abstract implementation of {@link ImageDecoder}.
 *
 * It does nothing by itself and merely calls an abstract method to do the job.
 *
 * @param <T> Type of returned image
 */
abstract public class AbstractImageDecoder<T> implements ImageDecoder<T> {
    private final static int PNG_MAGIC = 0x474E5089;

    abstract protected T decodeBmp(InputStream inputStream) throws IOException;
    abstract protected T decodePng(InputStream inputStream) throws IOException;

    @Override
    public T decodeImage(int magic, InputStream stream) throws IOException {
        if(magic == BitmapDecoder.BMP_MAGIC) {
            return decodeBmp(stream);
        }
        if(magic == PNG_MAGIC) {
            return decodePng(stream);
        }
        throw new IconFormatException("Failed to decode image with magic number " + Integer.toHexString(magic));
    }}
