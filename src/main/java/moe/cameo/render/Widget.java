package moe.cameo.render;

import java.awt.Color;
import java.awt.Graphics2D;

import moe.cameo.collision.Rect;
import moe.cameo.core.GameState;

public abstract class Widget {
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    protected final Color backgroundColor;
    protected final Color borderColor;

    protected final GameState state;

    protected final Rect collider;

    public Widget(GameState state, int x, int y, int width, int height,
        Color background, Color border
    ) {
        // Set vals
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.state = state;

        this.backgroundColor = background;
        this.borderColor = border;

        // Generate collider 
        this.collider = new Rect(x, y, width, height);
    }

    // Point collision
    public final boolean collides(int mx, int my) {
        return mx >= collider.left &&
               mx <= collider.right &&
               my >= collider.top &&
               my <= collider.bottom;
    }

    // Exposed final API
    public final void render(Graphics2D g) {
        // Draw the background
        g.setColor(borderColor);
        g.fillRect(x-2, y-2, width+4, height+4);
        g.setColor(backgroundColor);
        g.fillRect(x, y, width, height);

        // Create a Graphics2D for draw
        Graphics2D canvas = (Graphics2D) g.create(x, y, width, height);
        canvas.setClip(0, 0, width, height);

        draw(canvas);

        // Dispose it
        canvas.dispose();
    }

    // Personal should-be-overloaded method
    protected abstract void draw(Graphics2D g);

    // Clicking behavior
    public void onClick() {}

    // Hooks
    public void onHover() {}
    public void onExit() {}

    public final Rect getCollider() {
        return this.collider;
    }
}
