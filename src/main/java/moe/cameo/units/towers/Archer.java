package moe.cameo.units.towers;

import java.awt.image.BufferedImage;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Sprites;

public class Archer extends Tower {
    // Store the arrow's image here
    private static final BufferedImage arrowImage = Sprites.load("Arrow", "/projectiles/arrow");;

    protected Archer(int x, int y) {
        super(x, y);

        this.name = "Archer";

        this.base_damages = new double[] {1, 3, 12, 25, 50};
        this.base_range = new double[] {320.0, 320.0, 320.0, 320.0, 320.0};
        this.base_firerate = new double[] {1.0, 0.95, 0.9, 0.85, 0.8};
    }

    // Create an arrow
    private class Arrow extends Projectile {
        private final BufferedImage rotatedArrow;
        public Arrow(double x, double y, double angle) {
            super(x, y, 10, angle);
            this.x = x;
            this.y = y;
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
}
