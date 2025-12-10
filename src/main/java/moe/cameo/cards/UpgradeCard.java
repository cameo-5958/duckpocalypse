package moe.cameo.cards;

import java.awt.image.BufferedImage;
import java.util.function.IntConsumer;

import moe.cameo.core.GameState;
import moe.cameo.render.Sprites;

public class UpgradeCard extends Card {
    private static final int[] UPGRADE_COSTS = {67, 13, 8, 5, 2};

    // Registry
    public enum UPGRADE_CARD_TYPES {
        ECONOMY(1),
        HEAL(1),
        CARDCOSTS(1, 4),
        MAXBUFFS(1, 4); // Upgrades the ECONOMY

        final int weight;
        int can_buy;
        static int total;
        UPGRADE_CARD_TYPES(int weight) {
            this(weight, -1);
        }

        UPGRADE_CARD_TYPES(int weight, int can_buy) {
            this.weight = weight;
            this.can_buy = can_buy;
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
                t -= (uct.can_buy != 0) ? uct.weight : 0;

                if (t < 0) { return uct; }
            }

            return ECONOMY;
        }

        final void purchase() {
            if (can_buy > 0) { can_buy--; }
            if (can_buy == 0) {
                total -= this.weight;
            }
        }
    }

    // Get a random upgrade card
    public static final UpgradeCard chooseNextCard(IntConsumer inct, int index, GameState state) {
        return switch (UPGRADE_CARD_TYPES.choose()) {
            case ECONOMY -> {
                yield new UpgradeCard(inct, index, state::increaseIncome, state.getIncome() - 1,
                    "Stonks",
                    "Increase the per-wave yield of your economy by +1Ð.",
                    Sprites.load("EconomyStimulus", "/icons/econ_stim"),
                    UPGRADE_CARD_TYPES.ECONOMY
                );
            }

            case HEAL -> {
                yield new UpgradeCard(inct, index, state::heal, 2,
                    "Heal",
                    "Instantly gain +1 HP.",
                    Sprites.load("HealIcon", "/icons/heal"),
                    UPGRADE_CARD_TYPES.HEAL
                );
            }

            case CARDCOSTS -> {
                yield new UpgradeCard(inct, index, state::upgradeCardCostRatio, 
                    UPGRADE_COSTS[UPGRADE_CARD_TYPES.CARDCOSTS.can_buy],
                    "Upcost",
                    "Increase the chance of high-cost units from spawning!",
                    Sprites.load("UpcostIcon", "/icons/upcost"),
                    UPGRADE_CARD_TYPES.CARDCOSTS
                );
            }

            case MAXBUFFS -> {
                yield new UpgradeCard(inct, index, state::upgradeCardMaxCount, 
                    UPGRADE_COSTS[UPGRADE_CARD_TYPES.MAXBUFFS.can_buy],
                    "Upskill",
                    "Increase the amount of towers you purchase at once!",
                    Sprites.load("UpskillIcon", "/icons/upskill"),
                    UPGRADE_CARD_TYPES.MAXBUFFS
                );
            }
        };
    }

    private final Runnable on_card_played;

    // Duplicator version
    private final UPGRADE_CARD_TYPES uct;
    protected UpgradeCard(IntConsumer play_card, int x, Runnable go, 
        int cost, String name, String desc, BufferedImage sprite,
        UPGRADE_CARD_TYPES uct) {
        super(play_card, x);

        this.name = name;
        this.desc = desc;
        this.cost = cost;
        this.sprite = sprite;

        this.that_one_caption_number_at_the_top_right_corner_that_represents_how_many_cards_are_gained = 1;
        this.uct = uct;

        on_card_played = go;
    }

    public void go() {
        this.on_card_played.run();
        uct.purchase();
    }

    // Create a bunch of weighted UpgradeCards
    
}
