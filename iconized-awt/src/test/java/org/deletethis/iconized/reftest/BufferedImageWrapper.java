package org.deletethis.iconized.reftest;

import java.awt.image.BufferedImage;

class BufferedImageWrapper implements ImageWrapper {
    private final BufferedImage img;

    public BufferedImageWrapper(BufferedImage img) {
        this.img = img;
    }


    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public int getARGB(int x, int y) {
        return img.getRGB(x, y);
    }
}