/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.waves;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import moe.cameo.entities.enemy.EnemyTypes;
import moe.waves.Wave.Dup;
import moe.waves.Wave.Structure;
import moe.waves.Wave.WaveTypes;

/**
 *
 * @author kunru
 */
public final class DifficultyLoader {
    public static Structure load(String name) {
        System.out.println("Loading " + name);
        Structure s = new Structure();

        String path = "/difficulties/" + name.toLowerCase() + ".waves";

        // Open path and read the structure given here
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(
                DifficultyLoader.class.getResourceAsStream(path)
            )
        )) {
            // Begin reading the file
            String line;

            // Read it line by line
            while ((line = br.readLine()) != null) {
                // There is still data to read
                // Trim the line 
                line = line.trim();

                // Confirm this isn't the header line
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Split the line into the parts
                String[] parts = line.split("\\s+");

                // Get the waveType
                WaveTypes type = WaveTypes.valueOf(parts[0]);
                int level = Integer.parseInt(parts[1]);
                
                // Create a list of Dups, : seperated
                List<Dup> dups = new ArrayList<>();
                for (int i=2; i < parts.length; i++) {
                    String[] one_dup = parts[i].split(":");
                    dups.add(new Dup( 
                        EnemyTypes.valueOf(one_dup[0]),
                        Integer.parseInt(one_dup[1])
                    ));
                }

                // Create a new structure
                s.add(
                    Wave.newWave(type, level, dups.toArray(Dup[]::new))
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to load difficulty " + name, e
            );
        }

        return s;
    }
}
