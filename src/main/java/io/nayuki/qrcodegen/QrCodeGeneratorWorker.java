/*
 * Decompiled with CFR 0.137.
 */
package io.nayuki.qrcodegen;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrSegment;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class QrCodeGeneratorWorker {
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in, "US-ASCII");){
            input.useDelimiter("\r\n|\n|\r");
            while (QrCodeGeneratorWorker.processCase(input)) {
            }
        }
    }

    private static boolean processCase(Scanner input) {
        int length = input.nextInt();
        if (length == -1) {
            return false;
        }
        if (length > 32767) {
            throw new RuntimeException();
        }
        boolean isAscii = true;
        byte[] data = new byte[length];
        for (int i = 0; i < data.length; ++i) {
            int b = input.nextInt();
            if (b < 0 || b > 255) {
                throw new RuntimeException();
            }
            data[i] = (byte)b;
            isAscii &= b < 128;
        }
        int errCorLvl = input.nextInt();
        int minVersion = input.nextInt();
        int maxVersion = input.nextInt();
        int mask = input.nextInt();
        int boostEcl = input.nextInt();
        if (0 > errCorLvl || errCorLvl > 3 || -1 > mask || mask > 7 || boostEcl >>> 1 != 0 || 1 > minVersion || minVersion > maxVersion || maxVersion > 40) {
            throw new RuntimeException();
        }
        List<QrSegment> segs = isAscii ? QrSegment.makeSegments(new String(data, StandardCharsets.US_ASCII)) : Arrays.asList(QrSegment.makeBytes(data));
        try {
            QrCode qr = QrCode.encodeSegments(segs, QrCode.Ecc.values()[errCorLvl], minVersion, maxVersion, mask, boostEcl != 0);
            System.out.println(qr.version);
            for (int y = 0; y < qr.size; ++y) {
                for (int x = 0; x < qr.size; ++x) {
                    System.out.println(qr.getModule(x, y) ? 1 : 0);
                }
            }
        }
        catch (IllegalArgumentException e) {
            if (!e.getMessage().equals("Data too long")) {
                throw e;
            }
            System.out.println(-1);
        }
        System.out.flush();
        return true;
    }
}
