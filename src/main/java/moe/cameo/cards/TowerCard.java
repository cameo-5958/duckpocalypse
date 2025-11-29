package moe.cameo.cards;

import java.util.function.IntConsumer;

import moe.cameo.units.towers.Tower;
import moe.cameo.units.towers.TowerType;

public class TowerCard extends Card {
    private final TowerType tt;

    public TowerCard(IntConsumer func, TowerType tt, int x) {
        this(func, tt, x, 1);
    }

    public TowerCard(IntConsumer func, TowerType tt, int x, int count) {
        super(func, x, count);
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
    public int getUpgradeAmount() { return this.that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained; }
}
