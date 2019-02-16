package com.jeec.game.log;

import java.util.ArrayList;
import java.util.List;

import com.jeec.game.Game;
import com.jeec.game.GameState;
import com.jeec.game.Player;

public class GameLog {

    private Game game = null;
    private List<RoundLog> rounds = new ArrayList<>();
    private GameState lastGameState;

    public GameLog(Game game) {
        this.game = game;
        this.lastGameState = game.getState();
    }

    public void logSelectOwn(final int playerId, final int ownCard) {

    }

    public void logSelectChoice(final int playerId, final int choiceCard) {

    }

    public void logGameStateChange() {
        if (lastGameState==GameState.WAITING_FOR_PLAYERS &&
            game.getState()==GameState.WAITING_FOR_CHOICES) {
            RoundLog newRound = new RoundLog(1, 0);
            for(Player player : game.getPlayers().values()) {
                newRound.newPlayerRound(player);
            }
            rounds.add(newRound);
        } else if (lastGameState==GameState.WAITING_FOR_CHOICES &&
                game.getState()==GameState.ROUND_ENDED) {
            RoundLog round = this.rounds.get(rounds.size()-1);
            for(Player player : game.getPlayers().values()) {
                round.updatePlayerRound(player);
            }
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

    public Object getLastRound() {
        return rounds.size();
    }
}
