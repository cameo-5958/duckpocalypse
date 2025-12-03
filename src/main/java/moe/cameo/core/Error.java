package moe.cameo.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import moe.cameo.render.Widget;

public class Error extends Widget {
    private final static Error WIDGET = new Error();
    private final static List<Text> QUEUE = new ArrayList<>();

    private static class Text {
        final String text;
        final long expires_at;

        Text(String text) {
            this.text = text;
            this.expires_at = System.nanoTime() + 5_000_000_000l;            
        }

        protected String getText( ) { return text; }
        protected long getExpiry( ) { return expires_at; }
    }

    private Error() {
        super(Constants.SCREEN_X - 6 * Constants.TILE_SIZE,
            Constants.SCREEN_Y - 5 * Constants.TILE_SIZE,
            6 * Constants.TILE_SIZE,
            5 * Constants.TILE_SIZE,

            new Color(0, 0, 0, 0), 
            new Color(100, 100, 100, 100)
        );
    }

    public static void raise(String s) {
        QUEUE.add(new Text(s));
    }

    @Override
    protected void draw(Graphics2D g) {
        // Set font 
        g.setFont(new Font("DejaVu Sans", Font.PLAIN, 12));
        g.setColor(Color.RED);

        // Every word gets 14px; check refreshes
        int why = 16;
        long now = System.nanoTime();
        for (int i=0; i<QUEUE.size(); i++) {
            if (QUEUE.get(i).getExpiry() < now) {
                // Delete it
                System.out.println("Deleted");
                QUEUE.remove(i);
                continue;
            }

            // Drew it successfully
            g.drawString(QUEUE.get(i).getText(), 2, why); 
            why += 14;
        }
    }

    public static Widget widget() {
        return WIDGET;
    }
}