/*
 * Decompiled with CFR 0.137.
 */
package com.jeec.game.forms;

import com.jeec.game.forms.BaseGameForm;

public class AddPlayerForm
extends BaseGameForm {
    private String colorName;

    public String getColorName() {
        return this.colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public String toString() {
        return super.toString() + ", color:" + this.getColorName();
    }
}
