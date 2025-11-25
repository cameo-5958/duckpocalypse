/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import moe.cameo.entities.Player;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public class GameState {
    // Store board
    private final Board board;
    private boolean gameOver = false;
    private Player player;

    public GameState(Board board) {
        this.board = board;

        // Create a player and add to board
        player = new Player();
        board.addEntity(player);
    }

    // Getter
    public Board getBoard()     { return this.board; }
    public boolean isGameOver() { return this.gameOver; }
    public Player getPlayer()   { return this.player; }

    // Tick updates
    public void update(double dt) {
        // Call renderStepped on the board
        board.renderStepped(dt);
    }
}
