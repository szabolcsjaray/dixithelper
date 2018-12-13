/*
 * Decompiled with CFR 0.137.
 */
package io.nayuki.qrcodegen;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrSegment;
import io.nayuki.qrcodegen.QrSegmentAdvanced;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public final class QrCodeGeneratorDemo {
    public static void main(String[] args) throws IOException {
        QrCodeGeneratorDemo.doBasicDemo();
        QrCodeGeneratorDemo.doVarietyDemo();
        QrCodeGeneratorDemo.doSegmentDemo();
        QrCodeGeneratorDemo.doMaskDemo();
    }

    private static void doBasicDemo() throws IOException {
        String text = "Hello, world!";
        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;
        QrCode qr = QrCode.encodeText(text, errCorLvl);
        BufferedImage img = qr.toImage(10, 4);
        File imgFile = new File("hello-world-QR.png");
        ImageIO.write((RenderedImage)img, "png", imgFile);
        String svg = qr.toSvgString(4);
        try (OutputStreamWriter out = new OutputStreamWriter((OutputStream)new FileOutputStream("hello-world-QR.svg"), StandardCharsets.UTF_8);){
            out.write(svg);
        }
    }

    private static void doVarietyDemo() throws IOException {
        QrCode qr = QrCode.encodeText("314159265358979323846264338327950288419716939937510", QrCode.Ecc.MEDIUM);
        QrCodeGeneratorDemo.writePng(qr.toImage(13, 1), "pi-digits-QR.png");
        qr = QrCode.encodeText("DOLLAR-AMOUNT:$39.87 PERCENTAGE:100.00% OPERATIONS:+-*/", QrCode.Ecc.HIGH);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 2), "alphanumeric-QR.png");
        qr = QrCode.encodeText("\u3053\u3093\u306b\u3061wa\u3001\u4e16\u754c\uff01 \u03b1\u03b2\u03b3\u03b4", QrCode.Ecc.QUARTILE);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 3), "unicode-QR.png");
        qr = QrCode.encodeText("Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, 'and what is the use of a book,' thought Alice 'without pictures or conversations?' So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.", QrCode.Ecc.HIGH);
        QrCodeGeneratorDemo.writePng(qr.toImage(6, 10), "alice-wonderland-QR.png");
    }

    private static void doSegmentDemo() throws IOException {
        String silver0 = "THE SQUARE ROOT OF 2 IS 1.";
        String silver1 = "41421356237309504880168872420969807856967187537694807317667973799";
        QrCode qr = QrCode.encodeText(silver0 + silver1, QrCode.Ecc.LOW);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 3), "sqrt2-monolithic-QR.png");
        List<QrSegment> segs = Arrays.asList(QrSegment.makeAlphanumeric(silver0), QrSegment.makeNumeric(silver1));
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 3), "sqrt2-segmented-QR.png");
        String golden0 = "Golden ratio \u03c6 = 1.";
        String golden1 = "6180339887498948482045868343656381177203091798057628621354486227052604628189024497072072041893911374";
        String golden2 = "......";
        qr = QrCode.encodeText(golden0 + golden1 + golden2, QrCode.Ecc.LOW);
        QrCodeGeneratorDemo.writePng(qr.toImage(8, 5), "phi-monolithic-QR.png");
        segs = Arrays.asList(QrSegment.makeBytes(golden0.getBytes(StandardCharsets.UTF_8)), QrSegment.makeNumeric(golden1), QrSegment.makeAlphanumeric(golden2));
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
        QrCodeGeneratorDemo.writePng(qr.toImage(8, 5), "phi-segmented-QR.png");
        String madoka = "\u300c\u9b54\u6cd5\u5c11\u5973\u307e\u3069\u304b\u2606\u30de\u30ae\u30ab\u300d\u3063\u3066\u3001\u3000\u0418\u0410\u0418\u3000\uff44\uff45\uff53\uff55\u3000\u03ba\u03b1\uff1f";
        qr = QrCode.encodeText(madoka, QrCode.Ecc.LOW);
        QrCodeGeneratorDemo.writePng(qr.toImage(9, 4), "madoka-utf8-QR.png");
        segs = Arrays.asList(QrSegmentAdvanced.makeKanji(madoka));
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
        QrCodeGeneratorDemo.writePng(qr.toImage(9, 4), "madoka-kanji-QR.png");
    }

    private static void doMaskDemo() throws IOException {
        List<QrSegment> segs = QrSegment.makeSegments("https://www.nayuki.io/");
        QrCode qr = QrCode.encodeSegments(segs, QrCode.Ecc.HIGH, 1, 40, -1, true);
        QrCodeGeneratorDemo.writePng(qr.toImage(8, 6), "project-nayuki-automask-QR.png");
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.HIGH, 1, 40, 3, true);
        QrCodeGeneratorDemo.writePng(qr.toImage(8, 6), "project-nayuki-mask3-QR.png");
        segs = QrSegment.makeSegments("\u7dad\u57fa\u767e\u79d1\uff08Wikipedia\uff0c\u8046\u807di/\u02ccw\u026ak\u1d7b\u02c8pi\u02d0di.\u0259/\uff09\u662f\u4e00\u500b\u81ea\u7531\u5167\u5bb9\u3001\u516c\u958b\u7de8\u8f2f\u4e14\u591a\u8a9e\u8a00\u7684\u7db2\u8def\u767e\u79d1\u5168\u66f8\u5354\u4f5c\u8a08\u756b");
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, 1, 40, 0, true);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 3), "unicode-mask0-QR.png");
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, 1, 40, 1, true);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 3), "unicode-mask1-QR.png");
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, 1, 40, 5, true);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 3), "unicode-mask5-QR.png");
        qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, 1, 40, 7, true);
        QrCodeGeneratorDemo.writePng(qr.toImage(10, 3), "unicode-mask7-QR.png");
    }

    private static void writePng(BufferedImage img, String filepath) throws IOException {
        ImageIO.write((RenderedImage)img, "png", new File(filepath));
    }
}
