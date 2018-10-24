// Nothing works here!!

/*
package org.deletethis.mejico;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.deletethis.mejico.reftest.RefTestSupport;
import org.deletethis.mejico.reftest.SuccessTestCase;
import org.deletethis.mejico.reftest.TestCaseList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
//@RunWith(Parameterized.class)
public class RefTest {

    //@Parameterized.Parameters(name = "{0}")
    //public static Iterable<SuccessTestCase> data() {
    //    return TestCaseList.getSuccessTestCases();
    //}
//
//    @Parameterized.Parameter // first data value (0) is default
//    public SuccessTestCase currentTestCase;

    private void assertImagesEqual(Bitmap ref, Bitmap icon) {
        RefTestSupport.assertImagesEqual(new BitmapWrapper(ref), new BitmapWrapper(icon));
    }

    private void doTestCase(SuccessTestCase currentTestCase) throws IOException {
        List<Bitmap> images = IcoParser.getInstance().getIcons(currentTestCase.getIcoFile());

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


    @Test
    public void testLoad() throws IOException {
        for(SuccessTestCase testCase: TestCaseList.getSuccessTestCases()){
            doTestCase(testCase);
        }
    }
}
*/