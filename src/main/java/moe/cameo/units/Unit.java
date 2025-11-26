package moe.cameo.units;

public class Unit {
    private int x;
    private int y;

    private double direction;

    public Unit() {
        this.x = 0;
        this.y = 0;
        this.direction = 0;
    }

    public Unit(int x, int y) {
        this();
        this.x = x; this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public double getDirection() { return direction; }

    public void onPlace() { }
}
