package org.deletethis.iconized;

import java.io.IOException;
import java.io.InputStream;

/**
 * A stream with bunch of additional capabilities.
 *
 * <p>
 * It can push bytes back to input stream.
 *
 * <p>
 * It also tracks offset from the beginning of the stream.
 *
 * <p>
 * In can create "substreams" of limited size, which will provide data
 * from the original stream. All streams share same state, reading will yield identical
 * results, besides the fact they will return end-of-file at different positions.
 * <p>
 * Substream are not thread safe with each other.
 * <p>
 * Closing a substream is a no-op and optional.
 */
public class IconInputStream extends InputStream {
    private static class Data {
        private static final int PUSH_BACK_BUFFER_SIZE = 16;
        private byte [] pushBackBuffer = new byte[PUSH_BACK_BUFFER_SIZE];
        private int pushBackBufferUse = 0;
        private long offset = 0;
        private InputStream in;

        public Data(InputStream in) {
            this.in = in;
        }

        int read(long boundary) throws IOException {

            if(boundary >= 0 && offset >= boundary){
                return -1;
            }

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

        int read(long boundary, byte[] buffer, int o, int length) throws IOException {
            if(length == 0) {
                return 0;
            }
            if(length < 0){
                throw new IllegalArgumentException();
            }
            if (boundary >= 0 && this.offset + length >= boundary) {
                length = (int) (boundary - this.offset);
                assert length >= 0;

                if (length == 0) {
                    return -1;
                }
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

        long skip(long boundary, long count) throws IOException {
            if(boundary >= 0 && offset + count >= boundary) {
                count = boundary - offset;
                assert count >= 0;
            }

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

        long getOffset() {
            return offset;
        }

        void close() throws IOException {
            in.close();
        }

        void pushBack(byte b) {
            pushBackBufferUse++;
            pushBackBuffer[PUSH_BACK_BUFFER_SIZE-pushBackBufferUse] = b;
            --offset;
        }
    }

    private final Data data;
    private final long boundary;
    private static final long NO_BONDRAY = -1;

    public IconInputStream(InputStream inputStream) {
        this(new Data(inputStream), NO_BONDRAY);
    }

    private boolean isMainStream() {
        return boundary != NO_BONDRAY;
    }

    public IconInputStream(Data data, long boundary) {
        this.data = data;
        this.boundary = boundary;
    }

    @Override
    public int read() throws IOException {
        return data.read(boundary);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return data.read(boundary, buffer, offset, length);
    }


    @Override
    public long skip(long count) throws IOException {
        return data.skip(boundary, count);
    }

    public long getOffset() {
        return data.getOffset();
    }

    public long getBoundary() {
        return boundary;
    }

    @Override
    public void close() throws IOException {
        if(isMainStream()) {
            data.close();
        }
    }

    public void pushBack(byte b) throws IOException {
        data.pushBack(b);
    }

    public IconInputStream substream(long length) throws IOException {
        if(length < 0) {
            throw new IllegalArgumentException();
        }
        if(boundary >= 0) {
            if(data.getOffset() + length > boundary) {
                throw new IOException("substream exceed current boundary");
            }
        }

        return new IconInputStream(data, data.getOffset() + length);
    }
}
