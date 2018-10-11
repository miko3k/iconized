package org.deletethis.iconized;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.*;
import java.io.IOException;
import java.util.Iterator;

public class AwtIconLoader extends BaseIcoDecoder<BufferedImage> {
    private static AwtIconLoader INSTANCE = new AwtIconLoader();

    public static AwtIconLoader getInstance() { return INSTANCE; }

    private AwtIconLoader() { }

    private ImageReader getPngReader() {
        Iterator<ImageReader> itr = ImageIO.getImageReadersByFormatName("png");
        if (itr.hasNext()) {
            return itr.next();
        } else {
            return null;
        }
    }

    private static BufferedImage pixmapToImage(Pixmap pixmap) {
        int[] bitMasks = new int[]{ 0xFF0000, 0xFF00, 0xFF, 0xFF000000 };
        SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(
                DataBuffer.TYPE_INT, pixmap.getWidth(), pixmap.getHeight(), bitMasks);
        DataBufferInt db = new DataBufferInt(pixmap.getData(), pixmap.getData().length);
        WritableRaster wr = Raster.createWritableRaster(sm, db, null);
        return new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
    }

    private BufferedImage readImage(ImageReader imageReader, Buffer buffer) {
        try {
            ImageInputStream input = ImageIO.createImageInputStream(buffer.toInputStream());
            imageReader.setInput(input);
            return imageReader.read(0);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid PNG file: " + e.getMessage(), e);
        }
    }

    private static final BufferDecoder<BufferedImage> BMP_LOADER = new BufferDecoder<BufferedImage>() {
        @Override
        public BufferedImage decodeImage(Buffer buffer, Params params) {
            return pixmapToImage(BmpDecoder.getInstance().decodeImage(buffer, params));
        }
    };

    @Override
    protected BufferDecoder<BufferedImage> getImageDecoder(int magic) {
        if(magic == BufferDecoder.BMP_MAGIC) return BMP_LOADER;
        if(magic == BufferDecoder.PNG_MAGIC) {

            final ImageReader imageReader = getPngReader();
            if(imageReader == null) {
                return null;
            }

            return new BufferDecoder<BufferedImage>() {
                @Override
                public BufferedImage decodeImage(Buffer buffer, Params params) {
                    return readImage(imageReader, buffer);
                }
            };
        }
        return null;
    }
}
