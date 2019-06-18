/*
 * mejico - an .ico parser in Java
 *
 * Copyright (c) 2018-2019 Peter Hanula
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
package org.deletethis.mejico.awt;

import org.deletethis.mejico.IconParser;
import org.deletethis.mejico.IconReader;

import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * The Java parser of {@code .ico}/{@code .cur} files.
 *
 * It uses ImageIO to load PNG files. {@link BufferedImage} is used as the output format.
 */
public class AwtMejico {
    private static IconParser<BufferedImage> INSTANCE = new IconParser<>(new AwtImageDecoder());

    private AwtMejico() {}

    /**
     * Returns the singleton instance. This method is fast, result does not need to be cached.
     *
     * @return the {@link AwtMejico} instance
     */
    public static IconParser<BufferedImage> getIconParser() { return INSTANCE; }
}
