package moe.cameo.world;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.entities.Entity;
import moe.cameo.units.Unit;
import moe.cameo.units.UnitType;

public class Board {
    // Board size
    private final int width;    
    private final int height;   

    // Occupied
    private final Unit[][] unit_locations;
    private final Rect[][] tile_colliders;

    // Distance array
    private final int[][] distances;

    private final List<Unit> units      = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {1, 0, -1, 0};

    public Board(int width, int height) {
        // Initialize board size
        this.width = width;
        this.height = height;

        // Create occupied
        unit_locations = new Unit[height][width];
        for (int x=0; x<width; x++) {
            this.place(UnitType.TREE, x, 0);
            this.place(UnitType.TREE, 0, height-1);
        }
        for (int y=0; y<height; y++) {
            this.place(UnitType.TREE, 0, y);
            this.place(UnitType.TREE, width-1, y);
        }

        // Colliders
        tile_colliders = new Rect[height][width];
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                tile_colliders[y][x] = tileRect(x, y);
            }
        }

        // Initialize distances
        distances = new int[height][width];
    }

    private void place(Unit u, int x, int y) {
        // !! DANGER !! doesn't check location occupancy
        unit_locations[y][x] = u;
        this.units.add(u);

        // Call unit's onPlace
        u.onPlace();
    }

    private void place(UnitType ut, int x, int y) {
        // !! DANGER !! doesn't check location occupancy
        Unit new_unit = ut.create(x, y);
        this.place(new_unit, x, y);
    }

    // Getters
    public int getWidth()   { return this.width; }
    public int getHeight()  { return this.height; }
    public List<Unit>   getUnits()      { return this.units; }
    public List<Entity> getEntities()   { return this.entities; }

    public boolean inBounds(int x, int y) {
        return !(x < 0 || x >= this.width || y < 0 || y >= this.height);
    }

    public boolean getOccupied(int x, int y) {
        if (!inBounds(x, y)) return true;

        return this.unit_locations[y][x] != null;
    }

    public Unit getUnitAt(int x, int y) {
        if (!inBounds(x, y)) return null;

        return this.unit_locations[y][x];
    }

    public static Rect tileRect(double tx, double ty) {
        return new Rect((tx + 0.5) * Constants.TILE_SIZE, (ty + 0.5) * Constants.TILE_SIZE, Constants.TILE_HITBOX_SIZE, Constants.TILE_HITBOX_SIZE);
    }

    public Rect getTileCollider(int tx, int ty) {
        if (this.getOccupied(tx, ty)) {
            return this.tile_colliders[ty][tx];
        }

        return Rect.NULL;
    }

    public void calculateDistanceArray() {
        int tcx = (width / 2) - 1;
        int tcy = (height / 2) - 1;
        // Clear current distance field
        for (int y=0; y < height; y++) 
            for (int x=0; x < width; x++) 
                if (x != tcx && x != tcx+1 &&
                    y != tcy && y != tcy+1)
                    distances[y][x] = Integer.MAX_VALUE;
                else
                    distances[y][x] = 0;

        // Create a deque
        ArrayDeque<Point> q = new ArrayDeque<>();

        q.add(new Point(tcx, tcy));
        q.add(new Point(tcx+1, tcy));
        q.add(new Point(tcx, tcy+1));
        q.add(new Point(tcx+1, tcy+1));

        // Repeat until distance array is filled
        while (!q.isEmpty()) {
            Point p = q.poll();
            int d = distances[p.y][p.x];

            for (int i=0; i<4; i++) {
                int nx = p.x + DX[i];
                int ny = p.y + DY[i];

                if (!inBounds(nx, ny)) continue;
                if (getOccupied(nx, ny)) continue;

                if (distances[ny][nx] > d) {
                    distances[ny][nx] = d + 1;
                    q.add(new Point(nx, ny));
                }
            }
        }
    
    }

    // Manage UNITS (unmoveable; "troops")
    public void removeUnit(Unit u)  { 
        this.removeUnit(u.getX(), u.getY());
    }

    public void removeUnit(int x, int y) {
        Unit u = this.getUnitAt(x, y);
        this.units.remove(u);
        unit_locations[y][x] = null;
    }

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

    // renderStepped
    public void renderStepped(double dt) {
        // Update entities first
        for (Entity e : this.entities) {
            e._renderStep(dt);
        }

        // Move each entity

        // Sort the entities list
        this.sortEntities();
    }
}
