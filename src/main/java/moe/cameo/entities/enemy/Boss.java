/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public class Boss extends Enemy {
    public static final int MAX_HP_AMOUNT = 160;

    public Boss(Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 5;
        
        this.SIZE = 256;
        this.abilityCooldown = 15;

        scaleStats();
    }

    // Animator
    private final Animator animator = new Animator("HasanWalk");

    // Audios
    // private final Audio vine_boom = new Audio("vine-boom");

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
    public void onAbilityTick() {
        // For now just spawn a bunch of Shadows
        // Spawn four MiniShadows
        animator.play("HasanAttack");
        for (int i=0; i<4; i++) 
            this.board.addEntity(
                new Shadow(
                    this.board,
                    this.x, 
                    this.y,
                    this.level
                )
            );

        // vine_boom.play();
    }

    private void attack1() {
        // Delete anywhere from 1 to 5 towers
        int num = (int) (Math.random() * 5) + 1;

        // Play vine boom effect
        // vine_boom.play();

        for (int i=0; i<num; i++) {
            board.deleteRandomTower();
        }
    }
}
