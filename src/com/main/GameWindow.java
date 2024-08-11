package com.main;

import com.io.KeyInput;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

// The window in which the game will be rendered (using swing and awt)
public class GameWindow extends JFrame implements Runnable {

    private final GamePanel panel;      // game context
    private boolean running;            // breaks game loop when set to false

    private static long startTime;      // defines the time (in millis.) when the game started.

    // queueing to prevent ConcurrentModifierException
    private final static Queue<Consumer<Graphics>> rToAdd = new LinkedList<>();
    private final static Queue<Consumer<Graphics>> rToPop = new LinkedList<>();
    private final static Queue<Runnable> uToAdd = new LinkedList<>();
    private final static Queue<Runnable> uToPop = new LinkedList<>();

    private final LinkedList<Consumer<Graphics>> renderables = new LinkedList<>();
    private final LinkedList<Runnable> updatables = new LinkedList<>();


    public GameWindow(String title, int width, int height) {
        Dimension size = new Dimension(width, height);

        // config JFrame
        setTitle(title);
        add(panel = new GamePanel(size));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        running = true;

        // start game loop
        new Thread(this).start();
    }



    public static void addRenderable(Consumer<Graphics> renderable) {
        rToAdd.add(renderable);
    }

    public static void removeRenderable(Consumer<Graphics> renderable) {
        rToPop.add(renderable);
    }

    public static void addUpdatable(Runnable updatable) {
        uToAdd.add(updatable);
    }

    public static void removeUpdatable(Runnable updatable) {
        uToPop.add(updatable);
    }



    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        long lastTime = System.nanoTime();
        double tps = 60.0;
        double ns = 1000000000.0 / tps;
        double delta = 0.0;

        // game loop running at a fixed rate (60 tps / fps)
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta > 1) {
                delta--;

                tick();
                panel.repaint();
            }
        }
    }


    // update the game
    private void tick() {
        // update callback lists
        while (!uToAdd.isEmpty())
            updatables.add(uToAdd.poll());
        while (!uToPop.isEmpty())
            updatables.remove(uToAdd.poll());

        if (Main.gameOver)
            return;


        updatables.forEach(Runnable::run);
    }


    // render the game
    private void render(Graphics g) {
        // update callback lists
        while (!rToAdd.isEmpty())
            renderables.add(rToAdd.poll());
        while (!rToPop.isEmpty())
            renderables.remove(rToAdd.poll());

        renderables.forEach(r -> r.accept(g));
    }


    public static void restartTimer() {
        startTime = System.currentTimeMillis();
    }


    // Game 'Context'
    private class GamePanel extends JPanel {

        public GamePanel(Dimension size) {
            // set size of panel
            setSize(size);
            setPreferredSize(size);

            addKeyListener(new KeyInput());
            setFocusable(true);
            requestFocus();
        }

        @Override
        public void paintComponent(Graphics g) {
            // call render method every time panel should be redrawn
            Graphics2D g2d = (Graphics2D) g;

            // enable antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // draw background
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            render(g);
        }
    }

    public static float gameTime() {
        return (System.currentTimeMillis() - startTime) * 1E-3f;
    }
}
