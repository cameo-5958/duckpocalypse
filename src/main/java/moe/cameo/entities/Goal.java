/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities;

import java.util.List;

import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.enemy.Enemy;

/**
 *
 * @author kunru
 */
public class Goal extends Entity {
    private long last_collided = System.nanoTime();
    
    public Goal() {
        super();
        this.x = Constants.SCREEN_X / 2;
        this.y = Constants.SCREEN_Y / 2;
        this.hp = 10;
        this.SIZE = 64;
    }

    @Override
    public void renderStepped(double dt) {}

    @Override
    public void onCollide(GameState state, List<Enemy> collisions) {
        // We don't care how many enemies we collided with.
        // As long as we collided with one, we'll check
        // to decrement HP.
        if (System.nanoTime() - last_collided > Constants.GRACE_TIME_PER_ATTACK * 1_000_000_000) {
            last_collided = System.nanoTime();
            this.hp -= 1;
        }
    }
}
