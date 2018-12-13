/*
 * Decompiled with CFR 0.137.
 */
package com.jeec.game;

import com.jeec.game.Player;
import java.util.ArrayList;
import java.util.List;

public class Device {
    private String deviceHash;
    private List<Player> players;

    public Device(String deviceHash) {
        this.deviceHash = deviceHash;
        this.players = new ArrayList<Player>();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public String getDeviceHash() {
        return this.deviceHash;
    }

    public void setDeviceHash(String deviceHash) {
        this.deviceHash = deviceHash;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
