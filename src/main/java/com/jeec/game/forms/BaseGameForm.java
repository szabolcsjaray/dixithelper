/*
 * Decompiled with CFR 0.137.
 */
package com.jeec.game.forms;

public class BaseGameForm {
    private String deviceHash;
    private String playerName;

    public String getDeviceHash() {
        return this.deviceHash;
    }

    public void setDeviceHash(String deviceHash) {
        this.deviceHash = deviceHash;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String toString() {
        return "Device: " + this.getDeviceHash() + ", player: " + this.getPlayerName();
    }
}
