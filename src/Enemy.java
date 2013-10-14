
// Simple enemy entity

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Enemy {
    private int x, y, speed, hp, size;
    private final Color COLOR = Color.RED;
    private Random rand = new Random();
    private FileLoad fileLoad;
    private long beginTime;
    private BufferedImage image, projectileEvilImage;
    private int score;
    private int location = 70;

    Enemy(BufferedImage enemyImage, BufferedImage projectileEvilImage) {
        this.image = enemyImage;
        this.projectileEvilImage = projectileEvilImage;
        setSize(enemyImage.getHeight());
        setHp(250);
        speed = rand.nextInt(4);
        if (speed == 0)
            speed = 1;
        x = 800;
        do {
            y = rand.nextInt(381);
        } while (y < 60);

        beginTime = System.nanoTime() / 1000000L;
    }

    public boolean checkBoundries() {
        if (getX() < 0)
            return true;
        return false;
    }

    public boolean checkColision(ArrayList<Projectile> projectiles) {
        boolean collision = false;
        int size = projectiles.size();
        Rectangle projectile;
        Rectangle enemy = new Rectangle(getX(), getY(), getSize(),
                getSize());
        for (int i = 0; i < size; i++) {
            projectile = new Rectangle(projectiles.get(i).getX(),
                    projectiles.get(i).getY(), projectiles.get(i)
                    .getSIZE(), projectiles.get(i).getSIZE());
            if (projectile.intersects(enemy)) {
                collision = true;
                setHp(getHp() - projectiles.get(i).getDamage());
                projectiles.remove(i);
                break;
            }
        }
        return collision;
    }

    public boolean checkColision(Hero hero) {
        score = hero.getScore();
        boolean collision = false;
        Rectangle heroe = new Rectangle(hero.getX(), hero.getY(),
                hero.getSize(), hero.getSize());
        Rectangle enemy = new Rectangle(getX(), getY(), getSize(),
                getSize());
        if (heroe.intersects(enemy))
            collision = true;
        return collision;
    }

    public void shoot(ArrayList<EvilProjectile> evilProjectiles) {
        long passedTime = (System.nanoTime() / 1000000L) - beginTime;
        if (passedTime > 3000 - (score / 3)) {
            evilProjectiles.add(new EvilProjectile(this, projectileEvilImage));
            beginTime = System.nanoTime() / 1000000L;
        }
    }

    public void update() {
        setX(getX() - speed);
    }

    public Color getCOLOR() {
        return COLOR;
    }

    public int getY() {
        return y;
    }

    public void setY(int yEnemy) {
        this.y = yEnemy;
    }

    public int getX() {
        return x;
    }

    public void setX(int xEnemy) {
        this.x = xEnemy;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
