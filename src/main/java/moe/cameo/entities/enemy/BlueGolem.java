package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.core.Constants;
import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;
import moe.cameo.world.Board;

public class BlueGolem extends Enemy {
    public BlueGolem(Board board, int x, int y, int level) {
        this(board,             
            (x + 0.5f) * Constants.TILE_SIZE,
            (y + 0.5f) * Constants.TILE_SIZE,
            level);
    }

    public BlueGolem(Board board, double x, double y, int level) {
        super(board, x, y, level);

        this.max_hp = 30;
        this.base_speed = 15;

        scaleStats();
    }

    // Animator
    private final Animator animator = new Animator("BlueGolemWalk");

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
        if (dx < 0) {
            frame = Sprites.flip(frame);
        }

        return frame;
    }
}
