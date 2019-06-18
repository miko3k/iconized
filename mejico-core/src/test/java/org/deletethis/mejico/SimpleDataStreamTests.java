package org.deletethis.mejico;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

public class SimpleDataStreamTests {
    private ByteArrayInputStream b(int ... data){
        byte[] bytes = new byte[data.length];
        for(int i=0;i<data.length;++i) {
            bytes[i] = (byte)data[i];
        }
        return new ByteArrayInputStream(bytes);
    }

    private SimpleDataStream stream(int ... data){
        return new SimpleDataStream(b(data));
    }

    @Test
    public void simple() throws IOException {
        Assert.assertEquals(0x12345678, stream(0x12, 0x34, 0x56, 0x78).readInt());
        Assert.assertEquals(0x78563412, stream(0x12, 0x34, 0x56, 0x78).readIntelInt());
        Assert.assertEquals(0x1234, stream(0x12, 0x34).readShort());
        Assert.assertEquals(0x4321, stream(0x21, 0x43).readIntelShort());
        Assert.assertEquals(0x33, stream(0x33).readByte());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void minusFully() throws IOException {
        byte [] buf = new byte[1];
        new SimpleDataStream(b()).readFully(buf, 0, -1);
    }

    @Test(expected = EOFException.class)
    public void eofByte() throws IOException {
        new SimpleDataStream(b()).readByte();
    }

    @Test(expected = EOFException.class)
    public void eofBuffer() throws IOException {
        byte [] buf = new byte[4];
        new SimpleDataStream(b()).readFully(buf);
    }


}
