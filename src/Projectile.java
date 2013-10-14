
// Projectile class for shooting etc

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Projectile {
    private int x, y;
    private final Color COLOR = Color.BLACK;
    private int damage;
    private final int SIZE;
    private int SPEED = 15;
    private FileLoad fileLoad;
    private int spread;
    private final BufferedImage projectileImage;

    Projectile(Hero hero, BufferedImage projectileImage) {
        this.projectileImage = projectileImage;
        spread = 0;
        SIZE = projectileImage.getHeight();
        setDamage((int) (Math.random() * 100) + 80);
        setX(hero.getX());
        setY(hero.getY() + hero.getSize() / 2);
    }

    public void updateProjectile() {
        setX(getX() + SPEED);
        setY(getY() + (int) ((SPEED / 15) * -spread));
    }

    public boolean checkBoundries() {
        if (x > 800)
            return true;
        else
            return false;
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

    public int getSPEED() {
        return SPEED;
    }

    public void setSPEED(int speed) {
        this.SPEED = speed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public BufferedImage getProjectileImage() {
        return projectileImage;
    }

    public int getSpread() {
        return spread;
    }

    public void setSpread(int spread) {
        this.spread = spread;
    }
}
