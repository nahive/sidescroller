
// Boss class with hp, shoot, bonuses etc

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Boss extends Enemy {
    private long beginTime;
    private int stop;
    private int rage;
    private boolean heart1 = false, heart2 = false;
    private Heart heart;
    private RocketBonus rocketBonus;

    Boss(BufferedImage enemyImage, BufferedImage projectileBossImage) {
        super(enemyImage, projectileBossImage);
        setHp(100000);
        stop = 800 - enemyImage.getWidth();
        rage = 300;
        setX(800);
        setY(20);
        setSpeed(1);
        beginTime = System.nanoTime() / 1000000L;
    }

    public void shoot(ArrayList<EvilProjectile> evilProjectiles) {
        long passedTime = (System.nanoTime() / 1000000L) - beginTime;
        if (passedTime > rage) {
            evilProjectiles.add(new EvilProjectile(this));
            beginTime = System.nanoTime() / 1000000L;
        }
    }

    public boolean shootHeart() {
        if (getHp() < 50000 && !heart1) {
            heart1 = true;
            heart = new Heart(this);
            return true;
        }
        if (getHp() < 25000 && !heart2) {
            heart2 = true;
            heart = new Heart(this);
            return true;
        }
        return false;
    }

    public boolean shootWeaponRocketBonus() {
        if (getHp() == 100000) {
            rocketBonus = new RocketBonus(this);
            return true;
        }
        return false;
    }

    public void update() {
        if (getX() > stop) setX(getX() - getSpeed());
        rage = getHp() / 1000 + 100;
    }

    public void setRage(int rage) {
        this.rage = rage;
    }

    public Heart getHeart() {
        return heart;
    }

    public RocketBonus getRocketBonus() {
        return rocketBonus;
    }

    public void setHeart() {
        heart = null;
    }

    public void setRocketBonus() {
        rocketBonus = null;
    }
}
