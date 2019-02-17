package com.jeec.game.log;

import com.jeec.game.Player;

public class SavedPlayer {
    private String name;
    private int playerId;
    private String color;

    public SavedPlayer(Player player) {
        this.name = player.getName();
        this.playerId = player.getPlayerId();
        this.color = player.getColorStr();
    }
    public String getName() {
        return name;
    }
    public int getPlayerId() {
        return playerId;
    }
    public String getColor() {
        return color;
    }


}
