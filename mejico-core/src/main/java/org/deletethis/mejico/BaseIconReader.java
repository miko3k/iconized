package org.deletethis.mejico;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple extension of {@link AbstractIconReader} which can dispatch BMP or PNG decoder based on the magic number.
 *
 * @param <T> Type of output image
 */
abstract public class BaseIconReader<T> extends AbstractIconReader<T> {
    private final static int PNG_MAGIC = 0x474E5089;

    abstract protected T decodeBmp(InputStream inputStream) throws IOException;
    abstract protected T decodePng(InputStream inputStream) throws IOException;

    public BaseIconReader(InputStream stream) {
        super(stream);
    }

    @Override
    protected T decodeImage(int magic, InputStream stream) throws IOException {
        if(magic == BitmapDecoder.BMP_MAGIC) {
            return decodeBmp(stream);
        }
        if(magic == PNG_MAGIC) {
            return decodePng(stream);
        }
        throw new IcoFormatException("Failed to decode image with magic number " + Integer.toHexString(magic));
    }
}
