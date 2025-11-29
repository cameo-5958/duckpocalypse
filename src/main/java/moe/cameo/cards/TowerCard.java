package moe.cameo.cards;

import moe.cameo.units.towers.Tower;
import moe.cameo.units.towers.TowerType;

public class TowerCard extends Card {
    private final TowerType tt;

    public TowerCard(TowerType tt) {
        this(tt, 1);
    }

    public TowerCard(TowerType tt, int count) {
        Tower template = tt.getTemplate(tt);

        this.cost = template.getCost() * count;
        this.name = template.getName();
        this.desc = template.getDesc();
        this.sprite = template.getImage();
        this.tt = tt;
        this.that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained
            = count;
    }

    public TowerType getTowerType() { return this.tt; }
}
