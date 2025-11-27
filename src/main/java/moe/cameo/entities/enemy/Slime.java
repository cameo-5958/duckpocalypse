package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;

public class Slime extends Enemy {
    public Slime(moe.cameo.world.Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = 5;
        this.baseSpeed = 60;

        scaleStats();
    }
    
    // Give fresh new drip
    private final Animator animator = new Animator("SlimeWalk");

    // Animate
    @Override
    protected void renderStepped(double dt) {
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
