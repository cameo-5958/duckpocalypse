package moe.cameo.entities;

import java.awt.Color;
import java.util.List;

import moe.cameo.collision.Rect;
import moe.cameo.core.GameState;
import moe.cameo.entities.enemy.Enemy;

public abstract class Entity {
    protected double x;
    protected double y;
    protected int max_hp; 
    protected double hp;

    protected double direction; // In degrees

    protected Color COLOR;
    protected int SIZE;

    protected Rect collider;

    protected boolean collides_with_tiles = true;

    protected double dx, dy;

    // Default constructor
    public Entity() {
        // Initialize with 0s for
        // all values that might need it
        this.x = 0;
        this.y = 0;
        this.direction = 0;
        this.SIZE = 32;
        this.COLOR = Color.PINK;
    }

    // x,y constructor
    public Entity(float x, float y) {
        this();

        this.x = x;
        this.y = y;
    }

    // Getters
    public boolean isAlive() { return this.hp > 0; }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getDirection() { return this.direction; }

    public double getHP() { return this.hp; }
    public double getMaxHP() { return this.max_hp; }

    public int getSize()    { return this.SIZE; }
    public Color getColor() { return this.COLOR; }

    public double getDX() { return this.dx; }
    public double getDY() { return this.dy; }

    public Rect getCollider() { return this.collider; }

    // Setters
    public void changeHP(int amount) {
        this.hp += amount;

        // Cap HP
        if (this.hp < 0) {
            this.hp = 0;
        } else if (this.hp > this.max_hp) {
            this.hp = this.max_hp;
        }
    }

    public void shiftX(double dx) { this.x += dx; }
    public void shiftY(double dy) { this.y += dy; }

    // Other
    public void move(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void _renderStep(double dt) {
        // Recalculate rect
        collider = new Rect(this.x, this.y, this.SIZE, this.SIZE);

        // Reset dx, dy requests
        dx = 0; dy = 0;
        
        // Call renderStepped
        renderStepped(dt);
    }

    // Abstract methods
    public abstract void renderStepped(double dt); // mirroring RBLX renderstepped event
    public abstract void onCollide(GameState state, List<Enemy> collisions);

}
