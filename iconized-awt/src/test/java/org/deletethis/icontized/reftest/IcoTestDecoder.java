package org.deletethis.icontized.reftest;

import org.apache.commons.io.IOUtils;
import org.deletethis.iconized.AwtIconLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IcoTestDecoder {
    public static List<BufferedImage> loadIcons(String name)  {
        InputStream resourceAsStream = IcoAssert.class.getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new AssertionError("resource not found: " + name);
        }
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(resourceAsStream);
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }

        try {
            return AwtIconLoader.getInstance().decode(bytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}
