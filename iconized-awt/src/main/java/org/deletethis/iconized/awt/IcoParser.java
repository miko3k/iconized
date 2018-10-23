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
package org.deletethis.iconized.awt;

import org.deletethis.iconized.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class IcoParser extends AbstractIcoParser<BufferedImage> {
    private static IcoParser INSTANCE = new IcoParser();

    public static IcoParser getInstance() { return INSTANCE; }

    private IcoParser() { }

    private ImageReader getPngReader() {
        Iterator<ImageReader> itr = ImageIO.getImageReadersByFormatName("png");
        if (itr.hasNext()) {
            return itr.next();
        } else {
            return null;
        }
    }

    private BufferedImage readImage(ImageReader imageReader, InputStream iconInputStream) {
        try {
            // we do not use ImageIO.createImageInputStream, because it creates cache file on the hard drive
            ImageInputStream input = new MemoryCacheImageInputStream(iconInputStream);
            imageReader.setInput(input);
            return imageReader.read(0);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid PNG file: " + e.getMessage(), e);
        }
    }

    private static class AwtImage implements Image {
        private final int [] data;
        private final BufferedImage bufferedImage;
        private final int width, height;

        AwtImage(int width, int height) {
            this.width = width;
            this.height = height;
            this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.data = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
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


    private static final ImageDecoder<BufferedImage> BMP_LOADER = new ImageDecoder<BufferedImage>() {
        private final BitmapDecoder<AwtImage> decoder = new BitmapDecoder<>(new ImageFactory<AwtImage>() {
            @Override
            public AwtImage createImage(int width, int height) {
                return new AwtImage(width, height);
            }
        });

        @Override
        public BufferedImage decodeImage(InputStream stream) throws IOException {
            AwtImage awtImage = decoder.decodeImage(stream);
            return awtImage.bufferedImage;
        }
    };

    @Override
    protected ImageDecoder<BufferedImage> getImageDecoder(int magic) {
        if(magic == BMP_MAGIC) return BMP_LOADER;
        if(magic == PNG_MAGIC) {

            final ImageReader imageReader = getPngReader();
            if(imageReader == null) {
                return null;
            }

            return new ImageDecoder<BufferedImage>() {
                @Override
                public BufferedImage decodeImage(InputStream stream) {
                    return readImage(imageReader, stream);
                }
            };
        }
        return null;
    }
}
