package com.objects;

import com.main.GameWindow;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

// handles rendering and updating of all the game objects
public class GameObjectHandler {

    // queueing to prevent ConcurrentModifierException (see `GameWindow`)
    public static Queue<GameObject> gToAdd = new LinkedList<>();
    public static Queue<GameObject> gToPop = new LinkedList<>();

    private static final LinkedList<GameObject> gameObjects = new LinkedList<>();


    public GameObjectHandler() {
        GameWindow.addUpdatable(this::tick);
        GameWindow.addRenderable(this::render);
    }

    public static void addObject(GameObject object) { gToAdd.add(object); }
    public static void addObjects(GameObject... objects) { gToAdd.addAll(Arrays.asList(objects)); }
    public static void removeObject(GameObject object) { gToPop.add(object); }
    public static void removeObjects(GameObject... objects) { gToPop.addAll(Arrays.asList(objects)); }
    public static LinkedList<GameObject> getObjects() { return gameObjects; }


    public void tick() {
        while (!gToAdd.isEmpty())
            gameObjects.add(gToAdd.poll());
        while (!gToPop.isEmpty())
            gameObjects.remove(gToPop.poll());

        gameObjects.forEach(GameObject::tick);
    }

    public void render(Graphics g) {
        gameObjects.forEach(go -> go.render(g));
    }
}