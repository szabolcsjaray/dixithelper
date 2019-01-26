package com.jeec.game;

import com.jeec.game.ColorCode;
import com.jeec.game.Game;
import com.jeec.game.GameState;
import com.jeec.game.Player;
import com.jeec.game.PlayerState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGame {
    private static final String PLAYER3 = "Jane";
    private static final String PLAYER2 = "Jack";
    private static final String PLAYER1 = "Joe";
    private Game game;
    static final String DEV_HASH = "AA";
    int player1Id, player2Id, player3Id;

    @Before
    public void initTests() {
        this.game = new Game();
    }

    @Test
    public void testAddPlayer() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        Assert.assertEquals(res, "OK");
        Assert.assertEquals(ColorCode.values().length-1, game.getAvailableColors().size());
        Assert.assertNull(game.getAvailableColors().get(ColorCode.BLUE.name()));
        long pHash = this.game.getPlayer(0).getPlayerHash();
        Assert.assertTrue(pHash>=10000 && pHash<100000);
    }

    @Test
    public void testAvailableColors() {
        Assert.assertEquals(ColorCode.values().length, this.game.getAvailableColors().size());
    }

    @Test
    public void testAddSamePlayerName() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.GREEN.name());
        Assert.assertNotEquals("OK", res);

    }

    @Test
    public void testAddSameColor() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, "Jill", ColorCode.BLUE.name());
        Assert.assertNotEquals("OK", res);

    }

    @Test
    public void testAddPlayerNameWrongColor() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, "ilyen sz\u00edn nincs");
        Assert.assertNotEquals("OK", res);
    }

    @Test
    public void testAddPlayerNameWrongDevice() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer("WRONGHASH", PLAYER1, ColorCode.GREEN.name());
        Assert.assertNotEquals("OK", res);
    }

    @Test
    public void testGetPlayer() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        Player player = this.game.getPlayer(0);
        Assert.assertNotNull(player);
        Assert.assertEquals(PLAYER1, player.getName());
    }

    @Test
    public void testAddDevice() {
        String res = this.game.addDevice("AAA33FF");
        Assert.assertEquals(res, "OK");
    }

    @Test
    public void testAddSameDeviceHash() {
        String res = this.game.addDevice("AAA33FF");
        res = this.game.addDevice("AAA33FF");
        Assert.assertNotEquals(res, "OK");
    }

    private void initPlayers() {
    	this.game = new Game();
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, PLAYER1, ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER2, ColorCode.GREEN.name());
        res = this.game.addPlayer(DEV_HASH, PLAYER3, ColorCode.ORANGE.name());
        player1Id = this.game.getPlayers().get(PLAYER1).getPlayerId();
        player2Id = this.game.getPlayers().get(PLAYER2).getPlayerId();
        player3Id = this.game.getPlayers().get(PLAYER3).getPlayerId();

    }

    @Test
    public void testFindPlayer() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        Assert.assertEquals(1L, player.getPlayerOrder());
        Assert.assertEquals(PLAYER2, player.getName());
    }

    @Test
    public void testPlayerUp() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        this.game.playerUp(player);
        Assert.assertEquals(0L, player.getPlayerOrder());
        Assert.assertEquals(PLAYER2, player.getName());
        Assert.assertEquals(PLAYER1, this.game.findPlayerByOrder(1).getName());
    }

    @Test
    public void testPlayerDown() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        this.game.playerDown(player);
        Assert.assertEquals(2L, player.getPlayerOrder());
        Assert.assertEquals(PLAYER2, player.getName());
        Assert.assertEquals(PLAYER3, this.game.findPlayerByOrder(1).getName());
    }

    @Test
    public void testGameStarted() {
        this.initPlayers();
        this.game.nextRound();
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player3Id).getState());
        Assert.assertEquals(true, this.game.getPlayer(player1Id).isTeller());
    }

    @Test
    public void testGamePlayerSetOwn() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setOwnCard(player2Id, 1);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player3Id).getState());
    }


    @Test
    public void testGamePlayerChoose() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setOwnCard(player2Id, 1);
        this.game.setChoiceCard(PLAYER2, 1, 2);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_CHOICE, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player3Id).getState());
    }

    @Test
    public void testGamePlayerChooseOwn() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setOwnCard(player2Id, 1);
        String res = this.game.setChoiceCard(PLAYER2, 1, 1);
        Assert.assertNotEquals("OK", res);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player3Id).getState());
    }

    @Test
    public void testGamePlayerAndTellerChoose() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setOwnCard(player2Id, 1);
        this.game.setChoiceCard(PLAYER2, 1, 2);
        String res = this.game.setOwnCard(player1Id, 0);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_CHOICE, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_CHOICE, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CARD, this.game.getPlayer(player3Id).getState());
    }

    @Test
    public void testGameConflictingChoice() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setOwnCard(player2Id, 1);
        this.game.setChoiceCard(PLAYER2, 1, 2);
        String res = this.game.setOwnCard(player1Id, 1);
        Assert.assertNotEquals("OK", res);
        Assert.assertEquals(GameState.CONFLICTING_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.CONFLICT_RESET, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.CONFLICT_RESET, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.CONFLICT_RESET, this.game.getPlayer(player3Id).getState());
    }

    @Test
    public void testGameConflictingChoiceCorrected() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setOwnCard(player2Id, 1);
        String res = this.game.setChoiceCard(PLAYER2, 1, 2);
        res = this.game.setOwnCard(player1Id, 1);
        Assert.assertNotEquals("OK", res);
        Assert.assertEquals(GameState.CONFLICTING_CHOICES, this.game.getState());
        res = this.game.setOwnCard(player2Id, 1);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.CONFLICTING_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.CONFLICT_RESET, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.CONFLICT_RESET, this.game.getPlayer(player3Id).getState());
    }

    @Test
    public void testWholeRound() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setOwnCard(player1Id, 1);
        res = this.game.setChoiceCard(PLAYER1, 1, -1);
        res = this.game.setOwnCard(player2Id, 0);
        res = this.game.setChoiceCard(PLAYER2, 0, 2);
        res = this.game.setOwnCard(player3Id, 2);
        res = this.game.setChoiceCard(PLAYER3, 2, 1);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.ROUND_ENDED, this.game.getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player3Id).getState());
        Assert.assertEquals(3L, this.game.getPlayer(player1Id).getPoint());
        Assert.assertEquals(0L, this.game.getPlayer(player2Id).getPoint());
        Assert.assertEquals(4L, this.game.getPlayer(player3Id).getPoint());
    }

    @Test
    public void testRoundEveryoneFoundOut() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setOwnCard(player1Id, 1);
        res = this.game.setChoiceCard(PLAYER1, 1, -1);
        res = this.game.setOwnCard(player2Id, 0);
        res = this.game.setChoiceCard(PLAYER2, 0, 1);
        res = this.game.setOwnCard(player3Id, 2);
        res = this.game.setChoiceCard(PLAYER3, 2, 1);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.ROUND_ENDED, this.game.getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player3Id).getState());
        Assert.assertEquals(0L, this.game.getPlayer(player1Id).getPoint());
        Assert.assertEquals(2L, this.game.getPlayer(player2Id).getPoint());
        Assert.assertEquals(2L, this.game.getPlayer(player3Id).getPoint());
    }

    @Test
    public void testRoundNobodyFoundOut() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setOwnCard(player1Id, 1);
        res = this.game.setChoiceCard(PLAYER1, 1, -1);
        res = this.game.setOwnCard(player2Id, 0);
        res = this.game.setChoiceCard(PLAYER2, 0, 2);
        res = this.game.setOwnCard(player3Id, 2);
        res = this.game.setChoiceCard(PLAYER3, 2, 0);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.ROUND_ENDED, this.game.getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player1Id).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player2Id).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(player3Id).getState());
        Assert.assertEquals(0L, this.game.getPlayer(player1Id).getPoint());
        Assert.assertEquals(1L, this.game.getPlayer(player2Id).getPoint());
        Assert.assertEquals(1L, this.game.getPlayer(player3Id).getPoint());
    }
}
