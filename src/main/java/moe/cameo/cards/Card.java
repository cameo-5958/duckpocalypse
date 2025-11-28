/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.cards;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author kunru
 */
public abstract class Card {
    // SIZE
    private static final int WIDTH = 160;
    private static final int HEIGHT = 160;

    // Simpler class
    protected int cost;
    protected BufferedImage sprite;
    protected String name;
    protected String desc;
    protected int that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained;

    // Draw self
    public BufferedImage getRender() {
        BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = canvas.createGraphics();

        // Enable antialiasing
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
        );


        // Background
        g2d.setColor(new Color(100, 0, 0, 100));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(new Color(128, 0, 0, 100));
        g2d.fillRect(5, 5, WIDTH-10, HEIGHT-10);

        // Sprite
        int sx = 8;
        int sy = 8;

        // Draw sprite
        g2d.drawImage(sprite, sx, sy, 64, 64, null);

        // Draw its name and some data
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("DejaVu Sans", Font.PLAIN, 12));
        g2d.drawString(cost + "Ð", 80, sy+10);

        g2d.drawString("x" + that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained,
            126, sy+10
        );
        
        wrapString(g2d, desc, sx, 80);

        g2d.setFont(new Font("DejaVu Sans", Font.PLAIN, 16));
        g2d.drawString(name, 80, sy+30);
        g2d.dispose();

        return canvas;
    }

    public static void wrapString(Graphics2D g, String text, int x, int yTop){
        FontMetrics fm = g.getFontMetrics();

        String[] words = text.split(" ");
        String line = "";

        int y = yTop + fm.getAscent(); // baseline fix

        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;

            if (fm.stringWidth(test) > 144) {
                g.drawString(line, x, y);
                y += fm.getHeight();
                line = word;
            } else {
                line = test;
            }
        }

        if (!line.isEmpty()) {
            g.drawString(line, x, y);
        }
    }

    public static BufferedImage emptyCardSlot() {
        BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = canvas.createGraphics();

        // Enable antialiasing
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
        );

        // Background
        g2d.setColor(new Color(60, 60, 60, 60));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(new Color(10, 10, 10, 60));
        g2d.fillRect(5, 5, WIDTH-10, HEIGHT-10);

        g2d.dispose();

        return canvas;
    }
}
