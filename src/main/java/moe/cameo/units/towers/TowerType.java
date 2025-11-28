package moe.cameo.units.towers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum TowerType {
    // UNITS BABYYYYY
    ARCHER(Archer::new);

    private final BiFunction<Integer, Integer, Tower> factory;
    private final static Map<TowerType, Tower> templates = new HashMap<>();

    TowerType(BiFunction<Integer, Integer, Tower> f) {
        this.factory = f;
    }

    static {
        // Once all enums are created
        for (TowerType type : values()) {
            templates.put(type, type.factory.apply(0, 0));
        }
    }

    // Getter for templates
    public Tower getTemplate(TowerType tt) { return templates.get(tt); }

    public Tower create(int x, int y) {
        return this.factory.apply(x, y);
    }
}
