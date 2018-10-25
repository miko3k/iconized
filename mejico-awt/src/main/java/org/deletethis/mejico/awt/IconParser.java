package org.deletethis.mejico.awt;

import org.deletethis.mejico.AbstractIconParser;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class IconParser extends AbstractIconParser<BufferedImage> {
    private static IconParser INSTANCE = new IconParser();

    public static IconParser getInstance() { return INSTANCE; }

    @Override
    public IconReaderImpl openReader(InputStream stream) {
        return new IconReaderImpl(stream);
    }
}
