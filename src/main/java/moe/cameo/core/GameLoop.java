/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import moe.cameo.render.Renderer;

/**
 *
 * @author kunru
 */
public class GameLoop implements Runnable{
    // Store state, rendering, running state
    private final GameState state;
    private final Renderer renderer; 
    private boolean running = true;

    // Calculate per-frame waiting time
    private static final int FRAME_TIME_MS = 1000 / Constants.FPS;

    public GameLoop(GameState state, Renderer renderer) {
        this.state = state;
        this.renderer = renderer;
    }

    @Override
    public void run() {
        // Main loop
        long prev = System.nanoTime();

        while (running && !state.isGameOver()) {
            // Calculate deltatime for other processes
            long now = System.nanoTime();
            float dt = (now - prev) / 1_000_000_000f;
            prev = now;

            state.update(dt);
            renderer.repaint();

            try {
                Thread.sleep(FRAME_TIME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
}
