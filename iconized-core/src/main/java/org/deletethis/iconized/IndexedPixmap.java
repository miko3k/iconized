package org.deletethis.iconized;

/**
 * Write only version of pixmap with indexed color model
 */
public class IndexedPixmap {
    private final int width, height;
    private int [] data;

    public IndexedPixmap(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new int[width*height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getData() {
        return data;
    }

    public int getRGB(int x, int y) {
        int w = width;
        if(x < 0 || x >= w)
            throw new IllegalArgumentException("not withing the buffer");

        if(y < 0 || y >= height)
            throw new IllegalArgumentException("not withing the buffer");

        return data[w*y+x];
    }

    public void setRGB(int x, int y, int rgb) {
        int w = width;
        if(x < 0 || x >= w)
            throw new IllegalArgumentException("not withing the buffer");

        if(y < 0 || y >= height)
            throw new IllegalArgumentException("not withing the buffer");

        data[w*y+x] = rgb;
    }
}
