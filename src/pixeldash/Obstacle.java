package pixeldash;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Obstacle {
    //attributes
    private int x;//horizontal position
    private int y;//vertical position
    private int width;//how wide
    private int height;//how tall

    public Obstacle(
            int x,
            int y,
            int width,
            int height) {

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }
    // method to make enemy move to the left
    public void move(int speed) {

        x -= speed;
    }

    public Rectangle getBounds() {

        return new Rectangle(
                x,
                y,
                width,
                height
        );
    }

    public int getX() {

        return x;
    }
    
    //method to draw obstacle
    public void draw(Graphics2D g) {

        // Minion body
        g.setColor(
                new Color(150, 50, 200)
        );

        g.fillRect(
                x,
                y,
                width,
                height
        );

        // Eyes
        g.setColor(Color.WHITE);

        g.fillOval(
                x + 8,
                y + 10,
                12,
                12
        );

        g.fillOval(
                x + 28,
                y + 10,
                12,
                12
        );

        // Pupils
        g.setColor(Color.BLACK);

        g.fillOval(
                x + 12,
                y + 14,
                5,
                5
        );

        g.fillOval(
                x + 32,
                y + 14,
                5,
                5
        );
    }
}