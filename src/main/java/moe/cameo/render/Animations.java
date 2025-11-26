/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 *
 * @author kunru
 */
public class Animations {
    private static final HashMap<String, Animation> sheets = new HashMap<>();

    static {
        load("DuckIdle", "/animations/duck-stand", 0.4, true);
        load("DuckWalk", "/animations/duck-walk", 0.125, false);
        load("DuckJump", "/animations/duck-jump", 0.1, false);
        load("DuckWaddle", "/animations/duck-waddle", 0.2, false);
    }
    
    public static Animation load(String key, String path, int frameWidth, double frameTime, boolean loop) {
        if (sheets.containsKey(key)) 
            return sheets.get(key);

        try {
            BufferedImage strip = ImageIO.read(Animations.class.getResource(path + ".png"));
            BufferedImage[] frames = slice(strip, frameWidth);
            Animation anim = new Animation(frames, frameTime, loop);

            sheets.put(key, anim);
            return anim;
        } catch (IOException e) {
            throw new RuntimeException("Failed loading: " + path, e); 
        }
    }

    public static Animation load(String key, String path, double frameTime, boolean loop) {
        if (sheets.containsKey(key)) 
            return sheets.get(key);

        try {
            BufferedImage strip = ImageIO.read(Animations.class.getResource(path + ".png"));
            BufferedImage[] frames = slice(strip, strip.getHeight());
            Animation anim = new Animation(frames, frameTime, loop);
            
            sheets.put(key, anim);
            return anim;
        } catch (IOException e) {
            throw new RuntimeException("Failed loading: " + path, e); 
        }
    }

    private static BufferedImage[] slice(BufferedImage strip, int frameWidth) {
        int count = strip.getWidth() / frameWidth;
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            frames[i] = strip.getSubimage(i * frameWidth, 0, frameWidth, strip.getHeight());
        }
        return frames;
    }

    public static Animation get(String key) {
        return sheets.get(key).newAnimation();
    }
}
