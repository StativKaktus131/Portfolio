package com.objects;

import com.main.Main;
import com.math.MathUtil;
import com.math.PolarVector;
import com.math.Vector2;

import java.awt.*;

public class Spike extends GameObject {

    private float targetRadius;
    private Player player;
    private float velocity;


    // optional constructor (default moving = false)
    public Spike(String tag, PolarVector position, int width, int height, Player player) {
        this(tag, position, width, height, player, false);
    }

    public Spike(String tag, PolarVector position, int width, int height, Player player, boolean moving) {
        super(tag, position);

        this.width = width;
        this.height = height;
        this.player = player;

        // chance of negative velocity should be higher
        if (moving)
            velocity = Main.RNG.nextFloat() * 2f - 1.5f;


        polygon = new Vector2[] { new Vector2(), new Vector2(), new Vector2()};

        // for clean lerp animation
        targetRadius = position.radius;
        position.radius -= height - 2;

        satPackage = new SATPackage(polygon, new Vector2[] {new Vector2(), new Vector2(), new Vector2()} );
    }

    @Override
    public void tick() {
        // animate spike and
        position.radius = MathUtil.lerp(position.radius, targetRadius, 0.16f);
        // move it (velocity == 0 if !moving)
        position.angle += velocity;


        float x = position.getX();
        float y = position.getY();

        polygon[0] = new Vector2(x + width / 2f * position.sin(), y - width / 2f * position.cos());
        polygon[1] = new Vector2(x - width / 2f * position.sin(), y + width / 2f * position.cos());
        polygon[2] = new Vector2(x + height * position.cos(), y + height * position.sin());


        // calculating edge normals according to the polygon
        Vector2[] normals = new Vector2[3];
        for (int i = 0; i < 3; i++) {
            normals[i] = polygon[(i + 1) % 3].sub(polygon[i]);
            normals[i] = new Vector2(-normals[i].y, normals[i].x);
        }

        satPackage.update(polygon, normals);

        // player has overtaken spike
        if (player.position.angle > position.angle) {
            // animate downwards
            targetRadius = Main.RADIUS - height - 10;

            // remove when invisible
            if (position.radius - targetRadius <= 0.01f)
                GameObjectHandler.removeObject(this);
        }
    }

    @Override
    public void render(Graphics g) {
        // draw Triangle
        g.setColor(Color.RED);
        ((Graphics2D)g).fill(getPolygon());
    }

    @Override
    public Polygon getPolygon() {
        return new Polygon(
                new int[] {(int) polygon[0].x, (int) polygon[1].x, (int) polygon[2].x, (int) polygon[0].x},
                new int[] {(int) polygon[0].y, (int) polygon[1].y, (int) polygon[2].y, (int) polygon[0].y},
                4
        );
    }
}