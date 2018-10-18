package org.deletethis.iconized;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.deletethis.iconized.reftest.RefTestSupport;
import org.deletethis.iconized.reftest.SuccessTestCase;
import org.deletethis.iconized.reftest.TestCaseList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.List;

@RunWith(Parameterized.class)
public class RefTest {
    @Parameterized.Parameters(name = "{0}")
    public static Iterable<SuccessTestCase> data() {
        return TestCaseList.getSuccessTestCases();
    }

    @Parameterized.Parameter // first data value (0) is default
    public SuccessTestCase currentTestCase;

    private void assertImagesEqual(Bitmap ref, Bitmap icon) {
        RefTestSupport.assertImagesEqual(new BitmapWrapper(ref), new BitmapWrapper(icon));
    }

    @Test
    public void testLoad() throws IOException {
        List<Bitmap> images = AndroidIconLoader.getInstance().decode(currentTestCase.getIcoFile());

        List<byte[]> pngs = currentTestCase.getResultAsPng();

        for (int num = 0; num < pngs.size(); ++num) {
            byte[] png = pngs.get(num);
            if(png == null)
                continue;

            Bitmap ref = BitmapFactory.decodeByteArray(png, 0, png.length);
            Bitmap icon = images.get(num);

            try {
                assertImagesEqual(ref, icon);
            } catch (AssertionError e) {
                throw new AssertionError("icon #" + num + ": " + e.getMessage());
            }
        }
    }
}
