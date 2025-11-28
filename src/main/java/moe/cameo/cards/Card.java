/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.cards;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author kunru
 */
public abstract class Card {
    // SIZE
    private final int WIDTH = 160;
    private final int HEIGHT = 220;

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

        // Background
        g2d.setColor(new Color(128, 0, 0));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(new Color(100, 0, 0));
        g2d.drawRect(1, 1, WIDTH - 1, HEIGHT - 1);

        // Sprite
        int scale = 64 / sprite.getWidth();
        int sx = (WIDTH - 64) / 2;
        int sy = 8;

        // Draw sprite
        g2d.drawImage(sprite, sx, sy, scale, scale, null);
        g2d.dispose();

        return canvas;
    }
}
