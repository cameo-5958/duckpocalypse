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
                new Color(130, 40, 40), 
                new Color(180, 50, 50), 
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
            public void onClick() {
                state.cancelPlacing();
            }
        });

        State.BUILDING.addWidget(// ALL WIDGETS RELATED TO BUILDING STAGE
            new Widget(20 + 170 + 170 + 170, Constants.SCREEN_Y - 170 + 10, 96, 32,
                new Color(130, 40, 40), 
                new Color(180, 50, 50), 
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
            public void onClick() {
                state.play();
            }
        }
    );
    }
}
