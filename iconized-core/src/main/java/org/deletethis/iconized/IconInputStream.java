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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An improved {@link InputStream} for reading icons.
 *
 * It provides several features:
 * <ul>
 *    <li>Can read short and int values (similar to {@link java.io.DataInputStream}, but in little-endian
 *    <li>Can push back several input bytes (similar to {@link java.io.PushbackInputStream}
 *    <li>Tracks current offset from beginning of stream
 * </ul>
 */
public class IconInputStream extends FilterInputStream {
    private static final int PUSH_BACK_BUFFER_SIZE = 8;

    private final byte [] pushBackBuffer = new byte[PUSH_BACK_BUFFER_SIZE];

    private int offset = 0;
    private int pushBackBufferUse;

    IconInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int result;

        if(pushBackBufferUse > 0) {
            result = (pushBackBuffer[PUSH_BACK_BUFFER_SIZE-pushBackBufferUse] & 0xFF);
            --pushBackBufferUse;
        } else {
            result = in.read();
        }

        if (result >= 0)
            ++offset;

        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (len < 0) {
            throw new IllegalArgumentException();
        }
        int result;
        if(pushBackBufferUse > 0) {
            if(len >= pushBackBufferUse) {
                len = pushBackBufferUse;
            }
            System.arraycopy(pushBackBuffer, PUSH_BACK_BUFFER_SIZE-pushBackBufferUse, b, off, len);

            pushBackBufferUse -= len;
            result = len;
        } else {
            result = in.read(b, off, len);
        }
        offset += result;
        return result;
    }

    public void unread(byte b) {
        pushBackBufferUse++;
        pushBackBuffer[PUSH_BACK_BUFFER_SIZE-pushBackBufferUse] = b;

        --offset;
    }

    public void unreadIntLE(int value) {
        unread((byte)(value >> 24));
        unread((byte)(value >> 16));
        unread((byte)(value >> 8));
        unread((byte)(value));
    }

    public int getOffset() {
        return offset;
    }

    public void skipFully(long n) throws IOException {
        for(long i = 0; i < n; ++i) {
            read();
        }
    }

    public byte readByte() throws IOException {
        int ch = read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    public short readIntelShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch2 << 8) + (ch1));
    }

    public int readIntelInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1));
    }
}
