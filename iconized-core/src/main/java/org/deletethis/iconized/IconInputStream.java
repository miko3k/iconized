package org.deletethis.iconized;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class IconInputStream extends InputStream {
    private static final int PUSH_BACK_BUFFER_SIZE = 8;

    private final InputStream stream;
    private final byte [] pushBackBuffer = new byte[PUSH_BACK_BUFFER_SIZE];

    private int offset = 0;
    private int pushBackBufferUse;

    IconInputStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        if(pushBackBufferUse > 0) {
            ++offset;
            return pushBackBuffer[--pushBackBufferUse];
        } else {
            int result = stream.read();
            if (result >= 0)
                ++offset;

            return result;
        }
    }

    public void unreadInt(byte b) {
        --offset;
        pushBackBuffer[pushBackBufferUse++] = b;
    }

    public void unreadIntLE(int value) {
        unreadInt((byte)(value >> 24));
        unreadInt((byte)(value >> 16));
        unreadInt((byte)(value >> 8));
        unreadInt((byte)(value));
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

    public int readShortLE() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch2 << 8) + (ch1));
    }

    public int readIntLE() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1));
    }
}
