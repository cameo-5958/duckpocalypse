/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import java.util.ArrayList;
import java.util.List;

import moe.cameo.collision.Collision;
import moe.cameo.collision.Rect;
import moe.cameo.entities.Entity;
import moe.cameo.entities.Goal;
import moe.cameo.entities.Player;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.entities.enemy.EnemyTypes;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.units.RequestsGamestates;
import moe.cameo.units.Spawner;
import moe.cameo.units.Unit;
import moe.cameo.units.UnitType;
import moe.cameo.units.towers.Tower;
import moe.cameo.units.towers.TowerType;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public final class GameState {
    // Store board
    private final Board board;
    private boolean gameOver = false;
    private boolean paused = false;
    private final Player player;
    private final Goal goal;

    // Store initial state
    private State state = State.PLACING_UNIT;

    // "Select tile" coordinate
    private int selected_x = 0;
    private int selected_y = 0;
    private Unit selected_unit = null;

    // Mouse pos
    private int mouse_x = 0;
    private int mouse_y = 0;

    // Current game level and wave
    private int wave = 0;
    private Wave current_wave = null;

    public enum State {
        MENU, BUILDING, AUTO,

        PLACING_UNIT,
    };

    public GameState(Board board) {
        this.board = board;
        
        // Create a player and add to board
        player = new Player();
        board.addEntity(player);

        // Create a new goal and add to board
        goal = new Goal();
        board.addEntity(goal);

        // Create a new enemy
        spawnEnemy(EnemyTypes.SLIME);

        // Assign unit tile squares
        this.setUnitTileSquares();

        // Set initial state
        setState(State.BUILDING);
    }

    // Getter
    public Board getBoard()     { return this.board; }
    public boolean isGameOver() { return this.gameOver; }
    public boolean isPaused()   { return this.paused; }
    public Player getPlayer()   { return this.player; }

    public int getMouseX() { return this.mouse_x; }
    public int getMouseY() { return this.mouse_y; }

    public int getFocusedTileX() { return this.selected_x; }
    public int getFocusedTileY() { return this.selected_y; }
    public Unit focusedTile() { return this.selected_unit; }
    public Goal getGoal() { return this.goal; }
    public State getState() { return this.state; }
    public int getLevel() { return this.wave; }

    // Setter
    public void setMouseX(int x) { this.mouse_x = x; }
    public void setMouseY(int y) { this.mouse_y = y; }

    // State changers
    private void setState(State state) {
        if (this.state == state) { return; }

        // Exit hook
        onExitState(this.state);
        this.state = state;
        onEnterState(this.state);
    }

    private void onExitState(State state) {
        switch (state) {
            case PLACING_UNIT -> {
                // Deselect the current placingType
                this.placingType = null;
            }
            default -> {
                // Increase wave by one
            }
        }
    }

    private void onEnterState(State state) {
        switch (state) {
            case PLACING_UNIT -> {
                // Default to tree


                // Hide CardGUI, show cancelButton

            }
            case BUILDING -> {
                // Increase wave by one
                this.wave++;

                // Select a wave for next round
                Wave.WaveTypes wt;
                if (wave % 25 == 0) // Select a boss wave
                    wt = Wave.WaveTypes.BOSS;
                else if (wave % 5 == 0) // Select a miniboss wave
                    wt = Wave.WaveTypes.MINI_BOSS;
                else {
                    // Select another wave. 50% normal, 25% others
                    double r = Math.random();
                    if (r < 0.5) 
                        wt = Wave.WaveTypes.NORMAL;
                    else 
                        wt = Wave.requestNonSpecialWave();
                }

                // Set the wave
                this.current_wave = Wave.requestWave(wt);
            }
            case AUTO -> {
                // Start the wave
                this.current_wave.start(this);
                // Hide cancelButton, show CardGUI
                
            }
            default -> {}
        }
    }

    // Calculate "focused" MouseTile
    public void focus() {
        // Player pos, deltas
        double px = this.player.getX(); double py = this.player.getY();
        double dx = this.mouse_x - px; double dy = this.mouse_y - py;

        // magnitude
        double mag = Math.sqrt(dx*dx + dy*dy);
        double ux = dx / mag;
        double uy = dy / mag;

        // Look distance
        // If the magnitude is NOT greater than lookMult,
        // use the mouse as the worldcoord
        double lookMult = Math.min(mag, Constants.TILE_SIZE * 2.0);

        double fx = (mag < lookMult) ? this.mouse_x : px + ux * lookMult;
        double fy = (mag < lookMult) ? this.mouse_y : py + uy * lookMult;

        double dir = Math.toDegrees(Math.atan2(uy, ux));
        this.player.setDirection(dir);

        // Convert to tile_index
        this.selected_x = (int) (fx / Constants.TILE_SIZE);
        this.selected_y = (int) (fy / Constants.TILE_SIZE);
        
        // Store selected unit
        this.selected_unit = this.board.getUnitAt(selected_x, selected_y);

        // Set "canPlace" to its given status
        this.canPlace = board.isLegalPlacement(this.selected_x, this.selected_y) &&
                        // Can't collide with player or we get softlocked
                        !Collision.intersects(
                            this.player.getCollider(), 
                            Board.tileRect(this.selected_x, this.selected_y)
                        );
    }

    // Spawn enemy
    public void spawnEnemy(EnemyTypes et) {
        // Rather than spawn at a random location, 
        // spawn at a random spawner
        Spawner sp = board.getRandomSpawner();

        Enemy e = et.spawn(this.board, sp.getX(), sp.getY(), this.wave);
        board.addEntity(e);
    }

    // Handle entity movement
    private void resolveMovement(Entity e) {
        double dx = e.getDX(); double dy = e.getDY();

        // Try X axis
        if (!e.collideable() || !Collision.tileCollision(board, e.getCollider().shift(dx, 0))) {
            e.shiftX(dx);
        } else { dx = 0;}

        // Try Y axis
        if (!e.collideable() || !Collision.tileCollision(board, e.getCollider().shift( dx, dy))) {
            e.shiftY(dy);
        }
    }

    // Set goal unit squares
    private void setUnitTileSquares() {
        int cx = board.getWidth()/2-1; int cy = board.getHeight()/2-1;
        for (int i=0; i<2; i++) for (int j=0; j<2; j++){
            queryPlace(UnitType.GOAL, cx+i, cy+j);
        }
    }

    // Attempt to place a Unit
    private void queryPlace(UnitType ut, int x, int y) {
        Unit u = board.addUnit(ut, x, y);

        if (u instanceof RequestsGamestates rsu) {
            rsu.setGameState(this);
        }
    }

    private void queryPlace(TowerType tt, int x, int y) {
        Unit u = board.addUnit(tt.create(x, y), x, y);

        if (u instanceof RequestsGamestates rsu) {
            rsu.setGameState(this);
        }
    }

    // Collision handler
    private void collisionEngine() {
        // Calculate collisions. O(n^2) but whatever
        // Store immutable list of entities
        List<Entity> entities = new ArrayList<>(this.board.getEntities());
        for (Entity e : entities) { // For all non-enemies...
            Rect r = e.getCollider();

            if (e instanceof Enemy) continue; 
            List<Enemy> colls = new ArrayList<>();            

            for (Entity f : entities) { // Loop through all entities...
                if (e == f) continue; // Skip self...
                if (!(f instanceof Enemy em)) continue; // Skip non-enemies...
                if (!e.mayICollide(em)) continue; // Skip if you may NOT collide...

                // Check collision
                if (Collision.intersects(r, em.getCollider())) {
                    colls.add(em);
                }
            }

            // Call onCollide ONLY if necessary
            if (!colls.isEmpty())
                e.onCollide(this, colls);
        }
    }

    // Handle unit placement
    private TowerType placingType = null;
    private boolean canPlace = false;

    // Begin placement of a unit
    public void setPlacingType(TowerType tt) {
        // Only possible if building / placing
        if (this.state != State.BUILDING && this.state != State.PLACING_UNIT) return;

        // Set state first as it overrides then
        this.setState(State.PLACING_UNIT);
        this.placingType = tt;
    }

    // Cancel placement type
    public void cancelPlacing() {
        this.setState(State.BUILDING);
    }

    // Click handler
    public void click() {
        switch (state) {
            case PLACING_UNIT -> click_place_object();
            case BUILDING -> check_gui_clicks();
            default -> check_gui_clicks();
        }

        spawnEnemy(EnemyTypes.SLIME);
    }

    // Debug: toggle pause state
    public void togglePause() {
        this.paused = !this.paused;
    }

    // Click with state as PLACING_UNIT
    private void click_place_object() {
        // No placement if can't place
        if (!canPlace) { return; }

        // Create a new unit of type placingType
        // at the focused point
        this.queryPlace(placingType, selected_x, selected_y);

        // Cancel the placement
        cancelPlacing();
    }

    // Check GUI clicked
    private void check_gui_clicks() {}

    // Get canPlace
    public boolean canPlace() { return this.canPlace; }

    // Tick updates
    public void update(double dt) {
        // DEBUG: Set type to Tree, mode to PLACING_UNIT
        // this.gameState = State.PLACING_UNIT;
        // this.placingType = UnitType.TREE;

        // Skip update if paused
        if (this.paused) return;

        // RenderStep entities
        for (Entity e : this.board.getEntities()) {
            e._renderStep(dt);
        }

        // RenderStep units
        // Do this after in case a tower creates a 
        // projectile (so first tick doesn't immediately)
        // move the projectile

        // Also force RequestGamestates to accept a GameState
        for (Unit u : this.board.getUnits()) {
            if (u instanceof RequestsGamestates rsu) {
                rsu.setGameState(this);
            }

            if (u != null) {
                u._renderStep(dt);
            }
        }

        // Create projetiles requested by towers
        for (Unit u : this.board.getUnits()) {
            if (u instanceof Tower t) {
                for (Projectile p : t.getQueuedProjectiles()) {
                    this.board.addEntity(p);
                }
            }
        }

        // Move entities requesting movement
        for (Entity e : this.board.getEntities()) {
            if (e.getDX() != 0 || e.getDY() != 0)
                this.resolveMovement(e);
        }

        // Destroy entities that are dead
        for (Entity e : new ArrayList<>(this.board.getEntities())) {
            if (e.getHP() <= 0 && !(e instanceof Goal)) {
                this.board.removeEntity(e);
            }
        }

        // Attempt to spawn enemies from spawners
        // if state is correct
        if (this.state == State.AUTO) {
            // Check if wave can still spawn
            if (this.current_wave.stillGoing()) {
                this.current_wave.update(dt, this);
            }
        }
 
        // Calculate collisions
        this.collisionEngine();

        // Refocus player
        this.focus();

        // Sort entities
        this.board.sortEntities();

        // Check if game over
        if (!goal.isAlive()) {
            this.gameOver = true;
        }
    }
}
