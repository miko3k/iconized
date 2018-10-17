/*
 * Iconized - an .ico parser in Java
 *
 * Copyright (c) 2008-2010, Matthias Mann
 * Copyright (c) 2018, Peter Hanula
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.deletethis.iconized;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * A PngDecoder.
 */
public class PngDecoder {

    private static final byte[] SIGNATURE = {(byte)137, 80, 78, 71, 13, 10, 26, 10};

    private static final int IHDR = 0x49484452;
    private static final int PLTE = 0x504C5445;
    private static final int tRNS = 0x74524E53;
    private static final int IDAT = 0x49444154;
    private static final int IEND = 0x49454E44;
    private static final int gAMA = 0x67414d41;

    private static final byte COLOR_GREYSCALE = 0;
    private static final byte COLOR_TRUECOLOR = 2;
    private static final byte COLOR_INDEXED = 3;
    private static final byte COLOR_GREYALPHA = 4;
    private static final byte COLOR_TRUEALPHA = 6;

    private final InputStream input;
    private final CRC32 crc;
    private final byte[] buffer;

    private int chunkLength;
    private int chunkType;
    private int chunkRemaining;

    private int width;
    private int height;
    private int bitdepth;
    private int colorType;
    private int bytesPerPixel;
    private int[] palette;
    private byte[] transPixel;
    private GammaCorrection gammaCorrection = null;

    public PngDecoder(InputStream input) throws IOException {
        this.input = input;
        this.crc = new CRC32();
        this.buffer = new byte[4096];

        readFully(buffer, 0, SIGNATURE.length);
        if(!checkSignature(buffer)) {
            throw new IOException("Not a valid PNG file");
        }

        openChunk(IHDR);
        readIHDR();
        closeChunk();

        searchIDAT: for(;;) {
            openChunk();
            switch (chunkType) {
                case IDAT:
                    break searchIDAT;
                case PLTE:
                    readPLTE();
                    break;
                case gAMA:
                    readgAMA();
                    break;
                case tRNS:
                    readtRNS();
                    break;
            }
            closeChunk();
        }

        if(colorType == COLOR_INDEXED && palette == null) {
            throw new IOException("Missing PLTE chunk");
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Decodes the image into the specified buffer. The first line is placed at
     * the current position. After decode the buffer position is at the end of
     * the last line.
     *
     * @param buffer the buffer
     * @param stride the stride in bytes from start of a line to start of the next line, can be negative.
     * @throws IOException if a read or data error occurred
     * @throws IllegalArgumentException if the start position of a line falls outside the buffer
     * @throws UnsupportedOperationException if the image can't be decoded into the desired format
     */
    public void decode(IntBuffer buffer, int stride) throws IOException {
        final int offset = buffer.position();
        final int lineSize = ((width * bitdepth + 7) / 8) * bytesPerPixel;
        byte[] curLine = new byte[lineSize+1];
        byte[] prevLine = new byte[lineSize+1];
        byte[] palLine = (bitdepth < 8) ? new byte[width+1] : null;

        final Inflater inflater = new Inflater();
        try {
            for(int y=0 ; y<height ; y++) {
                readChunkUnzip(inflater, curLine, 0, curLine.length);
                unfilter(curLine, prevLine);

                buffer.position(offset + y*stride);

                switch (colorType) {
                    case COLOR_TRUECOLOR:
                        copyRGB(buffer, curLine);
                        break;
                    case COLOR_TRUEALPHA:
                        copyRGBA(buffer, curLine);
                        break;
                    case COLOR_GREYSCALE:
                        switch(bitdepth) {
                            case 8: palLine = curLine; break;
                            case 4: expand4(curLine, palLine); break;
                            case 2: expand2(curLine, palLine); break;
                            case 1: expand1(curLine, palLine); break;
                            default: throw new UnsupportedOperationException("Unsupported bitdepth for this image");
                        }
                        copyGreyscale(buffer, palLine);
                        break;
/*                    case COLOR_GREYALPHA:
                        switch (fmt) {
                            case LUMINANCE_ALPHA: copy(buffer, curLine); break;
                            default: throw new UnsupportedOperationException("Unsupported format " + fmt + " for color type " + colorType);
                        }
                        break;
                        */
                    case COLOR_INDEXED:
                        switch(bitdepth) {
                            case 8: palLine = curLine; break;
                            case 4: expand4(curLine, palLine); break;
                            case 2: expand2(curLine, palLine); break;
                            case 1: expand1(curLine, palLine); break;
                            default: throw new UnsupportedOperationException("Unsupported bitdepth for this image");
                        }
                        copyPAL(buffer, palLine);
                        break;
                    default:
                        throw new UnsupportedOperationException("Not yet implemented");
                }

                byte[] tmp = curLine;
                curLine = prevLine;
                prevLine = tmp;
            }
        } finally {
            inflater.end();
        }
    }

    private byte toByte(int value) {
        switch (bitdepth) {
            case 1:
                if(value != 0) return (byte)0xFF;
                else return 0;

            case 2:
                switch (value&3) {
                    case 0: return 0;
                    case 1: return (byte)0x55;
                    case 2: return (byte)0xAA;
                    case 3: return (byte)0xFF;
                }
                break;

            case 4:
                return (byte)((value & 0xF) | ((value << 4) & 0xF0));
            case 8:
                return (byte)value;
            default:
                break;
        }
        throw new UnsupportedOperationException("unsupported bit width: " + bitdepth);
    }

    private void copyGreyscale(IntBuffer buffer, byte[] curLine) {
        for(int i=1,n=curLine.length ; i<n ; i+=1) {
            int val = toByte(curLine[i]) & 0xFF;
            if(gammaCorrection != null) {
                val = gammaCorrection.correctSample(val);
            }

            buffer.put(Colors.create(val,val,val,0xFF));
        }
    }

    private void copyRGB(IntBuffer buffer, byte[] curLine) {
        if (transPixel != null) {
            byte tr = transPixel[1];
            byte tg = transPixel[3];
            byte tb = transPixel[5];
            for (int i = 1, n = curLine.length; i < n; i += 3) {
                byte r = curLine[i];
                byte g = curLine[i + 1];
                byte b = curLine[i + 2];
                byte a = (byte) 0xFF;
                if (r == tr && g == tg && b == tb) {
                    a = 0;
                }
                buffer.put(rgba(r, g, b, a));
            }
        } else {
            for (int i = 1, n = curLine.length; i < n; i += 3) {
                buffer.put(rgba(curLine[i], curLine[i + 1], curLine[i+2], (byte) 0xFF));
            }
        }
    }

    private void copyRGBA(IntBuffer buffer, byte[] curLine) {
        for(int i=1,n=curLine.length ; i<n ; i+=4) {
            buffer.put(rgba(curLine[i], curLine[i+1], curLine[i+2], curLine[i+3]));
        }
    }

    private int rgba(byte r, byte g, byte b, byte a) {
        return Colors.create(r,g,b,a);
    }

    private void copyPAL(IntBuffer buffer, byte[] curLine) {
        for (int i = 1, n = curLine.length; i < n; i += 1) {
            int idx = curLine[i] & 255;
            buffer.put(palette[idx]);
        }
    }

    private void expand4(byte[] src, byte[] dst) {
        for(int i=1,n=dst.length ; i<n ; i+=2) {
            int val = src[1 + (i >> 1)] & 255;
            switch(n-i) {
                default: dst[i+1] = (byte)(val & 15);
                case 1:  dst[i  ] = (byte)(val >> 4);
            }
        }
    }

    private void expand2(byte[] src, byte[] dst) {
        for(int i=1,n=dst.length ; i<n ; i+=4) {
            int val = src[1 + (i >> 2)] & 255;
            switch(n-i) {
                default: dst[i+3] = (byte)((val     ) & 3);
                case 3:  dst[i+2] = (byte)((val >> 2) & 3);
                case 2:  dst[i+1] = (byte)((val >> 4) & 3);
                case 1:  dst[i  ] = (byte)((val >> 6)    );
            }
        }
    }

    private void expand1(byte[] src, byte[] dst) {
        for(int i=1,n=dst.length ; i<n ; i+=8) {
            int val = src[1 + (i >> 3)] & 255;
            switch(n-i) {
                default: dst[i+7] = (byte)((val     ) & 1);
                case 7:  dst[i+6] = (byte)((val >> 1) & 1);
                case 6:  dst[i+5] = (byte)((val >> 2) & 1);
                case 5:  dst[i+4] = (byte)((val >> 3) & 1);
                case 4:  dst[i+3] = (byte)((val >> 4) & 1);
                case 3:  dst[i+2] = (byte)((val >> 5) & 1);
                case 2:  dst[i+1] = (byte)((val >> 6) & 1);
                case 1:  dst[i  ] = (byte)((val >> 7)    );
            }
        }
    }

    private void unfilter(byte[] curLine, byte[] prevLine) throws IOException {
        switch (curLine[0]) {
            case 0: // none
                break;
            case 1:
                unfilterSub(curLine);
                break;
            case 2:
                unfilterUp(curLine, prevLine);
                break;
            case 3:
                unfilterAverage(curLine, prevLine);
                break;
            case 4:
                unfilterPaeth(curLine, prevLine);
                break;
            default:
                throw new IOException("invalide filter type in scanline: " + curLine[0]);
        }
    }

    private void unfilterSub(byte[] curLine) {
        final int bpp = this.bytesPerPixel;
        for(int i=bpp+1,n=curLine.length ; i<n ; ++i) {
            curLine[i] += curLine[i-bpp];
        }
    }

    private void unfilterUp(byte[] curLine, byte[] prevLine) {
        final int bpp = this.bytesPerPixel;
        for(int i=1,n=curLine.length ; i<n ; ++i) {
            curLine[i] += prevLine[i];
        }
    }

    private void unfilterAverage(byte[] curLine, byte[] prevLine) {
        final int bpp = this.bytesPerPixel;

        int i;
        for(i=1 ; i<=bpp ; ++i) {
            curLine[i] += (byte)((prevLine[i] & 0xFF) >>> 1);
        }
        for(int n=curLine.length ; i<n ; ++i) {
            curLine[i] += (byte)(((prevLine[i] & 0xFF) + (curLine[i - bpp] & 0xFF)) >>> 1);
        }
    }

    private void unfilterPaeth(byte[] curLine, byte[] prevLine) {
        final int bpp = this.bytesPerPixel;

        int i;
        for(i=1 ; i<=bpp ; ++i) {
            curLine[i] += prevLine[i];
        }
        for(int n=curLine.length ; i<n ; ++i) {
            int a = curLine[i - bpp] & 255;
            int b = prevLine[i] & 255;
            int c = prevLine[i - bpp] & 255;
            int p = a + b - c;
            int pa = p - a; if(pa < 0) pa = -pa;
            int pb = p - b; if(pb < 0) pb = -pb;
            int pc = p - c; if(pc < 0) pc = -pc;
            if(pa<=pb && pa<=pc)
                c = a;
            else if(pb<=pc)
                c = b;
            curLine[i] += (byte)c;
        }
    }

    private void readIHDR() throws IOException {
        checkChunkLength(13);
        readChunk(buffer, 0, 13);
        width = readInt(buffer, 0);
        height = readInt(buffer, 4);
        bitdepth = buffer[8] & 255;
        colorType = buffer[9] & 255;

        switch (colorType) {
            case COLOR_GREYSCALE:
                if(bitdepth != 8 && bitdepth != 4 && bitdepth != 2 && bitdepth != 1) {
                    throw new IOException("Unsupported bit depth for COLOR_GREYSCALE: " + bitdepth);
                }
                bytesPerPixel = 1;
                break;
            case COLOR_GREYALPHA:
                if(bitdepth != 8) {
                    throw new IOException("Unsupported bit depth for COLOR_GREYALPHA: " + bitdepth);
                }
                bytesPerPixel = 2;
                break;
            case COLOR_TRUECOLOR:
                if(bitdepth != 8) {
                    throw new IOException("Unsupported bit depth for COLOR_TRUECOLOR: " + bitdepth);
                }
                bytesPerPixel = 3;
                break;
            case COLOR_TRUEALPHA:
                if(bitdepth != 8) {
                    throw new IOException("Unsupported bit depth for COLOR_TRUEALPHA: " + bitdepth);
                }
                bytesPerPixel = 4;
                break;
            case COLOR_INDEXED:
                switch(bitdepth) {
                    case 8:
                    case 4:
                    case 2:
                    case 1:
                        bytesPerPixel = 1;
                        break;
                    default:
                        throw new IOException("Unsupported bit depth for COLOR_INDEXED: " + bitdepth);
                }
                break;
            default:
                throw new IOException("unsupported color format: " + colorType);
        }

        if(buffer[10] != 0) {
            throw new IOException("unsupported compression method");
        }
        if(buffer[11] != 0) {
            throw new IOException("unsupported filtering method");
        }
        if(buffer[12] != 0) {
            throw new IOException("unsupported interlace method: " + buffer[12]);
        }
    }

    private void readPLTE() throws IOException {
        int paletteEntries = chunkLength / 3;
        if(paletteEntries < 1 || paletteEntries > 256 || (chunkLength % 3) != 0) {
            throw new IOException("PLTE chunk has wrong length");
        }
        byte [] buffer = new byte[paletteEntries*3];
        readChunk(buffer, 0, buffer.length);

        this.palette = new int[paletteEntries];
        for(int i=0,j=0;j<paletteEntries;i+=3,++j) {
            this.palette[j] = Colors.create(buffer[i], buffer[i+1], buffer[i+2]);
        }
    }

    private void readgAMA() throws IOException {
        byte [] data = new byte[4];

        checkChunkLength(4);

        readChunk(data, 0,4);

        long gammaLong = (data[0] & 0xFF) << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
        gammaLong &= 0xFFFFFFFF;

        final double gamma = 1.0 / (gammaLong / 100000.0);

        final double targetGamma = 1.0;
        final double diff = Math.abs(targetGamma - gamma);

        if (diff >= 0.0) {
            gammaCorrection = new GammaCorrection(gamma, targetGamma);
        }

        if (gammaCorrection != null) {
            if (palette != null) {
                for(int i=0;i<palette.length; ++i) {
                    palette[i] = gammaCorrection.correctARGB(palette[i]);
                }
            }
        }
    }

    private void readtRNS() throws IOException {
        switch (colorType) {
            case COLOR_GREYSCALE:
                checkChunkLength(2);
                transPixel = new byte[2];
                readChunk(transPixel, 0, 2);
                break;
            case COLOR_TRUECOLOR:
                checkChunkLength(6);
                transPixel = new byte[6];
                readChunk(transPixel, 0, 6);
                break;
            case COLOR_INDEXED:
                if(palette == null) {
                    throw new IOException("tRNS chunk without PLTE chunk");
                }
                byte [] paletteA = new byte[palette.length];
                Arrays.fill(paletteA, (byte)0xFF);
                readChunk(paletteA, 0, paletteA.length);

                for(int i=0;i<paletteA.length;++i) {
                    palette[i] = Colors.setAlpha(palette[i], paletteA[i]);
                }
                break;
            default:
                // just ignore it
        }
    }

    private void closeChunk() throws IOException {
        if(chunkRemaining > 0) {
            // just skip the rest and the CRC
            skip(chunkRemaining + 4);
        } else {
            readFully(buffer, 0, 4);
            int expectedCrc = readInt(buffer, 0);
            int computedCrc = (int)crc.getValue();
            if(computedCrc != expectedCrc) {
                throw new IOException("Invalid CRC");
            }
        }
        chunkRemaining = 0;
        chunkLength = 0;
        chunkType = 0;
    }

    private void openChunk() throws IOException {
        readFully(buffer, 0, 8);
        chunkLength = readInt(buffer, 0);
        chunkType = readInt(buffer, 4);
        chunkRemaining = chunkLength;
        crc.reset();
        crc.update(buffer, 4, 4);   // only chunkType
    }

    private void openChunk(int expected) throws IOException {
        openChunk();
        if(chunkType != expected) {
            throw new IOException("Expected chunk: " + Integer.toHexString(expected));
        }
    }

    private void checkChunkLength(int expected) throws IOException {
        if(chunkLength != expected) {
            throw new IOException("Chunk has wrong size");
        }
    }

    private int readChunk(byte[] buffer, int offset, int length) throws IOException {
        if(length > chunkRemaining) {
            length = chunkRemaining;
        }
        readFully(buffer, offset, length);
        crc.update(buffer, offset, length);
        chunkRemaining -= length;
        return length;
    }

    private void refillInflater(Inflater inflater) throws IOException {
        while(chunkRemaining == 0) {
            closeChunk();
            openChunk(IDAT);
        }
        int read = readChunk(buffer, 0, buffer.length);
        inflater.setInput(buffer, 0, read);
    }

    private void readChunkUnzip(Inflater inflater, byte[] buffer, int offset, int length) throws IOException {
        assert(buffer != this.buffer);
        try {
            do {
                int read = inflater.inflate(buffer, offset, length);
                if(read <= 0) {
                    if(inflater.finished()) {
                        throw new EOFException();
                    }
                    if(inflater.needsInput()) {
                        refillInflater(inflater);
                    } else {
                        throw new IOException("Can't inflate " + length + " bytes");
                    }
                } else {
                    offset += read;
                    length -= read;
                }
            } while(length > 0);
        } catch (DataFormatException ex) {
            throw new IOException("inflate error", ex);
        }
    }

    private void readFully(byte[] buffer, int offset, int length) throws IOException {
        do {
            int read = input.read(buffer, offset, length);
            if(read < 0) {
                throw new EOFException();
            }
            offset += read;
            length -= read;
        } while(length > 0);
    }

    private int readInt(byte[] buffer, int offset) {
        return
                ((buffer[offset  ]      ) << 24) |
                        ((buffer[offset+1] & 255) << 16) |
                        ((buffer[offset+2] & 255) <<  8) |
                        ((buffer[offset+3] & 255)      );
    }

    private void skip(long amount) throws IOException {
        while(amount > 0) {
            long skipped = input.skip(amount);
            if(skipped < 0) {
                throw new EOFException();
            }
            amount -= skipped;
        }
    }

    private static boolean checkSignature(byte[] buffer) {
        for(int i=0 ; i<SIGNATURE.length ; i++) {
            if(buffer[i] != SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }
}