package com.objects;

import com.math.PolarVector;
import com.math.Vector2;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GameObject {

    protected String tag;
    protected PolarVector position;
    protected int width;
    protected int height;
    protected Vector2[] polygon;

    public SATPackage satPackage;

    public GameObject(String tag, PolarVector position) {
        this.tag = tag;
        this.position = position;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Polygon getPolygon();

    public PolarVector getPosition() {
        return position;
    }
    public String getTag() { return tag; }



    public boolean colliding(String tag) {
        // find all objects with given tag
        List<GameObject> objects = GameObjectHandler.getObjects().stream().filter(o -> o.getTag().equals(tag)).collect(Collectors.toList());

        if (objects.isEmpty())
            return false;

        // if colliding with even one object return true
        for (GameObject obj : objects) {
            if (colliding(obj))
                return true;
        }

        return false;
    }

    // SAT collision algorithm
    private boolean colliding(GameObject obj) {

        LinkedList<Vector2> axes = new LinkedList<>();
        axes.addAll(Arrays.asList(satPackage.axes));
        axes.addAll(Arrays.asList(obj.satPackage.axes));

        // projecting all points onto every axis of the object
        // only works for convex polygons
        for (Vector2 axis : axes) {
            float min1 = Float.MAX_VALUE;
            float min2 = Float.MAX_VALUE;
            float max1 = -Float.MAX_VALUE;
            float max2 = -Float.MAX_VALUE;

            for (Vector2 corner : satPackage.corners) {
                min1 = Math.min(min1, corner.dot(axis));
                max1 = Math.max(max1, corner.dot(axis));
            }

            for (Vector2 corner : obj.satPackage.corners) {
                min2 = Math.min(min2, corner.dot(axis));
                max2 = Math.max(max2, corner.dot(axis));
            }

            if (min1 > max2 || max1 < min2)
                return false;
        }

        return true;
    }
}
