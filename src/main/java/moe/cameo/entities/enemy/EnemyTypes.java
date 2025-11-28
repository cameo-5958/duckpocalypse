package moe.cameo.entities.enemy;

import moe.cameo.world.Board;

public enum EnemyTypes {
    TEST(Test::new, 0.2), SLIME(Slime::new, 0.3);
    // Enemy spawner

    public interface EnemyFactory {
        Enemy create(Board board, int x, int y, int level);
    }

    public final EnemyFactory factory;
    private final double cooldown;

    EnemyTypes(EnemyFactory factory, double cooldown) {
        this.factory = factory;
        this.cooldown = cooldown;
    }

    public Enemy spawn(Board board, int x, int y, int level) {
        Enemy e = factory.create(board, x, y, level); 
        e.scaleStats();
        return e;
    }

    public double getSpawnCooldown() {
        return this.cooldown;
    }
}
