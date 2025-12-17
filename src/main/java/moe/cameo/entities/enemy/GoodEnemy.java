package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;
import moe.cameo.sound.Audio;
import moe.cameo.world.Board;

public class GoodEnemy extends Enemy {
    public static final int MAX_HP_AMOUNT = 5;
    private final Audio audio = new Audio("good-enemy-calling-sound");

    public GoodEnemy(Board board, double x, double y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 90;
        this.SIZE = 48;
    }

    public GoodEnemy(Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 90;
        this.SIZE = 48;

        audio.play();
    }

    // Animator
    private final Animator animator = new Animator("GoodEnemy");

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
