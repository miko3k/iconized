package org.deletethis.iconized;

import java.io.IOException;
import java.io.InputStream;

abstract public class BaseIcoParser<T> extends AbstractIcoParser<T> {
    abstract protected T decodeBmp(InputStream inputStream) throws IOException;
    abstract protected T decodePng(InputStream inputStream) throws IOException;

    private final ImageDecoder<T> BMP = new ImageDecoder<T>() {
        @Override
        public T decodeImage(InputStream stream) throws IOException {
            return decodeBmp(stream);
        }
    };
    private final ImageDecoder<T> PNG = new ImageDecoder<T>() {
        @Override
        public T decodeImage(InputStream stream) throws IOException {
            return decodePng(stream);
        }
    };


    @Override
    protected ImageDecoder<T> getImageDecoder(int magic) {
        if(magic == BMP_MAGIC) {
            return BMP;
        }
        if(magic == PNG_MAGIC) {
            return PNG;
        }
        return null;
    }
}
