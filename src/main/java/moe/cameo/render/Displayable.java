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
    BufferedImage getImage();
    String getName();
    

    interface HasHealth {
        int getHP();
        int getMaxHP();
    }

    interface HasLevel {
        int getLevel();
        int getMaxLevel();
    }

    interface HasCards {
        int getCards();
        int getMaxCards();
    }

    interface HasStats {
        Map<String, Integer> getStats();
    }
}
