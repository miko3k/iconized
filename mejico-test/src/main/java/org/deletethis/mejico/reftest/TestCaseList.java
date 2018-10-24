package org.deletethis.mejico.reftest;

import org.deletethis.mejico.IcoFormatException;

public class TestCaseList {

    // fuck it, static, this is for tests anyways
    private static TestCaseRegistry R = new TestCaseRegistry();
    static {
        String DIR_1BPP = "mozilla/ico-bmp-1bpp";
        String DIR_CUR = "mozilla/cur";
        String DIR_4BPP = "mozilla/ico-bmp-4bpp";
        String DIR_8BPP = "mozilla/ico-bmp-8bpp";
        String DIR_24BPP = "mozilla/ico-bmp-24bpp";
        String DIR_32BPP = "mozilla/ico-bmp-32bpp";
        String DIR_ORDER = "order";
        String DIR_MIXED = "mozilla/ico-mixed";
        String DIR_MOZPNG = "mozilla/ico-png";
        String DIR_PNG = "png";
        String DIR_CURRUPTED = "mozilla/ico-bmp-corrupted";


        R.ok(DIR_CUR, "pointer.cur", "pointer.png");
        R.ok(DIR_1BPP, "ico-size-1x1-1bpp.ico", "ico-size-1x1-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-2x2-1bpp.ico", "ico-size-2x2-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-3x3-1bpp.ico", "ico-size-3x3-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-4x4-1bpp.ico", "ico-size-4x4-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-5x5-1bpp.ico", "ico-size-5x5-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-6x6-1bpp.ico", "ico-size-6x6-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-7x7-1bpp.ico", "ico-size-7x7-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-8x8-1bpp.ico", "ico-size-8x8-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-9x9-1bpp.ico", "ico-size-9x9-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-15x15-1bpp.ico", "ico-size-15x15-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-16x16-1bpp.ico", "ico-size-16x16-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-17x17-1bpp.ico", "ico-size-17x17-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-31x31-1bpp.ico", "ico-size-31x31-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-32x32-1bpp.ico", "ico-size-32x32-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-33x33-1bpp.ico", "ico-size-33x33-1bpp.png");
        R.ok(DIR_1BPP, "ico-size-256x256-1bpp.ico", "ico-size-256x256-1bpp.png");
        R.ok(DIR_1BPP, "ico-partial-transparent-1bpp.ico", "ico-partial-transparent-1bpp.png");
        R.ok(DIR_1BPP, "ico-transparent-1bpp.ico", "ico-transparent-1bpp.png");
        R.ok(DIR_1BPP, "ico-not-square-transparent-1bpp.ico", "ico-not-square-transparent-1bpp.png");

        R.ok(DIR_4BPP, "ico-size-1x1-4bpp.ico", "ico-size-1x1-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-2x2-4bpp.ico", "ico-size-2x2-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-3x3-4bpp.ico", "ico-size-3x3-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-4x4-4bpp.ico", "ico-size-4x4-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-5x5-4bpp.ico", "ico-size-5x5-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-6x6-4bpp.ico", "ico-size-6x6-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-7x7-4bpp.ico", "ico-size-7x7-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-8x8-4bpp.ico", "ico-size-8x8-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-9x9-4bpp.ico", "ico-size-9x9-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-15x15-4bpp.ico", "ico-size-15x15-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-16x16-4bpp.ico", "ico-size-16x16-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-17x17-4bpp.ico", "ico-size-17x17-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-31x31-4bpp.ico", "ico-size-31x31-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-32x32-4bpp.ico", "ico-size-32x32-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-33x33-4bpp.ico", "ico-size-33x33-4bpp.png");
        R.ok(DIR_4BPP, "ico-size-256x256-4bpp.ico","ico-size-256x256-4bpp.png");
        R.ok(DIR_4BPP, "ico-partial-transparent-4bpp.ico", "ico-partial-transparent-4bpp.png");
        R.ok(DIR_4BPP, "ico-transparent-4bpp.ico", "ico-transparent-4bpp.png");
        R.ok(DIR_4BPP, "ico-not-square-transparent-4bpp.ico", "ico-not-square-transparent-4bpp.png");

        R.ok(DIR_8BPP, "ico-size-1x1-8bpp.ico", "ico-size-1x1-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-2x2-8bpp.ico", "ico-size-2x2-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-3x3-8bpp.ico", "ico-size-3x3-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-4x4-8bpp.ico", "ico-size-4x4-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-5x5-8bpp.ico", "ico-size-5x5-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-6x6-8bpp.ico", "ico-size-6x6-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-7x7-8bpp.ico", "ico-size-7x7-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-8x8-8bpp.ico", "ico-size-8x8-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-9x9-8bpp.ico", "ico-size-9x9-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-15x15-8bpp.ico", "ico-size-15x15-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-16x16-8bpp.ico", "ico-size-16x16-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-17x17-8bpp.ico", "ico-size-17x17-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-31x31-8bpp.ico", "ico-size-31x31-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-32x32-8bpp.ico", "ico-size-32x32-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-33x33-8bpp.ico", "ico-size-33x33-8bpp.png");
        R.ok(DIR_8BPP, "ico-size-256x256-8bpp.ico", "ico-size-256x256-8bpp.png");
        R.ok(DIR_8BPP, "ico-partial-transparent-8bpp.ico", "ico-partial-transparent-8bpp.png");
        R.ok(DIR_8BPP, "ico-transparent-8bpp.ico", "ico-transparent-8bpp.png");
        R.ok(DIR_8BPP, "ico-not-square-transparent-8bpp.ico", "ico-not-square-transparent-8bpp.png");

        R.ok(DIR_24BPP, "ico-size-1x1-24bpp.ico", "ico-size-1x1-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-2x2-24bpp.ico", "ico-size-2x2-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-3x3-24bpp.ico", "ico-size-3x3-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-4x4-24bpp.ico", "ico-size-4x4-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-5x5-24bpp.ico", "ico-size-5x5-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-6x6-24bpp.ico", "ico-size-6x6-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-7x7-24bpp.ico", "ico-size-7x7-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-8x8-24bpp.ico", "ico-size-8x8-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-9x9-24bpp.ico", "ico-size-9x9-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-15x15-24bpp.ico", "ico-size-15x15-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-16x16-24bpp.ico", "ico-size-16x16-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-17x17-24bpp.ico", "ico-size-17x17-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-31x31-24bpp.ico", "ico-size-31x31-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-32x32-24bpp.ico", "ico-size-32x32-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-33x33-24bpp.ico", "ico-size-33x33-24bpp.png");
        R.ok(DIR_24BPP, "ico-size-256x256-24bpp.ico", "ico-size-256x256-24bpp.png");
        R.ok(DIR_24BPP, "ico-partial-transparent-24bpp.ico", "ico-partial-transparent-24bpp.png");
        R.ok(DIR_24BPP, "ico-transparent-24bpp.ico", "ico-transparent-24bpp.png");
        R.ok(DIR_24BPP, "ico-not-square-transparent-24bpp.ico", "ico-not-square-transparent-24bpp.png");

        R.ok(DIR_32BPP, "ico-size-1x1-32bpp.ico", "ico-size-1x1-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-2x2-32bpp.ico", "ico-size-2x2-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-3x3-32bpp.ico", "ico-size-3x3-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-4x4-32bpp.ico", "ico-size-4x4-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-5x5-32bpp.ico", "ico-size-5x5-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-6x6-32bpp.ico", "ico-size-6x6-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-7x7-32bpp.ico", "ico-size-7x7-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-8x8-32bpp.ico", "ico-size-8x8-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-9x9-32bpp.ico", "ico-size-9x9-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-15x15-32bpp.ico", "ico-size-15x15-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-16x16-32bpp.ico", "ico-size-16x16-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-17x17-32bpp.ico", "ico-size-17x17-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-31x31-32bpp.ico", "ico-size-31x31-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-32x32-32bpp.ico", "ico-size-32x32-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-33x33-32bpp.ico", "ico-size-33x33-32bpp.png");
        R.ok(DIR_32BPP, "ico-size-256x256-32bpp.ico", "ico-size-256x256-32bpp.png");
        R.ok(DIR_32BPP, "ico-partial-transparent-32bpp.ico", "ico-partial-transparent-32bpp.png");
        R.ok(DIR_32BPP, "ico-transparent-32bpp.ico", "ico-transparent-32bpp.png");
        R.ok(DIR_32BPP, "ico-not-square-transparent-32bpp.ico", "ico-not-square-transparent-32bpp.png");

        R.ok(DIR_MIXED, "mixed-bmp-png.ico", "mixed-bmp-png48.png", "mixed-bmp-png32.png", null, "mixed-bmp-png.png");

        R.ok(DIR_ORDER, "icon.ico", "icon-2.png", "icon-1.png");
        R.ok(DIR_ORDER, "icon_reorder.ico", "icon-1.png", "icon-2.png");
        R.ok(DIR_ORDER, "icon_gap.ico", "icon-2.png", "icon-1.png");

        R.ok(DIR_PNG, "2png.ico", "2png1.png", "2png2.png");

        R.ok(DIR_MOZPNG, "ico-size-1x1-png.ico", "ico-size-1x1-png.png");
        R.ok(DIR_MOZPNG, "ico-size-256x256-png.ico", "ico-size-256x256-png.png");
        R.ok(DIR_MOZPNG, "ico-size-2x2-png.ico", "ico-size-2x2-png.png");
        R.ok(DIR_MOZPNG, "ico-size-3x3-png.ico", "ico-size-3x3-png.png");
        R.ok(DIR_MOZPNG, "ico-size-4x4-png.ico", "ico-size-4x4-png.png");
        R.ok(DIR_MOZPNG, "ico-size-5x5-png.ico", "ico-size-5x5-png.png");
        R.ok(DIR_MOZPNG, "ico-size-6x6-png.ico", "ico-size-6x6-png.png");
        R.ok(DIR_MOZPNG, "ico-size-7x7-png.ico", "ico-size-7x7-png.png");
        R.ok(DIR_MOZPNG, "transparent-png.ico", "transparent-png.png");
    
        R.fail(DIR_CURRUPTED, "invalid-bpp.ico", IcoFormatException.class);
        R.fail(DIR_CURRUPTED, "invalid-compression.ico", IcoFormatException.class);
        R.fail(DIR_CURRUPTED, "invalid-compression-RLE4.ico", IcoFormatException.class);
        R.fail(DIR_CURRUPTED, "invalid-compression-RLE8.ico", IcoFormatException.class);
    }

    public static TestCases<FailTestCase> getFailTestCases() {
        return R.getFailTestCases();
    }

    public static TestCases<SuccessTestCase> getSuccessTestCases() {
        return R.getSuccessTestCases();
    }
}
