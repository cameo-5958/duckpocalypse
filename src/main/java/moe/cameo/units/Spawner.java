package moe.cameo.units;

import java.awt.image.BufferedImage;

import moe.cameo.render.Displayable;
import moe.cameo.render.Sprites;

public class Spawner extends Unit implements Displayable{
    protected Spawner(int x, int y) {
        super(x, y);
    }

    @Override
    public String getName() {
        return "Spawner";
    }

    @Override
    public BufferedImage getImage() {
        return Sprites.get("Empty");
    }

    @Override
    public BufferedImage getSprite() {
        return Sprites.get("Empty");   
    }
}
