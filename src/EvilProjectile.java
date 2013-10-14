
// Evil-projectile, lacks inheritance from some major bullet-like class, but for this project it was simpler for me - 2012 :)

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EvilProjectile {
    private int x, y;
    private final Color COLOR = Color.BLACK;
    private int damage;
    private final int SIZE;
    private final int SPEED = -4;
    private FileLoad fileLoad;
    private final BufferedImage projectileEvilImage;
    private Random rand = new Random();

    EvilProjectile(Enemy enemy, BufferedImage projectileEvilImage) {
        this.projectileEvilImage = projectileEvilImage;
        SIZE = projectileEvilImage.getHeight();
        setDamage((int) (Math.random() * 100) + 30);
        setX(enemy.getX());
        setY(enemy.getY() + enemy.getSize() / 2);
    }

    EvilProjectile(Boss boss) {
        fileLoad = new FileLoad("images/evilshoot_boss.png");
        projectileEvilImage = fileLoad.getImage();
        SIZE = projectileEvilImage.getHeight();
        setDamage((int) (Math.random() * 100) + 30);
        setX(boss.getX());
        do {
            y = rand.nextInt(381);
        } while (y < 20);
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

    public BufferedImage getProjectileEvilImage() {
        return projectileEvilImage;
    }
}
