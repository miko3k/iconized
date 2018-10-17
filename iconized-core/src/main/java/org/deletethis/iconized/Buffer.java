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
import java.io.InputStream;

/** Poor man's {@link java.nio.ByteBuffer}.
 *
 * We are using it because we need to access underlying byte[] quite often, for example to construct
 * {@link java.io.ByteArrayInputStream} or {@link javax.imageio.stream.ImageInputStream}. We could
 * implement some kind of ByteBufferInputStream, but this class is sufficient.
 */
public class Buffer {
    private final byte [] data;
    private final int offset;
    private final int size;
    private int pos;

    public Buffer(byte[] data, int offset, int size) {
        this.pos = 0;
        this.data = data;
        this.offset = offset;
        this.size = size;

        if(offset + size > data.length)
            throw new IllegalArgumentException("buffer too big!");
    }

    public Buffer slice(int offset, int size) {
        if(offset + size > this.size) {
            throw new IllegalArgumentException("slice too long: " + offset + " + " + size
                    + ", current: " + this.offset + " + " + this.size);
        }
        return new Buffer(data, offset + this.offset, size);
    }

    public void skip(int skip) {
        if(pos+skip > size)
            throw new IllegalArgumentException("Not enough data");

        pos += skip;
    }


    public int int8() {
        int result = int8(pos);
        ++pos;
        return result;
    }

    public byte byte8() {
        byte result = byte8(pos);
        ++pos;
        return result;
    }


    public int int16() {
        int result = int16(pos);
        pos += 2;
        return result;
    }

    public int int32() {
        int result = int32(pos);
        pos += 4;
        return result;
    }

    private int int8(int index) {
        return byte8(index) & 0xFF;
    }

    public void readBytes(byte[] out, int length) {
        readBytes(pos, out, length);
        pos += length;
    }


    private byte byte8(int index) {
        if(index >= size) {
            throw new IllegalArgumentException("offset outside!");
        }
        return data[index+offset];
    }

    private int int16(int index) {
        if(index >= size-1) {
            throw new IllegalArgumentException("offset outside!");
        }
        int b1 = data[index+offset] & 0xFF;
        int b2 = data[index+offset+1] & 0xFF;

        return b1 | (b2 << 8);
    }

    public int int32(int index) {
        if(index >= size-3) {
            throw new IllegalArgumentException("offset outside!");
        }
        int b1 = data[index+offset] & 0xFF;
        int b2 = data[index+offset+1] & 0xFF;
        int b3 = data[index+offset+2] & 0xFF;
        int b4 = data[index+offset+3] & 0xFF;

        return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
    }

    private void readBytes(int index, byte[] out, int length) {
        if(index > size-length) {
            throw new IllegalArgumentException("offset outside!");
        }
        System.arraycopy(data, index+offset, out, 0, length);
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(data, offset, size);
    }

    public int size() {
        return size;
    }

    public int remaining() { return size-offset; }

}
