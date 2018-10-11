package org.deletethis.icontized.reftest;

import org.deletethis.iconized.codec.ico.ICODecoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IcoTestDecoder {
    public static List<BufferedImage> loadIcons(String name) throws IOException {
        InputStream resourceAsStream = IcoAssert.class.getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new AssertionError("resource not found: " + name);
        }
        return ICODecoder.read(resourceAsStream);

    }
}
