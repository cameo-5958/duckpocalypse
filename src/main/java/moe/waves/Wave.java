/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.waves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.cameo.core.GameState;
import moe.cameo.core.Pair;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.entities.enemy.EnemyTypes;

/**
 *
 * @author kunru
 */
public class Wave {
    // Alias
    public static final class Structure extends ArrayList<Pair<Wave, Integer>> {}

    // Three wave structures:
    // - LIGHTWORK: Tutorial, teaches about game
    // - ORDINARY : A bit longer and harder
    // - NIGHTMARE: self explanatory blud
    public enum Difficulty {
        LIGHTWORK, ORDINARY, NIGHTMARE;

        private final Structure structure;
        Difficulty() {
            structure = DifficultyLoader.load(name());
        }

        public Structure getStructure() { return structure; }
    }

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

    private static void registerWave(WaveTypes type, Dup... dups) {
        registerWave(new Wave(type, dups));
    }

    private static void registerWave(Wave wave) {
        List<Wave> wvs = waves.get(wave.type);
        wvs.add(wave);
    }

    public static Pair<Wave, Integer> newWave(WaveTypes wt, int level, Dup... dups) {
        // Create the wave
        Wave w = new Wave(wt, dups);

        // Create the pair
        Pair<Wave, Integer> pwi = new Pair<>(w, level);

        // Print the wave metadata
        int total = 0;
        for (Dup d : dups) {
            total += d.e.getEnemyHp() * d.count * Enemy.STATS_SCALING[level];
        }
        System.out.println("Created wave with effective HP & level: " + total + ", " + level) ;

        // return the Pair
        return pwi;
    }

    public static Structure getWaveStructure(Difficulty d) {
        return d.getStructure();
    }

    public static Structure getWaveStructure() {
        Structure s = new Structure();
        s.addAll(Arrays.asList(
            newWave(WaveTypes.NORMAL, 1, new Dup(EnemyTypes.SLIME, 3)),     // Wave 1
            newWave(WaveTypes.NORMAL, 1, new Dup(EnemyTypes.SLIME, 6)),     // Wave 2
            newWave(WaveTypes.RUSH, 1,                                          // Wave 3
                new Dup(EnemyTypes.BAT, 2),
                new Dup(EnemyTypes.SLIME, 4),
                new Dup(EnemyTypes.BAT, 2)                    
            ),
            newWave(WaveTypes.CLUMP, 1,
                new Dup(EnemyTypes.SLIME, 5), 
                new Dup(EnemyTypes.BAT, 5),
                new Dup(EnemyTypes.MUSHROOM, 4)
            ),
            newWave(WaveTypes.MINI_BOSS, 1,
                new Dup(EnemyTypes.SLIME, 10),
                new Dup(EnemyTypes.MUSHROOM,3),
                new Dup(EnemyTypes.SLIME, 5),
                new Dup(EnemyTypes.ORANGE, 1)
            ),
            // Increase difficulty for next ~3 waves.
            newWave(WaveTypes.NORMAL, 2, 
                new Dup(EnemyTypes.SLIME, 15)
            ),
            newWave(WaveTypes.NORMAL, 2, 
                new Dup(EnemyTypes.BAT, 10), 
                new Dup(EnemyTypes.SLIME, 8),
                new Dup(EnemyTypes.BAT, 10)
            ),
            newWave(WaveTypes.CLUMP, 2, 
                new Dup(EnemyTypes.MUSHROOM, 16)
            ),
            // Increase difficulty for the next 2 waves.
            newWave(WaveTypes.MINI_BOSS, 3, 
                new Dup(EnemyTypes.SLIME, 10), 
                new Dup(EnemyTypes.ORANGE, 2)
            ),
            newWave(WaveTypes.BOSS, 3, 
                new Dup(EnemyTypes.RETAINER, 8),
                new Dup(EnemyTypes.SHADOW, 1)
            )
        ));
        return s;
    }

    // Normal wave
    private final WaveTypes type;
    private final List<EnemyTypes> enemies = new ArrayList<>();

    private int currentEnemyIndex = 0;
    private double spawnCooldown = 0;
    private boolean going = false;

    public static class Dup {
        public final EnemyTypes e;
        public final int count;
        Dup(EnemyTypes e, int count) {
            this.e = e; this.count = count;
        }
    }

    // Constructor
    public Wave(WaveTypes type, Dup... dups) {
        this.type = type;

        for (Dup dup : dups) {
            for (int i=0; i<dup.count; i++) {
                this.enemies.add(dup.e);
            }
        }
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

        DEFAULT_WAVE = new Wave(WaveTypes.NORMAL,
            new Dup(EnemyTypes.SLIME, 5),
            new Dup(EnemyTypes.BAT, 5),
            new Dup(EnemyTypes.MUSHROOM, 5),
            new Dup(EnemyTypes.ORANGE, 1),
            new Dup(EnemyTypes.BLUE, 1)
        );
        registerWave(DEFAULT_WAVE);

        // NORMAL WAVES
        registerWave(WaveTypes.NORMAL, 
            new Dup(EnemyTypes.SLIME, 20)
        );
    }
}
