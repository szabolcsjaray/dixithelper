package com.jeec.game;

import com.jeec.game.ColorCode;
import com.jeec.game.Device;
import com.jeec.game.GameState;
import com.jeec.game.Player;
import com.jeec.game.PlayerState;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Game {
    private int stateVersion = 0;
    private GameState state = GameState.WAITING_FOR_PLAYERS;
    private Map<String, Player> players = new HashMap<String, Player>();
    private Map<String, Device> devices = new HashMap<String, Device>();
    private Map<String, String> availableColors = new HashMap<>();

	private int round;
    int tellerOrder = -1;
    private boolean playersLocked;
    private TreeMap<Integer, String> playerNames = new TreeMap();

    public Game() {
    	Player.resetPlayerIds();
        initAllColors();
    }

    private void initAllColors() {
		for (ColorCode color: ColorCode.values()) {
			availableColors.put(color.name(), color.getColorStr());
		}
	}

    public Map<String, String> getAvailableColors() {
        return availableColors;
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public String addPlayer(String deviceHash, String playerName, String colorName) {
        if (ColorCode.findColor(colorName) == null) {
            return "Nincs ilyen sz\u00edn.";
        }
        String checkResult = this.checkIfNameOrColorExists(playerName, colorName);
        if (!checkResult.isEmpty()) {
            return checkResult;
        }
        checkResult = this.checkIfDeviceExists(deviceHash);
        if (!checkResult.isEmpty()) {
            return checkResult;
        }
        Player player = new Player(playerName, ColorCode.findColor(colorName), deviceHash, this.players.size());
        uniquePlayerHash(player);
        this.availableColors.remove(ColorCode.findColor(colorName).name());
        this.players.put(playerName, player);
        this.devices.get(deviceHash).addPlayer(player);
        this.playerNames.put(player.getPlayerOrder(), playerName);
        ++this.stateVersion;
        return "OK";
    }

    private void uniquePlayerHash(Player playerToCheck) {
        long sameHash;
        long pHash = playerToCheck.getPlayerHash();
        do {
            playerToCheck.setPlayerHash(Player.createPlayerHash());
            sameHash = this.players.values().stream().filter(player -> pHash==player.getPlayerHash()).count();
        } while (sameHash>1);
    }

    private String checkIfDeviceExists(String deviceHash) {
        if (this.devices.containsKey(deviceHash)) {
            return "";
        }
        return "Ez az eszk\u00f6z (mobil, tablet) m\u00e9g nem lett csatlakoztatva.";
    }

    private String checkIfNameOrColorExists(String playerName, String colorName) {
        for (String name : this.players.keySet()) {
            if (name.equals(playerName)) {
                return "A j\u00e1t\u00e9kos n\u00e9v ("+playerName+") m\u00e1r foglalt.";
            }
            if (this.players.get(name).getColor().getColorName().equals(ColorCode.findColor(colorName).getColorName())) {
                return "A sz\u00edn m\u00e1r foglalt.";
            }
        }
        return "";
    }

    public String addDevice(String deviceHash) {
        if (this.findDevice(deviceHash)) {
            return "Az eszk\u00f6z m\u00e1r csatlakoztatva van";
        }
        ++this.stateVersion;
        this.devices.put(deviceHash, new Device(deviceHash));
        return "OK";
    }

    private boolean findDevice(String deviceHash) {
        return this.devices.keySet().contains(deviceHash);
    }

    public Player getPlayer(int playerId) {
        int id = 0;
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getPlayerId() == playerId) {
                return this.players.get(name);
            }
            ++id;
        }
        return null;
    }

    public Map<String, Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public Map<String, Device> getDevices() {
        return this.devices;
    }

    public void setDevices(Map<String, Device> devices) {
        this.devices = devices;
    }

    public int getStateVersion() {
        return this.stateVersion;
    }

    public void setStateVersion(int stateVersion) {
        this.stateVersion = stateVersion;
    }

    public void increaseStateVersion() {
        ++this.stateVersion;
    }

    public int nextRound() {
        this.state = GameState.WAITING_FOR_CHOICES;
        ++this.tellerOrder;
        if (this.tellerOrder >= this.players.size()) {
            this.tellerOrder = 0;
        }
        for (String playerName : this.players.keySet()) {
            Player player = this.players.get(playerName);
            player.setState(PlayerState.GAME_WAITING_FOR_MY_CHOICE);
            player.setMyChoice(-1);
            player.setMyCard(-1);
            player.setTeller(player.getPlayerOrder() == this.tellerOrder);
        }
        ++this.round;
        ++this.stateVersion;
        return this.round;
    }

    public String setPlayerChoice(String playerName, int myCard, int myChoice) {
        if (myCard == myChoice) {
            return "Nem v\u00e1lasythatod a saj\u00e1todat.";
        }
        if (!this.players.containsKey(playerName)) {
            return "Nincs ilyen j\u00e1t\u00e9kos.";
        }
        Player player = this.players.get(playerName);
        player.choose(myCard, myChoice);
        if (this.checkConflictingMyCards()) {
            this.state = GameState.CONFLICTING_CHOICES;
            for (String name : this.players.keySet()) {
                this.players.get(name).setState(PlayerState.CONFLICT_RESET);
                this.players.get(name).setMyCard(-1);
                this.players.get(name).setMyChoice(-1);
            }
            ++this.stateVersion;
            return "T\u00f6bben jel\u00f6lt\u00e9tek ugyanazt saj\u00e1tnak, ellen\u0151rizz\u00e9tek le!";
        }
        this.checkAndSetRoundEnd();
        ++this.stateVersion;
        return "OK";
    }

    private boolean checkConflictingMyCards() {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getState() != PlayerState.WAITING_FOR_OTHERS_CHOICE) continue;
            for (String otherName : this.players.keySet()) {
                Player otherPlayer = this.players.get(otherName);
                if (otherPlayer.getState() != PlayerState.WAITING_FOR_OTHERS_CHOICE || player == otherPlayer || player.getMyCard() != otherPlayer.getMyCard()) continue;
                return true;
            }
        }
        return false;
    }

    private void checkAndSetRoundEnd() {
        Player player;
        for (String name : this.players.keySet()) {
            player = this.players.get(name);
            if (player.getState() != PlayerState.GAME_WAITING_FOR_MY_CHOICE &&
                player.getState() != PlayerState.CONFLICT_RESET) continue;
            this.state = GameState.WAITING_FOR_CHOICES;
            return;
        }
        this.state = GameState.ROUND_ENDED;
        ++this.stateVersion;
        for (String name : this.players.keySet()) {
            player = this.players.get(name);
            player.setState(PlayerState.ROUND_ENDED);
        }
        this.calculatePoints();
    }

    private void calculatePoints() {
        this.resetRoundPoints();
        Player teller = this.findTeller();
        if (this.checkIfEveryPlayerFoundOut(teller)) {
            this.everybodyGotTwoPointsExpectTeller(teller);
            return;
        }
        if (this.atLeastSomeoneFoundOut(teller)) {
            teller.addPoint(3);
        }
        this.addPointsForThoseWhoFoundOut(teller);
        this.addPointsForOtherPlayersGuess(teller);
    }

    private void resetRoundPoints() {
        for (String name : this.players.keySet()) {
            this.players.get(name).setRoundPoint(0);
        }
    }

    private void addPointsForOtherPlayersGuess(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() == teller.getMyCard()) continue;
            Player otherPlayer = this.findPlayerByCard(player.getMyChoice());
            otherPlayer.addPoint(1);
        }
    }

    private Player findPlayerByCard(int card) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getMyCard() != card) continue;
            return player;
        }
        return null;
    }

    private void addPointsForThoseWhoFoundOut(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() != teller.getMyCard()) continue;
            player.addPoint(3);
        }
    }

    private boolean atLeastSomeoneFoundOut(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() != teller.getMyCard()) continue;
            return true;
        }
        return false;
    }

    private void everybodyGotTwoPointsExpectTeller(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller) continue;
            player.addPoint(2);
        }
    }

    private boolean checkIfEveryPlayerFoundOut(Player teller) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player == teller || player.getMyChoice() == teller.getMyCard()) continue;
            return false;
        }
        return true;
    }

    private Player findTeller() {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getPlayerOrder() != this.tellerOrder) continue;
            return player;
        }
        return null;
    }

    public void playerUp(Player player) {
        if (!this.playersLocked) {
            Player otherPlayer;
            this.playersLocked = true;
            if (player.getPlayerOrder() > 0 && (otherPlayer = this.findPlayerByOrder(player.getPlayerOrder() - 1)) != null) {
                int pOrder = player.getPlayerOrder();
                player.decreasePlayerOrder();
                otherPlayer.increasePlayerOrder();
                this.playerNames.remove(pOrder);
                this.playerNames.remove(pOrder - 1);
                this.playerNames.put(new Integer(pOrder - 1), player.getName());
                this.playerNames.put(new Integer(pOrder), otherPlayer.getName());
                ++this.stateVersion;
            }
            this.playersLocked = false;
        }
    }

    public void playerDown(Player player) {
        if (!this.playersLocked) {
            Player otherPlayer;
            this.playersLocked = true;
            if (player.getPlayerOrder() < playerNames.size()-1 && (otherPlayer = this.findPlayerByOrder(player.getPlayerOrder() + 1)) != null) {
                int pOrder = player.getPlayerOrder();
                player.increasePlayerOrder();
                otherPlayer.decreasePlayerOrder();
                this.playerNames.remove(pOrder);
                this.playerNames.remove(pOrder + 1);
                this.playerNames.put(new Integer(pOrder + 1), player.getName());
                this.playerNames.put(new Integer(pOrder), otherPlayer.getName());
                ++this.stateVersion;
            }
            this.playersLocked = false;
        }
    }

    public Player findPlayerByOrder(int order) {
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            if (player.getPlayerOrder() != order) continue;
            return player;
        }
        return null;
    }

    public int getRound() {
        return this.round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Integer startGame() {
        this.nextRound();
        this.state = GameState.WAITING_FOR_CHOICES;
        Player teller = this.findPlayerByOrder(0);
        teller.setTeller(true);
        for (String name : this.players.keySet()) {
            Player player = this.players.get(name);
            player.setState(PlayerState.GAME_WAITING_FOR_MY_CHOICE);
        }
        ++this.stateVersion;
        return this.players.size();
    }

    public int conflictReset(Player player) {
        Player pl;
        int resetCount = 0;
        player.setState(PlayerState.CONFLICT_RESET);
        for (String name : this.players.keySet()) {
            pl = this.players.get(name);
            if (pl.getState() != PlayerState.CONFLICT_RESET) continue;
            ++resetCount;
        }
        if (resetCount == this.players.size()) {
            for (String name : this.players.keySet()) {
                pl = this.players.get(name);
                pl.setState(PlayerState.GAME_WAITING_FOR_MY_CHOICE);
            }
            this.setState(GameState.WAITING_FOR_CHOICES);
            ++this.stateVersion;
        }
        return resetCount;
    }
}
