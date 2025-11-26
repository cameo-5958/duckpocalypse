/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.units;

import java.awt.image.BufferedImage;

import moe.cameo.render.Displayable;
import moe.cameo.render.Sprites;

/**
 *
 * @author kunru
 */
public class Tree extends Unit implements Displayable {
    public Tree(int x, int y) { super(x, y); }
    
    static {
        Sprites.load("TreeSprite", "tree");
    }

    public BufferedImage getImage() {
        return Sprites.get("TreeSprite");
    }

    public String getName() {
        return "Tree";
    }
}
