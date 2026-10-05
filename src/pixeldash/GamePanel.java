package pixeldash;

import javax.swing.JPanel;//surface to draw

import javax.swing.Timer;//allows game to repeatedly update
import javax.swing.JOptionPane;//creates pop-up message

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
//JPanel is a component that can be drawn on (canvas)
//ActionListener - allows the class to respond to game timer
//KeyListener - allows to detect keyboard keys
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

    // =========================
    // GAME SETTINGS
    // =========================

    private final int WIDTH = 1000;
    private final int HEIGHT = 600;

    private final int GROUND = 500;

    private Timer timer;

    private boolean gameStarted = false;//game hasn't started yet
    private boolean gameOver = false;//game is not over
    private boolean gameWon = false;
    private boolean paused = false;

    // =========================
    // LEVEL
    // =========================

    private int level = 1;//current level

    private int distance = 0;

    private final int LEVEL_LENGTH = 3000;

    // =========================
    // PLAYER
    // =========================

    private Player player;

    // =========================
    // GAME OBJECTS
    // =========================

    private ArrayList<Obstacle> obstacles;
    private ArrayList<Collectible> collectibles;

    private Random random;

    private int spawnCounter = 0;

    // =========================
    // SCORE
    // =========================

    private int score = 0;//player's score

    private int coins = 0;//stores no. of coins collected

    // =========================
    // HEALTH
    // =========================

    private int health = 3;//stores remaining lives

    // =========================
    // TIMER
    // =========================

    private int timeLeft = 1200;

    private long lastSecond;

    // =========================
    // CONSTRUCTOR
    // =========================

    public GamePanel() {

        setFocusable(true);

        addKeyListener(this);

        player = new Player(120, GROUND - 70);//creating a new player object. No.s specify player's starting position
        
        //list that store game objects
        //as game runs, objects are added and removed from these lists
        obstacles = new ArrayList<>();
        collectibles = new ArrayList<>();

        random = new Random();
        
        //makes game continually run
        timer = new Timer(20, this);//every 20 milliseconds timer triggers an event
        timer.start();

        lastSecond = System.currentTimeMillis();
    }

    // =========================
    // START GAME
    // =========================

    private void startGame() {

        gameStarted = true;

        gameOver = false;

        gameWon = false;

        paused = false;

        level = 1;

        score = 0;

        coins = 0;

        health = 3;

        timeLeft = 120;

        distance = 0;

        obstacles.clear();

        collectibles.clear();

        player.reset(120, GROUND - 70);

        lastSecond = System.currentTimeMillis();

        JOptionPane.showMessageDialog(
                this,
                "FLASHBACK\n\n"
                + "A mysterious group of minions attacked the kingdom.\n"
                + "A girl and her cat were taken to a castle.\n\n"
                + "Your mission is to run through the levels,\n"
                + "avoid the minions and rescue them!",
                "PixelDash",
                JOptionPane.INFORMATION_MESSAGE
        );

        requestFocusInWindow();
    }

    // =========================
    // GAME LOOP
    // =========================

    @Override
    public void actionPerformed(ActionEvent e) {

        if (!gameStarted || gameOver || gameWon || paused) {//no action is done - don't update the game
            repaint();
            return;
        }

        updateGame();

        repaint();
    }//when timer fires,the above method runs.

    // =========================
    // UPDATE GAME
    // =========================

    private void updateGame() {//perform all things that need to happen during the game

        updateTimer();

        player.update();

        spawnObjects();

        moveObjects();

        checkCollisions();

        distance++;

        // Level completed
        if (distance >= LEVEL_LENGTH) {

            if (level == 1) {

                startLevelTwo();

            } else {

                gameWon = true;
            }
        }

        // Game over
        if (health <= 0 || timeLeft <= 0) {

            gameOver = true;
        }
    }

    // =========================
    // TIMER
    // =========================

    private void updateTimer() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastSecond >= 1000) {//1 second has passed

            timeLeft--;//reduces time by 1

            lastSecond = currentTime;
        }
    }

    // =========================
    // LEVEL 2
    // =========================

    private void startLevelTwo() {

        level = 2;

        distance = 0;

        timeLeft += 45;

        obstacles.clear();

        collectibles.clear();

        JOptionPane.showMessageDialog(
                this,
                "LEVEL 2!\n\n"
                + "You are getting closer to the castle.\n"
                + "Rescue the girl and her cat!",
                "PixelDash",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================
    // SPAWN OBJECTS
    // =========================

    private void spawnObjects() {

        spawnCounter++;

        if (spawnCounter < 60) {
            return;
        }

        spawnCounter = 0;

        int choice = random.nextInt(100);

        // Obstacle
        if (choice < 60) {
           //create a new enemy
            obstacles.add(
                    new Obstacle(
                            WIDTH,
                            GROUND - 50,
                            50,
                            50//starts from right then moves to the left
                    )
            );
        }

        // Coin
        else if (choice < 90) {

            collectibles.add(
                    new Collectible(
                            WIDTH,
                            GROUND - 120,
                            false
                    )
            );
        }

        // Power-up
        else {

            collectibles.add(
                    new Collectible(
                            WIDTH,
                            GROUND - 170,
                            true
                    )
            );
        }
    }

    // =========================
    // MOVE OBJECTS
    // =========================

    private void moveObjects() {

        int speed = 7;

        if (player.isSpeedBoost()) {

            speed = 11;
        }

        for (Obstacle obstacle : obstacles) {

            obstacle.move(speed);
        }

        for (Collectible item : collectibles) {

            item.move(speed);
        }

        obstacles.removeIf(
                obstacle -> obstacle.getX() < -100
        );

        collectibles.removeIf(
                item -> item.getX() < -100
        );
    }

    // =========================
    // COLLISIONS
    // =========================

    private void checkCollisions() {

        Rectangle playerBounds =
                player.getBounds();//player gets invisible rectangle

        // Obstacles
        Iterator<Obstacle> obstacleIterator =
                obstacles.iterator();

        while (obstacleIterator.hasNext()) {

            Obstacle obstacle =
                    obstacleIterator.next();

            if (playerBounds.intersects(
                    obstacle.getBounds())) {//if rectangle touches obstacle:

                if (!player.isShieldActive()) {

                    health--;

                    score -= 10;

                    if (score < 0) {
                        score = 0;
                    }
                }

                obstacleIterator.remove();
            }
        }

        // Coins and power-ups
        Iterator<Collectible> itemIterator =
                collectibles.iterator();

        while (itemIterator.hasNext()) {

            Collectible item =
                    itemIterator.next();

            if (playerBounds.intersects(
                    item.getBounds())) {

                if (item.isPowerUp()) {

                    player.activatePowerUp();

                    score += 50;

                } else {

                    coins++;

                    score += 20;
                }

                itemIterator.remove();
            }
        }
    }

    // =========================
    // DRAWING
    // =========================

    @Override
    protected void paintComponent(Graphics g) {//drawing the screen

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g;

        drawBackground(g2);//draws background

        if (!gameStarted) {

            drawStartScreen(g2);

            return;
        }

        if (gameOver) {

            drawGameOver(g2);

            return;
        }

        if (gameWon) {

            drawWinScreen(g2);

            return;
        }

        drawCastle(g2);

        for (Collectible item : collectibles) {

            item.draw(g2);
        }

        for (Obstacle obstacle : obstacles) {

            obstacle.draw(g2);
        }

        player.draw(g2);

        drawHUD(g2);

        if (paused) {

            drawPauseScreen(g2);
        }
    }

    // =========================
    // BACKGROUND
    // =========================

    private void drawBackground(Graphics2D g) {

        if (level == 1) {

            g.setColor(
                    new Color(120, 200, 255)
            );

        } else {

            g.setColor(
                    new Color(50, 45, 90)
            );
        }

        g.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
        );

        // Clouds

        g.setColor(Color.WHITE);

        g.fillOval(100, 80, 100, 40);

        g.fillOval(140, 60, 100, 60);

        g.fillOval(600, 100, 100, 40);

        // Ground

        g.setColor(
                new Color(60, 170, 70)
        );

        g.fillRect(
                0,
                GROUND,
                WIDTH,
                HEIGHT - GROUND
        );

        g.setColor(
                new Color(40, 110, 40)
        );

        g.fillRect(
                0,
                GROUND,
                WIDTH,
                10
        );
    }

    // =========================
    // CASTLE
    // =========================

    private void drawCastle(Graphics2D g) {

        if (distance < LEVEL_LENGTH - 500) {
            return;
        }

        int x = 800;

        // Castle
        g.setColor(Color.GRAY);

        g.fillRect(
                x,
                280,
                150,
                220
        );

        // Towers

        g.fillRect(
                x - 30,
                220,
                50,
                280
        );

        g.fillRect(
                x + 130,
                220,
                50,
                280
        );

        // Roofs

        g.setColor(Color.RED);

        int[] leftRoofX = {
                x - 35,
                x - 5,
                x + 25
        };

        int[] leftRoofY = {
                220,
                160,
                220
        };

        g.fillPolygon(
                leftRoofX,
                leftRoofY,
                3
        );

        int[] rightRoofX = {
                x + 125,
                x + 155,
                x + 185
        };

        int[] rightRoofY = {
                220,
                160,
                220
        };

        g.fillPolygon(
                rightRoofX,
                rightRoofY,
                3
        );

        // Door

        g.setColor(Color.BLACK);

        g.fillRect(
                x + 55,
                420,
                40,
                80
        );

        // Girl

        g.setColor(Color.PINK);

        g.fillOval(
                x + 55,
                330,
                30,
                30
        );

        g.setColor(Color.MAGENTA);

        g.fillRect(
                x + 55,
                360,
                30,
                50
        );

        // Cat

        g.setColor(Color.ORANGE);

        g.fillOval(
                x + 100,
                370,
                30,
                25
        );
    }

    // =========================
    // HUD
    // =========================

    private void drawHUD(Graphics2D g) {

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        g.setColor(Color.WHITE);

        g.drawString(
                "LEVEL: " + level,
                20,
                30
        );

        g.drawString(
                "SCORE: " + score,
                20,
                60
        );

        g.drawString(
                "COINS: " + coins,
                20,
                90
        );

        g.drawString(
                "TIME: " + timeLeft,
                800,
                30
        );

        // Hearts

        g.drawString(
                "HEALTH:",
                780,
                60
        );

        for (int i = 0; i < health; i++) {

            g.setColor(Color.RED);

            g.fillOval(
                    870 + i * 30,
                    43,
                    20,
                    20
            );
        }

        // Power-ups

        if (player.isShieldActive()) {

            g.setColor(Color.CYAN);

            g.drawString(
                    "SHIELD!",
                    450,
                    30
            );
        }

        if (player.isSpeedBoost()) {

            g.setColor(Color.YELLOW);

            g.drawString(
                    "SPEED!",
                    450,
                    55
            );
        }
    }

    // =========================
    // START SCREEN
    // =========================

    private void drawStartScreen(Graphics2D g) {

        g.setColor(
                new Color(20, 20, 40)
        );

        g.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
        );

        g.setColor(Color.WHITE);

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        70
                )
        );

        g.drawString(
                "PIXELDASH",
                270,
                180
        );

        g.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        25
                )
        );

        g.drawString(
                "RUN • JUMP • DUCK • SLIDE • RESCUE",
                250,
                240
        );

        g.drawString(
                "Press ENTER to start",
                365,
                330
        );

        g.drawString(
                "SPACE / UP ARROW = Jump",
                340,
                380
        );

        g.drawString(
                "DOWN ARROW = Duck",
                355,
                420
        );

        g.drawString(
                "S = Slide",
                410,
                460
        );
    }

    // =========================
    // GAME OVER
    // =========================

    private void drawGameOver(Graphics2D g) {

        g.setColor(
                new Color(40, 0, 0)
        );

        g.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
        );

        g.setColor(Color.RED);

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        70
                )
        );

        g.drawString(
                "GAME OVER",
                300,
                230
        );

        g.setColor(Color.WHITE);

        g.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        25
                )
        );

        g.drawString(
                "Score: " + score,
                420,
                300
        );

        g.drawString(
                "Coins: " + coins,
                420,
                340
        );

        g.drawString(
                "Press R to restart",
                380,
                410
        );
    }

    // =========================
    // WIN SCREEN
    // =========================

    private void drawWinScreen(Graphics2D g) {

        g.setColor(
                new Color(30, 110, 60)
        );

        g.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
        );

        g.setColor(Color.YELLOW);

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        60
                )
        );

        g.drawString(
                "YOU WON!",
                340,
                180
        );

        g.setColor(Color.WHITE);

        g.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        25
                )
        );

        g.drawString(
                "The girl and her cat have been rescued!",
                290,
                250
        );

        g.drawString(
                "Final Score: " + score,
                390,
                310
        );

        g.drawString(
                "Coins: " + coins,
                420,
                350
        );

        g.drawString(
                "Press R to play again",
                370,
                420
        );
    }

    // =========================
    // PAUSE SCREEN
    // =========================

    private void drawPauseScreen(Graphics2D g) {

        g.setColor(
                new Color(0, 0, 0, 150)
        );

        g.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
        );

        g.setColor(Color.WHITE);

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        60
                )
        );

        g.drawString(
                "PAUSED",
                390,
                280
        );
    }

    // =========================
    // KEYBOARD
    // =========================

    @Override
    public void keyPressed(KeyEvent e) {//runs whenever we press a key

        int key = e.getKeyCode();

        // Start
        if (key == KeyEvent.VK_ENTER &&
                !gameStarted) {

            startGame();

            return;
        }

        // Restart
        if (key == KeyEvent.VK_R &&
                (gameOver || gameWon)) {

            startGame();

            return;
        }

        // Pause
        if (key == KeyEvent.VK_P &&
                gameStarted &&
                !gameOver &&
                !gameWon) {

            paused = !paused;

            return;
        }

        if (!gameStarted ||
                gameOver ||
                gameWon ||
                paused) {

            return;
        }

        // Jump
        if ((key == KeyEvent.VK_SPACE ||
                key == KeyEvent.VK_UP)
                && !player.isJumping()) {

            player.jump();
        }

        // Duck
        if (key == KeyEvent.VK_DOWN) {

            player.setDucking(true);
        }

        // Slide
        if (key == KeyEvent.VK_S) {

            player.setSliding(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {//runs when we let go of a key

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_DOWN) {

            player.setDucking(false);
        }

        if (key == KeyEvent.VK_S) {

            player.setSliding(false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}