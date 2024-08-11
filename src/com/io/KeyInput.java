package com.io;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.function.BiConsumer;

public class KeyInput extends KeyAdapter {

    // key callback -> easy to use lambda / custom methods to check for key input
    public static LinkedList<BiConsumer<KeyEvent, Integer>> keyCallbacks = new LinkedList<>();

    // magic numbers
    public static final int KEY_PRESSED = 0;
    public static final int KEY_RELEASED = 1;
    public static final int KEY_TYPED = 2;


    // overridden methods from `KeyAdapter` call callback methods
    @Override
    public void keyPressed(KeyEvent e) {
        keyCallbacks.forEach(k -> k.accept(e, KEY_PRESSED));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyCallbacks.forEach(k -> k.accept(e, KEY_RELEASED));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyCallbacks.forEach(k -> k.accept(e, KEY_TYPED));
    }
}
