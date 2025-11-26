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

        // Create a bunch of units and add to board
        for (int x=0; x<board.getWidth(); x++) {
            for(int y=0; y<board.getHeight(); y++) {
                if (((x / 5) % 5) == 0 && (y % 5) == 0) {
                    Unit u = new Unit(x, y);
                    board.addUnit(u);
                }
            }
        }
    }

    // Getter
    public Board getBoard()     { return this.board; }
    public boolean isGameOver() { return this.gameOver; }
    public Player getPlayer()   { return this.player; }

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

        // Sort entities
        this.board.sortEntities();

        // Check if game over
        if (!goal.isAlive()) {
            this.gameOver = true;
        }
    }
}
