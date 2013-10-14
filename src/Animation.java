
// This is simple animation class

import java.awt.image.BufferedImage;

public class Animation {
    private int x, y, stage;
    private final int width, height, howW, howH;
    private final BufferedImage source;

    Animation(int x, int y, int width, int height, int howW, int howH, BufferedImage source) {
        this.setX(x - width / 2);
        this.setY(y - height / 2);
        this.width = width;
        this.height = height;
        this.howW = howW;
        this.howH = howH;
        this.source = source;
        stage = 0;
    }

    public boolean playAnimation() {
        if (stage < howW * howH) return true;
        else return false;
    }

    public BufferedImage getAnimation() {
        BufferedImage finish = source.getSubimage(width * (stage % howH), height * (stage / howW), width, height);
        stage++;
        return finish;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
