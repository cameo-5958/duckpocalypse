package moe.cameo.entities;

public class Entity {
    private float x;
    private float y;
    private int max_hp; 
    private int hp;

    // Getters
    public boolean isAlive() { return this.hp > 0; }
    public float getX() { return this.x; }
    public float getY() { return this.y; }

}
