/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.cards;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.IntConsumer;

import moe.cameo.core.Constants;
import moe.cameo.render.Widget;

/**
 *
 * @author kunru
 */
public abstract class Card extends Widget {
    // SIZE
    private static final int WIDTH = 160;
    private static final int HEIGHT = 160;
    private static final int Y_POS = Constants.SCREEN_Y - HEIGHT - 10;
    private static final Font TITLE_FONT = new Font("DejaVu Sans", Font.PLAIN, 16);
    private static final Font DEFAULT_FONT = new Font("DejaVu Sans", Font.PLAIN, 12);

    // Hook + index
    private final IntConsumer callPlayCard;
    private final int index;

    // EMPTY CARDS
    public static final class EmptyCard extends Card {
        EmptyCard(int x) { super(x); }

        @Override public void draw(Graphics2D g) {};        
    }

    // Simpler class
    protected int cost;
    protected BufferedImage sprite;
    protected String name;
    protected String desc;
    protected int that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained;

    // Constructor
    public Card(IntConsumer play_card, int x) {
        super(10 + (170 * x), Y_POS, WIDTH, HEIGHT, 
            new Color(128, 30, 30, 200),
            new Color(160, 42, 42, 200),
            new Color(100, 30, 30, 200)
        );

        this.callPlayCard = play_card;
        this.index = x;
    }

    // Secret constructor for EmptyCards
    private Card(int x) {
        super(10 + (170 * x), Y_POS, WIDTH, HEIGHT, new Color(60, 60, 60, 60), new Color(10,10,10,60));

        this.callPlayCard = (int ooga_booga) -> {};
        this.index = -67;
    }

    // Getters
    public int getCost() { return cost; }

    // Draw self
    @Override
    protected void draw(Graphics2D g) {
        // Draw sprite
        g.drawImage(sprite, 8, 8, 64, 64, null);

        // Draw name and data
        g.setColor(Color.WHITE);
        g.setFont(DEFAULT_FONT);

        // Cost
        g.drawString(cost + "Ð", 80, 18);
        g.drawString("x" + that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained,
            126, 18
        );
        wrapString(g, desc, 8, 80);

        // Title
        g.setFont(TITLE_FONT);
        g.drawString(name, 80, 38);
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

    // Get empty card
    public static EmptyCard getEmptyCard(int x) {
        return new EmptyCard(x);
    }

    // OnPress
    @Override
    public boolean onClick() { 
        this.callPlayCard.accept(this.index);
        return true;
    }
}
