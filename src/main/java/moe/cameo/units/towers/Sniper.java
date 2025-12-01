package moe.cameo.units.towers;

import java.awt.image.BufferedImage;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Sprites;

public class Sniper extends Tower {
    // Store the arrow's image here
    private static final BufferedImage arrowImage = Sprites.load("Arrow", "/projectiles/arrow");;
    private static final BufferedImage bowImage   = Sprites.load("Bow", "/towers/bow");

    static {
        Sprites.load("Archer", "/sprites/archer");
    }

    protected Sniper(int x, int y) {
        super(x, y);

        this.name = "Sniper";
        this.desc = "Deals incredibly high damage to the strongest enemy. Fires incredibly slowly.";

        this.base_damages = new double[] {8, 20, 44, 48, 150};
        this.base_range = new double[] {400, 400, 600, 600, 900};
        this.base_firerate = new double[] {4, 4, 4, 2, 2};

        this.self_tower_type = TowerType.SNIPER;
    }

    // Create an arrow
    private class Arrow extends Projectile {
        private final BufferedImage rotatedArrow;
        public Arrow(double x, double y, double angle) {
            super(x, y, 1200, angle);
            this.SIZE = 16;

            this.pierce = 1;
            this.damage = (int) getDamage();

            rotatedArrow = Sprites.rotate(arrowImage, angle);
        }

        @Override
        public BufferedImage getSprite() {
            return rotatedArrow;  
        }

        @Override
        public Rect getCollider() {
            return new Rect(this.x, this.y, 4, 4);
        }
    }

    @Override
    protected void onShoot() {
        // Create an arrow that fires 
        // in my direction
        fire(new Arrow(((double) this.x + 0.5) * Constants.TILE_SIZE, ((double) this.y + 0.5) * Constants.TILE_SIZE, this.direction));
    }

    @Override
    public BufferedImage getSprite() {
        BufferedImage underlay = super.getSprite();
        BufferedImage bow = Sprites.rotate(bowImage, this.direction);

        return Sprites.overlay(underlay, bow);
    }

    @Override
    public BufferedImage getImage() {
        return Sprites.get("Archer");
    }
}
