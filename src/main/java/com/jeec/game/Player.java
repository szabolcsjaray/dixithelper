/*
 * Decompiled with CFR 0.137.
 */
package com.jeec.game;

import com.jeec.game.ColorCode;
import com.jeec.game.PlayerState;

public class Player {
    private String name;
    private int point;
    private int roundPoint;
    private boolean teller;
    private PlayerState state = PlayerState.WAITING_FOR_GAME_START;
    private ColorCode color;
    private String deviceHash;
    private int myCard;
    private int playerOrder;
    private int myChoice;
    private int playerId;
    private static int lastPlayerId = 0;

    static int getNextPlayerId() {
        return lastPlayerId++;
    }

    public Player(String name, ColorCode color, String deviceHash, int order) {
        this.name = name;
        this.setColor(color);
        this.deviceHash = deviceHash;
        this.playerOrder = order;
        this.myCard = -1;
        this.myChoice = -1;
        this.playerId = Player.getNextPlayerId();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return this.point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public boolean isTeller() {
        return this.teller;
    }

    public void setTeller(boolean teller) {
        this.teller = teller;
    }

    public PlayerState getState() {
        return this.state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public ColorCode getColor() {
        return this.color;
    }

    public void setColor(ColorCode color) {
        this.color = color;
    }

    public String getDeviceHash() {
        return this.deviceHash;
    }

    public void setDeviceHash(String deviceHash) {
        this.deviceHash = deviceHash;
    }

    public int getMyCard() {
        return this.myCard;
    }

    public void setMyCard(int myCard) {
        this.myCard = myCard;
    }

    public int getPlayerOrder() {
        return this.playerOrder;
    }

    public void setPlayerOrder(int playerOrder) {
        this.playerOrder = playerOrder;
    }

    public int getMyChoice() {
        return this.myChoice;
    }

    public void setMyChoice(int myChoice) {
        this.myChoice = myChoice;
    }

    public void addPoint(int point) {
        this.point += point;
        this.roundPoint += point;
    }

    public void choose(int myCard, int myChoice) {
        this.myCard = myCard;
        this.myChoice = myChoice;
        this.state = PlayerState.WAITING_FOR_OTHERS_CHOICE;
    }

    public void decreasePlayerOrder() {
        --this.playerOrder;
    }

    public void increasePlayerOrder() {
        ++this.playerOrder;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getRoundPoint() {
        return this.roundPoint;
    }

    public void setRoundPoint(int roundPoint) {
        this.roundPoint = roundPoint;
    }

	public static void resetPlayerIds() {
		lastPlayerId = 0;
	}
}
