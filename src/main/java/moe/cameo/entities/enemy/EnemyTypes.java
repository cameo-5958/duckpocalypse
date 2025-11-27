package moe.cameo.entities.enemy;

import moe.cameo.world.Board;

public enum EnemyTypes {
    TEST(Test::new);
    // Enemy spawner

    public interface EnemyFactory {
        Enemy create(Board board, int x, int y, int level);
    }

    public final EnemyFactory factory;

    EnemyTypes(EnemyFactory factory) {
        this.factory = factory;
    }

    public Enemy spawn(Board board, int x, int y, int level) {
        Enemy e = factory.create(board, x, y, level); 
        e.scaleStats();
        return e;
    }
}
