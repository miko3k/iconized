package org.deletethis.iconized;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class IconInputStreamTests {
    private byte[] seq(int min, int size){
        byte[]result = new byte[size];
        for(int i=0;i<size;++i) {
            result[i] = (byte)(min+i);
        }
        return result;
    }

    @Test
    public void simple() throws IOException {
        IconInputStream in = new IconInputStream(new ByteArrayInputStream(seq(0,2)));
        Assert.assertEquals(0, in.getOffset());
        Assert.assertEquals(0, in.read());
        Assert.assertEquals(1, in.getOffset());
        Assert.assertEquals(1, in.read());
        Assert.assertEquals(2, in.getOffset());
        Assert.assertEquals(-1, in.read());
        Assert.assertEquals(-1, in.read());
        Assert.assertEquals(-1, in.read());
        in.pushBack((byte)99);
        Assert.assertEquals(1, in.getOffset());
        Assert.assertEquals(99, in.read());
        Assert.assertEquals(-1, in.read());
    }


    @Test
    public void zeroLength() throws IOException{
        IconInputStream in = new IconInputStream(new ByteArrayInputStream(seq(0, 2)));
        byte[] bytes = new byte[20];
        Assert.assertEquals(0, in.read(bytes, 4, 0));
        Assert.assertEquals(0, in.getOffset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeLength() throws IOException {
        IconInputStream in = new IconInputStream(new ByteArrayInputStream(seq(0, 2)));
        byte[] bytes = new byte[20];
        Assert.assertEquals(0, in.read(bytes, 4, -1));
    }


    @Test
    public void negativeOffset() throws IOException {
        IconInputStream in = new IconInputStream(new ByteArrayInputStream(seq(0, 0)));
        in.pushBack((byte)1);
        in.pushBack((byte)2);
        Assert.assertEquals(-2, in.getOffset());
        byte[] bytes = new byte[20];
        Assert.assertEquals(2, in.read(bytes, 4, 5));
        Assert.assertEquals(2, bytes[4]);
        Assert.assertEquals(1, bytes[5]);
        Assert.assertEquals(0, in.getOffset());

        in.pushBack((byte)1);
        in.pushBack((byte)2);
        Assert.assertEquals(-2, in.getOffset());
        Assert.assertEquals(0, in.skip(0));
        Assert.assertEquals(2, in.skip(15));
        Assert.assertEquals(0, in.skip(0));
        Assert.assertEquals(-1, in.read(bytes, 0, 1));

        in.pushBack((byte)1);
        in.pushBack((byte)2);
        Assert.assertEquals(1, in.skip(1));
        Assert.assertEquals(1, in.skip(1));
        Assert.assertEquals(-1, in.read(bytes, 0, 1));

        in.pushBack((byte)1);
        in.pushBack((byte)2);
        Assert.assertEquals(1, in.read(bytes, 4, 1));
        Assert.assertEquals(2, bytes[4]);
        Assert.assertEquals(1, in.read(bytes, 2, 1));
        Assert.assertEquals(1, bytes[2]);
        Assert.assertEquals(-1, in.read(bytes, 6, 1));
    }

}
