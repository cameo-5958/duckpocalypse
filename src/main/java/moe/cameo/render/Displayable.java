/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package moe.cameo.render;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 *
 * @author kunru
 */
public interface Displayable {
    public BufferedImage getImage();
    public String getName();

    interface HasHealth {
        int getHP();
        public int getMaxHP();
    }

    interface HasLevel {
        public int getLevel();
        public int getMaxLevel();
    }

    interface HasCards {
        public int getCards();
        public int getMaxCards();
    }

    interface HasStats {
        public Map<String, Integer> getStats();
    }
}
