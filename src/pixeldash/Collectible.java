package pixeldash;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Collectible {

    private int x;
    private int y;

    private boolean powerUp;

    public Collectible(
            int x,
            int y,
            boolean powerUp) {

        this.x = x;

        this.y = y;

        this.powerUp = powerUp;
    }

    public void move(int speed) {

        x -= speed;
    }

    public int getX() {

        return x;
    }

    public boolean isPowerUp() {

        return powerUp;
    }

    public Rectangle getBounds() {

        return new Rectangle(
                x,
                y,
                30,
                30
        );
    }

    public void draw(Graphics2D g) {

        if (powerUp) {

            // Power-up
            g.setColor(Color.RED);

            g.fillOval(
                    x,
                    y,
                    30,
                    30
            );//to draw oval

            g.setColor(Color.WHITE);

            g.drawString(
                    "P",
                    x + 10,
                    y + 21
            );

        } else {

            // Coin
            g.setColor(Color.YELLOW);

            g.fillOval(
                    x,
                    y,
                    25,
                    25
            );

            g.setColor(Color.ORANGE);

            g.drawOval(
                    x,
                    y,
                    25,
                    25
            );

            g.setColor(Color.BLACK);

            g.drawString(
                    "$",
                    x + 8,
                    y + 18
            );
        }
    }
}