package org.deletethis.iconized;

public interface BufferDecoder<T> {
    int BMP_MAGIC = 40;
    int PNG_MAGIC = 0x474E5089;

    class Params {
        private boolean lastImage;

        Params(boolean lastImage) {
            this.lastImage = lastImage;
        }

        public boolean isLastImage() {
            return lastImage;
        }
    }

    T decodeImage(Buffer buffer, Params params);
}
