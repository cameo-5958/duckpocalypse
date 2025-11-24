/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.render;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *
 * @author kunru
 */

// Static class for loading images

public class ImageLoader {
    public static BufferedImage load(String path) {
        try {
            return ImageIO.read(ImageLoader.class.getResource("sprites/" + path + "jpg"));
        } catch (IOException | IllegalArgumentException e) {
             e.printStackTrace();
             return null;
        }  
    }
}
