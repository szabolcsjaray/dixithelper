package com.jeec.game.log;

import java.util.ArrayList;
import java.util.List;

import com.jeec.game.Game;
import com.jeec.game.GameState;
import com.jeec.game.Player;

public class GameLog {

    private Game game = null;
    private List<RoundLog> rounds = new ArrayList<>();
    private List<SavedPlayer> players = new ArrayList<>();
    public List<SavedPlayer> getPlayers() {
        return players;
    }

    public List<RoundLog> getRounds() {
        return rounds;
    }

    private GameState lastGameState;

    public GameLog(Game game) {
        this.game = game;
        this.lastGameState = game.getState();
    }

    private void initSavedPlayers() {
        game.getPlayers().values().forEach((player) -> {
            savePlayer(player);
        });
    }

    private void savePlayer(Player player) {
        players.add(new SavedPlayer(player));

    }

    public void logSelectOwn(final int playerId, final int ownCard) {
        // later
    }

    public void logSelectChoice(final int playerId, final int choiceCard) {
        // later
    }

    private void addNewRound() {
        RoundLog newRound = new RoundLog(getLastRound()+1, game.getTellerId());
        for(Player player : game.getPlayers().values()) {
            newRound.newPlayerRound(player);
        }
        rounds.add(newRound);
    }

    public void logGameStateChange() {
        if (lastGameState==GameState.WAITING_FOR_PLAYERS &&
            game.getState()==GameState.WAITING_FOR_CHOICES) {
            initSavedPlayers();
            addNewRound();
        } else if (lastGameState==GameState.WAITING_FOR_CHOICES &&
                game.getState()==GameState.ROUND_ENDED) {
            RoundLog round = this.rounds.get(rounds.size()-1);
            for(Player player : game.getPlayers().values()) {
                round.updatePlayerRound(player);
            }
        } else if (lastGameState==GameState.ROUND_ENDED &&
                game.getState()==GameState.WAITING_FOR_CHOICES) {
            addNewRound();
        }
        lastGameState = game.getState();
    }

    public RoundLog getRoundLog(int round) {
        for (RoundLog roundLog : rounds) {
            if (roundLog.getRound()==round) {
                return roundLog;
            }
        }
        return null;
    }

    public int getLastRound() {
        return rounds.size();
    }
}
