package moe.cameo.collision;

public class Rect {
    public final int left, right, top, bottom;

    public Rect(int cx, int cy, int sx, int sy) {
        int rx = sx / 2; int ry = sy / 2;
        left = cx - rx;
        top = cy - ry;
        right = cx + rx;
        bottom = cy + ry;
    }
}
