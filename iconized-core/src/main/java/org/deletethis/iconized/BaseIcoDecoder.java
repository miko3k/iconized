package org.deletethis.iconized;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseIcoDecoder<T> {
    private final static int ICON = 1, CURSOR = 2;

    abstract protected BufferDecoder<T> getImageDecoder(int magic);

    final public List<T> decode(byte [] buffer) {
        return decode(new Buffer(buffer, 0, buffer.length));
    }

    final public List<T> decode(byte [] buffer, int offset, int length) {
        return decode(new Buffer(buffer, offset, length));
    }

    private List<T> decode(Buffer buffer) {

        if(buffer.int16() != 0) {
            throw new IllegalArgumentException("first WORD must be 0");
        }

        int type = buffer.int16();
        if(type != ICON && type != CURSOR)
            throw new IllegalArgumentException("second WORD must be 0 or 1");

        int numberOfImages = buffer.int16();

        List<T> result = new ArrayList<>(numberOfImages);

        for(int currentImage = 0; currentImage < numberOfImages; ++currentImage) {
            try {
                // information about colors etc are quite unreliable, coz they tend to overflow with big values,
                // we will just ignore them
                // discard:
                //    - width, height, number of colors, and reserved
                //    - (for ICO) planes and bpp information
                //    - (for CUR) hotspot coordinates
                buffer.skip(8);

                int dataSize = buffer.int32();
                int dataOffset = buffer.int32();

                int magic = buffer.int32(dataOffset);

                BufferDecoder<T> imageDecoder = getImageDecoder(magic);

                if (imageDecoder == null) {
                    throw new IllegalArgumentException("unknown magic: " + Integer.toHexString(magic));
                }

                boolean lastImage = currentImage >= numberOfImages-1;

                result.add(imageDecoder.decodeImage(buffer.slice(dataOffset, dataSize), new BufferDecoder.Params(lastImage)));
            } catch(IllegalArgumentException ex) {
                throw new IllegalArgumentException("Pixmap #" + currentImage + ": " + ex.getMessage(), ex);
            }
        }
        return result;
    }
}
