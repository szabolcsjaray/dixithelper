/*
 * Decompiled with CFR 0.137.
 * 
 * Could not load the following classes:
 *  com.jeec.game.ColorCode
 *  com.jeec.game.Game
 *  com.jeec.game.GameState
 *  com.jeec.game.Player
 *  com.jeec.game.PlayerState
 *  org.junit.Assert
 *  org.junit.Before
 *  org.junit.Test
 */
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
    private Game game;
    static final String DEV_HASH = "AA";

    @Before
    public void initTests() {
        this.game = new Game();
    }

    @Test
    public void testAddPlayer() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, "Joe", ColorCode.BLUE.name());
        Assert.assertEquals(res, "OK");
    }

    @Test
    public void testAddSamePlayerName() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, "Joe", ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, "Joe", ColorCode.GREEN.name());
        Assert.assertNotEquals("OK", res);
    }

    @Test
    public void testAddPlayerNameWrongColor() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, "Joe", "ilyen sz\u00edn nincs");
        Assert.assertNotEquals("OK", res);
    }

    @Test
    public void testAddPlayerNameWrongDevice() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer("WRONGHASH", "Joe", ColorCode.GREEN.name());
        Assert.assertNotEquals("OK", res);
    }

    @Test
    public void testGetPlayer() {
        this.game.addDevice(DEV_HASH);
        String res = this.game.addPlayer(DEV_HASH, "Joe", ColorCode.BLUE.name());
        Player player = this.game.getPlayer(0);
        System.out.print(player);
        Assert.assertNotNull(player);
        Assert.assertEquals("Joe", player.getName());
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
        String res = this.game.addPlayer(DEV_HASH, "Joe", ColorCode.BLUE.name());
        res = this.game.addPlayer(DEV_HASH, "Jack", ColorCode.GREEN.name());
        res = this.game.addPlayer(DEV_HASH, "Jane", ColorCode.ORANGE.name());
    }

    @Test
    public void testFindPlayer() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        Assert.assertEquals((long)1L, (long)player.getPlayerOrder());
        Assert.assertEquals("Jack", player.getName());
    }

    @Test
    public void testPlayerUp() {
        this.initPlayers();
        Player player = this.game.findPlayerByOrder(1);
        this.game.playerUp(player);
        Assert.assertEquals((long)0L, (long)player.getPlayerOrder());
        Assert.assertEquals("Jack", player.getName());
        Assert.assertEquals("Joe", this.game.findPlayerByOrder(1).getName());
    }

    @Test
    public void testGameStarted() {
        this.initPlayers();
        this.game.nextRound();
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(2).getState());
        Assert.assertEquals(true, this.game.getPlayer(0).isTeller());
    }

    @Test
    public void testGamePlayerChoose() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setPlayerChoice("Jack", 1, 2);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_CHOICE, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(2).getState());
    }

    @Test
    public void testGamePlayerChooseOwn() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setPlayerChoice("Jack", 1, 1);
        Assert.assertNotEquals("OK", res);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(2).getState());
    }

    @Test
    public void testGamePlayerAndTellerChoose() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setPlayerChoice("Jack", 1, 2);
        String res = this.game.setPlayerChoice("Joe", 0, -1);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_CHOICE, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_CHOICE, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(2).getState());
    }

    @Test
    public void testGameConflictingChoice() {
        this.initPlayers();
        this.game.nextRound();
        this.game.setPlayerChoice("Jack", 1, 2);
        String res = this.game.setPlayerChoice("Joe", 1, -1);
        Assert.assertNotEquals("OK", res);
        Assert.assertEquals(GameState.CONFLICTING_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(2).getState());
    }

    @Test
    public void testGameConflictingChoiceCorrected() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setPlayerChoice("Jack", 1, 2);
        res = this.game.setPlayerChoice("Joe", 1, -1);
        Assert.assertNotEquals("OK", res);
        Assert.assertEquals(GameState.CONFLICTING_CHOICES, this.game.getState());
        res = this.game.setPlayerChoice("Jack", 0, 2);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.WAITING_FOR_CHOICES, this.game.getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.WAITING_FOR_OTHERS_CHOICE, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.GAME_WAITING_FOR_MY_CHOICE, this.game.getPlayer(2).getState());
    }

    @Test
    public void testWholeRound() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setPlayerChoice("Joe", 1, -1);
        res = this.game.setPlayerChoice("Jack", 0, 2);
        res = this.game.setPlayerChoice("Jane", 2, 1);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.ROUND_ENDED, this.game.getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(2).getState());
        Assert.assertEquals((long)3L, (long)this.game.getPlayer(0).getPoint());
        Assert.assertEquals((long)0L, (long)this.game.getPlayer(1).getPoint());
        Assert.assertEquals((long)4L, (long)this.game.getPlayer(2).getPoint());
    }

    @Test
    public void testRoundEveryoneFoundOut() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setPlayerChoice("Joe", 1, -1);
        res = this.game.setPlayerChoice("Jack", 0, 1);
        res = this.game.setPlayerChoice("Jane", 2, 1);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.ROUND_ENDED, this.game.getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(2).getState());
        Assert.assertEquals((long)0L, (long)this.game.getPlayer(0).getPoint());
        Assert.assertEquals((long)2L, (long)this.game.getPlayer(1).getPoint());
        Assert.assertEquals((long)2L, (long)this.game.getPlayer(2).getPoint());
    }

    @Test
    public void testRoundNobodyFoundOut() {
        this.initPlayers();
        this.game.nextRound();
        String res = this.game.setPlayerChoice("Joe", 1, -1);
        res = this.game.setPlayerChoice("Jack", 0, 2);
        res = this.game.setPlayerChoice("Jane", 2, 0);
        Assert.assertEquals("OK", res);
        Assert.assertEquals(GameState.ROUND_ENDED, this.game.getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(0).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(1).getState());
        Assert.assertEquals(PlayerState.ROUND_ENDED, this.game.getPlayer(2).getState());
        Assert.assertEquals((long)0L, (long)this.game.getPlayer(0).getPoint());
        Assert.assertEquals((long)1L, (long)this.game.getPlayer(1).getPoint());
        Assert.assertEquals((long)1L, (long)this.game.getPlayer(2).getPoint());
    }
}