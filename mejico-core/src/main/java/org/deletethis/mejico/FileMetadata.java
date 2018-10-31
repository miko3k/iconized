package org.deletethis.mejico;

import java.util.List;

/**
 * Metadata about {@code .ico} or {@code .cur} file.
 */
public class FileMetadata {
    private final boolean cursor;
    private final List<ImageMetadata> imageMetadata;

    public FileMetadata(boolean cursor, List<ImageMetadata> imageMetadata) {
        this.cursor = cursor;
        this.imageMetadata = imageMetadata;
    }

    public boolean isCursor() {
        return cursor;
    }

    public List<ImageMetadata> getImageMetadata() {
        return imageMetadata;
    }
}