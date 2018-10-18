package org.deletethis.iconized;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.*;
import java.io.IOException;
import java.util.Iterator;

public class AwtIconLoader extends BaseIcoReader<BufferedImage> {
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


    private BufferedImage readImage(ImageReader imageReader, IconInputStream iconInputStream) {
        try {
            ImageInputStream input = ImageIO.createImageInputStream(iconInputStream);
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


    private static final ImageDecoder<BufferedImage> BMP_LOADER = new ImageDecoder<BufferedImage>() {
        @Override
        public BufferedImage decodeImage(IconInputStream iconInputStream) throws IOException {
            return BMP_DECODER.decodeImage(iconInputStream).image;
        }
    };

    @Override
    protected ImageDecoder<BufferedImage> getImageDecoder(int magic) {
        if(magic == ImageDecoder.BMP_MAGIC) return BMP_LOADER;
        if(magic == ImageDecoder.PNG_MAGIC) {

            final ImageReader imageReader = getPngReader();
            if(imageReader == null) {
                return null;
            }

            return new ImageDecoder<BufferedImage>() {
                @Override
                public BufferedImage decodeImage(IconInputStream iconInputStream) {
                    return readImage(imageReader, iconInputStream);
                }
            };
        }
        return null;
    }
}
