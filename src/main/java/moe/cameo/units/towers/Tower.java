/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.units.towers;

import java.awt.image.BufferedImage;

import moe.cameo.core.GameState;
import moe.cameo.entities.enemy.Enemy;
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
    protected final int base_cost = 1;

    // Tower stats
    protected double damage;
    protected double firerate;
    protected double range;

    // Damages, firerates, ranges
    protected final double[] base_damages  = {1, 2, 5, 12, 32};
    protected final double[] base_firerate = {0.2, 0.2, 0.2, 0.2, 0.2};
    protected final double[] base_range    = {4.0, 4.0, 4.0, 4.0, 4.0};

    // For attacking
    protected double cooldown = 0.0;

    // MUST create a projectile

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
    @Override public int getMaxCards()          { return this.max_cards; }
    @Override public int getLevel()             { return this.level; }
    @Override public int getMaxLevel()          { return this.max_level; }
    @Override public BufferedImage getImage()   { return Sprites.get("NULL"); }
    @Override public double[] getStats() {
        double[] stats = { this.getDamage(), this.getFirerate(), this.getRange() };
        return stats;
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

    // RenderStepped
    @Override
    protected void renderStepped(double dt) {
        // Check if we're allowed to shoot
        cooldown -= dt;
        if (cooldown <= 0) {
            this._shoot();
            cooldown = this.getFirerate();
        }
    }

    // SHOOTING!
    private void _shoot() {
        // Find an enemy
        Enemy e = state.getBoard().closestEnemyInRadius(
            getSX(), getSY(), getRange()
        );

        // Face the enemy
        this.direction = Math.toDegrees(Math.atan2(
            e.getY() - getSY(), e.getX() - getSX()
        ));

        onShoot();
    }

    protected void onShoot() {}
}
