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
        load("PlayerIdle", "/animations/player-stand", 0.4, true);
        load("DuckWalk", "/animations/duck-walk", 0.125, false);
        load("DuckJump", "/animations/duck-jump", 0.4, false);
        load("DuckWaddle", "/animations/duck-waddle", 0.15, false);
        
        load("SlimeWalk", "/animations/slime-walk", 0.1, true);

        load("TowerIdle", "/animations/tower-idle", 0.4, true);
        load("TowerShoot", "/animations/tower-fire", 0.12, false);

        load("Explosion", "/misc/explosion", 0.02, false);

        load("MortarIdle", "/animations/mortar-idle", 1, true);
        load("MortarShoot", "/animations/mortar-fire", 0.1, false);

        load("BatFly", "/animations/bat-run", 0.08, true);
        load("MushroomWalk", "/animations/mushroom-walk", 0.08, true);
        load("BlueGolemWalk", "/animations/blue-golem-walk", 0.2, true);
        load("OrangeGolemWalk", "/animations/orange-golem-walk", 0.2, true);

        load("GambleRoll", "/animations/die", 0.05, true);
        load("GambleOne", "/dicefaces/one", 1.5, false);
        load("GambleTwo", "/dicefaces/two", 1.5, false);
        load("GambleThree", "/dicefaces/three", 1.5, false);
        load("GambleFour", "/dicefaces/four", 1.5, false);
        load("GambleFive", "/dicefaces/five", 1.5, false);
        load("GambleSix", "/dicefaces/six", 1.5, false);

        load("ShadowBossWalk", "/animations/shadow", 0.35, true);
        load("MiniShadowWalk", "/animations/shadow-mini-walk", 0.15, true);

        load("HasanWalk", "/animations/hasan-walk", 0.5, true);
        load("HasanAttack", "/animations/hasan-attack", 0.4, false);

        load("SwordAttack", "/animations/sword-swing", 0.04, false);
        load("SwordIdle", "/animations/sword-idle", 1, true);
        load("ZombieWalk", "/animations/zombie-walk", 0.04, true);
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
