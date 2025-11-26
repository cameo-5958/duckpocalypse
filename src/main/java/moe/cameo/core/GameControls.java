/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import moe.cameo.entities.Player;
import moe.cameo.render.Renderer;

/**
 *
 * @author kunru
 */
public class GameControls {
    // Static method registering keystrokes
    // Stateful

    public static void registerAllControls(GameState state, Renderer renderer) {
        // ENABLE methods for registration
        registerPlayerMovementControls(state, renderer);
        enablePlayerMousePosition(state, renderer);
    }

    // Player Movement Controls
    private final static String[] KEYS = {"W", "A", "S", "D"};
    private final static String[] ACTION_MAP = {"Up", "Left", "Down", "Right"};
    private final static int[] KEY_MAP = {
        Player.KEY_UP, Player.KEY_LEFT, Player.KEY_DOWN, Player.KEY_RIGHT
    };
    public static void registerPlayerMovementControls(GameState state, Renderer renderer) {
        // Loop through keys
        for (int i=0; i < KEYS.length; i++) {
            // Pull context from maps
            final int key = KEY_MAP[i];
            String k = KEYS[i];
            String a = ACTION_MAP[i];

            // Register input on InputMap
            renderer.getInputMap().put(KeyStroke.getKeyStroke("pressed " + k), "move" + a);
            renderer.getInputMap().put(KeyStroke.getKeyStroke("released " + k), "stop" + a);

            // Register actions on ActionMap
            renderer.getActionMap().put("move" + a, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    state.getPlayer().setKeyState(key, true);
                }
            });

            renderer.getActionMap().put("stop" + a, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    state.getPlayer().setKeyState(key, false);
                }
            });
        }
    }

    // Mouse position
    private static void enablePlayerMousePosition(GameState state, Renderer renderer) {
        renderer.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                state.setMouseX(e.getX());
                state.setMouseY(e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {}
        });
    }
}
