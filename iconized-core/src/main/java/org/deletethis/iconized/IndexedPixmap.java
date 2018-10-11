package org.deletethis.iconized;

/**
 * Write only version of pixmap with indexed color model
 */
public class IndexedPixmap {
    public static class Palette {
        private final int [] colors;

        public Palette(int[] colors) {
            this.colors = colors;
        }

        public int getColor(int color) {
            return colors[color];
        }
    }

    private final Palette palette;
    private final Pixmap pixmap;

    public IndexedPixmap(int width, int height, Palette palette) {
        this.palette = palette;
        this.pixmap = new Pixmap(width, height);
    }

    public int getWidth() {
        return pixmap.getWidth();
    }

    public int getHeight() {
        return pixmap.getHeight();
    }

    public void setColor(int x, int y, int color) {
        pixmap.setRGB(x, y, palette.getColor(color));
    }

    public Pixmap getPixmap() {
        return pixmap;
    }
}
