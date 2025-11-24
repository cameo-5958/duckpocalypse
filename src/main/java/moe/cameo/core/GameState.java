/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public class GameState {
    // Store board
    private final Board board;
    private boolean gameOver = false;

    public GameState(Board board) {
        this.board = board;
    }

    // Getter
    public Board getBoard()     { return this.board; }
    public boolean isGameOver() { return this.gameOver; }

    // Tick updates
    public void update(float dt) {}
}
