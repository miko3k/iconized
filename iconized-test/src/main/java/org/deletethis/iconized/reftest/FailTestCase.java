package org.deletethis.iconized.reftest;

public interface FailTestCase extends IcoTestCase {
    Class<? extends Exception> getExceptionClass();
}
