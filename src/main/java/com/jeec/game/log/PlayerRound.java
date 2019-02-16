package com.jeec.game.log;

import com.jeec.game.Player;

public class PlayerRound {
    private int own;
    private int choice;
    private int point;
    private int roundPoint;
    private int playerId;

    PlayerRound(final Player player) {
        update(player);
    }

    public void update(final Player player) {
        this.setOwn(player.getMyCard());
        this.setChoice(player.getMyChoice());
        this.setPoint(player.getPoint());
        this.setRoundPoint(player.getRoundPoint());
        this.playerId = player.getPlayerId();
    }

    public int getOwn() {
        return own;
    }

    public void setOwn(int own) {
        this.own = own;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getRoundPoint() {
        return roundPoint;
    }

    public void setRoundPoint(int roundPoint) {
        this.roundPoint = roundPoint;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
