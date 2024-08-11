package com.objects;

import com.io.KeyInput;
import com.math.*;
import com.main.*;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends GameObject {

    private final Color playerColor = Color.ORANGE;
    private final float jumpforce = 9.5f;
    private final float gravity = 0.6f;

    private boolean canJump = true;
    private float speed = 0.8f;
    private float velocity;

    // double jump
    private float doubleJumpMeter = 0f;
    private final int barWidth = 200;
    private final int barHeight = 45;
    private final int borderThickness = 3;
    private final int djBarX = Main.WIDTH - 10 - barWidth;
    private final int djBarY = Main.HEIGHT - 10 - barHeight;
    private final float doubleJumpReloadTime = 2.2f;
    private final Font meterFont = new Font("arial", Font.ITALIC | Font.BOLD, 25);

    // dash
    private float dashMeter = 0f;
    private boolean dashing;
    private float dashDuration = 0.15f;
    private float dashTime = 0f;
    private final int dashBarX = Main.WIDTH - 10 - barWidth;
    private final int dashBarY = Main.HEIGHT - 20 - 2 * barHeight;
    private final float dashReloadTime = 6.5f;
    private final float additiveDashSpeed = 1f;

    // polygon
    private PolarVector upperPos;


    public Player(String tag, PolarVector position) {
        super(tag, position);

        width = 36;
        height = 60;

        polygon = new Vector2[4];

        KeyInput.keyCallbacks.add(this::keyEvent);

        upperPos = new PolarVector(position.radius + height, position.angle);

        satPackage = new SATPackage(polygon, new Vector2[]{new Vector2(), new Vector2() });
    }

    private void keyEvent(KeyEvent e, int state) {

        // jump
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            switch (state) {
                case KeyInput.KEY_PRESSED:
                    // player would double jump if they are elevated
                    boolean doubleJump = position.radius != Main.RADIUS;

                    // prevent holding space bar and double jumping when the player shouldn't be able to
                    if (!canJump || (doubleJump && doubleJumpMeter != 1f))
                        break;

                    // reset djMeter when double jumping
                    if (doubleJump)
                        doubleJumpMeter = 0f;

                    // jump
                    velocity = jumpforce;
                    canJump = false;
                    break;


                case KeyInput.KEY_RELEASED:
                    // interrupt jump
                    velocity = Math.min(velocity, 0);
                    canJump = true;
                    break;
            }
        }

        // dash
        if (e.getKeyCode() == KeyEvent.VK_SHIFT && state == KeyInput.KEY_PRESSED && dashMeter == 1f) {
            dashing = true;
            dashTime = dashDuration;
            dashMeter = 0f;             // reset dash meter
            velocity = 0f;              // gravity cancel
        }
    }

    @Override
    public void tick() {
        // use hyperbolic speed function (dps = speed * 60)
        speed = getSpeedAt(GameWindow.gameTime() * 0.2f);

        // speed up player when dashing
        if (dashing) {
            speed += additiveDashSpeed;
            dashTime -= 1f / 60f;
        }


        if (dashTime <= 0f)
            dashing = false;


        // move player
        position.angle += speed;

        // pause gravity when dashing
        if (!dashing) {
            velocity -= gravity;
            position.radius += velocity;
        }

        // update upper angle
        upperPos.angle = position.angle;
        upperPos.radius = position.radius + height;


        // reset the player to the ground
        if (position.radius <= Main.RADIUS) {
            velocity = 0;
            position.radius = Main.RADIUS;
        }


        // update polygon array
        float x = position.getX();
        float y = position.getY();
        float ux = upperPos.getX();
        float uy = upperPos.getY();

        polygon[0] = new Vector2(x + width / 2f * position.sin(), y - width / 2f * position.cos());
        polygon[1] = new Vector2(x - width / 2f * position.sin(), y + width / 2f * position.cos());
        polygon[2] = new Vector2(ux - width / 2f * position.sin(), uy + width / 2f * position.cos());
        polygon[3] = new Vector2(ux + width / 2f * position.sin(), uy - width / 2f * position.cos());

        // update package with local right and up vector as axes
        satPackage.update(polygon, new Vector2[] {new PolarVector(1, position.angle).getVector2(), new PolarVector(1, position.angle + 90).getVector2()} );

        if (colliding("spike"))
            Main.endGame();

        // reduce meters
        dashMeter = Math.min(1f, dashMeter + 1f / 60f / dashReloadTime);
        doubleJumpMeter = Math.min(1f, doubleJumpMeter + 1f / 60f / doubleJumpReloadTime);


        // update score (300 because players like big numbers)
        Main.score = (int) (GameWindow.gameTime() * 300);
    }

    @Override
    public void render(Graphics g) {
        drawMeters(g);

        g.setColor(playerColor);
        ((Graphics2D)g).fill(getPolygon());     // cast to graphics 2d to use fill(Shape) method
    }


    private void drawMeters(Graphics g) {



        // borders
        g.setColor(Color.BLACK);
        g.fillRect(djBarX, djBarY, barWidth, barHeight);
        g.fillRect(dashBarX, dashBarY, barWidth, barHeight);

        // background
        g.setColor(Color.WHITE);
        g.fillRect(djBarX + borderThickness, djBarY + borderThickness, barWidth - 2 * borderThickness, barHeight - 2 * borderThickness);
        g.fillRect(dashBarX + borderThickness, dashBarY + borderThickness, barWidth - 2 * borderThickness, barHeight - 2 * borderThickness);

        // meter
        g.setColor(Color.BLUE);
        g.fillRect(djBarX + borderThickness, djBarY + borderThickness, (int) (doubleJumpMeter * (barWidth - 2 * borderThickness)), barHeight - 2 * borderThickness);
        g.setColor(Color.MAGENTA);
        g.fillRect(dashBarX + borderThickness, dashBarY + borderThickness, (int) (dashMeter * (barWidth - 2 * borderThickness)), barHeight - 2 * borderThickness);

        // text
        g.setColor(Color.BLACK);
        g.setFont(meterFont);
        int djWidth = g.getFontMetrics(meterFont).stringWidth("double jump");
        g.drawString("double jump", djBarX + barWidth / 2 - djWidth / 2, djBarY + 30);
        int dashWidth = g.getFontMetrics(meterFont).stringWidth("dash");
        g.drawString("dash", dashBarX + barWidth / 2 - dashWidth / 2, dashBarY + 30);
    }


    private float getSpeedAt(float x) {
        // https://www.desmos.com/calculator/lbykrp9g5w
        return 1.7f*(-34.3f/(x+34.3f)+1)+0.9f;
    }

    @Override
    public Polygon getPolygon() {
        return new Polygon(
                new int[] {(int) polygon[0].x, (int) polygon[1].x, (int) polygon[2].x, (int) polygon[3].x, (int) polygon[0].x},
                new int[] {(int) polygon[0].y, (int) polygon[1].y, (int) polygon[2].y, (int) polygon[3].y, (int) polygon[0].y},
                5
        );
    }
}