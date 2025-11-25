/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.render;

import java.awt.image.BufferedImage;

/**
 *
 * @author kunru
 */

// Animation class

public class Animation {
    private final BufferedImage[] frames;
    private final double frameTime;
    private double timer = 0;
    private int index = 0;

    // Constructors
    public Animation(BufferedImage[] frames, double fps) {
        this.frames = frames;
        this.frameTime = fps;
    }
    public Animation(BufferedImage[] frames) {
        this(frames, Double.POSITIVE_INFINITY);
    }

    public void update(double dt) {
        // Update animation

        // Single frame animation
        if (frameTime == Double.POSITIVE_INFINITY) return; 

        timer += dt;
        while (timer >= frameTime) { 
            timer -= frameTime;
            index = ++index % frames.length;
        }
    }

    // Get current frame
    public BufferedImage getFrame() {
        return frames[index];
    }

    // Reset
    public void reset() {
        timer = 0;
        index = 0;
    }
}
