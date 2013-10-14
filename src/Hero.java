
// Our hero class, collision checking etc

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Hero {
    private int x, y;
    private final int SPEED = 3;
    private final Color COLOR = new Color(255, 255, 255);
    private int hp;
    private int size;
    private int score;
    private long beginTime, rocketTime, rocketPassedTime;
    private FileLoad fileLoad;
    private final BufferedImage image;
    private final BufferedImage projectileImage;
    private final BufferedImage rocketImage;
    private ArrayList<Projectile> projectiles;

    static enum WeaponType {
        NORMAL, SHOTGUN, ROCKET, BOOM
    }

    private static WeaponType weaponType;

    static enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    Hero(BufferedImage heroImage, BufferedImage projectileImage,
         BufferedImage rocketImage) {
        setWeaponType(WeaponType.NORMAL);
        this.image = heroImage;
        this.projectileImage = projectileImage;
        this.rocketImage = rocketImage;
        score = 0;
        x = 100;
        y = 200;
        setHp(1000);
        setSize(heroImage.getHeight());
        setProjectiles(new ArrayList<Projectile>());
        beginTime = System.nanoTime() / 1000000L;
        rocketTime = System.nanoTime() / 1000000L;
    }

    public void update(int x, int y) {
        setX(x);
        setY(y);
    }

    public void move(boolean up, boolean down, boolean left, boolean right) {
        if (up)
            update(getX(), getY() - SPEED);
        if (down)
            update(getX(), getY() + SPEED);
        if (right)
            update(getX() + SPEED, getY());
        if (left)
            update(getX() - SPEED, getY());
    }

    public void shoot(boolean shoot, WeaponType weaponType) {
        long passedTime = (System.nanoTime() / 1000000L) - beginTime;
        rocketPassedTime = (System.nanoTime() / 1000000L) - rocketTime;
        switch (weaponType) {
            case NORMAL:
                if (shoot && passedTime > 130) {
                    projectiles.add(new Projectile(this, projectileImage));
                    beginTime = System.nanoTime() / 1000000L;
                }
                break;
            case ROCKET:
                if (rocketPassedTime > 10000) {
                    Projectile rocket = new Projectile(this, rocketImage);
                    rocket.setDamage(10000);
                    rocket.setSPEED(6);
                    projectiles.add(rocket);
                    rocketTime = System.nanoTime() / 1000000L;
                }
                setWeaponType(WeaponType.NORMAL);
                break;
        }
    }

    public void checkBoundries() {
        if (x < 20)
            x = 20;
        else if (x > 780)
            x = 780;
        else if (y < 20)
            y = 20;
        else if (y > 380)
            y = 380;
    }

    public boolean checkColision(ArrayList<EvilProjectile> evilProjectiles) {
        boolean collision = false;
        int size = evilProjectiles.size();
        Rectangle projectile;
        Rectangle enemy = new Rectangle(getX(), getY(), getSize(), getSize());
        for (int i = 0; i < size; i++) {
            projectile = new Rectangle(evilProjectiles.get(i).getX(),
                    evilProjectiles.get(i).getY(), evilProjectiles.get(i)
                    .getSIZE(), evilProjectiles.get(i).getSIZE());
            if (projectile.intersects(enemy)) {
                collision = true;
                setHp(getHp() - evilProjectiles.get(i).getDamage());
                evilProjectiles.remove(i);
                break;
            }
        }
        return collision;
    }

    public boolean checkColision(Heart heart) {
        boolean collision = false;
        Rectangle hearty = new Rectangle(heart.getX(), heart.getY(),
                heart.getSIZE(), heart.getSIZE());
        Rectangle enemy = new Rectangle(getX(), getY(), getSize(), getSize());
        if (hearty.intersects(enemy))
            collision = true;
        return collision;
    }

    public boolean checkColision(RocketBonus rocket) {
        boolean collision = false;
        Rectangle rockety = new Rectangle(rocket.getX(), rocket.getY(),
                rocket.getSIZE(), rocket.getSIZE());
        Rectangle enemy = new Rectangle(getX(), getY(), getSize(), getSize());
        if (rockety.intersects(enemy))
            collision = true;
        return collision;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int xPlayer) {
        this.x = xPlayer;
    }

    public void setY(int yPlayer) {
        this.y = yPlayer;
    }

    public Color getCOLOR() {
        return COLOR;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void updateScore(int how) {
        score += how;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void setProjectiles(ArrayList<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public static WeaponType getWeaponType() {
        return weaponType;
    }

    public static void setWeaponType(WeaponType weaponType) {
        Hero.weaponType = weaponType;
    }

    public long getRocketPassedTime() {
        return rocketPassedTime;
    }

}
