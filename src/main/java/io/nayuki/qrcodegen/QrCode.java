/*
 * Decompiled with CFR 0.137.
 */
package io.nayuki.qrcodegen;

import io.nayuki.qrcodegen.BitBuffer;
import io.nayuki.qrcodegen.QrSegment;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class QrCode {
    public static final int MIN_VERSION = 1;
    public static final int MAX_VERSION = 40;
    public final int version;
    public final int size;
    public final Ecc errorCorrectionLevel;
    public final int mask;
    private boolean[][] modules;
    private boolean[][] isFunction;
    private static final int PENALTY_N1 = 3;
    private static final int PENALTY_N2 = 3;
    private static final int PENALTY_N3 = 40;
    private static final int PENALTY_N4 = 10;
    private static final byte[][] ECC_CODEWORDS_PER_BLOCK = new byte[][]{{-1, 7, 10, 15, 20, 26, 18, 20, 24, 30, 18, 20, 24, 26, 30, 22, 24, 28, 30, 28, 28, 28, 28, 30, 30, 26, 28, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30}, {-1, 10, 16, 26, 18, 24, 16, 18, 22, 22, 26, 30, 22, 22, 24, 24, 28, 28, 26, 26, 26, 26, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28}, {-1, 13, 22, 18, 26, 18, 24, 18, 22, 20, 24, 28, 26, 24, 20, 30, 24, 28, 28, 26, 30, 28, 30, 30, 30, 30, 28, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30}, {-1, 17, 28, 22, 16, 22, 28, 26, 26, 24, 28, 24, 28, 22, 24, 24, 30, 28, 28, 26, 28, 30, 24, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30}};
    private static final byte[][] NUM_ERROR_CORRECTION_BLOCKS = new byte[][]{{-1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4, 4, 6, 6, 6, 6, 7, 8, 8, 9, 9, 10, 12, 12, 12, 13, 14, 15, 16, 17, 18, 19, 19, 20, 21, 22, 24, 25}, {-1, 1, 1, 1, 2, 2, 4, 4, 4, 5, 5, 5, 8, 9, 9, 10, 10, 11, 13, 14, 16, 17, 17, 18, 20, 21, 23, 25, 26, 28, 29, 31, 33, 35, 37, 38, 40, 43, 45, 47, 49}, {-1, 1, 1, 2, 2, 4, 4, 6, 6, 8, 8, 8, 10, 12, 16, 12, 17, 16, 18, 21, 20, 23, 23, 25, 27, 29, 34, 34, 35, 38, 40, 43, 45, 48, 51, 53, 56, 59, 62, 65, 68}, {-1, 1, 1, 2, 4, 4, 4, 5, 6, 8, 8, 11, 11, 16, 16, 18, 16, 19, 21, 25, 25, 25, 34, 30, 32, 35, 37, 40, 42, 45, 48, 51, 54, 57, 60, 63, 66, 70, 74, 77, 81}};

    public static QrCode encodeText(String text, Ecc ecl) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(ecl);
        List<QrSegment> segs = QrSegment.makeSegments(text);
        return QrCode.encodeSegments(segs, ecl);
    }

    public static QrCode encodeBinary(byte[] data, Ecc ecl) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(ecl);
        QrSegment seg = QrSegment.makeBytes(data);
        return QrCode.encodeSegments(Arrays.asList(seg), ecl);
    }

    public static QrCode encodeSegments(List<QrSegment> segs, Ecc ecl) {
        return QrCode.encodeSegments(segs, ecl, 1, 40, -1, true);
    }

    public static QrCode encodeSegments(List<QrSegment> segs, Ecc ecl, int minVersion, int maxVersion, int mask, boolean boostEcl) {
        int dataUsedBits;
        Objects.requireNonNull(segs);
        Objects.requireNonNull(ecl);
        if (1 > minVersion || minVersion > maxVersion || maxVersion > 40 || mask < -1 || mask > 7) {
            throw new IllegalArgumentException("Invalid value");
        }
        int version = minVersion;
        do {
            int dataCapacityBits = QrCode.getNumDataCodewords(version, ecl) * 8;
            dataUsedBits = QrSegment.getTotalBits(segs, version);
            if (dataUsedBits != -1 && dataUsedBits <= dataCapacityBits) break;
            if (version >= maxVersion) {
                throw new IllegalArgumentException("Data too long");
            }
            ++version;
        } while (true);
        if (dataUsedBits == -1) {
            throw new AssertionError();
        }
        for (Ecc newEcl : Ecc.values()) {
            if (!boostEcl || dataUsedBits > QrCode.getNumDataCodewords(version, newEcl) * 8) continue;
            ecl = newEcl;
        }
        int dataCapacityBits = QrCode.getNumDataCodewords(version, ecl) * 8;
        BitBuffer bb = new BitBuffer();
        for (QrSegment seg : segs) {
            bb.appendBits(seg.mode.modeBits, 4);
            bb.appendBits(seg.numChars, seg.mode.numCharCountBits(version));
            bb.appendData(seg);
        }
        bb.appendBits(0, Math.min(4, dataCapacityBits - bb.bitLength()));
        bb.appendBits(0, (8 - bb.bitLength() % 8) % 8);
        int padByte = 236;
        while (bb.bitLength() < dataCapacityBits) {
            bb.appendBits(padByte, 8);
            padByte ^= 253;
        }
        if (bb.bitLength() % 8 != 0) {
            throw new AssertionError();
        }
        return new QrCode(version, ecl, bb.getBytes(), mask);
    }

    public QrCode(int ver, Ecc ecl, byte[] dataCodewords, int mask) {
        Objects.requireNonNull(ecl);
        if (ver < 1 || ver > 40 || mask < -1 || mask > 7) {
            throw new IllegalArgumentException("Value out of range");
        }
        Objects.requireNonNull(dataCodewords);
        this.version = ver;
        this.size = ver * 4 + 17;
        this.errorCorrectionLevel = ecl;
        this.modules = new boolean[this.size][this.size];
        this.isFunction = new boolean[this.size][this.size];
        this.drawFunctionPatterns();
        byte[] allCodewords = this.appendErrorCorrection(dataCodewords);
        this.drawCodewords(allCodewords);
        this.mask = this.handleConstructorMasking(mask);
    }

    public boolean getModule(int x, int y) {
        return 0 <= x && x < this.size && 0 <= y && y < this.size && this.modules[y][x];
    }

    public BufferedImage toImage(int scale, int border) {
        if (scale <= 0 || border < 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        if (border > 1073741823 || (long)this.size + (long)border * 2L > (long)(Integer.MAX_VALUE / scale)) {
            throw new IllegalArgumentException("Scale or border too large");
        }
        BufferedImage result = new BufferedImage((this.size + border * 2) * scale, (this.size + border * 2) * scale, 1);
        for (int y = 0; y < result.getHeight(); ++y) {
            for (int x = 0; x < result.getWidth(); ++x) {
                boolean val = this.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, val ? 0 : 16777215);
            }
        }
        return result;
    }

    public String toSvgString(int border) {
        if (border < 0) {
            throw new IllegalArgumentException("Border must be non-negative");
        }
        if ((long)this.size + (long)border * 2L > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Border too large");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
        sb.append(String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 %1$d %1$d\" stroke=\"none\">\n", 100));
        sb.append("\t<rect width=\"100%\" height=\"100%\" fill=\"#FFFFFF\"/>\n");
        sb.append("\t<path d=\"");
        boolean head = true;
        for (int y = - border; y < this.size + border; ++y) {
            for (int x = - border; x < this.size + border; ++x) {
                if (!this.getModule(x, y)) continue;
                if (head) {
                    head = false;
                } else {
                    sb.append(" ");
                }
                sb.append(String.format("M%d,%dh1v1h-1z", x + border, y + border));
            }
        }
        sb.append("\" fill=\"#000000\"/>\n");
        sb.append("</svg>\n");
        return sb.toString();
    }

    private void drawFunctionPatterns() {
        for (int i = 0; i < this.size; ++i) {
            this.setFunctionModule(6, i, i % 2 == 0);
            this.setFunctionModule(i, 6, i % 2 == 0);
        }
        this.drawFinderPattern(3, 3);
        this.drawFinderPattern(this.size - 4, 3);
        this.drawFinderPattern(3, this.size - 4);
        int[] alignPatPos = QrCode.getAlignmentPatternPositions(this.version);
        int numAlign = alignPatPos.length;
        for (int i = 0; i < numAlign; ++i) {
            for (int j = 0; j < numAlign; ++j) {
                if (i == 0 && j == 0 || i == 0 && j == numAlign - 1 || i == numAlign - 1 && j == 0) continue;
                this.drawAlignmentPattern(alignPatPos[i], alignPatPos[j]);
            }
        }
        this.drawFormatBits(0);
        this.drawVersion();
    }

    private void drawFormatBits(int mask) {
        int data;
        int i;
        int rem = data = this.errorCorrectionLevel.formatBits << 3 | mask;
        for (i = 0; i < 10; ++i) {
            rem = rem << 1 ^ (rem >>> 9) * 1335;
        }
        data = data << 10 | rem;
        if ((data ^= 21522) >>> 15 != 0) {
            throw new AssertionError();
        }
        for (i = 0; i <= 5; ++i) {
            this.setFunctionModule(8, i, QrCode.getBit(data, i));
        }
        this.setFunctionModule(8, 7, QrCode.getBit(data, 6));
        this.setFunctionModule(8, 8, QrCode.getBit(data, 7));
        this.setFunctionModule(7, 8, QrCode.getBit(data, 8));
        for (i = 9; i < 15; ++i) {
            this.setFunctionModule(14 - i, 8, QrCode.getBit(data, i));
        }
        for (i = 0; i <= 7; ++i) {
            this.setFunctionModule(this.size - 1 - i, 8, QrCode.getBit(data, i));
        }
        for (i = 8; i < 15; ++i) {
            this.setFunctionModule(8, this.size - 15 + i, QrCode.getBit(data, i));
        }
        this.setFunctionModule(8, this.size - 8, true);
    }

    private void drawVersion() {
        if (this.version < 7) {
            return;
        }
        int rem = this.version;
        for (int i = 0; i < 12; ++i) {
            rem = rem << 1 ^ (rem >>> 11) * 7973;
        }
        int data = this.version << 12 | rem;
        if (data >>> 18 != 0) {
            throw new AssertionError();
        }
        for (int i = 0; i < 18; ++i) {
            boolean bit = QrCode.getBit(data, i);
            int a = this.size - 11 + i % 3;
            int b = i / 3;
            this.setFunctionModule(a, b, bit);
            this.setFunctionModule(b, a, bit);
        }
    }

    private void drawFinderPattern(int x, int y) {
        for (int i = -4; i <= 4; ++i) {
            for (int j = -4; j <= 4; ++j) {
                int dist = Math.max(Math.abs(i), Math.abs(j));
                int xx = x + j;
                int yy = y + i;
                if (0 > xx || xx >= this.size || 0 > yy || yy >= this.size) continue;
                this.setFunctionModule(xx, yy, dist != 2 && dist != 4);
            }
        }
    }

    private void drawAlignmentPattern(int x, int y) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                this.setFunctionModule(x + j, y + i, Math.max(Math.abs(i), Math.abs(j)) != 1);
            }
        }
    }

    private void setFunctionModule(int x, int y, boolean isBlack) {
        this.modules[y][x] = isBlack;
        this.isFunction[y][x] = true;
    }

    private byte[] appendErrorCorrection(byte[] data) {
        if (data.length != QrCode.getNumDataCodewords(this.version, this.errorCorrectionLevel)) {
            throw new IllegalArgumentException();
        }
        int numBlocks = NUM_ERROR_CORRECTION_BLOCKS[this.errorCorrectionLevel.ordinal()][this.version];
        byte blockEccLen = ECC_CODEWORDS_PER_BLOCK[this.errorCorrectionLevel.ordinal()][this.version];
        int rawCodewords = QrCode.getNumRawDataModules(this.version) / 8;
        int numShortBlocks = numBlocks - rawCodewords % numBlocks;
        int shortBlockLen = rawCodewords / numBlocks;
        byte[][] blocks = new byte[numBlocks][];
        ReedSolomonGenerator rs = new ReedSolomonGenerator(blockEccLen);
        int k = 0;
        for (int i = 0; i < numBlocks; ++i) {
            byte[] dat = Arrays.copyOfRange(data, k, k + shortBlockLen - blockEccLen + (i < numShortBlocks ? 0 : 1));
            byte[] block = Arrays.copyOf(dat, shortBlockLen + 1);
            k += dat.length;
            byte[] ecc = rs.getRemainder(dat);
            System.arraycopy(ecc, 0, block, block.length - blockEccLen, ecc.length);
            blocks[i] = block;
        }
        byte[] result = new byte[rawCodewords];
        int k2 = 0;
        for (int i = 0; i < blocks[0].length; ++i) {
            for (int j = 0; j < blocks.length; ++j) {
                if (i == shortBlockLen - blockEccLen && j < numShortBlocks) continue;
                result[k2] = blocks[j][i];
                ++k2;
            }
        }
        return result;
    }

    private void drawCodewords(byte[] data) {
        Objects.requireNonNull(data);
        if (data.length != QrCode.getNumRawDataModules(this.version) / 8) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        for (int right = this.size - 1; right >= 1; right -= 2) {
            if (right == 6) {
                right = 5;
            }
            for (int vert = 0; vert < this.size; ++vert) {
                for (int j = 0; j < 2; ++j) {
                    int y;
                    int x = right - j;
                    boolean upward = (right + 1 & 2) == 0;
                    int n = y = upward ? this.size - 1 - vert : vert;
                    if (this.isFunction[y][x] || i >= data.length * 8) continue;
                    this.modules[y][x] = QrCode.getBit(data[i >>> 3], 7 - (i & 7));
                    ++i;
                }
            }
        }
        if (i != data.length * 8) {
            throw new AssertionError();
        }
    }

    private void applyMask(int mask) {
        if (mask < 0 || mask > 7) {
            throw new IllegalArgumentException("Mask value out of range");
        }
        for (int y = 0; y < this.size; ++y) {
            for (int x = 0; x < this.size; ++x) {
                boolean invert;
                switch (mask) {
                    case 0: {
                        invert = (x + y) % 2 == 0;
                        break;
                    }
                    case 1: {
                        invert = y % 2 == 0;
                        break;
                    }
                    case 2: {
                        invert = x % 3 == 0;
                        break;
                    }
                    case 3: {
                        invert = (x + y) % 3 == 0;
                        break;
                    }
                    case 4: {
                        invert = (x / 3 + y / 2) % 2 == 0;
                        break;
                    }
                    case 5: {
                        invert = x * y % 2 + x * y % 3 == 0;
                        break;
                    }
                    case 6: {
                        invert = (x * y % 2 + x * y % 3) % 2 == 0;
                        break;
                    }
                    case 7: {
                        invert = ((x + y) % 2 + x * y % 3) % 2 == 0;
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
                boolean[] arrbl = this.modules[y];
                int n = x;
                arrbl[n] = arrbl[n] ^ invert & !this.isFunction[y][x];
            }
        }
    }

    private int handleConstructorMasking(int mask) {
        if (mask == -1) {
            int minPenalty = Integer.MAX_VALUE;
            for (int i = 0; i < 8; ++i) {
                this.drawFormatBits(i);
                this.applyMask(i);
                int penalty = this.getPenaltyScore();
                if (penalty < minPenalty) {
                    mask = i;
                    minPenalty = penalty;
                }
                this.applyMask(i);
            }
        }
        if (mask < 0 || mask > 7) {
            throw new AssertionError();
        }
        this.drawFormatBits(mask);
        this.applyMask(mask);
        return mask;
    }

    private int getPenaltyScore() {
        int x;
        int bits;
        int runY;
        int x2;
        int y;
        int result = 0;
        for (y = 0; y < this.size; ++y) {
            boolean colorX = false;
            int runX = 0;
            for (int x3 = 0; x3 < this.size; ++x3) {
                if (x3 == 0 || this.modules[y][x3] != colorX) {
                    colorX = this.modules[y][x3];
                    runX = 1;
                    continue;
                }
                if (++runX == 5) {
                    result += 3;
                    continue;
                }
                if (runX <= 5) continue;
                ++result;
            }
        }
        for (x = 0; x < this.size; ++x) {
            boolean colorY = false;
            runY = 0;
            for (int y2 = 0; y2 < this.size; ++y2) {
                if (y2 == 0 || this.modules[y2][x] != colorY) {
                    colorY = this.modules[y2][x];
                    runY = 1;
                    continue;
                }
                if (++runY == 5) {
                    result += 3;
                    continue;
                }
                if (runY <= 5) continue;
                ++result;
            }
        }
        for (y = 0; y < this.size - 1; ++y) {
            for (x2 = 0; x2 < this.size - 1; ++x2) {
                boolean color = this.modules[y][x2];
                if (color != this.modules[y][x2 + 1] || color != this.modules[y + 1][x2] || color != this.modules[y + 1][x2 + 1]) continue;
                result += 3;
            }
        }
        for (y = 0; y < this.size; ++y) {
            bits = 0;
            for (x2 = 0; x2 < this.size; ++x2) {
                bits = bits << 1 & 2047 | (this.modules[y][x2] ? 1 : 0);
                if (x2 < 10 || bits != 93 && bits != 1488) continue;
                result += 40;
            }
        }
        for (x = 0; x < this.size; ++x) {
            bits = 0;
            for (int y3 = 0; y3 < this.size; ++y3) {
                bits = bits << 1 & 2047 | (this.modules[y3][x] ? 1 : 0);
                if (y3 < 10 || bits != 93 && bits != 1488) continue;
                result += 40;
            }
        }
        int black = 0;
        boolean[][] y3 = this.modules;
        bits = y3.length;
        for (runY = 0; runY < bits; ++runY) {
            boolean[] row;
            for (boolean color : row = y3[runY]) {
                if (!color) continue;
                ++black;
            }
        }
        int total = this.size * this.size;
        int k = 0;
        while (black * 20 < (9 - k) * total || black * 20 > (11 + k) * total) {
            result += 10;
            ++k;
        }
        return result;
    }

    private static int[] getAlignmentPatternPositions(int ver) {
        if (ver < 1 || ver > 40) {
            throw new IllegalArgumentException("Version number out of range");
        }
        if (ver == 1) {
            return new int[0];
        }
        int numAlign = ver / 7 + 2;
        int step = ver != 32 ? (ver * 4 + numAlign * 2 + 1) / (2 * numAlign - 2) * 2 : 26;
        int[] result = new int[numAlign];
        result[0] = 6;
        int i = result.length - 1;
        int pos = ver * 4 + 10;
        while (i >= 1) {
            result[i] = pos;
            --i;
            pos -= step;
        }
        return result;
    }

    private static int getNumRawDataModules(int ver) {
        if (ver < 1 || ver > 40) {
            throw new IllegalArgumentException("Version number out of range");
        }
        int size = ver * 4 + 17;
        int result = size * size;
        result -= 192;
        result -= 31;
        result -= (size - 16) * 2;
        if (ver >= 2) {
            int numAlign = ver / 7 + 2;
            result -= (numAlign - 1) * (numAlign - 1) * 25;
            result -= (numAlign - 2) * 2 * 20;
            if (ver >= 7) {
                result -= 36;
            }
        }
        return result;
    }

    static int getNumDataCodewords(int ver, Ecc ecl) {
        if (ver < 1 || ver > 40) {
            throw new IllegalArgumentException("Version number out of range");
        }
        return QrCode.getNumRawDataModules(ver) / 8 - ECC_CODEWORDS_PER_BLOCK[ecl.ordinal()][ver] * NUM_ERROR_CORRECTION_BLOCKS[ecl.ordinal()][ver];
    }

    static boolean getBit(int x, int i) {
        return (x >>> i & 1) != 0;
    }

    private static final class ReedSolomonGenerator {
        private final byte[] coefficients;

        public ReedSolomonGenerator(int degree) {
            if (degree < 1 || degree > 255) {
                throw new IllegalArgumentException("Degree out of range");
            }
            this.coefficients = new byte[degree];
            this.coefficients[degree - 1] = 1;
            int root = 1;
            for (int i = 0; i < degree; ++i) {
                for (int j = 0; j < this.coefficients.length; ++j) {
                    this.coefficients[j] = (byte)ReedSolomonGenerator.multiply(this.coefficients[j] & 255, root);
                    if (j + 1 >= this.coefficients.length) continue;
                    byte[] arrby = this.coefficients;
                    int n = j;
                    arrby[n] = (byte)(arrby[n] ^ this.coefficients[j + 1]);
                }
                root = ReedSolomonGenerator.multiply(root, 2);
            }
        }

        public byte[] getRemainder(byte[] data) {
            Objects.requireNonNull(data);
            byte[] result = new byte[this.coefficients.length];
            for (byte b : data) {
                int factor = (b ^ result[0]) & 255;
                System.arraycopy(result, 1, result, 0, result.length - 1);
                result[result.length - 1] = 0;
                for (int i = 0; i < result.length; ++i) {
                    byte[] arrby = result;
                    int n = i;
                    arrby[n] = (byte)(arrby[n] ^ ReedSolomonGenerator.multiply(this.coefficients[i] & 255, factor));
                }
            }
            return result;
        }

        private static int multiply(int x, int y) {
            if (x >>> 8 != 0 || y >>> 8 != 0) {
                throw new IllegalArgumentException("Byte out of range");
            }
            int z = 0;
            for (int i = 7; i >= 0; --i) {
                z = z << 1 ^ (z >>> 7) * 285;
                z ^= (y >>> i & 1) * x;
            }
            if (z >>> 8 != 0) {
                throw new AssertionError();
            }
            return z;
        }
    }

    public static enum Ecc {
        LOW(1),
        MEDIUM(0),
        QUARTILE(3),
        HIGH(2);
        
        final int formatBits;

        private Ecc(int fb) {
            this.formatBits = fb;
        }
    }

}
