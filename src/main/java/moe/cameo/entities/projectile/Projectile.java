/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities.projectile;

import java.util.List;

import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;
import moe.cameo.entities.enemy.Enemy;

/**
 *
 * @author kunru
 */
public abstract class Projectile extends Entity {
    private double vx;
    private double vy;

    public Projectile(double vx, double vy) {
        this.vx = vx; this.vy = vy;
        this.collides_with_tiles = false;
    }

    @Override
    protected void renderStepped(double dt) {
        // Move by vx, vy
        this.move(vx, vy);
    }

    @Override
    public void onCollide(GameState state, List<Enemy> collisions) { 
        // Handle collisions with enemies
        for (Enemy e : collisions) {
            // Destroy self, deal damage

        }
    }
}
