package moe.cameo.units;

import java.awt.image.BufferedImage;

import moe.cameo.render.Sprites;

public class Unit {
    protected int x;
    protected int y;

    protected double direction;

    protected Unit() {
        this.x = 0;
        this.y = 0;
        this.direction = 0;
    }

    protected Unit(int x, int y) {
        this();
        this.x = x; this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public double getDirection() { return direction; }

    public void onPlace() { }

    public BufferedImage getSprite() { 
        return Sprites.get("NULL");
    };

    // Renderstepped
    public void _renderStep(double dt) {
        this.renderStepped(dt);
    }

    // Renderstep but overridable
    protected void renderStepped(double dt) { }
}
