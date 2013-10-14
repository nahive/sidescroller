
// Bonus rocket launcher that spawns from boss

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RocketBonus {
    private int x, y;
    private final Color COLOR = Color.BLACK;
    private int damage;
    private final int SIZE;
    private final int SPEED = -2;
    private FileLoad fileLoad;
    private final BufferedImage projectileRocketImage;
    private Random rand = new Random();


    RocketBonus(Boss boss) {
        fileLoad = new FileLoad("images/rocketbonusshoot_boss.png");
        projectileRocketImage = fileLoad.getImage();
        SIZE = projectileRocketImage.getHeight();
        setDamage((int) (Math.random() * 100) + 30);
        setX(boss.getX());
        do {
            y = rand.nextInt(381);
        } while (y < 60);
        setY(y);
    }

    public void updateProjectile() {
        setX(getX() + SPEED);
    }

    public boolean checkBoundries() {
        if (x < 0) return true;
        else return false;
    }

    public int getX() {
        return x;
    }

    public void setX(int xProject) {
        this.x = xProject;
    }

    public int getY() {
        return y;
    }

    public void setY(int yProject) {
        this.y = yProject;
    }

    public Color getCOLOR() {
        return COLOR;
    }

    public int getSIZE() {
        return SIZE;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public BufferedImage getProjectileRocketImage() {
        return projectileRocketImage;
    }
}
