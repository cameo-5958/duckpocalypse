package moe.cameo.units.towers;

import java.awt.image.BufferedImage;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Sprites;

public class Slinger extends Tower {
    // Store the arrow's image here
    private static final BufferedImage rockImage = Sprites.load("Rock", "/projectiles/rock");
    private static final BufferedImage splinterImage = Sprites.load("Splinter", "/projectiles/splinter");
    private static final BufferedImage slingshotImage   = Sprites.load("Slingshot", "/towers/slingshot");

    private final int[] spread_count;
    private final int[] shrapnel_damage;

    static {
        Sprites.load("Slinger", "/sprites/slinger");
    }

    protected Slinger(int x, int y) {
        super(x, y);    

        this.name = "Slinger";
        this.desc = "A slingshot-wielding tower whose projectiles split on impact.";

        this.base_damages = new double[] {1, 3, 5, 9, 8};
        this.base_range = new double[] {160.0, 200.0, 200.0, 200.0, 240.0};
        this.base_firerate = new double[] {6.0, 6.0, 6.0, 6.0, 4.0};
        this.spread_count = new int[] {3, 3, 5, 7, 13};
        this.shrapnel_damage = new int[] {1, 1, 2, 3, 4};

        this.self_tower_type = TowerType.SLINGER;
    }

    // Create rock
    private class Rock extends Projectile {
        public Rock(double x, double y, double angle) {
            super(x, y, 240, angle);
            this.SIZE = 16;

            this.pierce = 1;
            this.damage = (int) base_damages[level];
        }

        @Override
        public BufferedImage getSprite() {
            return rockImage;  
        }

        @Override
        public Rect getCollider() {
            return new Rect(this.x, this.y, 4, 4);
        }

        @Override
        public boolean onDamage(Enemy e) {
            // Do damage to the enemy and add to hits list
            e.damage(this.damage);
            hit.add(e);

            // We'll create 3 splinters
            int endpoint = 5 * (spread_count[level-1] / 2);
            for (int theta=-endpoint; theta <= endpoint; theta += 5) {
                fire(new Splinter(this.x, this.y, this.angle + theta), this.hit);
            }

            return true;
        }
    }

    private class Splinter extends Projectile {
        public final BufferedImage rotateSplinter;
        public Splinter(double x, double y, double angle) {
            super(x, y, 240, angle);
            this.SIZE = 8;

            this.pierce = 3;
            this.lifetime = 100;
            this.damage = shrapnel_damage[level-1];

            rotateSplinter = Sprites.rotate(splinterImage, angle);
        }

        @Override
        public BufferedImage getSprite() {
            return rotateSplinter;
        }

        @Override
        public Rect getCollider() {
            return new Rect(this.x, this.y, 4, 4);
        }
    }

    @Override
    protected void onShoot() {
        // Create a rock that fires 
        // in my direction
        fire(
            new Rock(((double) this.x + 0.5) * Constants.TILE_SIZE, ((double) this.y + 0.5) * Constants.TILE_SIZE, this.direction)
        );
    }

    @Override
    public BufferedImage getSprite() {
        BufferedImage underlay = super.getSprite();

        // Rather than rotate, we'll flip the 
        // slingshot image if facing left to
        // guarantee it always faces up
        BufferedImage slingshot = slingshotImage;
        if (this.direction >= 90 && this.direction < 270) {
            slingshot = Sprites.flip(slingshot);
            slingshot = Sprites.rotate(slingshot, this.direction - 180);
        } else {
            slingshot = Sprites.rotate(slingshot, this.direction);
        }

        return Sprites.overlay(underlay, slingshot);
    }

    @Override
    public BufferedImage getImage() {
        return Sprites.get("Slinger");
    }

    @Override
    public double getDamage() {
        return super.getDamage() + this.shrapnel_damage[level-1] * this.spread_count[level-1];
    }
}
