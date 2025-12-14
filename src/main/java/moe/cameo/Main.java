package moe.cameo;

import javax.swing.JFrame;

import moe.cameo.core.GameControls;
import moe.cameo.core.GameLoop;
import moe.cameo.core.GameState;
import moe.cameo.render.Renderer;

public class Main {
    public static void main(String[] args) {
        // Initialize world and rendering
        GameState state = new GameState();        
        Renderer renderer = new Renderer(state);

        // Create the JFrame
        JFrame frame = new JFrame("The Duckpocalypse");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);

        // Set focusable, request focus
        renderer.setFocusable(true);
        renderer.requestFocusInWindow();

        // Register events
        GameControls.registerAllControls(state, renderer);        

        // Create the thread for the main loop
        // and start it
        Thread loop = new Thread(new GameLoop(state, renderer));
        loop.start();
    }
}