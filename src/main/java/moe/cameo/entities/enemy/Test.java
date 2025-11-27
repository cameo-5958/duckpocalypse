package moe.cameo.entities.enemy;

public class Test extends Enemy {
    public Test(moe.cameo.world.Board board, int x, int y, int level) {
        super(board, x, y, level);

        this.max_hp = 3;
        this.baseSpeed = 240;

        scaleStats();
    }
}
