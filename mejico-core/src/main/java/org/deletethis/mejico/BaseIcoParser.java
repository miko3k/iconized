package org.deletethis.mejico;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple extension of {@link AbstractIcoParser} which can dispatch BMP or PNG decoder based on the magic number.
 *
 * @param <T> Type of output image
 */
abstract public class BaseIcoParser<T> extends AbstractIcoParser<T> {
    private final static int PNG_MAGIC = 0x474E5089;

    abstract protected T decodeBmp(InputStream inputStream) throws IOException;
    abstract protected T decodePng(InputStream inputStream) throws IOException;

    @Override
    protected T decodeImage(int magic, IconInputStream stream) throws IOException {
        if(magic == BitmapDecoder.BMP_MAGIC) {
            return decodeBmp(stream);
        }
        if(magic == PNG_MAGIC) {
            return decodePng(stream);
        }
        // or throw?
        return null;
    }
}
