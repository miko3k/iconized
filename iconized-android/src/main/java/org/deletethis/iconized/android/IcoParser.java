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
package org.deletethis.iconized.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.deletethis.iconized.AbstractIcoParser;
import org.deletethis.iconized.ArrayImage;
import org.deletethis.iconized.BitmapDecoder;
import org.deletethis.iconized.ImageDecoder;

import java.io.IOException;
import java.io.InputStream;

public class IcoParser extends AbstractIcoParser<Bitmap> {
    private IcoParser() { }

    private static IcoParser INSTANCE = new IcoParser();

    private static final ImageDecoder<Bitmap> BMP_LOADER = new ImageDecoder<Bitmap>() {
        private ImageDecoder<ArrayImage> realDecoder = new BitmapDecoder<>(ArrayImage.FACTORY);

        @Override
        public Bitmap decodeImage(InputStream stream) throws IOException {
            ArrayImage image = realDecoder.decodeImage(stream);
            Bitmap result = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
            result.setPixels(image.getData(), 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            return result;
        }
    };

    private static final ImageDecoder<Bitmap> PNG_DECODER = new ImageDecoder<Bitmap>() {
        @Override
        public Bitmap decodeImage(InputStream stream) {
            return BitmapFactory.decodeStream(stream);
        }
    };

    public static IcoParser getInstance() { return INSTANCE; }

    @Override
    protected ImageDecoder<Bitmap> getImageDecoder(int magic) {
        if(magic == BMP_MAGIC) return BMP_LOADER;
        if(magic == PNG_MAGIC) return PNG_DECODER;
        return null;
    }
}
