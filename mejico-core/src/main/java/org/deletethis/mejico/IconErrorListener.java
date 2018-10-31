package org.deletethis.mejico;

import java.io.IOException;

/**
 * Interface to be called by the {@link AbstractIconParser} when it fails to decode an image.
 *
 * By default such errors are ignored.
 */
public interface IconErrorListener {
    /**
     * Implementation which does nothing
     */
    IconErrorListener BLACKHOLE = new IconErrorListener() {
        @Override
        public void onIconError(ImageMetadata metadata, IconFormatException exception) {
        }
    };

    /**
     * Implementation which simply throws the exception
     */
    IconErrorListener MAKE_FATAL = new IconErrorListener() {
        @Override
        public void onIconError(ImageMetadata metadata, IconFormatException exception) throws IconFormatException {
            throw exception;

        }
    };

    /**
     * Called when decoding of an image fails. One might want to implement some logging here.
     *
     * Any exception thrown here will be handled no more and will end up in calling code.
     *
     * @param metadata metadata of current image
     * @param exception the exception
     *
     * @throws IOException throw only if you want to make this error fatal
     */
    void onIconError(ImageMetadata metadata, IconFormatException exception) throws IOException;
}
