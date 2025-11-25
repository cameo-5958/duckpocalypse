package moe.cameo.collision;

public class Rect {
    public final double left, right, top, bottom, centerx, centery;
    public final int sizex, sizey;

    // Define the NULL rect
    public final static Rect NULL = new Rect(-1000, -1000, 0, 0);

    public Rect(int cx, int cy, int sx, int sy) {
        this((double) cx, (double) cy, sx, sy);
    }

    public Rect(double cx, double cy, int sx, int sy) {
        int rx = sx / 2; int ry = sy / 2;

        this.left = cx - rx;    this.top = cy - ry;
        this.right = cx + rx;   this.bottom = cy + ry;
        this.centerx = cx;      this.centery = cy;
        this.sizex = sx;        this.sizey = sy;
    }

    public Rect shift(double dx, double dy) {
        return new Rect(this.centerx + dx, this.centery + dy, this.sizex, this.sizey);
    }
}
