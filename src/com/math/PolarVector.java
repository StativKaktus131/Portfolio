package com.math;

import com.main.Main;

// Vector in Polar coordinates (length / radius and angle in deg)
public class PolarVector {

    public float radius;
    public float angle;

    public PolarVector(float radius, float angle) {
        this.radius = radius;
        this.angle = angle;
    }


    // get screen coordinates (origin at the window center)
    public int getX() { return Main.WIDTH / 2 + (int) (Math.cos(Math.toRadians(angle)) * radius); }
    public int getY() { return Main.HEIGHT / 2 + (int) (Math.sin(Math.toRadians(angle)) * radius); }

    // conversion to v2 (NOT relative to window centre)
    public Vector2 getVector2() {
        return new Vector2(radius * cos(), radius * sin());
    }

    public float cos() { return (float) Math.cos(Math.toRadians(angle)); }
    public float sin() { return (float) Math.sin(Math.toRadians(angle)); }
}
