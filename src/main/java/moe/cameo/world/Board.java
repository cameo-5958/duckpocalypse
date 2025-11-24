package moe.cameo.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import moe.cameo.core.Constants;
import moe.cameo.entities.Entity;
import moe.cameo.units.Unit;

public class Board {
    // Board size
    private final int width;    
    private final int height;   

    private final List<Unit> units      = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    public Board(int width, int height) {
        // Initialize board size
        this.width = width;
        this.height = height;
    }

    // Getters
    public int getWidth()   { return this.width; }
    public int getHeight()  { return this.height; }
    public List<Unit>   getUnits() { return this.units; }
    public List<Entity> getEntities() { return this.entities; }

    // Manage UNITS (unmoveable; "troops")
    public void addUnit(Unit u)     { this.units.add(u); }
    public void removeUnit(Unit u)  { this.units.remove(u); }

    // Return all units in a tile radius 
    public List<Unit> unitsInRadius(float cx, float cy, float radiusTiles) {
        List<Unit> result = new ArrayList<>();
        float r2 = radiusTiles * radiusTiles;

        // Loop through units
        for (Unit u : this.units) {
            // Calculate pythagorean distance or whatever it's called
            float dx = (u.getX() * Constants.TILE_SIZE + 0.5f) - cx;
            float dy = (u.getY() * Constants.TILE_SIZE + 0.5f) - cy;
            if (dx*dx + dy*dy <= r2) {
                // Add to returned list
                result.add(u);
            }
        }

        return result;
    }

    // Manage ENTITIES (movable; player, "enemies")
    public void addEntity(Entity e)     { this.entities.add(e); }
    public void removeEntity(Entity e)  { this.entities.remove(e); }

    // Remove dead enemies
    public void cleanup() { this.entities.removeIf(e -> !e.isAlive()); }

    // emulate Z-depth sorting (in reality just compares Y values)
    public void sortEntities() {
        Collections.sort( this.entities, Comparator.comparing(Entity::getY));
    }
}
