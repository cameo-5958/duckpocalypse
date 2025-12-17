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
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.units.Spawner;
import moe.cameo.units.Unit;
import moe.cameo.units.UnitType;
import moe.cameo.units.towers.Tower;

public class Board {
    // Board size
    private final int width;    
    private final int height;   

    // Occupied
    private final Unit[][] unit_locations;
    private final Rect[][] tile_colliders;

    // Distance array
    private final int[][] distances;

    // Legality array
    private final boolean[][] legalPlacement;

    private final List<Unit> units      = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private final List<Spawner> spawners = new ArrayList<>();

    private static final int NUM_SPAWNERS = (int) (Math.random() * 3) + 2;

    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {1, 0, -1, 0};

    private int enemy_count = 0;

    public Board(int width, int height) {
        // Initialize board size
        this.width = width;
        this.height = height;

        // Define unit_locations
        unit_locations = new Unit[height][width];

        // Colliders
        tile_colliders = new Rect[height][width];
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                tile_colliders[y][x] = tileRect(x, y);
            }
        }

        // Initialize distances
        distances = new int[height][width];
        legalPlacement = new boolean[height][width];

        // Create borders
        this.defineInitialBorders();
    }

    private void defineInitialBorders() {
        // Create list of all points
        List<Point> border = new ArrayList<>();
        for (int x=0; x<width; x++) {
            border.add(new Point(x, 0));
            border.add(new Point(x, height-1));
        }
        for (int y=0; y<height; y++) {
            border.add(new Point(0, y));
            border.add(new Point(width-1, y));
        }

        // Randomly shuffle border points
        Collections.shuffle(border);

        // Remove first to_keep points and create spawners
        int removed = 0;
        int i = 0;
        while (removed < NUM_SPAWNERS) {
            Point p = border.get(i);

            // Confirm not corner point
            if ((p.x == 0 || p.x == width-1) && (p.y == 0 || p.y == height-1)) { i++; continue; }

            // Place a spawner
            this.place(UnitType.SPAWNER, p.x, p.y);
            spawners.add((Spawner) this.getUnitAt(p.x, p.y));
            
            // Obliterate the collider
            this.tile_colliders[p.y][p.x] = Rect.NULL;

            removed++;
            border.remove(i);
            i++;
        }

        // Add trees everywhere else
        for (Point p : border) {
            this.place(UnitType.TREE, p.x, p.y);    
        }
    }

    // Get a random spawner
    public Spawner getRandomSpawner() {
        return spawners.get((int) (Math.random() * NUM_SPAWNERS));
    }

    private Unit place(Unit u, int x, int y) {
        // !! DANGER !! doesn't check location occupancy
        // SUPER version. everyone calls this
        unit_locations[y][x] = u;
        this.units.add(u);

        // Recalculate distance grid
        this.boardChanged();

        // Call unit's onPlace
        u.onPlace();

        return u;
    }

    private Unit place(UnitType ut, int x, int y) {
        // !! DANGER !! doesn't check location occupancy
        Unit new_unit = ut.create(x, y);
        return this.place(new_unit, x, y);
    }

    public Unit addUnit(Unit u, int x, int y) {
        // Check location
        if (this.getOccupied(x, y)) { return null; }

        return this.place(u, x, y);
    }

    public Unit addUnit(UnitType ut, int x, int y) {
        // Check location
        if (this.getOccupied(x, y)) { return null; }

        return this.place(ut, x, y);
    }

    // Getters
    public int getWidth()   { return this.width; }
    public int getHeight()  { return this.height; }
    public List<Unit>   getUnits()      { return this.units; }
    public List<Entity> getEntities()   { return this.entities; }
    
    public boolean stillEnemies() { return this.enemy_count > 0; }

    public int getDistanceAt(int x, int y) {
        if (!inBounds(x, y)) return Integer.MAX_VALUE;

        return this.distances[y][x];
    }

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
        if (!this.inBounds(tx, ty)) {
            return tileRect(tx, ty);
        }

        if (this.getOccupied(tx, ty)) {
            return this.tile_colliders[ty][tx];
        }

        return Rect.NULL;
    }

    public boolean isLegalPlacement(int x, int y) {
        if (!inBounds(x, y)) return false;
    
        return legalPlacement[y][x];
    }

    // Recalculating methods
    public void calculateDistanceArray() {
        int tcx = (width / 2) - 1;
        int tcy = (height / 2) - 1;
        // Clear current distance field
        for (int y=0; y < height; y++) 
            for (int x=0; x < width; x++) 
                distances[y][x] =   (x == tcx || x == tcx + 1) &&
                                    (y == tcy || y == tcy + 1) ? 0 : Integer.MAX_VALUE;

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
                if (getOccupied(nx, ny) && !(getUnitAt(nx, ny) instanceof Spawner)) continue;

                if (distances[ny][nx] > d + 1) {
                    distances[ny][nx] = d + 1;
                    q.add(new Point(nx, ny));
                }
            }
        }
    }

    // Calculate legal placement positions
    private void calculateLegalPlacementPositions() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Must be empty to place
                if (getOccupied(x, y)) {
                    legalPlacement[y][x] = false;
                    continue;
                }

                // Temporarily block tile
                unit_locations[y][x] = UnitType.NULL.create(x, y);

                // Recalculate distances
                calculateDistanceArray();

                boolean valid = true;

                // All enemies must still reach the goal
                for (Spawner s : spawners) {
                    if (getDistanceAt(s.getX(), s.getY()) == Integer.MAX_VALUE) {
                        valid = false;
                    }
                }

                legalPlacement[y][x] = valid;

                // Restore tile
                unit_locations[y][x] = null;
            }
        }

        // Restore real distance field
        calculateDistanceArray();
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
    public List<Unit> unitsInRadius(double cx, double cy, double radiusTiles) {
        List<Unit> result = new ArrayList<>();
        double r2 = radiusTiles * radiusTiles;

        // Loop through units
        for (Unit u : this.units) {
            // Calculate pythagorean distance or whatever it's called
            double dx = (u.getX() * Constants.TILE_SIZE + 0.5f) - cx;
            double dy = (u.getY() * Constants.TILE_SIZE + 0.5f) - cy;
            if (dx*dx + dy*dy <= r2) {
                // Add to returned list
                result.add(u);
            }
        }

        return result;
    }

    // Manage ENTITIES (movable; player, "enemies")
    public void addEntity(Entity e)     { 
        this.entities.add(e);
        if (e instanceof Enemy) enemy_count++;
    }
    public void removeEntity(Entity e)  { this.entities.remove(e); 
        if (e instanceof Enemy) enemy_count--;
    }

    // Remove dead enemies
    public void cleanup() { this.entities.removeIf(e -> !e.isAlive()); }

    // emulate Z-depth sorting (in reality just compares Y values)
    public void sortEntities() {
        Collections.sort(this.entities, Comparator.comparing(Entity::getY));
    }

    // Return all entities in a tile radius 
    public List<Entity> entitiesInRadius(double cx, double cy, double radiusTiles) {
        List<Entity> result = new ArrayList<>();
        double r2 = radiusTiles * radiusTiles;

        // Loop through units
        for (Entity e : this.entities) {
            // Calculate pythagorean distance or whatever it's called
            double dx = e.getX() - cx;
            double dy = e.getY() - cy;
            if (dx*dx + dy*dy <= r2) {
                // Add to returned list
                result.add(e);
            }
        }

        // Sort list before returning
        Collections.sort(result, Comparator.comparing(Entity::getY));
        return result;
    }

    // Return closest enemy 
    public Enemy closestEnemyInRadius(double cx, double cy, double radius) {
        Enemy result = null;
        double distance = radius * radius;

        // Loop through units
        for (Entity e : this.entities) {
            if (!(e instanceof Enemy enem)) continue;

            // Calculate pythagorean distance or whatever it's called
            double dx = e.getX() - cx;
            double dy = e.getY() - cy;
            double dm = dx * dx + dy * dy;
            if (dm < distance) {
                // In range, or closer
                result = enem;  
                distance = dm;              
            }
        }

        // Return closest enemy
        return result;
    }

    // Return strongest enemy
    public Enemy strongestEnemyInRadius(double cx, double cy, double radius) {
        Enemy result = null;
        double hp = -1;

        // Loop through units
        for (Entity e : this.entities) {
            if (!(e instanceof Enemy enem)) continue;

            // Calculate pythagorean distance or whatever it's called
            double dx = e.getX() - cx;
            double dy = e.getY() - cy;
            double dm = dx * dx + dy * dy;
            if (dm < radius * radius && enem.getHP() > hp) {
                // In range and stronger
                result = enem;  
                hp = enem.getHP();
            }
        }

        // Return closest enemy
        return result;
    }

    // boardChanged updates everything
    public void boardChanged() {
        this.calculateDistanceArray();
        this.calculateLegalPlacementPositions();
    }

    // Destroy a random tower
    public void deleteRandomTowers(int times) {
        List<Unit> cpy = new ArrayList<>(this.getUnits());

        Collections.shuffle(cpy);
        for (Unit u : cpy) {
            if (u instanceof Tower) {
                this.removeUnit(u.getX(), u.getY());
                if (--times == 0) { break; }
            }
        }

        // Force all enemies to recalculate
        for (Entity e : getEntities()) {
            if (e instanceof Enemy enem) {
                enem.generateFullPath();
            }
        }
    }
}
