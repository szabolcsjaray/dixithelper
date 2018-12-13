/*
 * Decompiled with CFR 0.137.
 */
package io.nayuki.qrcodegen;

import io.nayuki.qrcodegen.BitBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QrSegment {
    public final Mode mode;
    public final int numChars;
    final BitBuffer data;
    public static final Pattern NUMERIC_REGEX = Pattern.compile("[0-9]*");
    public static final Pattern ALPHANUMERIC_REGEX = Pattern.compile("[A-Z0-9 $%*+./:-]*");
    private static final String ALPHANUMERIC_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";

    public static QrSegment makeBytes(byte[] data) {
        Objects.requireNonNull(data);
        BitBuffer bb = new BitBuffer();
        for (byte b : data) {
            bb.appendBits(b & 255, 8);
        }
        return new QrSegment(Mode.BYTE, data.length, bb);
    }

    public static QrSegment makeNumeric(String digits) {
        Objects.requireNonNull(digits);
        if (!NUMERIC_REGEX.matcher(digits).matches()) {
            throw new IllegalArgumentException("String contains non-numeric characters");
        }
        BitBuffer bb = new BitBuffer();
        int i = 0;
        while (i + 3 <= digits.length()) {
            bb.appendBits(Integer.parseInt(digits.substring(i, i + 3)), 10);
            i += 3;
        }
        int rem = digits.length() - i;
        if (rem > 0) {
            bb.appendBits(Integer.parseInt(digits.substring(i)), rem * 3 + 1);
        }
        return new QrSegment(Mode.NUMERIC, digits.length(), bb);
    }

    public static QrSegment makeAlphanumeric(String text) {
        Objects.requireNonNull(text);
        if (!ALPHANUMERIC_REGEX.matcher(text).matches()) {
            throw new IllegalArgumentException("String contains unencodable characters in alphanumeric mode");
        }
        BitBuffer bb = new BitBuffer();
        int i = 0;
        while (i + 2 <= text.length()) {
            int temp = ALPHANUMERIC_CHARSET.indexOf(text.charAt(i)) * 45;
            bb.appendBits(temp += ALPHANUMERIC_CHARSET.indexOf(text.charAt(i + 1)), 11);
            i += 2;
        }
        if (i < text.length()) {
            bb.appendBits(ALPHANUMERIC_CHARSET.indexOf(text.charAt(i)), 6);
        }
        return new QrSegment(Mode.ALPHANUMERIC, text.length(), bb);
    }

    public static List<QrSegment> makeSegments(String text) {
        Objects.requireNonNull(text);
        ArrayList<QrSegment> result = new ArrayList<QrSegment>();
        if (!text.equals("")) {
            if (NUMERIC_REGEX.matcher(text).matches()) {
                result.add(QrSegment.makeNumeric(text));
            } else if (ALPHANUMERIC_REGEX.matcher(text).matches()) {
                result.add(QrSegment.makeAlphanumeric(text));
            } else {
                result.add(QrSegment.makeBytes(text.getBytes(StandardCharsets.UTF_8)));
            }
        }
        return result;
    }

    public static QrSegment makeEci(int assignVal) {
        BitBuffer bb = new BitBuffer();
        if (0 <= assignVal && assignVal < 128) {
            bb.appendBits(assignVal, 8);
        } else if (128 <= assignVal && assignVal < 16384) {
            bb.appendBits(2, 2);
            bb.appendBits(assignVal, 14);
        } else if (16384 <= assignVal && assignVal < 1000000) {
            bb.appendBits(6, 3);
            bb.appendBits(assignVal, 21);
        } else {
            throw new IllegalArgumentException("ECI assignment value out of range");
        }
        return new QrSegment(Mode.ECI, 0, bb);
    }

    public QrSegment(Mode md, int numCh, BitBuffer data) {
        Objects.requireNonNull(md);
        Objects.requireNonNull(data);
        if (numCh < 0) {
            throw new IllegalArgumentException("Invalid value");
        }
        this.mode = md;
        this.numChars = numCh;
        this.data = data.clone();
    }

    public BitBuffer getBits() {
        return this.data.clone();
    }

    static int getTotalBits(List<QrSegment> segs, int version) {
        Objects.requireNonNull(segs);
        if (version < 1 || version > 40) {
            throw new IllegalArgumentException("Version number out of range");
        }
        long result = 0L;
        for (QrSegment seg : segs) {
            Objects.requireNonNull(seg);
            int ccbits = seg.mode.numCharCountBits(version);
            if (seg.numChars >= 1 << ccbits) {
                return -1;
            }
            if ((result += 4L + (long)ccbits + (long)seg.data.bitLength()) <= Integer.MAX_VALUE) continue;
            return -1;
        }
        return (int)result;
    }

    public static enum Mode {
        NUMERIC(1, 10, 12, 14),
        ALPHANUMERIC(2, 9, 11, 13),
        BYTE(4, 8, 16, 16),
        KANJI(8, 8, 10, 12),
        ECI(7, 0, 0, 0);
        
        final int modeBits;
        private final int[] numBitsCharCount;

        private /* varargs */ Mode(int mode, int ... ccbits) {
            this.modeBits = mode;
            this.numBitsCharCount = ccbits;
        }

        int numCharCountBits(int ver) {
            if (1 <= ver && ver <= 9) {
                return this.numBitsCharCount[0];
            }
            if (10 <= ver && ver <= 26) {
                return this.numBitsCharCount[1];
            }
            if (27 <= ver && ver <= 40) {
                return this.numBitsCharCount[2];
            }
            throw new IllegalArgumentException("Version number out of range");
        }
    }

}
