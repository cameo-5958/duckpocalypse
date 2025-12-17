/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities.enemy;

import java.awt.image.BufferedImage;

import moe.cameo.core.Constants;
import moe.cameo.render.Animator;
import moe.cameo.render.Flashbang;
import moe.cameo.render.Sprites;
import moe.cameo.sound.Audio;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public class Boss extends Enemy {
    public static final int MAX_HP_AMOUNT = 250;
    private static final String[] flashbangs = {"f1","f2","f3"};
    int i=0;
    
    static {
        Sprites.load("f1", "/flashbang/flashbang-1");
        Sprites.load("f2", "/flashbang/flashbang-2");
        Sprites.load("f3", "/flashbang/flashbang-3");
    }

    public Boss(Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = MAX_HP_AMOUNT;
        this.base_speed = 5;
        
        this.SIZE = 256;
        this.abilityCooldown = 15;
    }

    // Animator
    private final Animator animator = new Animator("HasanWalk");

    // Audios
    private final Audio vine_boom = new Audio("vine-boom");
    // Catch the ith frame for the ability
    // Bounce variable
    private boolean bouncer = false;
    private double NORM_SPEED = 0;

    // Animate
    @Override
    protected void renderStepped(double dt) {
        // Just animate the dang thing
        super.renderStepped(dt);
        animator.update(dt);
        this.NORM_SPEED = (this.speed != 0 ) ? this.speed : this.NORM_SPEED;

        if (animator.getCurrentKey().equals("HasanAttack") && 
            animator.getFrameIndex() == 4) {
            if (!bouncer) {
                attack();
                bouncer = true;
            }
        } else {
            bouncer = false;
        }

        if (animator.getCurrentKey().equals("HasanWalk")) {
            this.speed = NORM_SPEED;
        }
    }

    private void attack() {
        // The REAL attack method, fired
        // on sprite i of the animator
        vine_boom.play();

        this.board.deleteRandomTowers((int) (Math.random() * 2) + 1);
        Flashbang.queue(Sprites.get(flashbangs[i++]));
        i%=3;
    }

    private boolean facing_left = x < Constants.SCREEN_X / 2;
    @Override
    public BufferedImage getSprite() {
        BufferedImage frame = animator.getFrame();

        // Flip if direction changed
        if (dx != 0) {
            if (facing_left != (dx > 0)) {
                facing_left = dx > 0;
            }
        }

        // Flip if facing opposite direction
        if (!facing_left) {
            frame = Sprites.flip(frame);
        }

        return frame;
    }

    @Override 
    public void onAbilityTick() {
        // Play the animation (the code will
        // automatically switch back) and stop
        // movement
        animator.play("HasanAttack");

        this.speed = 0;
    }
}
