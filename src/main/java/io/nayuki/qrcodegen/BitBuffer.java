/*
 * Decompiled with CFR 0.137.
 */
package io.nayuki.qrcodegen;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrSegment;
import java.util.BitSet;
import java.util.Objects;

public final class BitBuffer
implements Cloneable {
    private BitSet data = new BitSet();
    private int bitLength = 0;

    public int bitLength() {
        return this.bitLength;
    }

    public int getBit(int index) {
        if (index < 0 || index >= this.bitLength) {
            throw new IndexOutOfBoundsException();
        }
        return this.data.get(index) ? 1 : 0;
    }

    public byte[] getBytes() {
        byte[] result = new byte[(this.bitLength + 7) / 8];
        for (int i = 0; i < this.bitLength; ++i) {
            byte[] arrby = result;
            int n = i >>> 3;
            arrby[n] = (byte)(arrby[n] | (this.data.get(i) ? 1 << 7 - (i & 7) : 0));
        }
        return result;
    }

    public void appendBits(int val, int len) {
        if (len < 0 || len > 31 || val >>> len != 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        int i = len - 1;
        while (i >= 0) {
            this.data.set(this.bitLength, QrCode.getBit(val, i));
            --i;
            ++this.bitLength;
        }
    }

    public void appendData(QrSegment seg) {
        Objects.requireNonNull(seg);
        BitBuffer bb = seg.data;
        int i = 0;
        while (i < bb.bitLength) {
            this.data.set(this.bitLength, bb.data.get(i));
            ++i;
            ++this.bitLength;
        }
    }

    public BitBuffer clone() {
        try {
            BitBuffer result = (BitBuffer)super.clone();
            result.data = (BitSet)result.data.clone();
            return result;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
