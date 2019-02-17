package com.jeec.game;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jeec.game.log.GameLog;
import com.jeec.game.log.PlayerRound;
import com.jeec.game.log.RoundLog;

public class TestGameLog {
    private static final String DEV_HASH = "dev12345";
    private static final String PLAYER1 = "PLAYER1";
    private static final String PLAYER2 = "PLAYER2";
    private static final String PLAYER3 = "PLAYER3";

    int player1Id;
    int player2Id;
    int player3Id;

    Game game;
    GameLog gameLog;

    private void initPlayers() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER2, ColorCode.GREEN.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER3, ColorCode.ORANGE.name());
        player1Id = this.game.getPlayers().get(PLAYER1).getPlayerId();
        player2Id = this.game.getPlayers().get(PLAYER2).getPlayerId();
        player3Id = this.game.getPlayers().get(PLAYER3).getPlayerId();

    }

    @Before
    public void initTests() {
        this.game = new Game();
        this.gameLog = game.getGameLog();
        initPlayers();
    }

    @Test
    public void testGameLog() {
        Assert.assertEquals(0, gameLog.getLastRound());
    }

    private void checkPlayerRound(final PlayerRound playerRound,
            final int own,
            final int choice,
            final int point,
            final int roundPoint) {
        Assert.assertNotNull(playerRound);
        Assert.assertEquals(own,playerRound.getOwn());
        Assert.assertEquals(choice,playerRound.getChoice());
        Assert.assertEquals(point,playerRound.getPoint());
        Assert.assertEquals(roundPoint,playerRound.getRoundPoint());
    }

    @Test
    public void testGameStarted() {
        game.startGame();
        Assert.assertEquals(1, gameLog.getLastRound());
        RoundLog roundLog = gameLog.getRoundLog(1);
        Assert.assertNotNull(roundLog);
        Assert.assertEquals(game.getPlayers().keySet().size(), roundLog.getPlayerRounds().size());
        Assert.assertEquals(0, roundLog.getTeller());
        PlayerRound playerRound = roundLog.getPlayerRound(player1Id);
        checkPlayerRound(playerRound, -1, -1, 0, 0);
        playerRound = roundLog.getPlayerRound(player2Id);
        checkPlayerRound(playerRound, -1, -1, 0, 0);
        playerRound = roundLog.getPlayerRound(player3Id);
        checkPlayerRound(playerRound, -1, -1, 0, 0);
    }

    @Test
    public void testSavedPlayers() {
        game.startGame();
        Assert.assertEquals(game.getPlayers().keySet().size(), gameLog.getPlayers().size());
        gameLog.getPlayers().forEach((savedPlayer) -> {
            Player player = game.getPlayer(savedPlayer.getPlayerId());
            Assert.assertNotNull(player);
            Assert.assertEquals(player.getName(), savedPlayer.getName());
            Assert.assertEquals(player.getColorStr(), savedPlayer.getColor());
        });
    }

    @Test
    public void testGameFirstRoundEnded() {
        game.startGame();

        game.setOwnCard(player1Id, 0);
        //game.setChoiceCard(PLAYER1, 0, 1);
        game.setOwnCard(player2Id, 1);
        game.setChoiceCard(PLAYER2, 1, 2);
        game.setOwnCard(player3Id, 2);
        game.setChoiceCard(PLAYER3, 2, 1);


        Assert.assertEquals(1, gameLog.getLastRound());
        RoundLog roundLog = gameLog.getRoundLog(1);
        Assert.assertNotNull(roundLog);
        Assert.assertEquals(game.getPlayers().keySet().size(), roundLog.getPlayerRounds().size());
        Assert.assertEquals(0, roundLog.getTeller());
        PlayerRound playerRound = roundLog.getPlayerRound(player1Id);
        checkPlayerRound(playerRound, 0, -1, 0, 0);
        playerRound = roundLog.getPlayerRound(player2Id);
        checkPlayerRound(playerRound, 1, 2, 1, 1);
        playerRound = roundLog.getPlayerRound(player3Id);
        checkPlayerRound(playerRound, 2, 1, 1, 1);
    }

    @Test
    public void testGameTwoRounds() {
        game.startGame();

        game.setOwnCard(player1Id, 0);
        //game.setChoiceCard(PLAYER1, 0, 1);
        game.setOwnCard(player2Id, 1);
        game.setChoiceCard(PLAYER2, 1, 2);
        game.setOwnCard(player3Id, 2);
        game.setChoiceCard(PLAYER3, 2, 1);

        game.nextRound();


        Assert.assertEquals(2, gameLog.getLastRound());
        RoundLog roundLog = gameLog.getRoundLog(2);
        Assert.assertNotNull(roundLog);
        Assert.assertEquals(game.getPlayers().keySet().size(), roundLog.getPlayerRounds().size());
        Assert.assertEquals(1, roundLog.getTeller());

        PlayerRound playerRound = roundLog.getPlayerRound(player1Id);
        checkPlayerRound(playerRound, -1, -1, 0, 0);
        playerRound = roundLog.getPlayerRound(player2Id);
        checkPlayerRound(playerRound, -1, -1, 1, 0);
        playerRound = roundLog.getPlayerRound(player3Id);
        checkPlayerRound(playerRound, -1, -1, 1, 0);
    }

    @Test
    public void testGameTwoRoundEnded() {
        game.startGame();

        game.setOwnCard(player1Id, 0);
        //game.setChoiceCard(PLAYER1, 0, 1);
        game.setOwnCard(player2Id, 1);
        game.setChoiceCard(PLAYER2, 1, 2);
        game.setOwnCard(player3Id, 2);
        game.setChoiceCard(PLAYER3, 2, 1);

        game.nextRound();
        game.setOwnCard(player1Id, 0);
        game.setChoiceCard(PLAYER1, 0, 1);
        game.setOwnCard(player2Id, 1);
        //game.setChoiceCard(PLAYER2, 1, 2);
        game.setOwnCard(player3Id, 2);
        game.setChoiceCard(PLAYER3, 2, 1);

        Assert.assertEquals(2, gameLog.getLastRound());
        RoundLog roundLog = gameLog.getRoundLog(2);
        Assert.assertNotNull(roundLog);
        Assert.assertEquals(game.getPlayers().keySet().size(), roundLog.getPlayerRounds().size());
        Assert.assertEquals(1, roundLog.getTeller());

        PlayerRound playerRound = roundLog.getPlayerRound(player1Id);
        checkPlayerRound(playerRound, 0, 1, 2, 2);
        playerRound = roundLog.getPlayerRound(player2Id);
        checkPlayerRound(playerRound, 1, -1, 1, 0);
        playerRound = roundLog.getPlayerRound(player3Id);
        checkPlayerRound(playerRound, 2, 1, 3, 2);
    }
}
