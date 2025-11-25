/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.entities.enemy;

import java.util.List;

import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;

/**
 *
 * @author kunru
 */
public class Enemy extends Entity {
    @Override
    public void renderStepped(double dt) {};

    @Override
    public void onCollide(GameState state, List<Enemy> collisions) {};

}
