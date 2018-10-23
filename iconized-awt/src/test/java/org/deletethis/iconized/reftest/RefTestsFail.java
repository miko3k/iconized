package org.deletethis.iconized.reftest;

import org.deletethis.iconized.awt.IcoParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RefTestsFail {
    @Parameterized.Parameters(name = "{0}")
    public static Iterable<FailTestCase> data() {
        return TestCaseList.getFailTestCases();
    }

    @Parameterized.Parameter
    public FailTestCase currentTestCase;

    @Test
    public void testLoad() {
        try {
            IcoParser.getInstance().decode(currentTestCase.getIcoFile());
        } catch(Exception ex) {
            if(!currentTestCase.getExceptionClass().isInstance(ex)) {
                throw new AssertionError("Wrong exception", ex);
            }
        }
    }
}
