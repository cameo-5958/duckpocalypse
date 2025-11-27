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
import moe.cameo.units.Spawner;
import moe.cameo.units.Unit;
import moe.cameo.units.UnitType;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public final class GameState {
    // Store board
    private final Board board;
    private boolean gameOver = false;
    private final Player player;
    private final Goal goal;

    // Store initial state
    private GAME_STATE gameState = GAME_STATE.PLACING_UNIT;

    // "Select tile" coordinate
    private int selected_x = 0;
    private int selected_y = 0;

    // Mouse pos
    private int mouse_x = 0;
    private int mouse_y = 0;

    public enum GAME_STATE {
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
        spawnEnemy(EnemyTypes.TEST, 1, 1, 1);
        spawnEnemy(EnemyTypes.SLIME, 1, 1, 1);

        // Assign unit tile squares
        this.setUnitTileSquares();
    }

    // Getter
    public Board getBoard()     { return this.board; }
    public boolean isGameOver() { return this.gameOver; }
    public Player getPlayer()   { return this.player; }

    public int getMouseX() { return this.mouse_x; }
    public int getMouseY() { return this.mouse_y; }

    public int getFocusedTileX() { return this.selected_x; }
    public int getFocusedTileY() { return this.selected_y; }
    public Unit focusedTile() { 
        return this.board.getUnitAt(this.selected_x, this.selected_y);
    }
    public Goal getGoal() { return this.goal; }
    public GAME_STATE getGameState() { return this.gameState; }

    // Setter
    public void setMouseX(int x) { this.mouse_x = x; }
    public void setMouseY(int y) { this.mouse_y = y; }

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
        double lookMult = Math.min(mag, Constants.TILE_SIZE * 1.2);

        double fx = (mag < lookMult) ? this.mouse_x : px + ux * lookMult;
        double fy = (mag < lookMult) ? this.mouse_y : py + uy * lookMult;

        double dir = Math.toDegrees(Math.atan2(uy, ux));
        this.player.setDirection(dir);

        // Convert to tile_index
        this.selected_x = (int) (fx / Constants.TILE_SIZE);
        this.selected_y = (int) (fy / Constants.TILE_SIZE);

        // Set "canPlace" to its given status
        this.canPlace = board.isLegalPlacement(this.selected_x, this.selected_y);
    }

    // Spawn enemy
    public void spawnEnemy(EnemyTypes et, int x, int y, int level) {
        // Rather than spawn at a random location, 
        // spawn at a random spawner
        Spawner sp = board.getRandomSpawner();

        Enemy e = et.spawn(this.board, sp.getX(), sp.getY(), level);
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
            board.addUnit(UnitType.GOAL, cx+i, cy+j);
            ((moe.cameo.units.Goal) board.getUnitAt(cx+i, cy+j)).assignGameState(this);
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
    private UnitType placingType = null;
    private boolean canPlace = false;

    // Set a placing type
    public void setPlacingType(UnitType ut) {
        // Only possible if building / placing
        if (this.gameState != GAME_STATE.BUILDING && this.gameState != GAME_STATE.PLACING_UNIT) return;
        this.placingType = ut;
        this.gameState = GAME_STATE.PLACING_UNIT;
    }

    // Cancel placement type
    public void cancelPlacing() {
        this.placingType = null;
        this.gameState = GAME_STATE.BUILDING;
    }

    // Get canPlace
    public boolean canPlace() { return this.canPlace; }

    // Tick updates
    public void update(double dt) {
        // RenderStep entities
        for (Entity e : this.board.getEntities()) {
            e._renderStep(dt);
        }

        // Move entities requesting movement
        for (Entity e : this.board.getEntities()) {
            if (e.getDX() != 0 || e.getDY() != 0)
                this.resolveMovement(e);
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
