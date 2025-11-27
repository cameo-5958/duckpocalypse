package moe.cameo.units.towers;

import java.util.function.BiFunction;

public class TowerType {
    // UNITS BABYYYYY
    ;

    private final BiFunction<Integer, Integer, Tower> factory;

    TowerType(BiFunction<Integer, Integer, Tower> f) {
        this.factory = f;
    }

    public Tower create(int x, int y) {
        return this.factory.apply(x, y);
    }
}
