package moe.cameo.entities.enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import moe.cameo.collision.Rect;
import moe.cameo.core.Constants;
import moe.cameo.core.GameState;
import moe.cameo.entities.Entity;
import moe.cameo.world.Board;

public class Enemy extends Entity {
    public static int MAX_HP_AMOUNT = 1;

    protected final Board board;
    protected final int level;

    protected double base_speed = 67;
    protected double speed;
    protected double stopTimer = 0;

    protected double abilityTimer = 0;
    protected double abilityCooldown = -1;

    private static final Random RNG = new Random();

    private final List<int[]> path = new ArrayList<>();
    private int pathIndex = 0;

    public Enemy(Board board, double x, double y, int level) {
        super(x, y);
        this.board = board;
        this.level = level;
        this.SIZE = 32;

        // Generate path
        generateFullPath();
    }

    public Enemy(Board board, int x, int y, int level) {
        this(
            board,
            (x + 0.5) * Constants.TILE_SIZE,
            (y + 0.5) * Constants.TILE_SIZE,
            level
        );
    }

    public static final double[] STATS_SCALING = {0, 1, 2, 6, 15, 67};
    public static final double[] SPEED_SCALING = {0, 1, 1.5, 2, 2.5, 3};
    protected void scaleStats() {
        this.max_hp *= STATS_SCALING[this.level];
        this.hp = this.max_hp;
        this.speed = this.base_speed * Math.sqrt(SPEED_SCALING[this.level]);
    }

    public int damage(int amt) {
        // Return the damage dealt
        // Make sure I haven't died already
        if (this.hp == 0) { return 0; }

        int dealt = Math.min((int) this.hp, amt);

        this.hp -= amt;
        if (this.hp <= 0) {
            this.hp = 0;
            onDeath();
        }

        return dealt;
    }

    public final void generateFullPath() {
        path.clear();
        pathIndex = 0;

        int tx = (int)(this.x / Constants.TILE_SIZE);
        int ty = (int)(this.y / Constants.TILE_SIZE);

        while (true) {
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

            if (candidates.isEmpty()) break;

            int[] chosen = candidates.get(RNG.nextInt(candidates.size()));
            path.add(chosen);

            tx = chosen[0];
            ty = chosen[1];
        }
    }

    public double[] predictFuturePoint(double dt) {
        double px = this.x;
        double py = this.y;

        int idx = this.pathIndex;
        double speedLeft = this.speed;

        double timeLeft = dt;

        // whoop
        while (timeLeft > 0 && idx < path.size()) {

            int[] node = path.get(idx);

            double tx =
                node[0] * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;
            double ty =
                node[1] * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;

            double ddx = tx - px;
            double ddy = ty - py;
            double dist = Math.sqrt(ddx * ddx + ddy * ddy);

            if (dist < 1e-6) {
                // Already at this node, advance
                idx++;
                continue;
            }

            double timeToNode = dist / speedLeft;

            if (timeLeft <= timeToNode) {
                // Somwhere here in this node
                double t = timeLeft / timeToNode;

                px += ddx * t;
                py += ddy * t;

                return new double[]{px, py};
            }

            // Reach node exactly, continue to next
            px = tx;
            py = ty;

            timeLeft -= timeToNode;
            idx++;
        }

        // ?? ihncw hats happening
        return new double[]{px, py};
    }


    @Override
    protected void renderStepped(double dt) {
        if (stopTimer > 0) {
            stopTimer -= dt;
            this.move(0, 0);
            onStopped(dt);
            return;
        }

        if (abilityCooldown > 0) {
            abilityTimer += dt;

            if (abilityTimer >= abilityCooldown) {
                abilityTimer = 0;
                onAbilityTick();
            }
        }

        if (pathIndex >= path.size()) {
            this.move(0, 0);
            return;
        }

        int[] target = path.get(pathIndex);

        double targetX =
            target[0] * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;
        double targetY =
            target[1] * Constants.TILE_SIZE + Constants.TILE_SIZE * 0.5;

        double vx = targetX - this.x;
        double vy = targetY - this.y;

        double mag = Math.sqrt(vx * vx + vy * vy);
        double step = speed * dt;

        if (mag <= step) {
            this.move(vx, vy);
            pathIndex++;   
            return;
        }

        vx /= mag;
        vy /= mag;

        this.move(vx * step, vy * step);
        this.setDirection(Math.toDegrees(Math.atan2(vy, vx)));

        onMoving(dt);
    }

   
    protected void stopForSeconds(double sec) {
        stopTimer = sec;
    }

    protected void onMoving(double dt) {}
    protected void onStopped(double dt) {}
    protected void onDeath() {}

    @Override
    // Enemies are not allowed to collide with each other!!!
    public final void onCollide(GameState state, List<Enemy> collisions) {}

    @Override
    // Enemies should have well-sized hitboxes.
    public final Rect getCollider() {
        int size = Math.min(this.SIZE, 48);
        return new Rect(this.x, this.y, size, size);
    }

    public final int getLevel() { return this.level; }

    protected void onAbilityTick() { }
}
