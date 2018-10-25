package org.deletethis.mejico;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface IconReader<T> extends Closeable {
    List<IconMeta> readMetadata()  throws IOException;
    T readImage(IconMeta meta) throws IOException;
}
