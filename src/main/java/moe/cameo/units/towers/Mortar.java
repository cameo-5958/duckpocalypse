package moe.cameo.units.towers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Animation;
import moe.cameo.render.Animator;
import moe.cameo.render.Sprites;

public class Mortar extends Tower {
    // Store the arrow's image here
    private static final BufferedImage mortarUp = Sprites.load("FlyingOrange", "/projectiles/mortar-up");
    private static final BufferedImage mortarDown = Sprites.load("OrangeShadow", "/projectiles/mortar-down");

    private static final BufferedImage bowImage   = Sprites.load("Bow", "/towers/bow");
    
    private final double[] explosion_radii;

    static {
        Sprites.load("Archer", "/sprites/archer");
    }

    protected Mortar(int x, int y) {
        super(x, y);

        this.name = "Mortar";
        this.desc = "Has a huge range and fires splashing rocks that deal high damage.";

        this.base_damages = new double[] {4, 6, 12, 32, 48};
        this.base_range = new double[] {400.0, 400.0, 480.0, 480.0, 600.0};
        this.base_firerate = new double[] {5.0, 5.0, 4.0, 4.0, 2.0};

        this.explosion_radii = new double[] { 64, 96, 96, 128, 160 };

        this.self_tower_type = TowerType.MORTAR;
    }

    // Create an arrow
    private class FlyingRock extends Projectile {
        private final double distance; 
        public FlyingRock(double x, double y, double angle, double distance) {
            super(x, y, 3, angle);
            this.distance = distance;

            this.pierce = 1;
            this.damage = (int) getDamage();
            this.lifetime = (int) distance;
        }
        
        private double calculateZ() {
            if (this.distance <= 0) return 0;

            double t = this.travelled / this.distance;  // normalized 0 → 1
            t = Math.max(0, Math.min(1, t));

            double maxHeight = 480; 

            return 4 * maxHeight * t * (1 - t);
        }

        @Override
        public BufferedImage getSprite() {
            int z = (int) Math.abs(calculateZ());

            int minHeight = 32 + 4;

            int height = Math.max(z + 32, minHeight);

            // Create a BufferedImage
            BufferedImage temp = new BufferedImage(height + 32, height + 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = temp.createGraphics();

            // top image
            g.drawImage(mortarUp,   height / 2 - 8, 0, 32, 32, null);

            // middle image, only if height > 32
            if (height > 64)
                g.drawImage(mortarDown, height / 2 - 8, height / 2, 32, 4, null);

            // Dispose and return
            g.dispose();

            // Set size
            this.SIZE = height;

            return temp;  
        }

        @Override
        public Rect getCollider() {
            return new Rect(10000, 10000, 13, 1);
        }
        
        @Override
        public void onDestroy() {
            // Create an explosion
            fire(new Explosion(this.x, this.y, explosion_radii[level-1]));
        }
    }

    private class Explosion extends Projectile {
        private final Animator animator = new Animator("Explosion");

        Explosion(double x, double y, double explosion_radius) {
            super(x, y, 0, 0);
            this.pierce = 50;
            this.SIZE = (int) explosion_radius;

            this.damage = (int) getDamage();
        }   

        @Override 
        protected void renderStepped(double dt) {
            // Nothing else really matters since we're
            // really only focused on the animator here
            animator.update(dt);

            // Check if frame = frameSize()
            Animation a = animator.getCurrentAnimation();
            if (a.getFrameCount() - 1 == a.getFrameIndex())
                kill();
        }

        @Override
        public BufferedImage getSprite() {
            // Get the sprite from the animator
            return animator.getFrame();
        }

        @Override 
        public Rect getCollider() {
            return new Rect(this.x, this.y, this.SIZE, this.SIZE);
        }
    }

    @Override
    protected void onShoot() {
        // Begin by calculating time to get
        // to enemy
        double travel_time = distanceToEnemy / 3.0 / Constants.FPS;

        double[] future = this.focusedEnemy.predictFuturePoint(travel_time);

        double ddx = future[0] - this.getSX();
        double ddy = future[1] - this.getSY();
        double angle = Math.toDegrees(Math.atan2(ddy, ddx));

        fire(
            new FlyingRock(
                this.getSX(), this.getSY(), 
                angle, Math.sqrt(ddx * ddx + ddy * ddy))
            );
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
