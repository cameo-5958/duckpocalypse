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
public class Animator {

    private Animation current = null;
    private String currentKey = null;

    private String defaultKey = null;  
    private Animation defaultAnim = null;

    private boolean playing = false;

    // Must be initialized with a default animation
    public Animator(String key) {
        defaultKey = key;
        defaultAnim = Animations.get(key);
        
        currentKey = key;
        current = defaultAnim;
        playing = true;
    }

    public void play(String key) {
        if (key == null) return;

        // If same animation is already playing, ignore
        if (key.equals(currentKey)) return;

        currentKey = key;
        current = Animations.get(key);
        playing = true;
    }

    // Force restart
    public void force(String key) {
        if (key == null) return;

        currentKey = key;
        current = Animations.get(key);
        current.reset();
        playing = true;
    }

    public void update(double dt) {
        if (current == null || !playing) return;

        current.update(dt);

        // If non-looping animation finishes, fallback to default
        if (!current.loops() && current.isFinished()) {
            // If a default is set, return to it
            if (defaultAnim != null) {
                currentKey = defaultKey;
                current = defaultAnim.newAnimation();
                current.reset();
                playing = true;
            }
        }
    }

    public BufferedImage getFrame() {
        return (current == null) ? null : current.getFrame();
    }

    public String getCurrentKey() { return currentKey; }
    public Animation getCurrentAnimation() { return current; }
    public int getFrameIndex() {
        return (current == null) ? -1 : current.getFrameIndex();    
    }
}