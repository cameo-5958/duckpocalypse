/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import moe.cameo.collision.Collision;
import moe.cameo.entities.Entity;
import moe.cameo.entities.Goal;
import moe.cameo.entities.Player;
import moe.cameo.units.Unit;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public class GameState {
    // Store board
    private final Board board;
    private boolean gameOver = false;
    private final Player player;
    private final Goal goal;

    // "Select tile" coordinate
    private int selected_x = 0;
    private int selected_y = 0;

    // Mouse pos
    private int mouse_x = 0;
    private int mouse_y = 0;

    public enum GAME_STATE {
        MENU, BUILDING, AUTO,
    };

    public GameState(Board board) {
        this.board = board;

        // Create a player and add to board
        player = new Player();
        board.addEntity(player);

        // Create a new goal and add to board
        goal = new Goal();
        board.addEntity(goal);
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
        double lookMult = Constants.TILE_SIZE * 1.2;

        // World coords
        double fx = px + ux * lookMult;
        double fy = py + uy * lookMult;

        double dir = Math.toDegrees(Math.atan2(uy, ux));
        this.player.setDirection(dir);

        // Convert to tile_index
        this.selected_x = (int) (fx / Constants.TILE_SIZE);
        this.selected_y = (int) (fy / Constants.TILE_SIZE);
    }

    // Handle entity movement
    private void resolveMovement(Entity e) {
        double dx = e.getDX(); double dy = e.getDY();

        // Try X axis
        if (!Collision.tileCollision(board, e.getCollider().shift(dx, 0))) {
            e.shiftX(dx);
        } else { dx = 0;}

        // Try Y axis
        if (!Collision.tileCollision(board, e.getCollider().shift( dx, dy))) {
            e.shiftY(dy);
        }
    }

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
