package moe.cameo.units.towers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import moe.cameo.core.Constants;
import moe.cameo.render.Sprites;
import moe.cameo.render.Widget;

public class Farmer extends Tower {
    // Store the sickle's image here
    private static final BufferedImage sickleImage   = Sprites.load("Sickle", "/towers/sickle");

    static {
        Sprites.load("Farmer", "/sprites/farmer");
    }

    // Create the Farmer Widget
    private class EarnedDisplay extends Widget {
        private final List<Float> nums = new ArrayList<>();
        private static final Font FONT = Constants.BUTTON_FONT;

        EarnedDisplay() {
            // This appears above the Farmer's head
            super((int) Farmer.this.getSX() - 32, (int) Farmer.this.getSY() - 80, 64, 64, 
                new Color(0,0,0,0), new Color(0,0,0,0));
        }

        @Override
        public void draw(Graphics2D g) {
            g.setFont(FONT);
            g.setColor(new Color(255, 255, 255));
            for (int i=0; i<nums.size();i++) {
                g.drawString("+1", 0.0f, nums.get(i)+10);
                nums.set(i, nums.get(i) - 0.5f);
            }

            for (int i=nums.size()-1; i>=0; i--) {
                if (nums.get(i) == 0) { nums.remove(i); }
            }
        }

        private void _hsc() {
            nums.add(64.0f);
        }
    }

    // Money earned
    private double earned = 0.0;
    private final EarnedDisplay hit_display;

    protected Farmer(int x, int y) {
        super(x, y);

        this.name = "Farmer";
        this.desc = "His sickle gives you money per hit. Deals melee damage";

        this.base_damages = new double[] {2, 4, 8, 14, 30};
        this.base_range = new double[] {100.0, 100.0, 100.0, 100.0, 100.0};
        this.base_firerate = new double[] {5, 4.5, 4, 3.5, 2.5};

        this.self_tower_type = TowerType.FARMER;
        this.base_cost = 2;

        this.hit_display = new EarnedDisplay();
    }

    @Override
    protected void onShoot() {
        // Deal damage, give money
        this.damage_dealt += this.focusedEnemy.damage((int) this.getDamage());
        earned += 1;
        
        this.hit_display._hsc();
    }

    @Override
    public BufferedImage getSprite() {
        BufferedImage underlay = super.getSprite();
        BufferedImage sickle = Sprites.rotate(sickleImage, this.direction);

        return Sprites.overlay(underlay, sickle);
    }

    @Override
    public BufferedImage getImage() {
        return Sprites.get("Farmer");
    }

    public double getEarned() {
        double e = earned;
        earned = 0;
        return e;
    }

    public Widget getThingy() {
        return this.hit_display;
    }
}
