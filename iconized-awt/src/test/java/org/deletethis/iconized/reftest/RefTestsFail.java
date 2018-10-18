package org.deletethis.iconized.reftest;

import org.deletethis.iconized.AwtIconLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.List;

@RunWith(Parameterized.class)
public class RefTestsFail {
    @Parameterized.Parameters(name = "{0}")
    public static Iterable<FailTestCase> data() {
        return TestCaseList.getFailTestCases();
    }

    @Parameterized.Parameter // first data value (0) is default
    public FailTestCase currentTestCase;

    @Test
    public void testLoad() {
        try {
            AwtIconLoader.getInstance().decode(currentTestCase.getIcoFile());
        } catch(Exception ex) {
            if(!currentTestCase.getExceptionClass().isInstance(ex)) {
                throw new AssertionError("Wrong exception", ex);
            }
        }
    }
}
