package moe.cameo.entities.enemy;

import moe.cameo.world.Board;

public enum EnemyTypes {
    SLIME(Slime.class, Slime::new, 0.3, Slime.MAX_HP_AMOUNT, null),
    BAT(Bat.class, Bat::new, 0.1, Bat.MAX_HP_AMOUNT, null), 
    ORANGE(OrangeGolem.class, OrangeGolem::new, 0.5, OrangeGolem.MAX_HP_AMOUNT, null),
    BLUE(BlueGolem.class, BlueGolem::new, 0.5, BlueGolem.MAX_HP_AMOUNT, BlueGolem::new), 
    MUSHROOM(Mushroom.class, Mushroom::new, 0.2, Mushroom.MAX_HP_AMOUNT, null),
    SHADOW(Shadow.class, Shadow::new, 2.5, Shadow.MAX_HP_AMOUNT, Shadow::new), 
    MINI_SHADOW(MiniShadow.class, MiniShadow::new, 0.5, MiniShadow.MAX_HP_AMOUNT, MiniShadow::new),
    BOSS(Boss.class, Boss::new, 0.1, Boss.MAX_HP_AMOUNT, null),
    ZOMBIE(Zombie.class, Zombie::new, 0.1, Zombie.MAX_HP_AMOUNT, null);
    // Enemy spawner

    public interface EnemyFactory {
        Enemy create(Board board, int x, int y, int level);
    }

    public interface EnemySpawner {
        Enemy create(Board board, double x, double y, int level);
    }

    private final Class<? extends Enemy> enemyClass;
    private final EnemyFactory factory;
    private final EnemySpawner spawner;
    private final double cooldown;
    private final int base_hp;

    EnemyTypes(Class<? extends Enemy> e, EnemyFactory factory, double cooldown, int base_hp, EnemySpawner spawner) {
        this.enemyClass = e;
        this.factory = factory;
        this.spawner = spawner;
        this.cooldown = cooldown;
        this.base_hp = base_hp;
    }

    public Enemy spawn(Board board, int x, int y, int level) {
        Enemy e = factory.create(board, x, y, level);

        e.scaleStats();
        return e;
    }

    public Enemy spawn(Board board, double x, double y, int level) {
        if (Spawnable.class.isAssignableFrom(this.enemyClass)) {
            Enemy e = spawner.create(board, x, y, level); 

            e.scaleStats();
            return e;
        }

        return null;
    }

    public int getEnemyHp() {
        return this.base_hp;
    }

    public double getSpawnCooldown() {
        return this.cooldown;
    }
}
