/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.core.Constants;
import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public class MiniShadow extends Enemy {
    public static final int MAX_HP_AMOUNT = 10;

    public MiniShadow(Board board, int x, int y, int level) {
        this(board, (x + 0.5) * Constants.TILE_SIZE , (y + 0.5) * Constants.TILE_SIZE, level);
    }

    public MiniShadow(Board board, double x, double y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 35;
        
        this.SIZE = 64;
    }

    // Animator
    private final Animator animator = new Animator("MiniShadowWalk");

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