/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.units;

import java.awt.image.BufferedImage;

import moe.cameo.core.GameState;
import moe.cameo.render.Displayable;
import moe.cameo.render.Sprites;

/**
 *
 * @author kunru
 */
public class Goal extends Unit implements Displayable, Displayable.HasHealth {
    private GameState state;

    protected Goal(int x, int y) { 
        super(x, y);
        }

    static {
        Sprites.load("GoalIcon", "/icons/stand");
    }

    public void assignGameState(GameState state) {
        this.state = state;
    }

    @Override
    public BufferedImage getImage() {
        return Sprites.get("GoalIcon");
    }

    @Override
    public BufferedImage getSprite() {
        return Sprites.get("Empty");
    }

    @Override
    public String getName() {
        return "Grape Stand";
    }

    @Override
    public int getHP() {
        return (int) state.getGoal().getHP();
    }

    @Override 
    public int getMaxHP() {
        return (int) state.getGoal().getMaxHP();
    }
}
