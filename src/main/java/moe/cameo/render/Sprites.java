package moe.cameo.render;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Sprites {
    private static final HashMap<String, BufferedImage> sheets = new HashMap<>();

    static {
        load("NULL", "/icons/placeholder");
        load("Empty", "/icons/empty");
    }
    
    public static BufferedImage load(String key, String path) {
        if (sheets.containsKey(key)) 
            return sheets.get(key);

        try {
            BufferedImage img = ImageIO.read(Sprites.class.getResource(path + ".png"));
            sheets.put(key, img);
            return img;
        } catch (IOException e) {
            throw new RuntimeException("Failed loading: " + path, e); 
        }
    }

    public static BufferedImage get(String key) {
        return sheets.get(key);
    }

    public static BufferedImage flip(BufferedImage original) {
        // WHY IS IT SO HARD TO FLIP AN IMAGE IN JAVA?!?!?!?
        BufferedImage flipped = new BufferedImage(
            original.getWidth(),
            original.getHeight(),
            original.getType()
        );

        Graphics2D g2d = flipped.createGraphics();

        // Flip horizontally
        AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
        at.translate(-original.getWidth(), 0);

        g2d.drawImage(original, at, null);
        g2d.dispose();

        return flipped;
    }
}
