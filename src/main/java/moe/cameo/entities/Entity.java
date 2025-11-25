package moe.cameo.entities;

public abstract class Entity {
    protected double x;
    protected double y;
    protected int max_hp; 
    protected double hp;

    protected double direction; // In degrees

    protected final int ENTITY_SIZE = 32;

    // Default constructor
    public Entity() {
        // Initialize with 0s for
        // all values that might need it
        this.x = 0;
        this.y = 0;
        this.direction = 0;
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

    public int getSize() { return this.ENTITY_SIZE; }

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

    // Other
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    // Abstract methods
    public abstract void renderStepped(double dt); // mirroring RBLX renderstepped event
    public abstract void onCollide(Entity e);

}
