package org.deletethis.mejico.reftest;

public class RefTestSupport {
    private RefTestSupport() { }

    public static void assertImagesEqual(ImageWrapper ref, ImageWrapper test) {
        int refw = ref.getWidth();
        int refh = ref.getHeight();
        int testw = test.getWidth();
        int testh = test.getHeight();

        if(refw != testw || refh != testh) {
            throw new AssertionError("bad size, expected: "
                    + refw + " x " + refh + ", actual " + testw + " x " + testh);
        }

        for (int y = 0; y < refh; y++) {
            for (int x = 0; x < refw; x++) {
                try {
                    int r = ref.getARGB(x, y);
                    int i = test.getARGB(x, y);

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
}
