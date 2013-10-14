
// Simple file loading class - all sprites are buffered images

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class FileLoad {
    private BufferedImage image;

    public FileLoad(String path) {
        try {
            setImage(ImageIO.read(getClass().getClassLoader().getResource(path)));
        } catch (IOException e) {
            System.err.println("Can't Load");
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
