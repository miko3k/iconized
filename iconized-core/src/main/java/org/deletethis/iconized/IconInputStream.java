package org.deletethis.iconized;

import java.io.IOException;
import java.io.InputStream;

public class IconInputStream extends InputStream {
    private static final int PUSH_BACK_BUFFER_SIZE = 8;
    private final InputStream stream;
    private int offset = 0;
    private int pushBackBufferUse;

    byte [] pushBackBuffer = new byte[PUSH_BACK_BUFFER_SIZE];

    public IconInputStream(InputStream stream) {
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

    public void pushBack(byte b) {
        --offset;
        pushBackBuffer[pushBackBufferUse++] = b;
    }

    public void pushBackInt(int value) {
        pushBack((byte)(value >> 24));
        pushBack((byte)(value >> 16));
        pushBack((byte)(value >> 8));
        pushBack((byte)(value));
    }

    public int getOffset() {
        return offset;
    }
}
