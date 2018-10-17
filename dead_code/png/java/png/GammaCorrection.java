/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deletethis.iconized;

public class GammaCorrection {
    private static final boolean DEBUG = true;

    private final int[] lookupTable;

    public GammaCorrection(final double srcGamma, final double dstGamma) {

        if (DEBUG) {
            System.out.println("src_gamma: " + srcGamma);
            System.out.println("dst_gamma: " + dstGamma);
        }

        lookupTable = new int[256];
        for (int i = 0; i < 256; i++) {
            double sample = correctSample(i, srcGamma, dstGamma);


            lookupTable[i] = (int) Math.round(sample);
            if (DEBUG) {
                System.out.println("lookup_table[" + i + "]: " + sample);
            }
        }
    }

    public int correctSample(final int sample) {
        return lookupTable[sample];
    }

    public int correctARGB(final int pixel) {
        int red = Colors.getRed(pixel);
        int green = Colors.getGreen(pixel);
        int blue = Colors.getBlue(pixel);
        int alpha = Colors.getAlpha(pixel);

        red = correctSample(red);
        green = correctSample(green);
        blue = correctSample(blue);

        return Colors.create(red, green, blue, alpha);
    }

    private double correctSample(final int sample, final double srcGamma, final double dstGamma) {
        // if (kUseAdobeGammaMethod && val <= 32)
        // {
        // double slope = Math.round(255.0d * Math.pow((32.0 / 255.0d),
        // src_gamma / dst_gamma)) / 32.d;
        // return (int) (sample * slope);
        // }

        return 255.0d * Math.pow((sample / 255.0d), srcGamma / dstGamma);
    }

}