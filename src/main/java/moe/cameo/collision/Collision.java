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
        double wx = r.centerx / Constants.TILE_SIZE;
        double wy = r.centery / Constants.TILE_SIZE;

        int wxl = (int) wx; int wyt = (int) wy;
        for(int dx=0;dx<2;dx++) {
            for (int dy=0; dy<2;dy++) {
                if (intersects(board.getTileCollider(wxl+dx,wyt+dy), r)) { return true; }
            }
        }

        return false;
    }
}
