/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.units.Unit;
import moe.cameo.units.towers.Tower;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public class Renderer extends JPanel {
    // Renderer class responsible for drawing
    // elements on screen
    
    private final GameState state;
    private static final int TILE_SIZE = Constants.TILE_SIZE;
    private static final int TSS = TILE_SIZE * 2;
    private static final int FONT_SIZE = 24;

    private static final int MARGIN = 10;

    private static final Font FONT;
    private static final Font NORMAL_FONT;

    // interfaceHeightMap stores heights required for displayables
    private static final Map<Class<?>, Integer> interfaceHeightMap = new HashMap<>();
    
    static {
        // Initialize fonts
        FONT = new Font("DejaVu Sans", Font.PLAIN, FONT_SIZE);
        NORMAL_FONT = new Font("DejaVu Sans", Font.PLAIN, FONT_SIZE / 2);   

        // Set heights for displayable classes
        interfaceHeightMap.put(Displayable.class, TSS + FONT.getSize()); 
        interfaceHeightMap.put(Displayable.HasHealth.class, FONT_SIZE);
        interfaceHeightMap.put(Displayable.HasLevel.class, FONT_SIZE);
        interfaceHeightMap.put(Displayable.HasCards.class, FONT_SIZE);
        interfaceHeightMap.put(Displayable.HasStats.class, FONT_SIZE);
    }

    private final int SCREEN_X;
    private final int SCREEN_Y;

    // Constructor
    public Renderer(GameState state) {
        // Set constants
        this.state = state;
        SCREEN_X = Constants.SCREEN_X;
        SCREEN_Y = Constants.SCREEN_Y;

        // Change window size
        setPreferredSize(new Dimension(
            SCREEN_X,
            SCREEN_Y
        ));

        // Enable double buffering
        setDoubleBuffered(true);
    }

    // Draw ground
    public void drawGround(Graphics g) {
        // Fill with green
        g.setColor(Constants.GROUND_COLOR);
        g.fillRect(0, 0, SCREEN_X, SCREEN_Y);

        // Grid lines
        g.setColor(Constants.GROUND_STRIP_COLOR);
        for (int x=TILE_SIZE; x<SCREEN_X; x += TILE_SIZE) {
            g.fillRect(x-1, 0, 2, SCREEN_Y);
        }

        for (int y=TILE_SIZE; y<SCREEN_Y; y += TILE_SIZE) {
            g.fillRect(0, y-1, SCREEN_X, 2);
        }
    }

    // Drawing units ----
    private void drawUnits(Graphics g) {
        Board board = state.getBoard();

        // Geta list of units and draw them
        for (Unit u : board.getUnits()) {
            drawUnit(g, u);
        }
    }

    private void drawUnit(Graphics g, Unit u) {
        // FOR NOW, just draw placeholder for 
        // unit location

        int us = Constants.TILE_SIZE;
        int ux = (int) u.getX() * us;
        int uy = (int) u.getY() * us;

        // Draw sprite
        g.drawImage(u.getSprite(), ux, uy, us, us, null);
        
        // Draw directional line
        // drawCenteredDirLine(g, ux + (us / 2), uy + (us / 2), us, u.getDirection());
    }

    // Drawing entities ----
    private void drawEntities(Graphics g) {
        Board board = state.getBoard();

        // Get the list of entities and draw them
        List<Entity> entities = new ArrayList<>(board.getEntities());
        for (Entity e : entities) {
            drawEntity(g, e);
        }
    }

    private void drawEntity(Graphics g, Entity e) {
        // Draw all sprites

        int es = e.getSize();
        int ex = (int) e.getX();
        int ey = (int) e.getY();

        // Draw sprite
        g.drawImage(e.getSprite(), ex - es / 2, ey - es / 2, es, es, null);

        // If its an enemy, draw its HP
        if (e instanceof Enemy enem) {
            double hp = enem.getHP();
            double max_hp = enem.getMaxHP();

            // Draw the HP
            g.setColor(Color.BLACK);
            g.fillRect(ex - es / 2, ey - es / 2, es, 6);
            g.setColor(Color.GREEN);
            g.fillRect(ex - es / 2 + 1, ey - es / 2 + 1, (int) ((es - 2.0) * hp / max_hp), 4);
        }
    }

    // Draw player selected box
    private void drawPlayerSelectedBox(Graphics g) {
        int tx = state.getFocusedTileX();
        int ty = state.getFocusedTileY();
        
        // Check the state of the game:
        if (state.getState() == GameState.State.PLACING_UNIT) {
            // Check if can place
            if (state.canPlace()) {
                this.drawTilebox(g, tx, ty, Color.GREEN);
            } else {
                this.drawTilebox(g, tx, ty, Color.RED);
            }
        } else {
            this.drawTilebox(g, tx, ty, Color.WHITE);
        }
    }

    // Draw outlined tile box
    private void drawTilebox(Graphics g, int tx, int ty, Color c) {
        g.setColor(new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 0.5f));
        g.drawRect(tx * Constants.TILE_SIZE - 2, ty * Constants.TILE_SIZE - 2, Constants.TILE_SIZE + 4, Constants.TILE_SIZE + 4);
        g.setColor(new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 0.3f));
        g.fillRect(tx * Constants.TILE_SIZE - 2, ty * Constants.TILE_SIZE - 2, Constants.TILE_SIZE + 4, Constants.TILE_SIZE + 4);
    }

    // Draw a progress bar
    private void drawProgressBar(Graphics g, int left, int top, int sx, int sy, 
                                 int progress, int max, int spacing)
    { 
        // Draw a roudned rectangle with progress filled in
        Graphics2D g2d = (Graphics2D) g;

        // Set the color to black to draw the background
        g2d.setColor(new Color(0f, 0f, 0f, 0.35f));
        g2d.fillRoundRect(left, top, sx, sy, sy, sy);

        // Decrease the bounds
        top += 2;   left += 2;
        sx -= 4;    sy -= 4;

        // Calculate pixels_per_space
        int spaces = max / spacing;
        float pixels_per_space = (float) sx / spaces;

        // Inner rect
        g2d.setColor(new Color(.8f, .8f, .8f, 0.9f));

        int cap = 5;

        if (progress == max) {
            g2d.fillRoundRect(left, top, sx, sy, sy, sy);
        } else if (progress > 0) {
            // Flat middle body (clips the right curve)
            g2d.fillRect(left + cap, top, (int) (pixels_per_space * progress - cap), sy);

            // Left rounded cap (always visible)
            g2d.fillRoundRect(left, top, cap * 2, sy, sy, sy);
        }

        g2d.setColor(new Color(0f, 0f, 0f, 0.35f));
        for (int i=1; i<spaces; i++) {
            int p = left + (int) (pixels_per_space * i);
            g2d.drawLine(p, top, p, top + sy);
        }
    }

    // Drawing GUI
    private void drawGui(Graphics g) {
        // Draw the infobox on the top right

        Graphics2D g2d = (Graphics2D) g;

        drawInfobox(g);

        // Draw all active widgets
        for (Widget w : state.getActiveWidgets())
            w.render(g2d);
    }

    // private void drawCards(Graphics g) {
    //     // Draw the cards
    //     int x = 10;
    //     int y = Constants.SCREEN_Y - 160 - 10;
    //     for (Card c : state.heldCards()) {
    //         if (c == null) {
    //             g.drawImage(Card.emptyCardSlot(), x, y, null);
    //         } else {
    //             // Get the rendered image
    //             BufferedImage card = c.getRender();
    //             g.drawImage(card, x, y, null);
    //         }

    //         x += 170;   
    //     }
    // }

    // The infobox
    private final class InfoboxLayout {
        final int left; 
        final int width; final int height;
        final int top;

        int current_y;

        public InfoboxLayout(Displayable d) {
            this(d, Constants.SCREEN_X - TSS - MARGIN * 3, MARGIN);
        }       
        
        public InfoboxLayout(Displayable d, int left, int top) {
            this.width = TSS;
            this.left = left;
            this.height = calculateHeight(d);
            this.top = top;

            this.current_y = MARGIN;
        }     

        public int calculateHeight(Displayable d) {
            int totalHeight = 0;
            // Check all directly-implemented interfaces
            for (Class<?> iface : d.getClass().getInterfaces()) {
                Integer ifaceHeight = interfaceHeightMap.get(iface);
                if (ifaceHeight != null) {
                    totalHeight += ifaceHeight + MARGIN;
                }
            }
            // Ensure minimum height (at least the base Displayable height)
            return Math.max(totalHeight, interfaceHeightMap.get(Displayable.class));
        }

        public int nextLine(int dy) { 
            int ret = this.top + this.current_y;
            this.current_y += dy + MARGIN + MARGIN;
            return ret;
        }
    }

    // Drawing the infobox
    private void drawInfobox(Graphics g) {
        // ONLY draw if state decrees an Unit is
        // being selected:
        Unit u = state.focusedTile();
        if (u == null || !(u instanceof Displayable disp)) { return; }

        // Create an infobox layout
        InfoboxLayout layout = new InfoboxLayout(disp);

        // Draw guaranteed items
        this.drawInfoboxBackground(g, layout);
        this.drawInfoboxImage(g, layout, disp.getImage());
        this.drawInfoboxText(g, layout, disp.getName());

        if (disp instanceof Displayable.HasHealth dhl) {
            this.drawLabeledBar(g, layout, "HP:", dhl.getHP(), dhl.getMaxHP(), 1);
        }

        if (disp instanceof Displayable.HasLevel dhl) {
            this.drawLabeledBar(g, layout, "Level:", dhl.getLevel(), dhl.getMaxLevel(), 1);
        }

        if (disp instanceof Displayable.HasCards dhc) {
            this.drawLabeledBar(g, layout, "Cards to Upgrade:", dhc.getCards(), dhc.getMaxCards(), 1);
        }
    }

    // Infobox background
    private void drawInfoboxBackground(Graphics g, InfoboxLayout layout) {
        g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.4f));
        g.fillRect(layout.left - MARGIN, layout.top, layout.width + MARGIN * 2, layout.height);
    }

    // Infobox text
    private void drawInfoboxText(Graphics g, InfoboxLayout layout, String text) {
        g.setFont(FONT);
        g.setColor(Color.BLACK);
        g.drawString(text,
            layout.left,
            layout.nextLine(FONT_SIZE)
        ); 
    } 

    // Stamp image
    private void drawInfoboxImage(Graphics g, InfoboxLayout layout, BufferedImage img) {
        g.drawImage(img,
            layout.left,
            layout.nextLine(TSS),
            TSS, TSS, null
        );
    }

    // Draw a labelled bar
    private void drawLabeledBar(Graphics g, InfoboxLayout layout, String label, int progress, int max, int spacing)
    {
        // Get the top y
        int top = layout.nextLine(FONT_SIZE / 2) - FONT_SIZE;

        g.setFont(NORMAL_FONT);
        g.setColor(Color.BLACK);
        g.drawString(label, layout.left, top);

        this.drawProgressBar(g, layout.left, top + 4, layout.width, FONT_SIZE / 2 - 4, progress, max, spacing);
    }

    // Draw a selected tower's range
    private void drawTowerRange(Graphics g, Tower t) {
        int range = (int) t.getRange() * 2;
        int x = (int) t.getSX();
        int y = (int) t.getSY();

        // Draw a circle centered at 
        // x, y with given range:
        g.setColor(new Color(0.5f, 0.5f, 1.0f, 0.2f));
        g.fillOval(x - range / 2, y - range / 2, range, range);

        g.setColor(new Color(0.5f, 0.5f, 1.0f, 0.5f));
        g.drawOval(x - range / 2, y - range / 2, range, range);
    }

    // Paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the ground to begin
        this.drawGround(g);

        // Draw player's selected box box
        this.drawPlayerSelectedBox(g);

        // Draw a tower's range if selected
        if (state.focusedTile() instanceof Tower t) {
            this.drawTowerRange(g, t);
        }

        // Then units
        this.drawUnits(g);

        // Entities after
        this.drawEntities(g);

        // Draw gui
        this.drawGui(g);
    }
}
