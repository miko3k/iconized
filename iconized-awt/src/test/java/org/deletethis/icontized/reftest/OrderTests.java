package org.deletethis.icontized.reftest;

import org.junit.Test;

import java.io.IOException;

public class OrderTests {
    @Test
    public void foo() throws IOException {
        String dir = "/order/";

        IcoAssert.assertIcoEquals(dir, "icon.ico", "icon-2.png", "icon-1.png");
        IcoAssert.assertIcoEquals(dir, "icon_reorder.ico", "icon-1.png", "icon-2.png");
        IcoAssert.assertIcoEquals(dir, "icon_gap.ico", "icon-2.png", "icon-1.png");
    }
}
