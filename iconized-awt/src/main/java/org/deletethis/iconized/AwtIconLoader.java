/*
 * Iconized - an .ico parser in Java
 *
 * Copyright (c) 2015-2017 Ian McDonagh
 * Copyright (c) 2018, Peter Hanula
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.deletethis.iconized;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
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

    private BufferedImage readImage(ImageReader imageReader, IconInputStream iconInputStream) {
        try {
            // we do not use ImageIO.createImageInputStream, because it creates cache file on the hard drive
            ImageInputStream input = new MemoryCacheImageInputStream(iconInputStream);
            imageReader.setInput(input);
            return imageReader.read(0);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid PNG file: " + e.getMessage(), e);
        }
    }

    private static class BufferedImagePixmap implements Pixmap {
        private final int [] data;
        private final BufferedImage image;
        private final int width, height;

        BufferedImagePixmap(int width, int height) {
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
        public void setARGB(int x, int y, int rgb) {
            data[y*width+x] = rgb;
        }
    }

    private static final ImageDecoder<BufferedImage> BMP_LOADER = new CoversionImageDecoder<>(
            new IconBmpDecoder<>(new PixmapFactory<BufferedImagePixmap>() {
                @Override
                public BufferedImagePixmap createPixmap(int width, int height) {
                    return new BufferedImagePixmap(width, height);
                }
            }),
            new CoversionImageDecoder.Conversion<BufferedImagePixmap, BufferedImage>() {
                @Override
                public BufferedImage convert(BufferedImagePixmap bufferedImagePixmap) {
                    return bufferedImagePixmap.image;
                }
            }
    );

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
                public BufferedImage decodeImage(IconInputStream stream) {
                    return readImage(imageReader, stream);
                }
            };
        }
        return null;
    }
}
