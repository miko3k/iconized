package org.deletethis.iconized;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

public class AwtIconLoader extends BaseIcoDecoder<BufferedImage> {
    private static AwtIconLoader INSTANCE = new AwtIconLoader();

    public static AwtIconLoader getInstance() { return INSTANCE; }

    private AwtIconLoader() { }

    @Override
    protected BufferDecoder<BufferedImage> getImageDecoder(int magic) {
        if(magic == BufferDecoder.BMP_MAGIC) return BmpDecoder.INSTANCE;
        if(magic == BufferDecoder.PNG_MAGIC) {

            final ImageReader imageReader;
            Iterator<ImageReader> itr = ImageIO.getImageReadersByFormatName("png");
            if (itr.hasNext()) {
                imageReader = itr.next();
            } else {
                return null;
            }

            return new BufferDecoder<BufferedImage>() {
                @Override
                public BufferedImage decodeImage(Buffer buffer, Params params) {
                    try {
                        ImageInputStream input = ImageIO.createImageInputStream(buffer.toInputStream());
                        imageReader.setInput(input);
                        return imageReader.read(0);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Invalid PNG file: " + e.getMessage(), e);
                    }
                }
            };
        }
        return null;
    }
}
