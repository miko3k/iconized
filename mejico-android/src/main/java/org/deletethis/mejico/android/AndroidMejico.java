/*
 * mejico - an .ico parser in Java
 *
 * Copyright (c) 2018 Peter Hanula
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
package org.deletethis.mejico.android;

import android.graphics.Bitmap;
import org.deletethis.mejico.IconParser;
import org.deletethis.mejico.IconReader;

import java.io.InputStream;

/**
 * The factory to retrieve parser of {@code .ico}/{@code .cur} files on Android.
 */
public class AndroidMejico {
    private static IconParser<Bitmap> INSTANCE = new IconParser<>(new AndroidImageDecoder());
    private AndroidMejico() {}

    /**
     * Returns the singleton instance. This method is fast, result does not need to be cached.
     *
     * @return the {@link AndroidMejico} instance
     */
    public static IconParser<Bitmap> getIconParser() { return INSTANCE; }
}