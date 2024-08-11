package com.math;

public class Vector2 {

    public float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(float var) {
        this.x = this.y = var;
    }

    public Vector2() {}


    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }


    public Vector2 add(Vector2 vec) {
        return new Vector2(x + vec.x, y + vec.y);
    }

    public Vector2 sub(Vector2 vec) {
        return new Vector2(x - vec.x, y - vec.y);
    }

    public Vector2 mul(float f) {
        return new Vector2(x * f, y * f);
    }

    public Vector2 div(float d) {
        return new Vector2(x / d, y / d);
    }

    public float dot(Vector2 vec) {
        return x * vec.x + y * vec.y;
    }


    public void normalize() {
        float m = magnitude();
        x /= m;
        y /= m;
    }

    public Vector2 normalized() {
        float m = magnitude();
        return m == 0 ? new Vector2() : new Vector2(x / m, y / m);
    }

    public void print() {
        System.out.printf("X: %f, Y: %f\n", x, y);
    }

    public PolarVector getPolarVector() {
        return new PolarVector(magnitude(), (float) Math.toDegrees(Math.atan2(x, y)));
    }
}
