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
        this.state = state;

        SCREEN_X = state.getBoard().getWidth() * TILE_SIZE;
        SCREEN_Y = state.getBoard().getHeight() * TILE_SIZE;

        setPreferredSize(new Dimension(
            SCREEN_X,
            SCREEN_Y
        ));
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

    // Paint
    @Override
    public void paintComponent(Graphics g) {
        // Draw the ground to begin
        this.drawGround(g);
    }
}
