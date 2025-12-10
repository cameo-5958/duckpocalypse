/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.units.towers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import moe.cameo.collision.Rect;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;

/**
 *
 * @author kunru
 */
public class Gambler extends Tower {
    private final Animator dice_animator = new Animator("GambleRoll");
    private final String[] faces = {
        "GambleOne", "GambleTwo", "GambleThree",
        "GambleFour", "GambleFive", "GambleSix"
    };

    private static final BufferedImage COIN_IMAGE = Sprites.load("Coin", "/projectiles/coin");

    static {
        Sprites.load("GamblerSprite", "/sprites/gambler");
    }

    protected Gambler(int x, int y) {
        super(x, y);

        this.name = "Gambler";
        this.desc = "Roll the dice: the higher you roll, the more enemies you obliterate.";

        this.base_damages = new double[] {1, 2, 6, 12, 12};
        this.base_range = new double[] {240, 240.0, 280.0, 280.0, 350.0};
        this.base_firerate = new double[] {2.5, 2.5, 3.0, 3.0, 1};

        this.self_tower_type = TowerType.GAMBLER;
        this.base_cost = 3;
    }

    private class Coin extends Projectile {
        private Enemy target;
        public Coin(double x, double y, double angle, Enemy target) {
            super(x, y, 0.0, angle);
            this.SIZE = 16;

            this.pierce = 2;
            this.damage = (int) getDamage();    
            this.lifetime = 2000;        
            this.target = target;
        }

        private void recalculateTrajectory(double dt) {
            // Calculate distance between me and currently_targetted,
            // if currently_targetted exists
            vmag += 150.0 * dt;
            vmag = Math.min(vmag, 400.0);

            if (this.target == null) {
                lifetime = 0;
                return;
            }

            // Check the angle that we're pointing in 
            // relative to our target
            double drx = this.target.getX() - x;
            double dry = this.target.getY() - y;
            // double dist = Math.sqrt(drx*drx + dry*dry);

            double desired = Math.toDegrees(Math.atan2(dry, drx));

            // If need to turn LEFT (within requirement and requirement + 180, mod)
            double diff = (desired - angle + 540) % 360 - 180;

            double turnSpeed = 720.0 * (vmag / 800.0) * dt;  // degrees per second

            // If this is sufficiently small, set the angle to desired
            if (Math.abs(diff) < 5)  angle = desired;
            else {
                // Clamp here
                angle += Math.max(-turnSpeed, Math.min(turnSpeed, diff));
            }                     

            double angleRad = Math.toRadians(angle);
            this.vx = vmag * Math.cos(angleRad);
            this.vy = vmag * Math.sin(angleRad);
        }

        double last_clear = 0;
        @Override
        protected void renderStepped(double dt) {
            super.renderStepped(dt);

            this.recalculateTrajectory(dt);

            // If last clear > 0.5, clear
            last_clear += dt;
            if (last_clear > 0.5) { last_clear = 0; this.hit.clear(); }

            // Recalculate if necessary
            if (this.target != null && this.target.getHP() <= 0) {
                target = Targetting.ALL.getEnemy(this.x, this.y, 9999, state.getBoard());
            }
        }

        @Override
        public BufferedImage getSprite() {
            return COIN_IMAGE;
        }

        @Override
        public Rect getCollider() {
            return new Rect(this.x, this.y, 4, 4);
        }

        @Override 
        protected boolean onDamage(Enemy e) {
            damage_dealt += super.doDamage(e);

            return true;
        }
    }

    @Override
    public BufferedImage getSprite() {
        BufferedImage overlay = super.getSprite();
        BufferedImage dice = Sprites.resize(dice_animator.getFrame(), 32);

        // Padding
                // Add a little bit of padding to the
        // top
        BufferedImage padded = new BufferedImage(
            dice.getWidth(), dice.getHeight() + 24, 
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = padded.createGraphics();

        g.drawImage(dice, 0, 28, null);
        g.dispose();

        // Flip the frame if necessary
        if (!(this.direction >= 90 && this.direction <= 270)) {
            padded = Sprites.flip(padded);
        } 

        return Sprites.overlay(overlay, padded);
    }
    
    @Override 
    protected void onShoot() {
        // Play the one, two, etc. roll
        // Select a random face to begin
        int roll = (int) (Math.random() * 6);
        dice_animator.play(faces[roll]);

        switch (roll) {
            case 3 -> {
                fire(new Coin(this.getSX(), this.getSY(), this.direction, focusedEnemy));
            }

            case 4 -> {
                for (int i=-1; i<=1; i++)
                    fire(new Coin(this.getSX(), this.getSY(), this.direction + (i * 45), focusedEnemy));
            }

            case 5 -> {
                for (int i=-3; i<3; i++) {
                    fire(new Coin(this.getSX(), this.getSY(), this.direction + (i * 60), focusedEnemy));
                }
            }
        }
    }
    
    @Override 
    protected void renderStepped(double dt) {
        super.renderStepped(dt);
        dice_animator.update(dt);
    }

    @Override
    public BufferedImage getImage() {
        return Sprites.get("GamblerSprite");
    }
}
