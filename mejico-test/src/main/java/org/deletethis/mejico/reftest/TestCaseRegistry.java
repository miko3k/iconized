package org.deletethis.mejico.reftest;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class TestCaseRegistry {
    private static byte [] getResource(String directory, String file) {
        String resourceName = directory + '/' + file;

        InputStream inputStream = TestCaseRegistry.class.getResourceAsStream(resourceName);
        if(inputStream == null) {
            throw new IllegalStateException("unable to find resource '" + resourceName + "'");
        }
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }
        return bytes;
    }

    private final TestCases<FailTestCase> failTestCases = new TestCases<>();
    private final TestCases<SuccessTestCase> successTestCases = new TestCases<>();

    void ok(final String directory, final String icoFile, String ... pngFiles) {
        final List<byte[]> pngFilesData = new ArrayList<>();
        for(String s: pngFiles) {
            if(s == null) {
                pngFilesData.add(null);
            } else {
                pngFilesData.add(getResource(directory, s));
            }
        }
        final byte data[] = getResource(directory, icoFile);
        successTestCases.add(new SuccessTestCase() {
            @Override
            public List<byte[]> getResultAsPng() {
                return pngFilesData;
            }

            @Override
            public String getName() {
                return directory + "/" + icoFile;
            }

            @Override
            public byte[] getIcoFile() {
                return data;
            }

            @Override
            public String toString() {
                return getName();
            }
        });
    }

    void fail(final String directory, final String icoFile, final Class<? extends Exception> exception) {
        final byte data[] = getResource(directory, icoFile);
        failTestCases.add(new FailTestCase() {
            @Override
            public Class<? extends Exception> getExceptionClass() {
                return exception;
            }

            @Override
            public String getName() {
                return directory + "/" + icoFile;
            }

            @Override
            public byte[] getIcoFile() {
                return data;
            }

            @Override
            public String toString() {
                return getName();
            }
        });
    }

    TestCases<FailTestCase> getFailTestCases() {
        return failTestCases;
    }

    TestCases<SuccessTestCase> getSuccessTestCases() {
        return successTestCases;
    }

}
