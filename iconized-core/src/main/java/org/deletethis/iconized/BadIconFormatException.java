package org.deletethis.iconized;

import java.io.IOException;

public class BadIconFormatException extends IOException {
    public BadIconFormatException(String message) {
        super(message);
    }

    public BadIconFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
