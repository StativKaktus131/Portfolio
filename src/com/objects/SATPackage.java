package com.objects;

import com.math.Vector2;

public class SATPackage {

    public Vector2[] corners;
    public Vector2[] axes;

    public SATPackage(Vector2[] corners, Vector2[] axes) {
        this.corners = corners;
        this.axes = axes;
    }

    public void update(Vector2[] corners, Vector2[] axes) {
        this.corners = corners;
        this.axes = axes;
    }
}
