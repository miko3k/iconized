package org.deletethis.icontized.reftest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class IcoAssert {
    private static void compareImages(BufferedImage ref, BufferedImage icon) {
        // fuck performance!
        String refsize = ref.getWidth() + " x " + ref.getWidth();
        String iconsize = icon.getWidth() + " x " + icon.getWidth();

        if (!refsize.equals(iconsize))
            throw new AssertionError("bad size, expected: "
                    + refsize + ", actual " + iconsize);


        for (int y = 0; y < ref.getHeight(); y++) {
            for (int x = 0; x < ref.getWidth(); x++) {
                try {
                    int r = ref.getRGB(x, y);
                    int i = icon.getRGB(x, y);

                    int ra = (r >> 24) & 0xFF;
                    int ia = (r >> 24) & 0xFF;

                    if (ra != ia) {
                        throw new AssertionError("bad alpha, expected: "
                                + ra + ", actual " + ia);
                    }
                    if (ra != 0) {
                        if (r != i) {
                            throw new AssertionError("bad color value, expected: "
                                    + Integer.toHexString(r) + ", actual " + Integer.toHexString(i));
                        }
                    }
                } catch (AssertionError e) {
                    throw new AssertionError("pixel (" + x + ", " + y + "): " + e.getMessage());
                }
            }
        }
    }

    public static void assertIcoEquals(String dir, String icoFile, String... pngFiles) throws IOException {
        List<BufferedImage> images = IcoTestDecoder.loadIcons(dir + icoFile);
        if (images.size() != pngFiles.length) {
            throw new AssertionError(icoFile + ": wrong number of images, expected: "
                    + pngFiles.length + ", actual: " + images.size());
        }

        for (int num = 0; num < pngFiles.length; ++num) {
            if(pngFiles[num] == null)
                continue;

            BufferedImage ref = ImageIO.read(IcoAssert.class.getResourceAsStream(dir + pngFiles[num]));
            BufferedImage icon = images.get(num);

            try {
                compareImages(ref, icon);
            } catch (AssertionError e) {
                throw new AssertionError(icoFile + ", icon #" + num + ": " + e.getMessage());
            }
        }
    }
}
