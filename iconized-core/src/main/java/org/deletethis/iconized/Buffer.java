package org.deletethis.iconized;

public class Buffer {
    private final int width, height;
    private int [] data;

    public Buffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new int[width*height];
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
