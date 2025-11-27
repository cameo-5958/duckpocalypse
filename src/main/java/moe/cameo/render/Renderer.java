/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;
import moe.cameo.units.Unit;
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
        FONT = new Font("DejaVu Sans", Font.PLAIN, FONT_SIZE);
        NORMAL_FONT = new Font("DejaVu Sans", Font.PLAIN, FONT_SIZE / 2);   

        interfaceHeightMap.put(Displayable.class, TSS + FONT.getSize() + MARGIN); 
        interfaceHeightMap.put(Displayable.HasHealth.class, FONT_SIZE);
        interfaceHeightMap.put(Displayable.HasLevel.class, FONT_SIZE);
        interfaceHeightMap.put(Displayable.HasCards.class, FONT_SIZE);
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

    // Calculate height required
    private int calculateHeightRequired(Unit u) {
        int total = 0;
        for (Class<?> iface : u.getClass().getInterfaces()) {
            total += interfaceHeightMap.getOrDefault(iface, -MARGIN) + MARGIN;
        }

        return total + MARGIN;
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
        // FOR NOW, just draw placeholder for 
        // entity location

        int es = e.getSize();
        int ex = (int) e.getX();
        int ey = (int) e.getY();

        // Draw sprite
        g.drawImage(e.getSprite(), ex - es / 2, ey - es / 2, es, es, null);

        // Draw directional line
        drawCenteredDirLine(g, ex, ey, es, e.getDirection());
    }

    // Draw DirLine. Mostly used for debug
    private void drawCenteredDirLine(Graphics g, int px, int py, int size, double dir) {
        // Convert to radians
        double ang = dir / 180 * Math.PI;

        // Arrow length
        int len = size / 3;

        // Tip of line
        int tx = px + (int) (Math.cos(ang) * len);
        int ty = py + (int) (Math.sin(ang) * len);

        // Draw line
        g.setColor(Color.RED);
        g.drawLine(px, py, tx, ty);
    }

    // Draw player selected box
    private void drawPlayerSelectedBox(Graphics g) {
        int tx = state.getFocusedTileX();
        int ty = state.getFocusedTileY();
        
        this.drawTilebox(g, tx, ty, Color.WHITE);
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
        // Set the color to black to draw the background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(left, top, sx, sy);

        // Decrease the bounds
        top += 2;   left += 2;
        sx -= 4;    sy -= 4;

        // Draw the filled in part
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(left, top, (int) (sx * ((double) progress / max)), sy);

        // Max dx
        int spaces = max / spacing;
        int pixels_per_space = sx / spaces;

        g.setColor(Color.DARK_GRAY);
        for (int i=1; i<spaces; i++) {
            g.fillRect(left + (pixels_per_space * i), top, 2, sy);
        }
    }

    // Drawing GUI
    private void drawGui(Graphics g) {
        // Draw the infobox on the top right

        drawInfobox(g);
    }

    // Draw infobox
    private void drawInfobox(Graphics g) {
        // ONLY draw if state decrees an Unit is
        // being selected:
        Unit u = state.focusedTile();
        if (u == null || !(u instanceof Displayable disp)) { return; }

        // Top left corner of infobox:
        int LEFT = Constants.SCREEN_X - TSS * 2 - MARGIN * 3;
        int TOP = MARGIN;

        g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.2f));
        g.fillRect(LEFT, TOP, TSS + MARGIN * 2, this.calculateHeightRequired(u));

        BufferedImage img = disp.getImage();
        if (img != null) {
            g.drawImage(img,
                LEFT + MARGIN,
                TOP + MARGIN,
                TSS, TSS, null
            );
        }

        TOP += TSS + MARGIN * 2;

        g.setFont(FONT);
        g.setColor(Color.BLACK);
        g.drawString(disp.getName(), LEFT + MARGIN, TOP + MARGIN);

        TOP += FONT_SIZE + MARGIN;

        if (disp instanceof Displayable.HasHealth d) {
            g.setColor(Color.BLACK);
            g.setFont(NORMAL_FONT);
            g.drawString("HP:", LEFT + MARGIN, TOP + MARGIN + 2);
            this.drawProgressBar(g, LEFT + MARGIN, TOP + MARGIN + 2 + FONT_SIZE / 2, TSS, FONT_SIZE / 2 - 4, d.getHP(), d.getMaxHP(), 1);
        }
    }

    // Paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the ground to begin
        this.drawGround(g);

        // Draw player's selected box box
        this.drawPlayerSelectedBox(g);

        // Then units
        this.drawUnits(g);

        // Entities after
        this.drawEntities(g);

        // Draw gui
        this.drawGui(g);
    }
}
