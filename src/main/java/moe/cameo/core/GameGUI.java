package moe.cameo.core;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import moe.cameo.core.GameState.State;
import moe.cameo.render.Sprites;
import moe.cameo.render.Widget;
import moe.waves.Wave;

public class GameGUI {
    public static void register(GameState state) {
        // Register all the GUIButtons here
        // Creating the buttons

        registerPlacingMenu(state);
        registerBuildingMenu(state);
        registerAutoMenu(state);
        registerMenuMenu(state);
    }

    private static void registerPlacingMenu(GameState state) {
        Widget cancel = new Widget(10, 10, 96, 32,
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
        };

        // Menu for placing
        Menu placing_menu = new Menu();
        placing_menu.addAll(cancel, Error.widget());
        State.PLACING.register(placing_menu);
    }

    private static void registerBuildingMenu(GameState state) {
        Widget begin_wave = new Widget(530, Constants.SCREEN_Y - 160, 96, 32,
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
                };
    
        Widget hand_draw = new Widget(530, Constants.SCREEN_Y - 118, 96, 32,
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
                };
    
        Widget money_info = new Widget(530, Constants.SCREEN_Y - 10 - 32, 96, 32,
                        new Color(100, 100, 100, 100),
                        new Color(100, 100, 100, 100)) {
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
                };
                
        // Create & register the menu
        Menu m = new Menu();
        m.addAll(
            begin_wave, hand_draw, money_info, Error.widget()
        );

        State.BUILDING.register(m);
    }

    private static void registerAutoMenu(GameState state) {
        Menu m = new Menu();
        state.getIncome();
        State.AUTO.register(m);
    }

    private static void registerMenuMenu(GameState state) {
        Menu m = new Menu();

        Wave.Difficulty[] diffs = Wave.Difficulty.values();
        int icon_size = 400 + diffs.length * 40;

        Widget game_icon = new Widget(Constants.SCREEN_X / 2 - 270, 100, 540, icon_size,
            new Color(100, 100, 100, 255),
            new Color(155,155,155,255)
        ) {
            @Override 
            public void draw(Graphics2D g) {
                BufferedImage logo = Sprites.load("StartLogo", "/icons/title");
                g.drawImage(logo, 0, 0, null);
            }
        };

        m.add(game_icon);
        int i = 0;
        for (Wave.Difficulty d : Wave.Difficulty.values()) {
            // Create a widget
            m.add(new Widget(Constants.SCREEN_X / 2 - 48, 480 + 40 * i++, 96, 32,
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

                        int tx = (96 - fm.stringWidth(d.name())) / 2;
                        int ty = (32 - fm.getHeight()) / 2 + fm.getAscent();

                        g.drawString(d.name(), tx, ty);
                    }

                    // OnPress
                    @Override
                    public boolean onClick() {
                        state.setDifficulty(d);
                        state.start();
                        return true;
                    }
            });
        }

        State.MENU.register(m);
    }
}
