package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.render.Animation;
import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;
import moe.cameo.world.Board;

public class Slime extends Enemy {
    public static final int MAX_HP_AMOUNT = 3;

    public Slime(Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 60;
    }
    
    // Give fresh new drip
    private final Animator animator = new Animator("SlimeWalk");

    // Calculate speed based on animation progress
    private double getAnimationSyncedSpeed() {
        // Get current animation
        Animation anim = animator.getCurrentAnimation();
        if (anim == null) return this.base_speed;
        
        // Get progress through animation cycle (0 to 1)
        double progress = (double) anim.getFrameIndex() / anim.getFrameCount();
        
        // Use sin to create a smooth curve: slow -> fast -> slow
        // sin(π * progress) goes from 0 at start, to 1 at middle, back to 0 at end
        double speedMultiplier = Math.sin(Math.PI * progress);
        
        return this.base_speed * speedMultiplier;
    }

    // Animate
    @Override
    protected void renderStepped(double dt) {
        // Update speed based on animation cycle
        this.speed = getAnimationSyncedSpeed();
        
        super.renderStepped(dt);

        // Advance animator
        animator.update(dt);
    }

    @Override
    public BufferedImage getSprite() {
        BufferedImage frame = animator.getFrame();

        // Flip if facing opposite direction
        if (dx < 0) {
            frame = Sprites.flip(frame);
        }
        return frame;
    }
}
