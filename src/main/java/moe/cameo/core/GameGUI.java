package moe.cameo.core;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import moe.cameo.core.GameState.State;
import moe.cameo.render.Widget;

public class GameGUI {
    public static void register(GameState state) {
        // Register all the GUIButtons here
        State.PLACING.addWidget(// ALL WIDGETS RELATED TO PLACING UNITS

            // CANCEL BUTTON ----
            new Widget(10, 10, 96, 32, 
                new Color(220, 40, 40), 
                new Color(255, 50, 50), 
                Color.WHITE) {

            // Draw method
            @Override
            public void draw(Graphics2D g) {            
                // Set font
                g.setFont(Constants.BUTTON_FONT);
                g.setColor(Color.WHITE);

                // Render label
                FontMetrics fm = g.getFontMetrics();

                int tx = (96 - fm.stringWidth("Cancel")) / 2;
                int ty = (32 - fm.getHeight()) / 2 + fm.getAscent();

                g.drawString("Cancel", tx, ty);
            }

            // OnPress
            @Override
            public boolean onClick() {
                state.cancelPlacing();
                return true;
            }
        }, Error.widget());

        State.BUILDING.addWidget(// ALL WIDGETS RELATED TO BUILDING STAGE
            new Widget(530, Constants.SCREEN_Y - 160, 96, 32,
                new Color(40, 220, 40), 
                new Color(50, 255, 50), 
                Color.WHITE) {

            // Draw method
            @Override
            public void draw(Graphics2D g) {            
                // Set font
                g.setFont(Constants.BUTTON_FONT);
                g.setColor(Color.WHITE);

                // Render label
                FontMetrics fm = g.getFontMetrics();

                int tx = (96 - fm.stringWidth("Begin Wave")) / 2;
                int ty = (32 - fm.getHeight()) / 2 + fm.getAscent();

                g.drawString("Begin Wave", tx, ty);
            }

            // OnPress
            @Override
            public boolean onClick() {
                state.play();
                return true;
            }
        },

        new Widget(530, Constants.SCREEN_Y - 118, 96, 32,
                new Color(160, 32, 240), 
                new Color(170, 42, 250), 
                Color.WHITE) {

            // Draw method
            @Override
            public void draw(Graphics2D g) {            
                // Set font
                g.setFont(Constants.BUTTON_FONT);
                g.setColor(Color.WHITE);

                // Render label
                FontMetrics fm = g.getFontMetrics();

                int tx = (96 - fm.stringWidth("2Ð Redraw")) / 2;
                int ty = (32 - fm.getHeight()) / 2 + fm.getAscent();

                g.drawString("2Ð Redraw", tx, ty);
            }

            // OnPress
            @Override
            public boolean onClick() {
                state.attemptRedraw();
                return true;
            }
        },
        
        new Widget(530, Constants.SCREEN_Y - 10 - 32, 96, 32,
            new Color(100, 100, 100, 100),
            new Color(100, 100, 100, 100)
        ) {
            // Draw method
            @Override
            public void draw(Graphics2D g) {
                g.setFont(Constants.BUTTON_FONT);
                g.setColor(Color.WHITE);

                // Render cost
                FontMetrics fm = g.getFontMetrics();
                int ty = (32 - fm.getHeight()) / 2 + fm.getAscent();

                g.drawString("Ð: " + state.getMoney() + " (+" + state.getIncome() + "Ð)", 10, ty);
            }
        },
        Error.widget()
    );
    }
}
