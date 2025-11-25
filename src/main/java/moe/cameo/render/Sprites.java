package moe.cameo.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Sprites {
    private static final HashMap<String, BufferedImage> sheets = new HashMap<>();
    
    public static BufferedImage load(String key, String path) {
        if (sheets.containsKey(key)) 
            return sheets.get(key);

        try {
            BufferedImage img = ImageIO.read(Sprites.class.getResource(path));
            sheets.put(key, img);
            return img;
        } catch (IOException e) {
            throw new RuntimeException("Failed loading: " + path, e); 
        }
    }

    public static BufferedImage get(String key) {
        return sheets.get(key);
    }
}
