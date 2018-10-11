package org.deletethis.icontized.reftest;

import org.junit.Test;

import java.io.IOException;

public class MozillaTests {
    /** test if tests actually work! */
    @Test(expected = AssertionError.class)
    public void diff() throws IOException {
        IcoAssert.assertIcoEquals("", "/mozilla/ico-bmp-1bpp/ico-size-16x16-1bpp.ico", "/mozilla/ico-bmp-4bpp/ico-size-16x16-4bpp.png");
    }

    @Test
    public void cur() throws IOException {
        String dir = "/mozilla/cur/";
        IcoAssert.assertIcoEquals(dir, "pointer.cur", "pointer.png");
    }

    @Test
    public void icoBmp1bpp() throws IOException {
        String dir = "/mozilla/ico-bmp-1bpp/";

        IcoAssert.assertIcoEquals(dir, "ico-size-1x1-1bpp.ico", "ico-size-1x1-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-2x2-1bpp.ico", "ico-size-2x2-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-3x3-1bpp.ico", "ico-size-3x3-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-4x4-1bpp.ico", "ico-size-4x4-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-5x5-1bpp.ico", "ico-size-5x5-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-6x6-1bpp.ico", "ico-size-6x6-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-7x7-1bpp.ico", "ico-size-7x7-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-8x8-1bpp.ico", "ico-size-8x8-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-9x9-1bpp.ico", "ico-size-9x9-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-15x15-1bpp.ico", "ico-size-15x15-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-16x16-1bpp.ico", "ico-size-16x16-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-17x17-1bpp.ico", "ico-size-17x17-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-31x31-1bpp.ico", "ico-size-31x31-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-32x32-1bpp.ico", "ico-size-32x32-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-33x33-1bpp.ico", "ico-size-33x33-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-256x256-1bpp.ico", "ico-size-256x256-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-partial-transparent-1bpp.ico", "ico-partial-transparent-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-transparent-1bpp.ico", "ico-transparent-1bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-not-square-transparent-1bpp.ico", "ico-not-square-transparent-1bpp.png");
    }


    @Test
    public void icoBmp4bpp() throws IOException {
        String dir = "/mozilla/ico-bmp-4bpp/";

        IcoAssert.assertIcoEquals(dir, "ico-size-1x1-4bpp.ico", "ico-size-1x1-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-2x2-4bpp.ico", "ico-size-2x2-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-3x3-4bpp.ico", "ico-size-3x3-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-4x4-4bpp.ico", "ico-size-4x4-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-5x5-4bpp.ico", "ico-size-5x5-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-6x6-4bpp.ico", "ico-size-6x6-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-7x7-4bpp.ico", "ico-size-7x7-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-8x8-4bpp.ico", "ico-size-8x8-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-9x9-4bpp.ico", "ico-size-9x9-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-15x15-4bpp.ico", "ico-size-15x15-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-16x16-4bpp.ico", "ico-size-16x16-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-17x17-4bpp.ico", "ico-size-17x17-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-31x31-4bpp.ico", "ico-size-31x31-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-32x32-4bpp.ico", "ico-size-32x32-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-33x33-4bpp.ico", "ico-size-33x33-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-256x256-4bpp.ico","ico-size-256x256-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-partial-transparent-4bpp.ico", "ico-partial-transparent-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-transparent-4bpp.ico", "ico-transparent-4bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-not-square-transparent-4bpp.ico", "ico-not-square-transparent-4bpp.png");
    }

    @Test
    public void icoBmp8bpp() throws IOException {
        String dir = "/mozilla/ico-bmp-8bpp/";

        IcoAssert.assertIcoEquals(dir, "ico-size-1x1-8bpp.ico", "ico-size-1x1-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-2x2-8bpp.ico", "ico-size-2x2-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-3x3-8bpp.ico", "ico-size-3x3-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-4x4-8bpp.ico", "ico-size-4x4-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-5x5-8bpp.ico", "ico-size-5x5-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-6x6-8bpp.ico", "ico-size-6x6-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-7x7-8bpp.ico", "ico-size-7x7-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-8x8-8bpp.ico", "ico-size-8x8-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-9x9-8bpp.ico", "ico-size-9x9-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-15x15-8bpp.ico", "ico-size-15x15-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-16x16-8bpp.ico", "ico-size-16x16-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-17x17-8bpp.ico", "ico-size-17x17-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-31x31-8bpp.ico", "ico-size-31x31-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-32x32-8bpp.ico", "ico-size-32x32-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-33x33-8bpp.ico", "ico-size-33x33-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-256x256-8bpp.ico", "ico-size-256x256-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-partial-transparent-8bpp.ico", "ico-partial-transparent-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-transparent-8bpp.ico", "ico-transparent-8bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-not-square-transparent-8bpp.ico", "ico-not-square-transparent-8bpp.png");
    }

    @Test
    public void icoBmp24bpp() throws IOException {
        String dir = "/mozilla/ico-bmp-24bpp/";

        IcoAssert.assertIcoEquals(dir, "ico-size-1x1-24bpp.ico", "ico-size-1x1-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-2x2-24bpp.ico", "ico-size-2x2-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-3x3-24bpp.ico", "ico-size-3x3-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-4x4-24bpp.ico", "ico-size-4x4-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-5x5-24bpp.ico", "ico-size-5x5-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-6x6-24bpp.ico", "ico-size-6x6-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-7x7-24bpp.ico", "ico-size-7x7-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-8x8-24bpp.ico", "ico-size-8x8-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-9x9-24bpp.ico", "ico-size-9x9-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-15x15-24bpp.ico", "ico-size-15x15-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-16x16-24bpp.ico", "ico-size-16x16-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-17x17-24bpp.ico", "ico-size-17x17-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-31x31-24bpp.ico", "ico-size-31x31-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-32x32-24bpp.ico", "ico-size-32x32-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-33x33-24bpp.ico", "ico-size-33x33-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-256x256-24bpp.ico", "ico-size-256x256-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-partial-transparent-24bpp.ico", "ico-partial-transparent-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-transparent-24bpp.ico", "ico-transparent-24bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-not-square-transparent-24bpp.ico", "ico-not-square-transparent-24bpp.png");
    }

    @Test
    public void icoBmp32bpp() throws IOException {
        String dir = "/mozilla/ico-bmp-32bpp/";

        IcoAssert.assertIcoEquals(dir, "ico-size-1x1-32bpp.ico", "ico-size-1x1-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-2x2-32bpp.ico", "ico-size-2x2-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-3x3-32bpp.ico", "ico-size-3x3-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-4x4-32bpp.ico", "ico-size-4x4-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-5x5-32bpp.ico", "ico-size-5x5-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-6x6-32bpp.ico", "ico-size-6x6-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-7x7-32bpp.ico", "ico-size-7x7-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-8x8-32bpp.ico", "ico-size-8x8-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-9x9-32bpp.ico", "ico-size-9x9-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-15x15-32bpp.ico", "ico-size-15x15-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-16x16-32bpp.ico", "ico-size-16x16-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-17x17-32bpp.ico", "ico-size-17x17-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-31x31-32bpp.ico", "ico-size-31x31-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-32x32-32bpp.ico", "ico-size-32x32-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-33x33-32bpp.ico", "ico-size-33x33-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-size-256x256-32bpp.ico", "ico-size-256x256-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-partial-transparent-32bpp.ico", "ico-partial-transparent-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-transparent-32bpp.ico", "ico-transparent-32bpp.png");
        IcoAssert.assertIcoEquals(dir, "ico-not-square-transparent-32bpp.ico", "ico-not-square-transparent-32bpp.png");
    }

    /*
    # Invalid value for bits per pixel (BPP) - detected when decoding the header.
    == wrapper.html?invalid-bpp.ico about:blank
    # Invalid BPP values for RLE4 - detected when decoding the image data.
    == wrapper.html?invalid-compression-RLE4.ico about:blank
    # Invalid BPP values for RLE8 - detected when decoding the image data.
    == wrapper.html?invalid-compression-RLE8.ico about:blank
    # Invalid compression value - detected when decoding the image data.
    == wrapper.html?invalid-compression.ico about:blank
     */
    @Test(expected = IllegalArgumentException.class)
    public void invalid1()  {
        IcoTestDecoder.loadIcons("/mozilla/ico-bmp-corrupted/invalid-bpp.ico");
    }
    @Test(expected = IllegalArgumentException.class)
    public void invalid2()  {
        IcoTestDecoder.loadIcons("/mozilla/ico-bmp-corrupted/invalid-compression.ico");
    }
    @Test(expected = IllegalArgumentException.class)
    public void invalid3()  {
        IcoTestDecoder.loadIcons("/mozilla/ico-bmp-corrupted/invalid-compression-RLE4.ico");
    }
    @Test(expected = IllegalArgumentException.class)
    public void invalid4() {
        IcoTestDecoder.loadIcons("/mozilla/ico-bmp-corrupted/invalid-compression-RLE8.ico");
    }

    @Test
    public void icoMixed() throws IOException {
        String dir = "/mozilla/ico-mixed/";

        IcoAssert.assertIcoEquals(dir, "mixed-bmp-png.ico", "mixed-bmp-png48.png", "mixed-bmp-png32.png", null, "mixed-bmp-png.png");
    }
}
