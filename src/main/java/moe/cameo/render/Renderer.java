/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.render;

import java.awt.Dimension;

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

    // Constructor
    public Renderer(GameState state) {
        this.state = state;

        setPreferredSize(new Dimension(
            state.getBoard().getWidth() * TILE_SIZE,
            state.getBoard().getHeight() * TILE_SIZE
        ));
    }
}
