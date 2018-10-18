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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AndroidIconLoader extends BaseIcoReader<Bitmap> {
    private static AndroidIconLoader INSTANCE = new AndroidIconLoader();

    public static AndroidIconLoader getInstance() { return INSTANCE; }

    private AndroidIconLoader() { }

    // is there a way to draw directly to a bitmap? Fast?
    private static final ImageDecoder<Bitmap> BMP_LOADER = new CoversionImageDecoder<>(
            new IconBmpDecoder<>(ArrayPixmap.FACTORY),
            new CoversionImageDecoder.Conversion<ArrayPixmap, Bitmap>() {
                @Override
                public Bitmap convert(ArrayPixmap src) {
                    Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
                    result.setPixels(src.getData(), 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
                    return result;
                }
            }
    );

    private final ImageDecoder<Bitmap> PNG_DECODER = new ImageDecoder<Bitmap>() {
        @Override
        public Bitmap decodeImage(IconInputStream stream) {
            return BitmapFactory.decodeStream(stream);
        }
    };

    @Override
    protected ImageDecoder<Bitmap> getImageDecoder(int magic) {
        if(magic == ImageDecoder.BMP_MAGIC) return BMP_LOADER;
        if(magic == ImageDecoder.PNG_MAGIC) return PNG_DECODER;
        return null;
    }
}
