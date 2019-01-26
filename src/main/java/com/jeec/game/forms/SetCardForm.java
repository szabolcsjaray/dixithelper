/*
 * Decompiled with CFR 0.137.
 */
package com.jeec.game.forms;

public class SetCardForm {
    private int ownCard;
    private int guessCard;
    private String player;
    private int playerId;

    public int getOwnCard() {
        return this.ownCard;
    }

    public void setOwnCard(int ownCard) {
        this.ownCard = ownCard;
    }

    public int getGuessCard() {
        return this.guessCard;
    }

    public void setGuessCard(int guessCard) {
        this.guessCard = guessCard;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "Player:" + this.player + ", own card:" + this.ownCard + ", guess card:" + this.guessCard;
    }

}
