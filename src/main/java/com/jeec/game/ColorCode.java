package com.jeec.game;

public enum ColorCode {
    RED("Piros", "#c6040e", true),
    BLUE("K\u00e9k", "rgb(12, 75, 176)", true),
    ORANGE("Narancs", "#f7741d"),
    GREEN("Z\u00f6ld", "#029105", true),
    PINK("R\u00f3zsasz\u00edn", "#f263c9"),
    WHITE("Feh\u00e9r", "#FFFFFF"),
    YELLOW("S\u00e1rga", "yellow"),
    LIGHTBLUE("Világoskék", "#87b5ff"),
    //CLARET("bordó", "#960d09"),
    LIGHTGREEN("Világoszöld", "#73ff60"),
    GRAY("Szürke", "#878787"),
    //MAGENTA("Magenta", "#d80097"),
    PURPLE("Lila", "#8c00b7", true),
    //BEIGE("Bézs", "#ffdf89"),
    BROWN("Barna", "#844800", true);

    private String colorStr;
    private String colorName;
    private boolean whiteText;

    public String getColorName() {
        return this.colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    private ColorCode(String colorName, String colorStr) {
        this.colorStr = colorStr;
        this.colorName = colorName;
        this.setWhiteText(false);
    }

    private ColorCode(String colorName, String colorStr, boolean whiteText) {
        this.colorStr = colorStr;
        this.colorName = colorName;
        this.setWhiteText(whiteText);
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

    public boolean isWhiteText() {
        return whiteText;
    }

    public void setWhiteText(boolean whiteText) {
        this.whiteText = whiteText;
    }
}
