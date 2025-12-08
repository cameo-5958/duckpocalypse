package moe.cameo.cards;

import java.util.function.IntConsumer;

public class UpgradeCard extends Card {
    private final Runnable on_card_played;

    UpgradeCard(IntConsumer play_card, int x, int level, Runnable go) {
        super(play_card, x, level);

        on_card_played = go;
    }

    public void go() {
        this.on_card_played.run();
    }

    // Create a bunch of weighted UpgradeCards
}
