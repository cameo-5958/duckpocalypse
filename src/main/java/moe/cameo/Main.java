package moe.cameo;

import javax.swing.JFrame;

import moe.cameo.core.GameLoop;
import moe.cameo.core.GameState;
import moe.cameo.render.Renderer;
import moe.cameo.world.Board;

public class Main {
    public static void main(String[] args) {
        // Initialize world and rendering
        Board board = new Board(10, 8);
        GameState state = new GameState(board);        
        Renderer renderer = new Renderer(state);

        // Create the JFrame
        JFrame frame = new JFrame("The Duckpocalypse");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);

        // Create the thread for the main loop
        // and start it
        Thread loop = new Thread(new GameLoop(state, renderer));
        loop.start();
    }
}