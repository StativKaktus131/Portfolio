package com.main;

import com.io.KeyInput;
import com.math.PolarVector;
import com.objects.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) { new Main(); }

    public static final int WIDTH = 900;
    public static final int HEIGHT = 900;
    public static final int RADIUS = 220;           // radius of the 'ground' circle
    private final int CIRCLE_STROKE_WIDTH = 14;
    public static final Random RNG = new Random();


    public static boolean gameOver = true;

    // prevents calling `ObstacleType.values()` everytime (optimisation)
    private final ObstacleType[] types = ObstacleType.values();

    // keeps track of the player (used for distance when spawning spikes)
    private final Player player;

    // score variables
    public static int score = 0;
    private static int highscore = 0;

    // font variables
    private final String GAME_OVER_TEXT = "GAME OVER";
    private final String RESTART_TEXT = "RESTART? [Y/N]";
    private final Font GAME_OVER_FONT = new Font("arial", Font.BOLD, 50);
    private final Font SCORE_FONT = new Font("consolas", Font.PLAIN, 25);



    Main() {
        // read highscore
        highscore = getHighscore();

        // creating game window
        new GameWindow("Java Sphere JnR Prototype", WIDTH, HEIGHT);

        // start object handling
        new GameObjectHandler();


        GameObjectHandler.addObjects(
                player = new Player("player", new PolarVector(RADIUS ,0))
        );

        // add renderables
        GameWindow.addRenderable(this::drawScores);
        GameWindow.addRenderable(this::drawCircle);
        GameWindow.addRenderable(this::drawGameOverScreen);

        // start spike spawning
        new Thread(this::spawnObstacle).start();

        // add restart request
        KeyInput.keyCallbacks.add(this::restartRequest);
    }



    private void spawnObstacle() {

        // sleep first so obstacle won't be spawned immediately
        try {
            Thread.sleep((long) Math.max(2000f - GameWindow.gameTime() * 17.5f, 1000f));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // spawn random obstacle
        if (!Main.gameOver)
            types[RNG.nextInt(types.length)].spawn(player);

        // call method recursively
        spawnObstacle();
    }


    private void drawCircle(Graphics g) {
        // outer circle
        g.setColor(Color.BLACK);
        g.fillOval(WIDTH / 2 - RADIUS, HEIGHT / 2 - RADIUS, RADIUS * 2, RADIUS * 2);

        // inner circle
        g.setColor(Color.WHITE);
        g.fillOval(WIDTH / 2 - (RADIUS - CIRCLE_STROKE_WIDTH), HEIGHT / 2 - (RADIUS - CIRCLE_STROKE_WIDTH), (RADIUS - CIRCLE_STROKE_WIDTH) * 2, (RADIUS - CIRCLE_STROKE_WIDTH) * 2);
    }


    private void restartRequest(KeyEvent e, int state) {
        // only check input if game over (guard case)
        if (!Main.gameOver)
            return;

        if (state == KeyInput.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_Y:
                    GameWindow.restartTimer();

                    // remove all spikes
                    GameObjectHandler.getObjects().stream().filter(o -> o.getTag().equals("spike")).collect(Collectors.toList()).forEach(GameObjectHandler::removeObject);
                    // reset player position
                    player.getPosition().angle = 0;
                    // restart game
                    gameOver = false;

                    break;
                case KeyEvent.VK_N:
                    System.exit(0);
                    break;
            }
        }
    }


    private void drawGameOverScreen(Graphics g) {
        // only draw if game over (guard case)
        if (!Main.gameOver)
            return;

        // fill screen with transparent overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WIDTH, HEIGHT);


        g.setColor(Color.WHITE);
        g.setFont(GAME_OVER_FONT);
        int width = g.getFontMetrics(GAME_OVER_FONT).stringWidth(GAME_OVER_TEXT);
        g.drawString(GAME_OVER_TEXT, WIDTH / 2 - width / 2, 100);
        width = g.getFontMetrics(GAME_OVER_FONT).stringWidth(RESTART_TEXT);
        g.drawString(RESTART_TEXT, WIDTH / 2 - width / 2, 800);
    }

    private void drawScores(Graphics g) {
        if (!Main.gameOver)

        g.setColor(Color.BLACK);
        g.setFont(SCORE_FONT);

        // draw (high-)score with 8 fixed characters (leading zeros)
        g.drawString(String.format("   %08d", score), 10, 40);
        g.drawString(String.format("HI %08d", highscore), 10, 65);
    }

    public static void endGame() {
        if (score > highscore) {
            highscore = score;
            setHighscore();
        }
        gameOver = true;
    }

    private static int getHighscore() {
        try {
            Scanner sc = new Scanner(new File("res/highscore.txt"));

            // store hs so scanner can be closed
            int hs = sc.nextInt();
            sc.close();
            return hs;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void setHighscore() {
        try {
            OutputStream o = new FileOutputStream("res/highscore.txt");
            o.write(String.valueOf(score).getBytes());
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
