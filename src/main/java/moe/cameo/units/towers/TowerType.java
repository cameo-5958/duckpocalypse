package moe.cameo.units.towers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public enum TowerType {
    // UNITS BABYYYYY
    ARCHER(Archer::new),
    SLINGER(Slinger::new),
    MORTAR(Mortar::new),
    SNIPER(Sniper::new),
    GAMBLER(Gambler::new);
    
    private final BiFunction<Integer, Integer, Tower> factory;
    private final static Map<TowerType, Tower> templates = new HashMap<>();
    private final static Map<Integer, List<TowerType>> by_cost = new HashMap<>();

    TowerType(BiFunction<Integer, Integer, Tower> f) {
        this.factory = f;
    }

    static {
        // Once all enums are created
        for (TowerType type : values()) {
            templates.put(type, type.factory.apply(0, 0));
        }

        for (TowerType type : values()) {
            Integer cost = templates.get(type).getCost();
            if (!by_cost.containsKey(cost)) {
                by_cost.put(cost, new ArrayList<>());
            }
            by_cost.get(templates.get(type).getCost()).add(type);
        }
    }

    // Getter for templates
    public Tower getTemplate(TowerType tt) { return templates.get(tt); }

    // Choose random tower from a distribution
    public static TowerType getRandomWithDistributions(List<Integer> distribution) {
        // Calculate total weight (sum of all weights)
        int totalWeight = 0;
        for (int weight : distribution) {
            totalWeight += weight;
        }

        // If no weight, default to ARCHER
        if (totalWeight <= 0) {
            return ARCHER;
        }

        // Select a random value between 0 and totalWeight
        int random = (int) (Math.random() * totalWeight);

        // Find which cost bucket this random value falls into
        int cumulativeWeight = 0;
        for (int cost = 1; cost <= distribution.size(); cost++) {
            cumulativeWeight += distribution.get(cost - 1);
            if (random < cumulativeWeight) {
                // Found the cost bucket, now pick a random tower from it
                List<TowerType> legal = by_cost.get(cost);
                if (legal != null && !legal.isEmpty()) {
                    int randomIndex = (int) (Math.random() * legal.size());
                    return legal.get(randomIndex);
                }
            }
        }

        // Fallback
        return ARCHER;
    }

    public Tower create(int x, int y) {
        return this.factory.apply(x, y);
    }
}
