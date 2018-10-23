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

public class IcoParser extends BaseIcoParser<BufferedImage> {
    private static IcoParser INSTANCE = new IcoParser();

    public static IcoParser getInstance() { return INSTANCE; }

    private IcoParser() { }

    private static class AwtImage implements Image {
        private final int [] data;
        private final BufferedImage bufferedImage;
        // attributes are not strictly necessary, as they are redundant with getWidth/getHeight on BufferedImage
        // but we keep them, because we access them a lot and it might speed up something (or not... but they do
        // not hurt)
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

    private static final BitmapDecoder<AwtImage> BMP_DECODER = new BitmapDecoder<>(new ImageFactory<AwtImage>() {
        @Override
        public AwtImage createImage(int width, int height) {
            return new AwtImage(width, height);
        }
    });

    @Override
    protected BufferedImage decodeBmp(InputStream inputStream) throws IOException {
        AwtImage awtImage = BMP_DECODER.decodeImage(inputStream);
        return awtImage.bufferedImage;
    }

    @Override
    protected BufferedImage decodePng(InputStream inputStream) throws IOException {
        ImageReader imageReader = null;
        Iterator<ImageReader> itr = ImageIO.getImageReadersByFormatName("png");
        if (itr.hasNext()) {
            imageReader = itr.next();
        }
        if(imageReader == null) {
            throw  new IOException("unable to find PNG decoder");
        }
        try {
            // we do not use ImageIO.createImageInputStream, because it creates cache file on the hard drive
            ImageInputStream input = new MemoryCacheImageInputStream(inputStream);
            imageReader.setInput(input);
            return imageReader.read(0);
        } catch (IOException e) {
            throw new IcoFormatException("Invalid PNG file: " + e.getMessage(), e);
        }
    }
}
