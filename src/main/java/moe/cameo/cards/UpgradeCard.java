package moe.cameo.cards;

import java.awt.image.BufferedImage;
import java.util.function.IntConsumer;

import moe.cameo.core.GameState;
import moe.cameo.render.Sprites;

public class UpgradeCard extends Card {
    // Registry
    public enum UPGRADE_CARD_TYPES {
        ECONOMY(1),
        HEAL(1); // Upgrades the ECONOMY

        final int weight;
        static final int total;
        UPGRADE_CARD_TYPES(int weight) {
            this.weight = weight;
        }

        static {
            int t = 0;
            for (UPGRADE_CARD_TYPES uct : values()) {
                t += uct.weight;
            }

            total = t;
        }

        static final UPGRADE_CARD_TYPES choose() {
            int t = (int) (Math.random() * total);
            for (UPGRADE_CARD_TYPES uct : UPGRADE_CARD_TYPES.values()) {
                t -= uct.weight;

                if (t < 0) { return uct; }
            }

            return ECONOMY;
        }
    }

    // Get a random upgrade card
    public static final UpgradeCard chooseNextCard(IntConsumer inct, int index, GameState state) {
        return switch (UPGRADE_CARD_TYPES.choose()) {
            case ECONOMY -> {
                yield new UpgradeCard(inct, index, state::increaseIncome, state.getIncome() - 1,
                    "Stonks",
                    "Increase the per-wave yield of your economy by +1Ð.",
                    Sprites.load("EconomyStimulus", "/icons/econ_stim")
                );
            }

            case HEAL -> {
                yield new UpgradeCard(inct, index, state::heal, 2,
                    "Heal",
                    "Instantly gain +1 HP.",
                    Sprites.load("HealIcon", "/icons/heal")
                );
            }
        };
    }

    private final Runnable on_card_played;

    // Duplicator version


    protected UpgradeCard(IntConsumer play_card, int x, Runnable go, int cost, String name, String desc, BufferedImage sprite) {
        super(play_card, x);

        this.name = name;
        this.desc = desc;
        this.cost = cost;
        this.sprite = sprite;

        this.that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained = 1;

        on_card_played = go;
    }

    public void go() {
        this.on_card_played.run();
    }

    // Create a bunch of weighted UpgradeCards
    
}
