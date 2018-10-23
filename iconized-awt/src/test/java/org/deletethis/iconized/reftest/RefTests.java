package org.deletethis.iconized.reftest;

import org.deletethis.iconized.awt.IcoParser;
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

    @Parameterized.Parameter
    public SuccessTestCase currentTestCase;

    private void assertImagesEqual(BufferedImage ref, BufferedImage icon) {
        RefTestSupport.assertImagesEqual(new BufferedImageWrapper(ref), new BufferedImageWrapper(icon));
    }

    @Test
    public void testLoad() throws IOException {
        List<BufferedImage> images = IcoParser.getInstance().decode(currentTestCase.getIcoFile());

        List<byte[]> pngs = currentTestCase.getResultAsPng();

        for (int num = 0; num < pngs.size(); ++num) {
            if(pngs.get(num) == null)
                continue;

            BufferedImage ref = ImageIO.read(new ByteArrayInputStream(pngs.get(num)));
            BufferedImage icon = images.get(num);

            try {
                assertImagesEqual(ref, icon);
            } catch (AssertionError e) {
                throw new AssertionError("icon #" + num + ": " + e.getMessage());
            }
        }
    }
}
