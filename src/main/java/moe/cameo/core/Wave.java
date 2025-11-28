/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.cameo.entities.enemy.EnemyTypes;

/**
 *
 * @author kunru
 */
public class Wave {
    // Global wave registry & Getters
    public static final Map<WAVE_TYPES, List<Wave>> waves = new HashMap<>();

    public enum WAVE_TYPES {
        NORMAL,     // Regular wave
        RUSH,       // Fast enemies
        CLUMP,      // "AoE is king" waves
        MINI_BOSS,  // Spawns a mini-boss/tank
        BOSS;       // Spawns a boss
    }

    public static final WAVE_TYPES[] NON_SPECIAL_TYPES;

    public static Wave requestWave(WAVE_TYPES type) {
        // Randomly select a wave from the options
        List<Wave> wvs = waves.get(type);
        int selected_index = (int)(Math.random() * wvs.size());
        return wvs.get(selected_index);
    }

    // Initialize the registry
    static {
        waves.put(WAVE_TYPES.NORMAL, new java.util.ArrayList<>());
        waves.put(WAVE_TYPES.RUSH, new java.util.ArrayList<>());
        waves.put(WAVE_TYPES.CLUMP, new java.util.ArrayList<>());
        waves.put(WAVE_TYPES.MINI_BOSS, new java.util.ArrayList<>());
        waves.put(WAVE_TYPES.BOSS, new java.util.ArrayList<>());

        // Set the NON_SPECIAL_TYPES array
        NON_SPECIAL_TYPES = waves.keySet().stream()
            .filter(t -> 
                t != WAVE_TYPES.MINI_BOSS && 
                t != WAVE_TYPES.BOSS      &&  
                t != WAVE_TYPES.NORMAL)
            .toArray(WAVE_TYPES[]::new);
    }

    // Randomly select a non-special wave
    public static WAVE_TYPES requestNonSpecialWave() {
        // Randomly select a wave from the options
        return NON_SPECIAL_TYPES[(int)(Math.random() * NON_SPECIAL_TYPES.length)];
    }

    // Normal wave
    private final WAVE_TYPES type;
    private final List<EnemyTypes> enemies = new ArrayList<>();

    private int currentEnemyIndex = 0;
    private double spawnCooldown = 0;
    private boolean going = false;

    // Constructor
    public Wave(WAVE_TYPES type, EnemyTypes... enemies) {
        // Add enemies to enemies list
        this.enemies.addAll(List.of(enemies));
        this.type = type;

        // Register self
        register();
    }

    // Register a wave
    private void register() {
        // Get the ArrayList for this type
        List<Wave> wvs = waves.get(this.type);
        wvs.add(this);
    }

    // Get wave state
    public boolean stillGoing() {
        return going;
    }

    // Start a wave
    public void startWave(GameState gs) {
        // Reset index and spawn cooldown
        currentEnemyIndex = 0;
        spawnCooldown = 0.5;
        going = true;
    }

    // Update wave (spawning enemies) 
    public void update(double dt, GameState gs) {
        // If all enemies spawned, do nothing
        if (currentEnemyIndex >= enemies.size()) {
            going = false;
            return;
        }

        // Update spawn cooldown
        spawnCooldown -= dt;
        if (spawnCooldown <= 0) {
            // Spawn enemy
            EnemyTypes e = enemies.get(currentEnemyIndex);
            gs.spawnEnemy(e);

            // Increment index
            currentEnemyIndex += 1;

            // Reset spawn cooldown
            spawnCooldown = 1.0; // 1 second between spawns
        }
    }
}
