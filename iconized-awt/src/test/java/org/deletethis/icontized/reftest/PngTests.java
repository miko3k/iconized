package org.deletethis.icontized.reftest;

import org.junit.Test;

import java.io.IOException;

public class PngTests {
    @Test
    public void foo() throws IOException {
        String dir = "/png/";

        IcoAssert.assertIcoEquals(dir, "2png.ico", "2png1.png", "2png2.png");
    }
}
