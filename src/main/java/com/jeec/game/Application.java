/*
 * Decompiled with CFR 0.137.
 *
 * Could not load the following classes:
 *  org.springframework.boot.CommandLineRunner
 *  org.springframework.boot.SpringApplication
 *  org.springframework.boot.autoconfigure.SpringBootApplication
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Bean
 */
package com.jeec.game;

import com.jeec.game.GameController;
import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("DixitHelper started.");
            String ip = GameController.getIp();
            String url = "http://" + ip + ":8080";
            if (args.length>0 && args[0].equalsIgnoreCase("dixittest")) {
                System.out.println("Arguments:" + args.length + " : " + args[0]);
                GameController.setTestMode(true);
            }
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                }
                catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url );
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Open this page on yuor mobile device to join game:\n http://" + ip + ":8080/start.html");
        };
    }
}
