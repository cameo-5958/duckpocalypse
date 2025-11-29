package moe.cameo.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import moe.cameo.collision.Collision;
import moe.cameo.collision.Rect;
import moe.cameo.core.GameState;

public class Cancel {

    public static final int WIDTH  = 96;
    public static final int HEIGHT = 32;

    private static final int X_POS = 10;
    private static final int Y_POS = 10;

    public static BufferedImage render(GameState state, int x, int y) {
        // Render the button
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Turn on antialiasing
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        // Check if mouse collides
        int cx = WIDTH / 2; int cy = HEIGHT / 2;
        boolean hovered = Collision.intersects(new Rect(x + cx, y + cy, WIDTH, HEIGHT), x, y);

        if (hovered)
            g2d.setColor(new Color(180, 50, 50));   // brighter on hover
        else
            g2d.setColor(new Color(130, 40, 40));  

        g2d.fillRect(x, y, WIDTH, HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, WIDTH - 1, HEIGHT - 1);

        g2d.setFont(new Font("DejaVu Sans", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);

        String label = "Cancel";
        FontMetrics fm = g2d.getFontMetrics();

        int tx = (WIDTH  - fm.stringWidth(label)) / 2;
        int ty = (HEIGHT - fm.getHeight()) / 2 + fm.getAscent();

        g2d.drawString(label, tx, ty);

        g2d.dispose();
        return img;
    }
}
