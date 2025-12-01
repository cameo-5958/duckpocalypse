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
    public static final Map<WaveTypes, List<Wave>> waves = new HashMap<>();
    public static final Wave DEFAULT_WAVE;

    public enum WaveTypes {
        NORMAL,     // Regular wave
        RUSH,       // Fast enemies
        CLUMP,      // "AoE is king" waves
        MINI_BOSS,  // Spawns a mini-boss/tank
        BOSS;       // Spawns a boss
    }

    public static final WaveTypes[] NON_SPECIAL_TYPES;

    public static Wave requestWave(WaveTypes type) {
        // Randomly select a wave from the options
        List<Wave> wvs = waves.get(type);

        // There MUST be at least one option
        if (wvs.isEmpty()) {
            // Spawn default wave for now
            return DEFAULT_WAVE;
        }
        int selected_index = (int)(Math.random() * wvs.size());
        return wvs.get(selected_index);
    }

    // Initialize the registry
    static {
        waves.put(WaveTypes.NORMAL, new java.util.ArrayList<>());
        waves.put(WaveTypes.RUSH, new java.util.ArrayList<>());
        waves.put(WaveTypes.CLUMP, new java.util.ArrayList<>());
        waves.put(WaveTypes.MINI_BOSS, new java.util.ArrayList<>());
        waves.put(WaveTypes.BOSS, new java.util.ArrayList<>());

        // Set the NON_SPECIAL_TYPES array
        NON_SPECIAL_TYPES = waves.keySet().stream()
            .filter(t -> 
                t != WaveTypes.MINI_BOSS && 
                t != WaveTypes.BOSS      &&  
                t != WaveTypes.NORMAL)
            .toArray(WaveTypes[]::new);
    }

    // Randomly select a non-special wave
    public static WaveTypes requestNonSpecialWave() {
        // Randomly select a wave from the options
        return NON_SPECIAL_TYPES[(int)(Math.random() * NON_SPECIAL_TYPES.length)];
    }

    // Register a wave
    private static void registerWave(WaveTypes type, EnemyTypes... enemies) {
        registerWave(new Wave(type, enemies));
    }

    private static void registerWave(Wave wave) {
        List<Wave> wvs = waves.get(wave.type);
        wvs.add(wave);
    }

    // Normal wave
    private final WaveTypes type;
    private final List<EnemyTypes> enemies = new ArrayList<>();

    private int currentEnemyIndex = 0;
    private double spawnCooldown = 0;
    private boolean going = false;

    // Constructor
    public Wave(WaveTypes type, EnemyTypes... enemies) {
        // Add enemies to enemies list
        this.enemies.addAll(List.of(enemies));
        this.type = type;
    }

    // Get wave state
    public boolean stillGoing() {
        return going;
    }

    // Start a wave
    public void start(GameState gs) {
        // Reset index and spawn cooldown
        currentEnemyIndex = 0;
        spawnCooldown = enemies.get(currentEnemyIndex).getSpawnCooldown();
        going = true;
    }

    // Update wave (spawning enemies) 
    public void update(double dt, GameState gs) {
        // Update spawn cooldown
        spawnCooldown -= dt;
        if (spawnCooldown <= 0) {
            // Spawn enemy
            EnemyTypes e = enemies.get(currentEnemyIndex);
            gs.spawnEnemy(e);

            // Increment index
            currentEnemyIndex += 1;

            if (currentEnemyIndex >= enemies.size()) 
                going = false;
            else
                // Reset spawn cooldown
                spawnCooldown = enemies.get(currentEnemyIndex).getSpawnCooldown(); // 1 second between spawns
        }
    }

    static {
        // Define waves HERE

        DEFAULT_WAVE = new Wave(WaveTypes.NORMAL, EnemyTypes.SLIME, EnemyTypes.SLIME);
        registerWave(DEFAULT_WAVE);

        // NORMAL WAVES
        registerWave(WaveTypes.NORMAL, 
            EnemyTypes.SLIME, EnemyTypes.SLIME, EnemyTypes.SLIME,
            EnemyTypes.SLIME, EnemyTypes.SLIME, EnemyTypes.SLIME,
            EnemyTypes.SLIME, EnemyTypes.SLIME, EnemyTypes.SLIME,
            EnemyTypes.SLIME, EnemyTypes.SLIME, EnemyTypes.SLIME,
            EnemyTypes.SLIME, EnemyTypes.SLIME, EnemyTypes.SLIME);
    }
}
