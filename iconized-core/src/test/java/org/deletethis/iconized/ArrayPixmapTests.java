package org.deletethis.iconized;

import org.junit.Assert;
import org.junit.Test;

public class ArrayPixmapTests {
    @Test
    public void factory() {
        Pixmap pixmap = ArrayPixmap.FACTORY.createPixmap(1, 2);
        Assert.assertEquals(1, pixmap.getWidth());
        Assert.assertEquals(2, pixmap.getHeight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void outside1() { new ArrayPixmap(1,1).setARGB(-1, 0, 0); }
    @Test(expected = IllegalArgumentException.class)
    public void outside2() { new ArrayPixmap(1,1).setARGB(0, -1, 0); }
    @Test(expected = IllegalArgumentException.class)
    public void outside3() { new ArrayPixmap(1,1).setARGB(1, 0, 0); }
    @Test(expected = IllegalArgumentException.class)
    public void outside4() { new ArrayPixmap(1,1).setARGB(0, 1, 0); }
    @Test(expected = IllegalArgumentException.class)
    public void outside5() { new ArrayPixmap(1,1).getARGB(-1, 0); }
    @Test(expected = IllegalArgumentException.class)
    public void outside6() { new ArrayPixmap(1,1).getARGB(0, -1); }
    @Test(expected = IllegalArgumentException.class)
    public void outside7() { new ArrayPixmap(1,1).getARGB(1, 0); }
    @Test(expected = IllegalArgumentException.class)
    public void outside8() { new ArrayPixmap(1,1).getARGB(0, 1); }

    @Test
    public void tests() {
        ArrayPixmap pixmap = new ArrayPixmap(4,4);

        Assert.assertEquals(pixmap, new ArrayPixmap(4, 4));
        Assert.assertNotEquals(pixmap, new ArrayPixmap(5, 4));
        Assert.assertEquals(pixmap.hashCode(), new ArrayPixmap(4, 4).hashCode());

        Assert.assertEquals(0, pixmap.getData()[0]);
        Assert.assertEquals("ArrayPixmap(4x4)", pixmap.toString());
        pixmap.setARGB(0,0,1);

        Assert.assertEquals(1, pixmap.getARGB(0, 0));
        pixmap.setARGB(0,0,0);
        Assert.assertEquals(0, pixmap.getARGB(0, 0));

    }

}
