/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.units.towers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import moe.cameo.core.GameState;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Animator;
import moe.cameo.render.Displayable;
import moe.cameo.render.Sprites;
import moe.cameo.units.RequestsGamestates;
import moe.cameo.units.Unit;

/**
 *
 * @author kunru
 */
public abstract class Tower 
extends Unit 
implements Displayable, Displayable.HasCards, Displayable.HasLevel, Displayable.HasStats,
RequestsGamestates {
    protected String name = "Tower";
    protected String desc = "The default, abstracted Tower.";
    // Cards required: 1 (unlock), 2 (+1), 5 (+3), 12 (+7), 32 (+17)
    protected int level = 1; // it's defaulted to one since you must
                            // place the tower
    protected int cards = 1;

    protected int max_cards = 32;

    // Remember card upgrade levels
    protected final int[] upgrades_at = {1, 2, 5, 12, 32};
    protected final int max_level = upgrades_at.length;

    // Damage, cash earn stats
    protected long damage_dealt = 0;
    protected long cash_earnt   = 0;

    // Cost
    protected int base_cost = 1;

    // Tower stats
    protected double damage;
    protected double firerate;
    protected double range;

    // Damages, firerates, ranges
    protected double[] base_damages  = {1, 2, 5, 12, 32};
    protected double[] base_firerate = {0.2, 0.2, 0.2, 0.2, 0.2};
    protected double[] base_range    = {4.0, 4.0, 4.0, 4.0, 4.0};

    // For attacking
    protected double cooldown = 0.0;

    // For instantiating Projectiles
    protected final List<Projectile> queued = new ArrayList<>();

    // Animator
    protected final Animator animator = new Animator("TowerIdle");

    // Self tower type
    protected TowerType self_tower_type = TowerType.ARCHER;

    protected Tower(int x, int y) {
        super(x, y);
    }      
    
    // Store the GameState to request
    // nearest enemy queries
    GameState state;
    @Override
    public void setGameState(GameState state) { this.state = state; }

    // Displayable interfaced methods
    @Override public String getName()           { return this.name; }
    @Override public int getCards()             { return this.cards; }
    @Override public int getMaxCards()          { return this.upgrades_at[level]; }
    @Override public int getLevel()             { return this.level; }
    @Override public int getMaxLevel()          { return this.max_level; }
    @Override public BufferedImage getImage()   { return Sprites.get("NULL"); }
    @Override public double[] getStats() {
        double[] stats = { this.getDamage(), this.getFirerate(), this.getRange() };
        return stats;
    }

    // Get the projectile queue
    public List<Projectile> getQueuedProjectiles() {
        // Return this.queued, and clear it
        List<Projectile> copy = new ArrayList<>(this.queued);
        this.queued.clear();
        return copy;
    }

    // Add a new projectile
    protected final void fire(Projectile p) {
        this.queued.add(p);
    }
    
    // Public API
    public void addCard() { 
        // If we're at max cards, don't add
        if (this.cards == this.max_cards) return; 

        // Add a card
        this.cards += 1;
        
        // Check if upgrade required
        if (this.cards == this.upgrades_at[this.level]) {
            this.level += 1;
        }
    }

    public void addCard(int num_of) {
        // Js call addCard a bunch of times
        // Don't overrepeat (only add necessary num of cards)
        int repeat_for = Math.min(num_of, this.max_cards - this.cards);
        for (int i=0; i < repeat_for; i++) {
            addCard();
        }
    }

    // Getter for stats
    public double getDamage() {
        return this.base_damages[level-1];
    }

    public double getFirerate() {
        return this.base_firerate[level-1];
    }

    public double getRange() {
        return this.base_range[level-1];
    }

    public int getCost() { return this.base_cost; }
    public String getDesc() { return this.desc; }
    public TowerType getTowerType() { return this.self_tower_type; }

    // RenderStepped
    @Override
    protected void renderStepped(double dt) {
        // Check if we're allowed to shoot
        cooldown -= dt;
        if (cooldown <= 0) {
            if (this._shoot()) cooldown = this.getFirerate();
        }

        // Update 
        this.animator.update(dt);
    }

    // SHOOTING!
    private boolean _shoot() {
        // Find an enemy
        Enemy e = state.getBoard().closestEnemyInRadius(
            getSX(), getSY(), getRange()
        );

        if (e == null) { return false; }

        // Face the enemy
        this.setDirection(Math.toDegrees(Math.atan2(
            e.getY() - getSY(), e.getX() - getSX()
        )));

        // Play tower shooting animation
        this.animator.play("TowerShoot");

        onShoot();

        return true;
    }

    protected void onShoot() {}

    @Override public BufferedImage getSprite() {
        BufferedImage frame = this.animator.getFrame();
                
        // Flip the frame if necessary
        if (this.direction >= 90 && this.direction <= 270) {
            return Sprites.flip(frame);
        } 
        return frame;
    }
}
