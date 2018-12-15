/*
 * Decompiled with CFR 0.137.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.ResponseBody
 *  org.springframework.web.bind.annotation.RestController
 */
package com.jeec.game;

import com.jeec.game.Game;
import com.jeec.game.Player;
import com.jeec.game.forms.AddPlayerForm;
import com.jeec.game.forms.SetCardForm;
import io.nayuki.qrcodegen.QrCode;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins={"*"})
public class GameController {
    private String ip;
    private Game game = new Game();

    public static String getIp() {
        String ip;
        try {
            try (DatagramSocket socket = new DatagramSocket();){
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = socket.getLocalAddress().getHostAddress();
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
            ip = e.getMessage();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            ip = e.getMessage();
        }
        return ip;
    }

    @RequestMapping(value={"/"})
    public String index() {
        String ip = GameController.getIp();
        StringBuilder page = new StringBuilder("Dixit Helper.<br> \n"+
            "Nyisd meg ezt a c\u00edmet a jelentkez\u00e9shez: ");
        String address="http://" + ip + ":8080/start.html";
        page.append("<b><a href=\""+address+"\">"+address+"</a></b>");
        page.append("<br>... vagy olvasd be a QR kódot az induló oldal címét, és úgy nyisd meg!<br>");
        final int STARTING_XML_HEADER_LENGTH = 137;
        page.append(new String(createQRImage()).substring(STARTING_XML_HEADER_LENGTH));
        return page.toString();
    }

    @RequestMapping(value={"/qrimage"})
    @ResponseBody
    public byte[] createQRImage() {
        this.ip = GameController.getIp();
        QrCode qr0 = QrCode.encodeText(this.ip, QrCode.Ecc.MEDIUM);
        String imageSVG = qr0.toSvgString(1);
        //System.out.println(imageSVG);
        return imageSVG.getBytes();
    }

    @RequestMapping(value={"/connect/{deviceHash}"})
    public String connectDevice(@PathVariable(value="deviceHash") String deviceHash) {
        return this.game.addDevice(deviceHash);
    }

    @PostMapping(value={"/addPlayer"})
    public ResponseEntity<String> addPlayer(@RequestBody AddPlayerForm addPlayerForm) {
        String result = this.game.addPlayer(addPlayerForm.getDeviceHash(), addPlayerForm.getPlayerName(), addPlayerForm.getColorName());
        if ("OK".equals(result)) {
            System.out.println("Added Player:" + addPlayerForm);
            return new ResponseEntity((Object)"OK", HttpStatus.OK);
        }
        System.out.println("Problem while adding Player:" + addPlayerForm);
        return new ResponseEntity((Object)result, HttpStatus.CONFLICT);
    }

    @RequestMapping(value={"/getPlayer/{playerId}"})
    public ResponseEntity<Player> getPlayer(@PathVariable(value="playerId") int playerId) {
        Player player = this.game.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity((Object)player, HttpStatus.NOT_FOUND);
        }
        ResponseEntity response = new ResponseEntity((Object)player, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/playerUp/{playerId}"})
    public ResponseEntity<Player> playerUp(@PathVariable(value="playerId") int playerId) {
        Player player = this.game.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity((Object)player, HttpStatus.NOT_FOUND);
        }
        this.game.playerUp(player);
        ResponseEntity response = new ResponseEntity((Object)player, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/conflictreset/{playerId}"})
    public ResponseEntity<Integer> conflictReset(@PathVariable(value="playerId") int playerId) {
        Player player = this.game.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity((Object)new Integer(-1), HttpStatus.NOT_FOUND);
        }
        int resetNum = this.game.conflictReset(player);
        ResponseEntity response = new ResponseEntity((Object)resetNum, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/nextRound"})
    public ResponseEntity<Integer> nextRound() {
        int round = this.game.nextRound();
        ResponseEntity response = new ResponseEntity((Object)round, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/getgame"})
    public ResponseEntity<Game> getGame() {
        ResponseEntity response = new ResponseEntity((Object)this.game, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/getgameversion"})
    public ResponseEntity<Integer> getGameVersion() {
        Integer res = this.game.getStateVersion();
        ResponseEntity response = new ResponseEntity((Object)res, HttpStatus.OK);
        return response;
    }

    @RequestMapping(value={"/startgame"})
    public ResponseEntity<Integer> startGame() {
        Integer res = this.game.startGame();
        ResponseEntity response = new ResponseEntity((Object)res, HttpStatus.OK);
        return response;
    }

    @PostMapping(value={"/setcards"})
    public ResponseEntity<String> setCards(@RequestBody SetCardForm setCardForm) {
        String result = this.game.setPlayerChoice(setCardForm.getPlayer(), setCardForm.getOwnCard(), setCardForm.getGuessCard());
        if ("OK".equals(result)) {
            System.out.println("Player Choice:" + setCardForm);
            return new ResponseEntity((Object)"OK", HttpStatus.OK);
        }
        System.out.println("Problem with choice:" + setCardForm);
        return new ResponseEntity((Object)result, HttpStatus.CONFLICT);
    }
}
