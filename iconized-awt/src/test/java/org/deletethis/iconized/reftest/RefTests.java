package org.deletethis.iconized.reftest;

import org.deletethis.iconized.AwtIconLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RunWith(Parameterized.class)
public class RefTests {
    @Parameterized.Parameters(name = "{0}")
    public static Iterable<SuccessTestCase> data() {
        return TestCaseList.getSuccessTestCases();
    }

    @Parameterized.Parameter // first data value (0) is default
    public /* NOT private */ SuccessTestCase currentTestCase;

    private void compareImages(BufferedImage ref, BufferedImage icon) {
        // fuck performance!
        String refDimensions = ref.getWidth() + " x " + ref.getWidth();
        String iconDimensions = icon.getWidth() + " x " + icon.getWidth();

        if (!refDimensions.equals(iconDimensions))
            throw new AssertionError("bad size, expected: "
                    + refDimensions + ", actual " + iconDimensions);


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


    @Test
    public void testLoad() throws IOException {
        List<BufferedImage> images = AwtIconLoader.getInstance().decode(currentTestCase.getIcoFile());

        List<byte[]> pngs = currentTestCase.getResultAsPng();

        for (int num = 0; num < pngs.size(); ++num) {
            if(pngs.get(num) == null)
                continue;

            BufferedImage ref = ImageIO.read(new ByteArrayInputStream(pngs.get(num)));
            BufferedImage icon = images.get(num);

            try {
                compareImages(ref, icon);
            } catch (AssertionError e) {
                throw new AssertionError("icon #" + num + ": " + e.getMessage());
            }
        }
    }
}
