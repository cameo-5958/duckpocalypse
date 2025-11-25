/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;
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
        g.setColor(new Color(0, 155, 0));
        g.fillRect(0, 0, SCREEN_X, SCREEN_Y);

        // Grid lines
        g.setColor(new Color(0, 100, 0));
        for (int x=TILE_SIZE; x<SCREEN_X; x += TILE_SIZE) {
            g.fillRect(x-1, 0, 2, SCREEN_Y);
        }

        for (int y=TILE_SIZE; y<SCREEN_Y; y += TILE_SIZE) {
            g.fillRect(0, y-1, SCREEN_X, 2);
        }
    }

    // Drawing entities
    private void drawEntities(Graphics g) {
        Board board = state.getBoard();

        // Get the list of entities and draw them
        for (Entity e : board.getEntities()) {
            drawEntity(g, e);
        }
    }

    private void drawEntity(Graphics g, Entity e) {
        // FOR NOW, just draw placeholder for 
        // entity location

        int es = e.getSize();
        int ex = (int) e.getX();
        int ey = (int) e.getY();

        // Main box
        g.setColor(Color.YELLOW);
        g.fillRect(ex - es / 2, ey - es / 2, es, es);

        // Border
        g.setColor(Color.BLACK);
        g.fillRect(ex - es / 2, ey - es / 2, es, es);

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

    // Paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the ground to begin
        this.drawGround(g);

        // Then units

        // Entities after
        this.drawEntities(g);
    }
}
