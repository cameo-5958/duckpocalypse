package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;
import moe.cameo.world.Board;

public class Bat extends Enemy {
    public Bat(Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = 1;
        this.base_speed = 80;

        scaleStats();
    }

    // Animator
    private final Animator animator = new Animator("BatFly");

    // Animate
    @Override
    protected void renderStepped(double dt) {
        // Just animate the dang thing
        super.renderStepped(dt);
        animator.update(dt);
    }

    @Override
    public BufferedImage getSprite() {
        BufferedImage frame = animator.getFrame();

        // Flip if facing opposite direction
        if (dx > 0) {
            frame = Sprites.flip(frame);
        }

        return frame;
    }
}
