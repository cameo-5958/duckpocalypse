/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities;

import java.awt.Color;
import java.util.List;

import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.enemy.Enemy;

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

    // Override Constructor to start near middle
    public Player() {
        super(); 

        this.x = Constants.SCREEN_X / 2;
        this.y = Constants.SCREEN_Y / 2;
        this.COLOR = new Color(0, 0, 160);
    } 

    // Setter
    public void setKeyState(int key, boolean down) {
        key_states[key] = down;
    }

    // Inherit RenderStepped
    @Override
    public void renderStepped(double dt) {
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


    
        // Change direction if vx > 0, etc.
        if (vx > 0) { this.direction = 0; }
        else if (vx < 0) { this.direction = 180; }
        
        // Move by vx, vy
        this.move(vx * dt, vy * dt);
    }   

    // Player doesn't need to handle collisions
    @Override
    public void onCollide(GameState state, List<Enemy> collisions) { }
}
