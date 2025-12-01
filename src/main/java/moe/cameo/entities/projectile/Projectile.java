/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities.projectile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;
import moe.cameo.entities.enemy.Enemy;

/**
 *
 * @author kunru
 */
public abstract class Projectile extends Entity {
    protected double vx;
    protected double vy;
    protected double vmag;
    protected double angle;

    protected int pierce;
    protected int damage;

    protected int lifetime;
    protected double travelled = 0.0;

    protected final List<Enemy> hit = new ArrayList<>();

    protected Projectile(double x, double y, double vmag, double angle) {
        super(x, y);

        double angleRad = Math.toRadians(angle);
        this.vx = vmag * Math.cos(angleRad);
        this.vy = vmag * Math.sin(angleRad);
        this.vmag = vmag;
        this.collides_with_tiles = false;
        this.angle = angle;

        // Be sure to set lifetime so projectiles
        // kill themselves eventually
        this.lifetime = Constants.SCREEN_X + Constants.SCREEN_Y;
    }

    protected void kill() {
        this.hp = 0;
        onDestroy();
    }

    @Override
    protected void renderStepped(double dt) {
        // Move by vx, vy
        this.move(vx * dt, vy * dt);

        // Decrease lifetime and increase travelled
        this.lifetime -= vmag * dt;
        this.travelled += vmag * dt;

        // Kill self if lifetime elapsed
        if (this.lifetime <= 0) { 
            kill();    
        }
    }

    @Override
    public void onCollide(GameState state, List<Enemy> collisions) { 
        // Handle collisions with enemies
        for (Enemy e : collisions) {
            // Decrement pierce, use onDamage
            if (this.onDamage(e)) this.pierce -= 1;

            // No more pierce: kill self
            if (this.pierce == 0) {
                kill();
                return;
            }
        }
    }

    @Override
    public boolean mayICollide(Enemy e) {
        // Can only hit enemies we haven't hit before
        return !hit.contains(e);
    }

    protected abstract boolean onDamage(Enemy e);

    protected int doDamage(Enemy e) {
        hit.add(e);
        return e.damage(this.damage);
    }

    // Add to hit list
    public void addTohitList(List<Enemy> enemies) {
        for (Enemy e : enemies) {
            hit.add(e);
        }
    }

    @Override
    public BufferedImage getSprite() {
        return null;
    }

    public void onDestroy() { }
}
