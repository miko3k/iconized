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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract public class AbstractIconParser<T>  {
    abstract public IconReader<T> openReader(InputStream stream);

    final public List<T> getIcons(byte [] data) throws IOException {
        return getIcons(data, 0, data.length);
    }

    final public List<T> getIcons(byte [] data, int offset, int length) throws IOException {
        return getIcons(new ByteArrayInputStream(data, offset, length));
    }

    final public List<T> getIcons(InputStream inputStream) throws IOException {

        try {
            IconReader<T> iconReader = openReader(inputStream);
            List<IconMeta> iconMetas = iconReader.readMetadata();
            Map<Integer, T> data = new TreeMap<>();

            try {
                for(IconMeta m: iconMetas) {
                    T t = iconReader.readImage(m);
                    data.put(m.getImageNumber(), t);
                }
            } catch(IcoFormatException ex) {
                // no nothing, we could use a version which reports this somehow
            }
            return new ArrayList<>(data.values());
        } finally {
            inputStream.close();
        }
    }
}


