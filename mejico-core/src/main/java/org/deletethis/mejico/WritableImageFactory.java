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
package org.deletethis.mejico;

/**
 * An abstract way of creating the {@link WritableImage}
 *
 * @param <T> Type of image which is produced by this factory
 */
public interface WritableImageFactory<T extends WritableImage> {
    /**
     * Creates an image with specified dimensions
     *
     * @param width desired width
     * @param height desired height
     * @return a brand new image
     */
    T createWritableImage(int width, int height);
}
