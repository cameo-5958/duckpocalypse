/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

package moe.cameo.units;

import java.util.function.BiFunction;

/**
 *
 * @author kunru
 */
public enum UnitType {
    // Register legal UnitTypes HERE
    TREE(Tree::new);

    private final BiFunction<Integer, Integer, Unit> factory;

    UnitType(BiFunction<Integer, Integer, Unit> f) {
        this.factory = f;
    }

    public Unit create(int x, int y) {
        return factory.apply(x, y);
    }
}
