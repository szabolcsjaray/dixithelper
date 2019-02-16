package com.jeec.game.log;

import java.util.List;

import com.jeec.game.Player;

import java.util.ArrayList;

public class RoundLog {
    private int round;
    private int teller;
    private List<PlayerRound> playerRounds = new ArrayList<>();

    public RoundLog(int round, int teller) {
        this.round = round;
        this.setTeller(teller);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void newPlayerRound(Player player) {
        PlayerRound playerRound = new PlayerRound(player);
        playerRounds.add(playerRound);
    }

    public void addPlayerRound(final PlayerRound playerRound) {
        playerRounds.add(playerRound);
    }

    public List<PlayerRound> getPlayerRounds() {
        return playerRounds;
    }

    public int getTeller() {
        return teller;
    }

    public void setTeller(int teller) {
        this.teller = teller;
    }

    public PlayerRound getPlayerRound(int player1Id) {
        for(PlayerRound playerRound : playerRounds) {
            if (playerRound.getPlayerId()==player1Id) {
                return playerRound;
            }
        }
        return null;
    }

    public void updatePlayerRound(Player player) {
        PlayerRound playerRound = getPlayerRound(player.getPlayerId());
        playerRound.update(player);
    }
}