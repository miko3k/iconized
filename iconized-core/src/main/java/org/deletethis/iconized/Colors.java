package org.deletethis.iconized;

public class Colors {
    private static void range(int b) {
        if((b & 0xFF) != b)
            throw new IllegalArgumentException("not a color");
    }

    public static int create(int red, int green, int blue, int alpha) {
        range(red);
        range(green);
        range(blue);
        range(alpha);

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int create(int red, int green, int blue) {
        range(red);
        range(green);
        range(blue);

        return 0xFF << 24 | red << 16 | green << 8 | blue;
    }

    public static int getAlpha(int color) {
        return color >>> 24;
    }

    public static int setAlpha(int color, int alpha) {
        range(alpha);
        return (color & 0xFFFFFF) | alpha << 24;
    }

}
