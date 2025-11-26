package moe.cameo.entities.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;
import moe.cameo.world.Board;

public class Enemy extends Entity {

    protected final Board board;
    protected final int level;

    protected double baseSpeed = 67;
    protected double speed;
    protected double stopTimer = 0;

    private int targetTX = -1;
    private int targetTY = -1;

    private static final Random RNG = new Random();

    public Enemy(Board board, int x, int y, int level) {
        super(
            (x + 0.5f) * Constants.TILE_SIZE,
            (y + 0.5f) * Constants.TILE_SIZE
        );
        this.board = board;
        this.level = level;
    }

    protected void scaleStats() {
        this.hp = this.max_hp;
        this.speed = this.baseSpeed * (1.0 + 0.1 * (level - 1));
    }

    @Override
    public void renderStepped(double dt) {

        if (stopTimer > 0) {
            stopTimer -= dt;
            this.move(0, 0);
            onStopped(dt);
            return;
        }

        if (atTargetTile()) {
            pickNextTile();
        }

        if (targetTX == -1) {
            this.move(0, 0);
            return;
        }

        double targetX =
            targetTX * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;
        double targetY =
            targetTY * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;

        double vx = targetX - this.x;
        double vy = targetY - this.y;

        double mag = Math.sqrt(vx * vx + vy * vy);
        double step = speed * dt;

        if (mag <= step) {
            this.move(vx, vy);
            return;
        }

        vx /= mag;
        vy /= mag;

        this.move(vx * step, vy * step);
        this.setDirection(Math.toDegrees(Math.atan2(vy, vx)));

        onMoving(dt);
    }

    private void pickNextTile() {

        int tx = (int)(this.x / Constants.TILE_SIZE);
        int ty = (int)(this.y / Constants.TILE_SIZE);

        int bestD = board.getDistanceAt(tx, ty);

        int[] DX = {0, 1, 0, -1};
        int[] DY = {1, 0, -1, 0};

        List<int[]> candidates = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int nx = tx + DX[i];
            int ny = ty + DY[i];

            if (!board.inBounds(nx, ny)) continue;
            if (board.getOccupied(nx, ny) && board.getDistanceAt(nx, ny) == Integer.MAX_VALUE) continue;

            int d = board.getDistanceAt(nx, ny);

            if (d < bestD) {
                candidates.add(new int[]{nx, ny});
            }
        }

        if (candidates.isEmpty()) return;

        int[] chosen = candidates.get(RNG.nextInt(candidates.size()));
        targetTX = chosen[0];
        targetTY = chosen[1];
    }

    protected void stopForSeconds(double sec) {
        stopTimer = sec;
    }

    private boolean atTargetTile() {

        if (targetTX == -1) return true;

        double cx =
            targetTX * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;
        double cy =
            targetTY * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;

        double ddx = cx - this.x;
        double ddy = cy - this.y;

        return (ddx * ddx + ddy * ddy) < 1.0;
    }

    protected void onMoving(double dt) {}
    protected void onStopped(double dt) {}
    protected void onDeath() {}

    @Override
    // Enemies are not allowed to collide with each other!!!
    public final void onCollide(GameState state, List<Enemy> collisions) {}
}
