package moe.cameo.cards;

import moe.cameo.units.towers.Tower;
import moe.cameo.units.towers.TowerType;

public class TowerCard extends Card {
    public TowerCard(TowerType tt) {
        this(tt, 1);
    }

    public TowerCard(TowerType tt, int count) {
        Tower template = tt.getTemplate(tt);

        this.cost = template.getCost();
        this.name = template.getName();
        this.desc = template.getDesc();
        this.sprite = template.getImage();
    }
}
