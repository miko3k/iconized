package org.deletethis.mejico.reftest;

public interface FailTestCase extends IcoTestCase {
    Class<? extends Exception> getExceptionClass();
}
