package moe.cameo.collision;

import moe.cameo.core.Constants;
import moe.cameo.world.Board;

public class Collision {
    public static boolean intersects(Rect a, Rect b) {
        return  a.left < b.right &&
                a.right > b.left &&
                a.top < b.bottom && 
                a.bottom > b.top;
    }

    public static boolean tileCollision(Board board, Rect r) {
        // Calculate "centerpoint" of Rect r in world-sized units
        int left   = (int) Math.floor(r.left   / Constants.TILE_SIZE);
        int right  = (int) Math.floor((r.right  - 0.0001) / Constants.TILE_SIZE);
        int top    = (int) Math.floor(r.top    / Constants.TILE_SIZE);
        int bottom = (int) Math.floor((r.bottom - 0.0001) / Constants.TILE_SIZE);

        for (int tx = left; tx <= right; tx++) {
            for (int ty = top; ty <= bottom; ty++) {

                if (!board.getOccupied(tx, ty)) continue;

                if (intersects(board.getTileCollider(tx, ty), r)) {
                    return true;
                }
            }
        }

        return false;
    }
}
