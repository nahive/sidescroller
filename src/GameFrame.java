
//  This is a simple sidescrolling game created in pure Java.
//  Sprites and images are in folder /images, all methods should be self-explanatory
//  All collision-detection is coded in representative class (for hero it's hero class etc)
//
//                         Created by
//                         Szymon Maslanka
//                         2012
//
//  Coding is fun!

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame {

    static final int CANVAS_WIDTH = 800; // width and height of the game screen
    static final int CANVAS_HEIGHT = 400;
    static final int UPDATE_RATE = 60; // number of game update per second
    static final long UPDATE_PERIOD = 1000000000L / UPDATE_RATE; // nanoseconds
    private final int timeBoss = 60; // time for boss to spawn
    private boolean up, down, right, left, shoot, bossTime = false, // moving booleans
            rocketAvailable = false;
    private int backgroundMovement = 0;
    private long startTime;
    private final static String VERSION = "0.01b";

    static enum State {
        INITIALIZED, PLAYING, PAUSED, GAMEOVER, DESTROYED, WON // state of a game
    }


    static State state;
    private GameCanvas canvas;
    private Hero hero;
    private Boss boss;
    private Enemy[] enemies; // array with all the enemies
    private ArrayList<EvilProjectile> evilProjectiles;
    private ArrayList<Animation> animations;
    private FileLoad fileLoad; // loader for files/sprites/images
    private BufferedImage explosionImage, enemyImage, heroImage, bossImage,
            explosionImageSmall, bossHitImage, projectileEvilImage,
            projectileImage, projectileBossImage, rocketImage; // all the images we need

    public GameFrame() {
        gameInit(); // initialize game
        canvas = new GameCanvas(); // new JPanel of game
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT)); // create canvas
        setLocation(200, 200);
        setResizable(false);
        setContentPane(canvas);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setTitle("SleepingHeroOnePointZero"); // name of the game
        setVisible(true);
    }

    public void loadPictures() {
        fileLoad = new FileLoad("images/enemy.png");
        enemyImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/explosion.png");
        explosionImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/explosion_small.png");
        explosionImageSmall = fileLoad.getImage();
        fileLoad = new FileLoad("images/hero.png");
        heroImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/boss.png");
        bossImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/bossHIT.png");
        bossHitImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/evilshoot.png");
        projectileEvilImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/shoot.png");
        projectileImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/evilshoot_boss.png");
        projectileBossImage = fileLoad.getImage();
        fileLoad = new FileLoad("images/rocket.png");
        rocketImage = fileLoad.getImage();
    }

    public void gameInit() { // here we create entities and basic initializations
        loadPictures();
        up = false;
        down = false;
        right = false;
        left = false;
        shoot = false;
        bossTime = false;
        state = State.INITIALIZED;
        hero = new Hero(heroImage, projectileImage, rocketImage);
        boss = new Boss(bossImage, projectileBossImage);
        enemies = new Enemy[5];
        int locationCounter = 1;
        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Enemy(enemyImage, projectileEvilImage);
            if (locationCounter < 6)
                locationCounter++;
            else
                locationCounter = 1;
        }
        evilProjectiles = new ArrayList<EvilProjectile>(); // store enemy projectiles
        animations = new ArrayList<Animation>(); // store animations
    }

    public void gameShutdown() {
        // unwritted game saving
    }

    public void gameStart() {
        startTime = System.nanoTime() / 1000000000L;
        Thread gameThread = new Thread() {
            @Override
            public void run() {
                gameLoop();
            }
        };
        gameThread.start(); // start thread for drawing and game loop
    }

    private void gameLoop() {
        state = State.PLAYING;

        long beginTime, timeTaken, timeLeft;
        while (true) {
            beginTime = System.nanoTime();
            if (state == State.GAMEOVER || state == State.DESTROYED
                    || state == State.WON)
                break; // break the loop to finish the current play
            if (state == State.PLAYING) {
                gameUpdate();
            }
            repaint();

            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (UPDATE_PERIOD - timeTaken) / 1000000L; // in
            // milliseconds
            if (timeLeft < 10)
                timeLeft = 10;
            try {
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void gameUpdate() {
        updateImageBackground(); // update bg
        hero.move(up, down, left, right); // update hero
        hero.checkBoundries(); // check if it collides with edges
        if (hero.checkColision(evilProjectiles)) { // check if it collides with enemy projectiles
            animations.add(new Animation(hero.getX() + hero.getSize() / 2, hero
                    .getY() + hero.getSize() / 2, 64, 64, 5, 5,
                    explosionImageSmall));
        }
        hero.shoot(shoot, hero.getWeaponType()); // enable shoot
        if (System.nanoTime() / 1000000000 - startTime > timeBoss) { // check if it's time for boss to spawn
            bossTime = true;
        }
        if (!bossTime) {
            for (int i = 0; i < enemies.length; i++) {
                if (enemies[i].checkColision(hero.getProjectiles())) { // check if enemy collides with hero projectiles
                    if (enemies[i].getHp() <= 0) {
                        animations.add(new Animation(enemies[i].getX() + enemies[i].getImage().getWidth()/2,
                                enemies[i].getY() + enemies[i].getImage().getHeight()/2, 64, 64, 5, 5,
                                explosionImage));
                        enemies[i] = new Enemy(enemyImage, projectileEvilImage);
                        hero.updateScore(100);
                    }
                } else if (enemies[i].checkColision(hero)) { // check if enemy collides with hero
                    animations.add(new Animation(enemies[i].getX() + enemies[i].getImage().getWidth()/2, enemies[i]
                            .getY() + enemies[i].getImage().getHeight()/2, 64, 64, 5, 5, explosionImage));
                    hero.setHp(hero.getHp() - enemies[i].getHp());
                    enemies[i] = new Enemy(enemyImage, projectileEvilImage);
                } else if (enemies[i].checkBoundries()) {
                    enemies[i] = new Enemy(enemyImage, projectileEvilImage);
                }
                enemies[i].shoot(evilProjectiles);
                enemies[i].update();
            }
        } else {
            for (int i = 0; i < enemies.length; i++) {
                enemies[i].setSpeed(6); // clear screen from enemies
                enemies[i].update();
            }
            boss.update(); // update boss
            boss.shootHeart(); // shoot heart if nessessary
            boss.shootWeaponRocketBonus(); // shoot weapon bonus
            if (boss.checkColision(hero.getProjectiles()) // check collision with hero projectiles
                    && hero.getProjectiles().size() != 0) {
                animations.add(new Animation(boss.getX()
                        + +(int) (bossImage.getWidth() * Math.random()), hero
                        .getProjectiles().get(0).getY(), 64, 64, 5, 5,
                        explosionImage));
                hero.updateScore(10);
            }
            if (boss.checkColision(hero)) { // check collision with hero
                if (hero.getHp() % 4 == 0) {
                    animations.add(new Animation(hero.getX() + hero.getSize()
                            / 2, hero.getY() + hero.getSize() / 2, 64, 64, 5, 5, explosionImageSmall));
                }
                hero.setHp(hero.getHp() - 2);
            }
            if (boss.getHeart() != null) {
                if (hero.checkColision(boss.getHeart())) {
                    hero.setHp(hero.getHp() + 300);
                    boss.setHeart();
                }
            }
            if (boss.getRocketBonus() != null) {
                if (hero.checkColision(boss.getRocketBonus())) {
                    rocketAvailable = true;
                    boss.setRocketBonus();
                }
            }
            boss.shoot(evilProjectiles); // shoot for boss
            if (boss.getHp() <= 10000) {
                boss.setImage(bossHitImage);
            }
            if (boss.getHeart() != null) {
                boss.getHeart().updateProjectile(); // update damage from boss
            }
            if (boss.getRocketBonus() != null) {
                boss.getRocketBonus().updateProjectile();
            }
        }
        int size = hero.getProjectiles().size();
        for (int i = 0; i < size; i++) {
            hero.getProjectiles().get(i).updateProjectile();
            if (hero.getProjectiles().get(i).checkBoundries()) {
                hero.getProjectiles().remove(i);
                size--;
            }
        }

        size = evilProjectiles.size();
        for (int i = 0; i < size; i++) {
            evilProjectiles.get(i).updateProjectile();
            if (evilProjectiles.get(i).checkBoundries()) {
                evilProjectiles.remove(i);
                size--;
            }
        }
        if (hero.getHp() <= 0)
            state = State.DESTROYED;
        if (boss.getHp() <= 0) {
            state = State.WON;
        }
    }

    private void gameDraw(Graphics2D g2d) {
        switch (state) {
            case INITIALIZED:
                gameDrawInitialized(g2d);
                break;
            case PLAYING:
                gameDrawStarted(g2d);
                break;
            case PAUSED:
                // ......
                break;
            case DESTROYED:
                gameDrawDestroyed(g2d);
                break;
            case WON:
                gameDrawWon(g2d);
                break;
            case GAMEOVER:
                break;
        }
    }

    private void gameDrawInitialized(Graphics2D g2d) { // main screen
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(hero.getCOLOR());
        g2d.setFont(new Font("OCR A Extended", 1, 60));
        g2d.drawString("Sleeping Hero", getWidth() / 2 - 260,
                getHeight() / 3 + 50);
        g2d.setFont(new Font("OCR A Extended", 1, 20));
        g2d.drawString("Press space to start your first dream!",
                getWidth() / 2 - 248, getHeight() / 2 + 50);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("OCR A Extended", 1, 12));
        g2d.drawString("Navigate with arrows, space to shoot.",
                getWidth() / 2 - 130, 380);

    }

    private void gameDrawStarted(Graphics2D g2d) { // drawing playing game onscreen, updating animations, sprites etc
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(hero.getImage(), hero.getX(), hero.getY(), this);
        if (bossTime) {
            g2d.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);
            if (boss.getHeart() != null)
                g2d.drawImage(boss.getHeart().getProjectileHeartImage(), boss
                        .getHeart().getX(), boss.getHeart().getY(), this);
            if (boss.getRocketBonus() != null)
                g2d.drawImage(boss.getRocketBonus().getProjectileRocketImage(),
                        boss.getRocketBonus().getX(), boss.getRocketBonus()
                        .getY(), this);
        }
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i] != null) {
                g2d.drawImage(enemies[i].getImage(), enemies[i].getX(),
                        enemies[i].getY(), this);
            }
        }
        int size = animations.size();
        for (int i = 0; i < size; i++) {
            if (animations.get(i).playAnimation())
                g2d.drawImage(animations.get(i).getAnimation(),
                        animations.get(i).getX(), animations.get(i).getY(),
                        this);
            else {
                animations.remove(i);
                size--;
            }
        }
        switch (hero.getWeaponType()) {
            case NORMAL:
                size = hero.getProjectiles().size();
                for (int i = 0; i < size; i++) {
                    g2d.drawImage(
                            hero.getProjectiles().get(i).getProjectileImage(), hero
                            .getProjectiles().get(i).getX(), hero
                            .getProjectiles().get(i).getY(), this);
                }
                break;
            case SHOTGUN:
                break;
        }

        size = evilProjectiles.size();
        for (int i = 0; i < size; i++) {
            g2d.drawImage(evilProjectiles.get(i).getProjectileEvilImage(),
                    evilProjectiles.get(i).getX(), evilProjectiles.get(i)
                    .getY(), this);
        }
        canvas.drawForeground(g2d);
        g2d.setColor(hero.getCOLOR());
        g2d.setFont(new Font("OCR A Extended", 1, 20));
        g2d.drawString("HP: " + hero.getHp() + " SCORE: " + hero.getScore(),
                30, 30);
        if (bossTime) {
            g2d.drawString("BOSS HP: " + boss.getHp(), 600, 30);
            if (rocketAvailable)
                g2d.drawString(
                        "TIME TO ROCKET: "
                                + (((10 - hero.getRocketPassedTime() / 1000) < 1) ? "READY"
                                : (10 - hero.getRocketPassedTime() / 1000)),
                        30, 380);
        } else
            g2d.drawString(
                    "TIME TO BOSS: "
                            + (timeBoss - (System.nanoTime() / 1000000000 - startTime)),
                    30, 380);
    }

    private void gameDrawDestroyed(Graphics2D g2d) { // loosing screen
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(hero.getCOLOR());
        g2d.setFont(new Font("OCR A Extended", 1, 80));
        g2d.drawString("GameOver!", getWidth() / 2 - 210, getHeight() / 3 + 20);
        g2d.setFont(new Font("OCR A Extended", 1, 20));
        g2d.drawString("SCORE: " + hero.getScore(), getWidth() / 2 - 130,
                getHeight() / 2);
        g2d.drawString("Press enter to restart", getWidth() / 2 - 130,
                getHeight() / 2 + 30);
        g2d.drawString("or press ESC to exit!", getWidth() / 2 - 130,
                getHeight() / 2 + 60);
    }

    private void gameDrawWon(Graphics2D g2d) { // won screen
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(hero.getCOLOR());
        g2d.setFont(new Font("OCR A Extended", 1, 80));
        g2d.drawString("OH MY GOD! ", getWidth() / 2 - 210,
                getHeight() / 3 - 20);
        g2d.drawString("You Won!", getWidth() / 2 - 210, getHeight() / 3 + 40);
        g2d.setFont(new Font("OCR A Extended", 1, 20));
        g2d.drawString("SCORE: " + hero.getScore(), getWidth() / 2 - 130,
                getHeight() / 2);
        g2d.drawString("Press ESC to exit!", getWidth() / 2 - 130,
                getHeight() / 2 + 30);
    }

    public void gameKeyPressed(int keyCode) { // key events for movement
        if (state == State.PLAYING) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    up = true;
                    break;
                case KeyEvent.VK_DOWN:
                    down = true;
                    break;
                case KeyEvent.VK_LEFT:
                    left = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    right = true;
                    break;
                case KeyEvent.VK_SPACE:
                    shoot = true;
                    break;
                case KeyEvent.VK_CONTROL:
                    if (bossTime && rocketAvailable)
                        hero.setWeaponType(Hero.WeaponType.ROCKET);
                    break;
            }
        } else if (state == State.INITIALIZED) {
            if (keyCode == KeyEvent.VK_SPACE)
                gameStart();
        } else if (state == State.DESTROYED) {
            if (keyCode == KeyEvent.VK_ENTER) {
                gameInit();
                repaint();
            } else if (keyCode == KeyEvent.VK_ESCAPE)
                dispose();
        } else if (state == State.WON) {
            if (keyCode == KeyEvent.VK_ESCAPE)
                dispose();
        }
    }

    public void gameKeyReleased(int keyCode) { // so it's smooth
        if (state == State.PLAYING) {
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    up = false;
                    break;
                case KeyEvent.VK_DOWN:
                    down = false;
                    break;
                case KeyEvent.VK_LEFT:
                    left = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    right = false;
                    break;
                case KeyEvent.VK_SPACE:
                    shoot = false;
                    break;
            }
        }
    }

    public void gameKeyTyped(char keyChar) {
    }

    public void updateImageBackground() { // parallell background sliding
        if (backgroundMovement % 5 == 0)
            canvas.setBackgroundX(canvas.getBackgroundX() - 1);
        if (backgroundMovement % 3 == 0) {
            canvas.setBackgroundX2(canvas.getBackgroundX2() - 1);
        }
        if (backgroundMovement % 2 == 0) {
            canvas.setBackgroundX3(canvas.getBackgroundX3() - 1);
        }
        backgroundMovement++;
    }

    // Custom drawing panel, written as an inner class.
    class GameCanvas extends JPanel implements KeyListener { // background
        private FileLoad fileLoad;
        private BufferedImage backgroundL1 = null, backgroundL1rev = null;
        private BufferedImage backgroundL2 = null, backgroundL2rev = null;
        private BufferedImage backgroundL3 = null, backgroundL3rev = null;
        private int backgroundX1 = 0, backgroundX2 = 0, backgroundX3 = 0;

        public GameCanvas() {
            backgroundX1 = 0;
            loadImages();
            setFocusable(true);
            setDoubleBuffered(true);
            requestFocus();
            addKeyListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            super.paintComponent(g2d); // kolorowanie bg
            setBackground(Color.BLACK);
            drawBackground(g2d);
            gameDraw(g2d);
        }

        public void drawBackground(Graphics2D g2d) {
            if (backgroundL1 != null)
                if (backgroundX1 > 0 - backgroundL1.getWidth()) {
                    g2d.drawImage(backgroundL1, backgroundX1, 0, null);
                    g2d.drawImage(backgroundL1rev, backgroundL1.getWidth()
                            + backgroundX1, 0, null);

                } else {
                    backgroundX1 = 0;
                    BufferedImage help = backgroundL1;
                    backgroundL1 = backgroundL1rev;
                    backgroundL1rev = help;
                    g2d.drawImage(backgroundL1, backgroundX1, 0, null);
                    g2d.drawImage(backgroundL1rev, backgroundL1.getWidth()
                            + backgroundX1, 0, null);
                }
            if (backgroundL2 != null)
                if (backgroundX2 > 0 - backgroundL2.getWidth()) {
                    g2d.drawImage(backgroundL2, backgroundX2, 0, null);
                    g2d.drawImage(backgroundL2rev, backgroundL2.getWidth()
                            + backgroundX2, 0, null);

                } else {
                    backgroundX2 = 0;
                    BufferedImage help = backgroundL2;
                    backgroundL2 = backgroundL2rev;
                    backgroundL2rev = help;
                    g2d.drawImage(backgroundL2, backgroundX2, 0, null);
                    g2d.drawImage(backgroundL2rev, backgroundL2.getWidth()
                            + backgroundX2, 0, null);
                }
        }

        public void drawForeground(Graphics2D g2d) {
            if (backgroundL3 != null)
                if (backgroundX3 > 0 - backgroundL3.getWidth()) {
                    g2d.drawImage(backgroundL3, backgroundX3, 0, null);
                    g2d.drawImage(backgroundL3rev, backgroundL3.getWidth()
                            + backgroundX3, 0, null);

                } else {
                    backgroundX3 = 0;
                    BufferedImage help = backgroundL3;
                    backgroundL3 = backgroundL3rev;
                    backgroundL3rev = help;
                    g2d.drawImage(backgroundL3, backgroundX3, 0, null);
                    g2d.drawImage(backgroundL3rev, backgroundL3.getWidth()
                            + backgroundX3, 0, null);
                }

        }

        public void loadImages() {
            fileLoad = new FileLoad("images/backgroundL1.jpg");
            backgroundL1 = fileLoad.getImage();
            fileLoad = new FileLoad("images/backgroundL1rev.jpg");
            backgroundL1rev = fileLoad.getImage();
            fileLoad = new FileLoad("images/backgroundL2.png");
            backgroundL2 = fileLoad.getImage();
            fileLoad = new FileLoad("images/backgroundL2rev.png");
            backgroundL2rev = fileLoad.getImage();
            fileLoad = new FileLoad("images/backgroundL3.png");
            backgroundL3 = fileLoad.getImage();
            fileLoad = new FileLoad("images/backgroundL3rev.png");
            backgroundL3rev = fileLoad.getImage();
        }

        public BufferedImage getImageBackground() {
            return backgroundL1;
        }

        public void setImageBackgound(BufferedImage image) {
            backgroundL1 = image;
        }

        public void setBackgroundX(int x) {
            backgroundX1 = x;
        }

        public int getBackgroundX() {
            return backgroundX1;
        }

        public void setBackgroundX2(int x) {
            backgroundX2 = x;
        }

        public int getBackgroundX2() {
            return backgroundX2;
        }

        public void setBackgroundX3(int x) {
            backgroundX3 = x;
        }

        public int getBackgroundX3() {
            return backgroundX3;
        }

        // KeyEvent handlers
        @Override
        public void keyPressed(KeyEvent e) {
            gameKeyPressed(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            gameKeyReleased(e.getKeyCode());
        }

        @Override
        public void keyTyped(KeyEvent e) {
            gameKeyTyped(e.getKeyChar());
        }
    }

    public static void main(String[] args) { // stargin app
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameFrame();
            }
        });
    }
}
