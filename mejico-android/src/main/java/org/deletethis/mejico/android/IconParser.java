package org.deletethis.mejico.android;

import android.graphics.Bitmap;
import org.deletethis.mejico.AbstractIconParser;

import java.io.InputStream;

public class IconParser extends AbstractIconParser<Bitmap> {
    private static IconParser INSTANCE = new IconParser();

    public static IconParser getInstance() { return INSTANCE; }

    @Override
    public IconReaderImpl openReader(InputStream stream) {
        return new IconReaderImpl(stream);
    }
}