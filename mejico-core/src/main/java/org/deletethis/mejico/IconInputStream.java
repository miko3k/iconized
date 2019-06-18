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
 * A stream with bunch of additional capabilities.
 *
 * It can push bytes back to input stream and tracks offset from the beginning of the stream.
 */
class IconInputStream extends InputStream {
    private static final int PUSH_BACK_BUFFER_SIZE = 16;
    private byte [] pushBackBuffer = new byte[PUSH_BACK_BUFFER_SIZE];
    private int pushBackBufferUse = 0;
    private long offset = 0;
    private InputStream in;

    IconInputStream(InputStream inputStream) {
        in = inputStream;
    }

    public int read() throws IOException {

        int result;
        if(pushBackBufferUse > 0) {
            result = pushBackBuffer[PUSH_BACK_BUFFER_SIZE-pushBackBufferUse] & 0xFF;
            --pushBackBufferUse;
        } else {
            result = in.read();
        }
        if(result >= 0)
            ++offset;

        return result;
    }

    public int read(byte[] buffer, int o, int length) throws IOException {
        if(length == 0) {
            return 0;
        }
        if(length < 0){
            throw new IllegalArgumentException();
        }

        if(pushBackBufferUse > 0) {
            if(length >= pushBackBufferUse) {
                length = pushBackBufferUse;
            }
            System.arraycopy(pushBackBuffer, PUSH_BACK_BUFFER_SIZE-pushBackBufferUse, buffer, o, length);
            pushBackBufferUse -= length;
        } else {
            length = in.read(buffer, o, length);
        }
        this.offset += length;
        return length;
    }

    public long skip(long count) throws IOException {
        if(pushBackBufferUse > 0) {
            if(count > pushBackBufferUse)
                count = pushBackBufferUse;

            pushBackBufferUse -= count;
        } else {
            count = in.skip(count);
        }
        offset += count;
        return count;
    }

    public long getOffset() {
        return offset;
    }


    void pushBack(byte b) {
        pushBackBufferUse++;
        pushBackBuffer[PUSH_BACK_BUFFER_SIZE-pushBackBufferUse] = b;
        --offset;
    }


}
