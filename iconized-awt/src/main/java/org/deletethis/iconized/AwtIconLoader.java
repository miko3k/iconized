package org.deletethis.iconized;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.*;
import java.io.IOException;
import java.util.Iterator;

public class AwtIconLoader extends BaseIcoDecoder<BufferedImage> {
    private static AwtIconLoader INSTANCE = new AwtIconLoader();

    public static AwtIconLoader getInstance() { return INSTANCE; }

    private AwtIconLoader() { }

    private ImageReader getPngReader() {
        Iterator<ImageReader> itr = ImageIO.getImageReadersByFormatName("png");
        if (itr.hasNext()) {
            return itr.next();
        } else {
            return null;
        }
    }

    private static class BufferedImagePixmap implements Pixmap {
        private final int [] data;
        private final BufferedImage image;
        private final int width, height;

        public BufferedImagePixmap(int width, int height) {
            this.width = width;
            this.height = height;
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        }


        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void setRGB(int x, int y, int rgb) {
            data[y*width+x] = rgb;
        }
    }


    private BufferedImage readImage(ImageReader imageReader, Buffer buffer) {
        try {
            ImageInputStream input = ImageIO.createImageInputStream(buffer.toInputStream());
            imageReader.setInput(input);
            return imageReader.read(0);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid PNG file: " + e.getMessage(), e);
        }
    }

    private static final IconBmpDecoder<BufferedImagePixmap> BMP_DECODER = new IconBmpDecoder<>(new PixmapFactory<BufferedImagePixmap>() {
        @Override
        public BufferedImagePixmap createPixmap(int width, int height) {
            return new BufferedImagePixmap(width, height);
        }
    });


    private static final BufferDecoder<BufferedImage> BMP_LOADER = new BufferDecoder<BufferedImage>() {
        @Override
        public BufferedImage decodeImage(Buffer buffer) {
            return BMP_DECODER.decodeImage(buffer).image;
        }
    };

    @Override
    protected BufferDecoder<BufferedImage> getImageDecoder(int magic) {
        if(magic == BufferDecoder.BMP_MAGIC) return BMP_LOADER;
        if(magic == BufferDecoder.PNG_MAGIC) {

            final ImageReader imageReader = getPngReader();
            if(imageReader == null) {
                return null;
            }

            return new BufferDecoder<BufferedImage>() {
                @Override
                public BufferedImage decodeImage(Buffer buffer) {
                    return readImage(imageReader, buffer);
                }
            };
        }
        return null;
    }
}
