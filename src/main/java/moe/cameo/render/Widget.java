package moe.cameo.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import moe.cameo.collision.Rect;

public abstract class Widget {
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    protected final Color backgroundColor;
    protected final Color backgroundHoverColor;
    protected final Color borderColor;

    protected Rect collider;

    protected boolean hovered = false;

    // No hover color Widgets
    public Widget(int x, int y, int width, int height,
        Color background, Color border
    ) {
        this(x, y, width, height, background, background, border);
    }

    public Widget(int x, int y, int width, int height,
        Color background, Color backgroundHoverColor, Color border
    ) {
        // Set vals
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.backgroundColor = background;
        this.backgroundHoverColor = backgroundHoverColor;
        this.borderColor = border;

        // Generate collider 
        this.calculateCollider();
    }

    public final void calculateCollider() {
        this.collider = new Rect(x + width/2, y + height/2, width, height);
    }

    // Point collision
    public final void update(int mx, int my) {
        hovered = mx >= collider.left &&
               mx <= collider.right &&
               my >= collider.top &&
               my <= collider.bottom;
    }

    // Exposed final API
    public final void render(Graphics2D g) {
        // Draw the background
        g.setColor(borderColor);
        g.fillRect(x-2, y-2, width+4, height+4);

        // Swap backgroundColors if hovering
        if (this.hovered) {
            g.setColor(backgroundHoverColor);
        } else {
            g.setColor(backgroundColor);
        }

        g.fillRect(x, y, width, height);

        // Create a Graphics2D for draw
        Graphics2D canvas = (Graphics2D) g.create(x, y, width, height);
        canvas.setClip(0, 0, width, height);

        // Turn on antialiasing
        canvas.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

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

    public final Rect getCollider() { return this.collider;  }
    public final boolean getHovered() { return this.hovered; }

    // RenderStepped
    public void renderStepped(double dt) {}
}
