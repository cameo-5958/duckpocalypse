package moe.cameo.render;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import moe.cameo.core.Constants;

public class Flashbang {
    private static boolean queued = false;
    private static BufferedImage img;
    private static double elapsed = 0.0;
    
    public static boolean queued() { return queued; }
    public static void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the img at a transparency
        double transparency;
        if (elapsed <= 0.25) {
            transparency = elapsed * 1020.0;
        } else {
            transparency = 255 * Math.pow(Math.E, -0.8 * (elapsed - 0.25));
        }

        float a = (float)Math.max(0.0, Math.min(1.0, transparency / 255.0));

        Composite old = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));

        g2d.drawImage(img, 0, 0, Constants.SCREEN_X, Constants.SCREEN_Y, null);
        g2d.setComposite(old);
    }

    public static void queue(BufferedImage image) {
        elapsed = 0.0;
        img = image;
        queued = true;
    }

    public static void renderStepped(double dt) {
        // Increase elapsed
        elapsed += dt;
        
        // Flashbangs exist for 4 seconds. 
        // Unqueue after 4
        if (elapsed > 4) {
            queued = false;
            img = null;
            elapsed = 0.0;
        }
    }
}
