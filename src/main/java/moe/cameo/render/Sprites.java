package moe.cameo.render;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

    public static BufferedImage rotate(BufferedImage original, double angle) {
        // Get original info
        int w = original.getWidth();
        int h = original.getHeight();

        double radians = Math.toRadians(angle);

        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        // New image dimensions
        int newW = (int) Math.floor(w * cos + h * sin);
        int newH = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();

        // High-quality rules for rotation (optional but good)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Transform to rotate around center
        AffineTransform at = new AffineTransform();
        at.translate((newW - w) / 2.0, (newH - h) / 2.0);
        at.rotate(radians, w / 2.0, h / 2.0);

        g2d.drawImage(original, at, null);
        g2d.dispose();

        return rotated;
    }

    public static BufferedImage overlay(BufferedImage base, BufferedImage top) {
        // Overlays one sprite with another
        int w = base.getWidth();
        int h = base.getHeight();

        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        // Draw base
        g.drawImage(base, 0, 0, null);

        // Draw overlay AT CENTER
        int topW = top.getWidth();
        int topH = top.getHeight();
        int x = (w - topW) / 2;
        int y = (h - topH) / 2;
        g.drawImage(top, x, y, null);

        g.dispose();
        return combined;
    }
}
