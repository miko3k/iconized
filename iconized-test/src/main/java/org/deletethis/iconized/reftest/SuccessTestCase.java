package org.deletethis.iconized.reftest;

import java.util.List;

public interface SuccessTestCase extends IcoTestCase {
    List<byte[]> getResultAsPng();
}
