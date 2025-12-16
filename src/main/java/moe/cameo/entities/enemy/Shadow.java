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
public class Shadow extends Enemy {
    public static final int MAX_HP_AMOUNT = 160;

    public Shadow(Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 15;
        
        this.SIZE = 256;
        this.abilityCooldown = 8;
    }
    
    public Shadow(Board board, double x, double y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 15;
        
        this.SIZE = 256;
        this.abilityCooldown = 8;
    }
    // Animator
    private final Animator animator = new Animator("ShadowBossWalk");

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
        // Spawn four MiniShadows
        for (int i=0; i<4; i++) 
            this.board.addEntity(
                new MiniShadow(
                    this.board,
                    this.x, 
                    this.y,
                    this.level
                )
            );
    }
}