package com.math;

// useful math functions
public class MathUtil {
    public static float lerp(float a, float b, float s) {
        return a + s * (b - a);
    }
}
