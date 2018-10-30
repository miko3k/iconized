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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A stream which provides similar functionality to {@link java.io.DataInputStream}, but reads both little and big
 * endian values.
 *
 * It reads from underlying stream strictly when needed, does not do any buffering on its own. This allows
 * to layer it cleanly on top of {@link IconInputStream}.
 */
public class SimpleDataStream extends FilterInputStream {
    private byte [] buffer = new byte[4];

    public SimpleDataStream(InputStream inputStream) {
        super(inputStream);
    }

    public final void readFully(byte[] buffer) throws IOException {
        this.readFully(buffer, 0, buffer.length);
    }

    public final void readFully(byte[] buffer, int offset, int size) throws IOException {
        if (size < 0) {
            throw new IndexOutOfBoundsException();
        } else {
            int currentRead;
            for(int totalRead = 0; totalRead < size; totalRead += currentRead) {
                currentRead = this.in.read(buffer, offset + totalRead, size - totalRead);
                if (currentRead < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    public final void skipFully(int size) throws IOException {
        if (size < 0) {
            throw new IndexOutOfBoundsException();
        }
        while(size > 0) {
            long skipped = in.skip(size);
            if(skipped == 0) {
                int read = read();
                if(read < 0) {
                    throw new EOFException();
                } else {
                    --size;
                }
            } else {
                size -= (int)skipped;
            }
        }
    }


    public int readIntelInt() throws IOException {
        readFully(buffer, 0, 4);
        return (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8 | (buffer[2] & 0xFF) << 16 | (buffer[3] & 0xFF) << 24;
    }

    public int readInt() throws IOException {
        readFully(buffer, 0, 4);
        return (buffer[3] & 0xFF) | (buffer[2] & 0xFF) << 8 | (buffer[1] & 0xFF) << 16 | (buffer[0] & 0xFF) << 24;
    }

    public short readIntelShort() throws IOException {
        readFully(buffer, 0, 2);
        return (short)((buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8);
    }

    public short readShort() throws IOException {
        readFully(buffer, 0, 2);
        return (short)((buffer[1] & 0xFF) | (buffer[0] & 0xFF) << 8);
    }

    public byte readByte() throws IOException {
        int data = in.read();
        if(data < 0){
            throw new EOFException();
        }
        return (byte)data;
    }

    public int readUnsignedByte() throws IOException {
        return readByte()&0xFF;
    }

    public int readUnsignedShort() throws IOException {
        return readShort()&0xFFFF;
    }
}
