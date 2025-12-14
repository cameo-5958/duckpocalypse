package moe.cameo.units.towers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Sprites;

public class Sniper extends Tower {
    // Store the arrow's image here
    private static final BufferedImage arrowImage = Sprites.load("Arrow", "/projectiles/arrow");;
    private static final BufferedImage sniperImage   = Sprites.load("SniperGun", "/towers/sniper");

    static {
        Sprites.load("Sniper", "/sprites/sniper");
    }

    protected Sniper(int x, int y) {
        super(x, y);

        this.name = "Sniper";
        this.desc = "Deals incredibly high damage to the strongest enemy. Fires incredibly slowly.";

        this.base_damages = new double[] {10, 24, 64, 160, 216};
        this.base_range = new double[] {400, 400, 600, 600, 900};
        this.base_firerate = new double[] {4, 4, 4, 2, 2};

        this.self_tower_type = TowerType.SNIPER;

        this.targets = Targetting.STRONGEST;

        this.base_cost = 2;
    }

    // Create an arrow
    private class Bullet extends Projectile {
        private final BufferedImage rotatedArrow;
        public Bullet(double x, double y, double angle) {
            super(x, y, 1600, angle);
            this.SIZE = 16;

            this.pierce = 1;
            this.damage = 0; // The bullet doesn't do damage
                             // and is purely visual

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

        @Override 
        protected boolean onDamage(Enemy e) {
            damage_dealt += doDamage(e);
            return true;
        }
    }

    @Override
    protected void onShoot() {
        // Create an arrow that fires 
        // in my direction
        fire(new Bullet(((double) this.x + 0.5) * Constants.TILE_SIZE, ((double) this.y + 0.5) * Constants.TILE_SIZE, this.direction));

        // Damage the selected enemy here
        this.damage_dealt += focusedEnemy.damage((int) getDamage());
    }

    @Override
    public BufferedImage getSprite() {
        BufferedImage underlay = super.getSprite();

        // Rather than rotate, we'll flip the 
        // slingshot image if facing left to
        // guarantee it always faces up
        BufferedImage sniper = sniperImage;
        if (this.direction >= 90 && this.direction < 270) {
            sniper = Sprites.flip(sniper);
            sniper = Sprites.rotate(sniper, this.direction - 180);
        } else {
            sniper = Sprites.rotate(sniper, this.direction);
        }

        // Add a little bit of padding to the
        // top
        BufferedImage padded = new BufferedImage(
            sniper.getWidth(), sniper.getHeight() + 24, 
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = padded.createGraphics();

        g.drawImage(sniper, 0, 24, null);
        g.dispose();

        return Sprites.overlay(underlay, padded);
    }

    @Override
    public BufferedImage getImage() {
        return Sprites.get("Sniper");
    }
}
