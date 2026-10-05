package pixeldash;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {
    //where player is
    private int x;//controls left/right
    private int y;//controls up/down

    private int width = 50;//player's width
    private int height = 70;//player's height

    private int groundY;

    private double velocityY = 0;

    private boolean jumping = false;
    private boolean ducking = false;
    private boolean sliding = false;

    private boolean shield = false;
    private boolean speedBoost = false;

    private long powerUpEndTime;

    public Player(int x, int y) {

        this.x = x;

        this.y = y;

        groundY = y + height;
    }

    public void reset(int x, int y) {

        this.x = x;

        this.y = y;

        groundY = y + height;

        velocityY = 0;//how quickly sth is moving

        jumping = false;

        ducking = false;

        sliding = false;

        shield = false;

        speedBoost = false;
    }

    public void update() {
        //gravity
        if (jumping) {

            velocityY += 1;//moves player down after jumping

            y += velocityY;

            if (y >= groundY - height) {

                y = groundY - height;

                velocityY = 0;

                jumping = false;
            }
        }

        if (!jumping) {

            if (ducking || sliding) {

                height = 40;

            } else {

                height = 70;
            }

            y = groundY - height;
        }

        // Power-up duration
        if (System.currentTimeMillis()
                > powerUpEndTime) {

            shield = false;

            speedBoost = false;
        }
    }

    public void jump() {

        if (!jumping) {

            velocityY = -17;//player moves up

            jumping = true;
        }
    }

    public void activatePowerUp() {

        // Shield or speed
        if (Math.random() < 0.5) {

            shield = true;

        } else {

            speedBoost = true;
        }

        powerUpEndTime =
                System.currentTimeMillis() + 7000;//lasts for 7 secs
    }

    public Rectangle getBounds() {

        return new Rectangle(
                x,
                y,
                width,
                height
        );
    }

    public void draw(Graphics2D g) {

        // Shield
        if (shield) {

            g.setColor(
                    new Color(
                            0,
                            220,
                            255,
                            100
                    )
            );

            g.fillOval(
                    x - 10,
                    y - 10,
                    width + 20,
                    height + 20
            );
        }

        // Body
        g.setColor(Color.BLUE);

        g.fillRect(
                x,
                y + 20,
                width,
                height - 20
        );

        // Head
        g.setColor(Color.ORANGE);

        g.fillOval(
                x + 10,
                y,
                30,
                30
        );

        // Eye
        g.setColor(Color.BLACK);

        g.fillOval(
                x + 30,
                y + 8,
                5,
                5
        );

        // Legs
        if (!ducking && !sliding) {

            g.fillRect(
                    x + 5,
                    y + height - 5,
                    12,
                    15
            );

            g.fillRect(
                    x + 32,
                    y + height - 5,
                    12,
                    15
            );
        }
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isShieldActive() {
        return shield;
    }

    public boolean isSpeedBoost() {
        return speedBoost;
    }

    public void setDucking(boolean ducking) {

        this.ducking = ducking;
    }

    public void setSliding(boolean sliding) {

        this.sliding = sliding;
    }
}