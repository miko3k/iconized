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
package org.deletethis.mejico.awt;

import org.deletethis.mejico.AbstractIconParser;
import org.deletethis.mejico.IconReader;

import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * The Java parser of {@code .ico}/{@code .cur} files.
 *
 * It uses ImageIO to load PNG files. {@link BufferedImage} is used as the output format.
 */
public class IconParser extends AbstractIconParser<BufferedImage> {
    private static IconParser INSTANCE = new IconParser();
    private static AwtImageDecoder IMAGE_DECODER = new AwtImageDecoder();
    private IconParser() {}

    /**
     * Returns the singleton instance. This method is fast, result does not need to be cached.
     *
     * @return the {@link IconParser} instance
     */
    public static IconParser getInstance() { return INSTANCE; }

    /**
     * {@inheritDoc}
     */
    @Override
    public IconReader<BufferedImage> openReader(InputStream stream) {
        return new IconReader<>(IMAGE_DECODER, stream);
    }
}
