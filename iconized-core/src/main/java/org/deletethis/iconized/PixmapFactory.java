package org.deletethis.iconized;

public interface PixmapFactory<T extends Pixmap> {
    T createPixmap(int width, int height);
}
