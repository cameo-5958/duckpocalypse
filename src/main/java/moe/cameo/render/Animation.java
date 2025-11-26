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
    private final int frameCount;
    private final boolean loop;

    private double timer = 0;
    private int index = 0;

    // Constructors
    protected Animation(BufferedImage[] frames, double spf, boolean loops) {
        this.frames = frames;
        this.frameTime = spf;
        this.frameCount = frames.length;
        this.loop = loops;
    }

    protected Animation newAnimation() {
        return new Animation(this.frames, this.frameTime, this.loop);
    }

    public void update(double dt) {
        // Update animation

        timer += dt;
        while (timer >= frameTime) { 
            timer -= frameTime;
            index = ++index % frameCount;
        }
    }

    // Get current frame
    public BufferedImage getFrame() {
        return frames[index];
    }
    
    // Get looping status
    public boolean loops() { return this.loop; }
    public int getFrameIndex() { return this.index; } 
    public int getFrameCount() { return this.frameCount; }

    // Reset
    public void reset() {
        timer = 0;
        index = 0;
    }
}
