    package moe.cameo.entities.enemy;

    import moe.cameo.world.Board;

    public enum EnemyTypes {
        SLIME(Slime::new, 0.3, Slime.MAX_HP_AMOUNT),
        BAT(Bat::new, 0.1, Bat.MAX_HP_AMOUNT), ORANGE(OrangeGolem::new, 1.0, OrangeGolem.MAX_HP_AMOUNT),
        BLUE(BlueGolem::new, 0.5, BlueGolem.MAX_HP_AMOUNT), MUSHROOM(Mushroom::new, 0.8, Mushroom.MAX_HP_AMOUNT),
        SHADOW(Shadow::new, 2.5, Shadow.MAX_HP_AMOUNT), RETAINER(MiniShadow::new, 1.5, MiniShadow.MAX_HP_AMOUNT),
        BOSS(Boss::new, 2.5, Boss.MAX_HP_AMOUNT);
        // Enemy spawner

        public interface EnemyFactory {
            Enemy create(Board board, int x, int y, int level);
        }

        private final EnemyFactory factory;
        private final double cooldown;
        private final int base_hp;

        EnemyTypes(EnemyFactory factory, double cooldown, int base_hp) {
            this.factory = factory;
            this.cooldown = cooldown;
            this.base_hp = base_hp;
        }

        public Enemy spawn(Board board, int x, int y, int level) {
            Enemy e = factory.create(board, x, y, level); 
            e.scaleStats();
            return e;
        }

        public int getEnemyHp() {
            return this.base_hp;
        }

        public double getSpawnCooldown() {
            return this.cooldown;
        }
    }
