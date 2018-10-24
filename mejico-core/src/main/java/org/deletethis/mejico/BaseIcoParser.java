package org.deletethis.mejico;

import java.io.IOException;
import java.io.InputStream;

abstract public class BaseIcoParser<T> extends AbstractIcoParser<T> {
    private final static int BMP_MAGIC = 40;
    private final static int PNG_MAGIC = 0x474E5089;

    abstract protected T decodeBmp(InputStream inputStream) throws IOException;
    abstract protected T decodePng(InputStream inputStream) throws IOException;

    @Override
    protected T decodeImage(int magic, IconInputStream stream) throws IOException {
        if(magic == BMP_MAGIC) {
            return decodeBmp(stream);
        }
        if(magic == PNG_MAGIC) {
            return decodePng(stream);
        }
        // or throw?
        return null;
    }
}
