package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;
import moe.cameo.world.Board;

public class OrangeGolem extends Enemy {
    public static final int MAX_HP_AMOUNT = 40;

    public OrangeGolem(Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 10;
        this.SIZE = 96;

        scaleStats();
    }

    // Animator
    private final Animator animator = new Animator("OrangeGolemWalk");

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

    @Override
    protected void onDeath() {
        for (int i=0; i<2; i++) 
            this.board.addEntity(
                new BlueGolem(
                    this.board,
                    this.x + (Math.random() - 0.5) * 16, 
                    this.y + (Math.random() - 0.5) * 16,
                    this.level
                )
            );
    }
}
