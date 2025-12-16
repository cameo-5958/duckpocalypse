/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;

/**
 *
 * @author kunru
 */
public class Player extends Entity {
    // Store states on the movement keys
    // "Enum" values
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;

    // Store key states
    private final boolean[] key_states = new boolean[4];
    private final double acceleration = Constants.PLAYER_ACCELERATION;
    private final double decceleration = Constants.PLAYER_DECCELERATION;
    private final double max_speed = Constants.PLAYER_MAX_SPEED;
    private final double max_diagonal = Constants.PLAYER_MAX_SPEED / Math.sqrt(2.0);
    
    // Store speed
    private double vx = 0.0f;
    private double vy = 0.0f;

    // Create animator
    private final Animator mov_animator = new Animator("PlayerIdle");
    private final Animator sword_animator = new Animator("SwordIdle");

    // Flipped state
    private boolean isFlipped = false;

    // Override Constructor to start near middle
    public Player() {
        super(); 

        this.x = Constants.SCREEN_X / 2;
        this.y = Constants.SCREEN_Y / 2 + Constants.TILE_SIZE * 2;
        this.COLOR = new Color(0, 0, 160);
        this.SIZE = 128;
    } 

    // Setter
    public void setKeyState(int key, boolean down) {
        key_states[key] = down;
    }

    // Play an attack animation
    public void playAttackAnimation() {
        this.sword_animator.play("SwordAttack");
    }

    // Inherit RenderStepped
    @Override
    protected void renderStepped(double dt) {
        double max_cap = ((key_states[KEY_RIGHT] ^ key_states[KEY_LEFT]) && (key_states[KEY_UP] ^ key_states[KEY_DOWN])) ? max_diagonal : max_speed;

        // VERTICAL direction handling
        if (key_states[KEY_UP] ^ key_states[KEY_DOWN]) {
            // Accelerate in one of the two directions
            if (key_states[KEY_UP]) {
                // DECREASE vy
                this.vy -= this.acceleration * dt;
                this.vy = Math.max(this.vy, -max_cap);
            } else {
                // INCREASE vy
                this.vy += this.acceleration * dt;
                this.vy = Math.min(this.vy, max_cap);
            }
        } else {
            // Deccelerate
            if (this.vy > 0) {
                this.vy -= decceleration * dt;
                this.vy = Math.max(0, this.vy);
            } else if (this.vy < 0) {
                this.vy += decceleration * dt;
                this.vy = Math.min(0, this.vy);
            }
        }
        
        // HORIZONTAL direction handling
        if (key_states[KEY_RIGHT] ^ key_states[KEY_LEFT]) {
            // Accelerate in one of the two directions
            if (key_states[KEY_LEFT]) {
                // DECREASE vx
                this.vx -= this.acceleration * dt;
                this.vx = Math.max(this.vx, -max_cap);
            } else {
                // INCREASE vx
                this.vx += this.acceleration * dt;
                this.vx = Math.min(this.vx, max_cap);
            }
        } else {
            // Deccelerate
            if (this.vx > 0) {
                this.vx -= decceleration * dt;
                this.vx = Math.max(0, this.vx);
            } else if (this.vx < 0) {
                this.vx += decceleration * dt;
                this.vx = Math.min(0, this.vx);
            }
        }

        // Play animation if moving
        if (Math.abs(this.vx) > 0.1 || Math.abs(this.vy) > 0.1) {
            // Check flipped state
            if (this.vx < 0) this.isFlipped = true;
            else if (this.vx > 0) this.isFlipped = false;
            
            mov_animator.play("DuckWaddle");
        } else {
            mov_animator.play("PlayerIdle");
        }

        // Update animator
        mov_animator.update(dt);
        sword_animator.update(dt);

        // Move by vx, vy
        this.move(vx * dt, vy * dt);
    }   

    // Player doesn't need to handle collisions
    @Override
    public void onCollide(GameState state, List<Enemy> collisions) { }

    @Override
    public BufferedImage getSprite() {
        // Create the sprite
        double theta = Math.toRadians(direction + 90);
        BufferedImage sword = Sprites.rotate(sword_animator.getFrame(), direction+90);

        // Overlay the sword OVER the frame if the 
        // sword is attacking; otherwise, overlay it 
        // under
        // if (sword_animator.getCurrentKey().equals("SwordAttack")) {
            // frame = Sprites.overlay(frame, sword);
        // } else {
        // }

        this.SIZE = (int) Math.ceil(
    128 * (Math.abs(Math.cos(theta)) + Math.abs(Math.sin(theta)))
);


        return sword;
    }

    public BufferedImage getSpriteFront() {
        BufferedImage frame = this.mov_animator.getFrame();
        
        // Flip if facing opposite direction
        if (isFlipped) {
            frame = Sprites.flip(frame);
        }

        return frame;
    }
    public BufferedImage getSpriteBack() {        // Create the sprite
        double theta = Math.toRadians(direction + 90);
        BufferedImage sword = Sprites.rotate(sword_animator.getFrame(), direction+90);

        // Overlay the sword OVER the frame if the 
        // sword is attacking; otherwise, overlay it 
        // under
        // if (sword_animator.getCurrentKey().equals("SwordAttack")) {
            // frame = Sprites.overlay(frame, sword);
        // } else {
        // }

        this.SIZE = (int) Math.ceil(
    128 * (Math.abs(Math.cos(theta)) + Math.abs(Math.sin(theta)))
);


        return sword;}

    // Recalculate rect
    @Override
    protected void recalculateRect() {
        collider = new Rect(this.x, this.y, 64, 64);
    }
}
