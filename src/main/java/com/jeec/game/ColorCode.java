/*
 * Decompiled with CFR 0.137.
 */
package com.jeec.game;

public enum ColorCode {
    RED("Piros", "#d6131d"),
    BLUE("K\u00e9k", "#065be2"),
    ORANGE("Narancs", "#f7741d"),
    GREEN("Z\u00f6ld", "#029105"),
    PINK("R\u00f3zsasz\u00edn", "#f263c9"),
    WHITE("Feh\u00e9r", "#FFFFFF"),
    YELLOW("S\u00e1rga", "yellow"),
    LIGHTBLUE("", ""),
    CLARET("", ""),
    LIGHTGREEN("", ""),
    GRAY("", ""),
    MAGENTA("", ""),
    PURPLE("", ""),
    BEIGE("", ""),
    BROWN("", "");
    
    private String colorStr;
    private String colorName;

    public String getColorName() {
        return this.colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    private ColorCode(String colorName, String colorStr) {
        this.colorStr = colorStr;
        this.colorName = colorName;
    }

    public String getColorStr() {
        return this.colorStr;
    }

    public static ColorCode findColor(String colorName2) {
        for (ColorCode colorCode : ColorCode.values()) {
            if (colorCode.getColorName().equals(colorName2)) {
                return colorCode;
            }
            if (!colorCode.name().equals(colorName2)) continue;
            return colorCode;
        }
        return null;
    }
}
