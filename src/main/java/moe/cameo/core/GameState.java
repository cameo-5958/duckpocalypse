/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package moe.cameo.core;

import java.util.ArrayList;
import java.util.List;

import moe.cameo.cards.Card;
import moe.cameo.cards.TowerCard;
import moe.cameo.cards.UpgradeCard;
import moe.cameo.collision.Collision;
import moe.cameo.collision.Rect;
import moe.cameo.entities.Entity;
import moe.cameo.entities.Goal;
import moe.cameo.entities.Player;
import moe.cameo.entities.enemy.Enemy;
import moe.cameo.entities.enemy.EnemyTypes;
import moe.cameo.entities.projectile.Projectile;
import moe.cameo.render.Widget;
import moe.cameo.units.RequestsGamestates;
import moe.cameo.units.Spawner;
import moe.cameo.units.Unit;
import moe.cameo.units.UnitType;
import moe.cameo.units.towers.Tower;
import moe.cameo.units.towers.TowerType;
import moe.cameo.waves.Wave;
import moe.cameo.waves.Wave.Structure;
import moe.cameo.world.Board;

/**
 *
 * @author kunru
 */
public final class GameState {
    // Store board
    private final Board board;
    private boolean gameOver = false;
    private boolean paused = false;
    private final Player player;
    private final Goal goal;

    // Store initial state
    private State state;

    // "Select tile" coordinate
    private int selected_x = 0;
    private int selected_y = 0;
    private Unit selected_unit = null;

    // Mouse pos
    private int mouse_x = 0;
    private int mouse_y = 0;

    // Current game level and wave
    // stores wave structures
    private int wave = 0;
    private int level = 1;
    private Wave current_wave = null;
    private Structure wave_structure;  

    // Store held cards and currently selected card
    private final List<Card> held_cards = new ArrayList<>();
    private int selected_card = -1;

    // Select which card to be displayed
    private final int[][] card_cost_distros = {
        {1, 0, 0, 0, 0},
        {6, 3, 1, 0, 0},
        {8, 5, 4, 1, 0},
        {4, 10, 8, 4, 1},
        {0, 4, 4, 8, 4}
    };
    private int card_cost_level = 0;

    // Click queued? 
    // true = not handled, false = handled
    private boolean queued_click = false;

    // Money
    private int money = 4;
    private int per_wave = 3;

    // Maximum stacks of tower per purchase
    private int max_purchase = 1;

    // Active widgets
    // DO NOT DIRECTLY TRY AND GET 
    // active_widgets. ALWAYS USE THE
    // GETTER
    private final List<Widget> active_widgets = new ArrayList<>();

    public enum State {
        MENU(), 
        BUILDING(), 
        AUTO(),
        LOST(), 
        PLACING();

        private Menu reserved_widgets;

        public void register(Menu m) {
            reserved_widgets = m;
        }

        public List<Widget> getWidgets() { 
            return reserved_widgets.get(); 
        }
    };

    public GameState() {
        // Create a new board with default dimensions
        this(new Board(Constants.GAME_COLUMNS, Constants.GAME_ROWS));
    }

    public GameState(Board board) {
        this.board = board;

        // Register GUI
        registerGUI();

        // Create a player and add to board
        player = new Player();
        board.addEntity(player);

        // Create a new goal and add to board
        goal = new Goal();
        board.addEntity(goal);

        // Assign unit tile squares
        this.setUnitTileSquares();

        // Set initial state
        setState(State.MENU); // This will auto set widgets

        // Deal initial cards
        dealCards();
    }

    // GUI Registerer
    public void registerGUI() {
        GameGUI.register(this);
    }

    // Set difficulty (THIS SHOULD HAPPEN BEFORE ANYTHING)
    public void setDifficulty(Wave.Difficulty d) {
        this.wave_structure = Wave.getWaveStructure(d);
    }

    // Getter
    public Board getBoard()     { return this.board; }
    public boolean isGameOver() { return this.gameOver; }
    public boolean isPaused()   { return this.paused; }
    public Player getPlayer()   { return this.player; }

    public int getMouseX() { return this.mouse_x; }
    public int getMouseY() { return this.mouse_y; }

    public int getFocusedTileX() { return this.selected_x; }
    public int getFocusedTileY() { return this.selected_y; }
    public Unit focusedTile() { return this.selected_unit; }
    public Goal getGoal() { return this.goal; }
    public State getState() { return this.state; }
    public int getLevel() { return this.wave; }
    public int getMoney() { return this.money; }
    public int getIncome() { return this.per_wave; }
    public List<Card> heldCards() { return this.held_cards; }
    public List<Widget> getActiveWidgets() { 
        // This has to extend active_widgets with held_cards
        // if state == BUILDING
        List<Widget> temp = new ArrayList<>(this.active_widgets);
        if (state == State.BUILDING) {
            temp.addAll(this.held_cards);
        }
        return temp; 
    }

    // Setter
    public void setMouseX(int x) { this.mouse_x = x; }
    public void setMouseY(int y) { this.mouse_y = y; }

    // State changers
    private void setState(State state) {
        if (this.state == state) { return; }

        // Exit hook
        // Don't call exit hook if we're
        // stateless (default)
        if (this.state != null) onExitState(this.state);
        this.state = state;
        onEnterState(this.state);
    }

    private void onExitState(State state) {
        switch (state) {
            case PLACING -> {
                // Deselect the current placingType
                this.placing_type = null;
            }
            case AUTO -> {
                // Increase wave by one
                this.wave++;

                // Select a wave for next round
                this.current_wave = selectWave();

                // Redeal cards
                this.dealCards();

                // Give money
                this.money += this.per_wave;
            }
            case MENU -> {
                this.paused = false;

                // Select a wave for first round
                this.current_wave = selectWave();
            }
            default -> {
            }
        }
    }

    private void onEnterState(State state) {
        // Swap GUI to given state
        this.active_widgets.clear();
        this.active_widgets.addAll(state.getWidgets());

        // Handle swapping state
        switch (state) {
            case PLACING -> {
                // Default to tree


                // Hide CardGUI, show cancelButton

            }
            case BUILDING -> {

            }
            case AUTO -> {
                // Start the wave
                this.current_wave.start(this);
                // Hide cancelButton, show CardGUI
                
            }
            case MENU -> {
                this.paused = true;
            }
            default -> {}
        }
    }

    // Change state from BUILDING -> AUTO
    public void play() {
        if (this.state == State.BUILDING)
            this.setState(State.AUTO);
    }

    // Change state from MENU -> BUILDING
    public void start() {
        if (this.state == State.MENU) 
            this.setState(State.BUILDING);
    }

    // Calculate "focused" MouseTile
    public void focus() {
        // Player pos, deltas
        double px = this.player.getX(); double py = this.player.getY();
        double dx = this.mouse_x - px; double dy = this.mouse_y - py;

        // magnitude
        double mag = Math.sqrt(dx*dx + dy*dy);
        double ux = dx / mag;
        double uy = dy / mag;

        // Look distance
        // If the magnitude is NOT greater than lookMult,
        // use the mouse as the worldcoord
        double lookMult = Math.min(mag, Constants.TILE_SIZE * 2.0);

        double fx = (mag < lookMult) ? this.mouse_x : px + ux * lookMult;
        double fy = (mag < lookMult) ? this.mouse_y : py + uy * lookMult;

        double dir = Math.toDegrees(Math.atan2(uy, ux));
        this.player.setDirection(dir);

        // Convert to tile_index
        this.selected_x = (int) (fx / Constants.TILE_SIZE);
        this.selected_y = (int) (fy / Constants.TILE_SIZE);
        
        // Store selected unit
        this.selected_unit = this.board.getUnitAt(selected_x, selected_y);

        // Set "canPlace" to its given status
        // A bit complicated:
        // If the space is open, defer logic to 
        // old logic:
        if (!board.getOccupied(this.selected_x, this.selected_y))
            this.can_place = board.isLegalPlacement(this.selected_x, this.selected_y) &&
                        // Can't collide with player or we get softlocked
                        !Collision.intersects(
                            this.player.getCollider(), 
                            Board.tileRect(this.selected_x, this.selected_y)
                        );
        else {
            this.can_place = false;

            // If the TowerType of this and 
            // my currently selected TowerCard
            // are the same AND the Status is correct
            if (state != State.PLACING || selected_card == -1) return;

            Unit u = board.getUnitAt(selected_x, selected_y);
            if (!(u instanceof Tower t)) return; 

            Card c = this.held_cards.get(selected_card);
            if (!(c instanceof TowerCard tc))  return;

            // If they're the same type of card
            // it's legal
            this.can_place = t.getTowerType() == tc.getTowerType();
        }
    }

    // Buying stuff
    private boolean buy(int cost) {
        if (this.money < cost) {
            Error.raise("You don't have enough money to buy that.");
            return false;
        }

        this.money -= cost;
        return true;
    }

    // Spawn enemy
    public void spawnEnemy(EnemyTypes et) {
        // Rather than spawn at a random location, 
        // spawn at a random spawner
        Spawner sp = board.getRandomSpawner();

        Enemy e = et.spawn(this.board, sp.getX(), sp.getY(), this.level);
        board.addEntity(e);
    }

    // Handle entity movement
    private void resolveMovement(Entity e) {
        double dx = e.getDX(); double dy = e.getDY();

        // Try X axis
        if (!e.collideable() || !Collision.tileCollision(board, e.getCollider().shift(dx, 0))) {
            e.shiftX(dx);
        } else { dx = 0;}

        // Try Y axis
        if (!e.collideable() || !Collision.tileCollision(board, e.getCollider().shift( dx, dy))) {
            e.shiftY(dy);
        }
    }

    // Set goal unit squares
    private void setUnitTileSquares() {
        int cx = board.getWidth()/2-1; int cy = board.getHeight()/2-1;
        for (int i=0; i<2; i++) for (int j=0; j<2; j++){
            queryPlace(UnitType.GOAL, cx+i, cy+j);
        }
    }

    // Attempt to place a Unit
    private void queryPlace(UnitType ut, int x, int y) {
        Unit u = board.addUnit(ut, x, y);

        if (u instanceof RequestsGamestates rsu) {
            rsu.setGameState(this);
        }
    }

    private void queryPlace(TowerType tt, int x, int y) {
        Unit u = board.addUnit(tt.create(x, y), x, y);

        if (u instanceof RequestsGamestates rsu) {
            rsu.setGameState(this);
        }
    }

    // Reroll the current held cards
    private void dealCards() {
        this.held_cards.clear();

        // Add to held_cards
        for (int i=0; i<Constants.MAX_CARDS_HELD; i++) {
            this.held_cards.add(this.decideCard(i));
        }
    }

    // Decide which cards to draw
    private Card decideCard(int index) {
        // If towercard
        // if (true) {
        // Default 80-20 chance to get a TowerCard
        double rnum = Math.random();
        if (rnum < 0.6)
            return new TowerCard(
                this::useCard, 
                TowerType.getRandomWithDistributions(card_cost_distros[card_cost_level]),
                index, (int) (Math.random() * (max_purchase-1)) + 1
            );
        else {
            // Allow UpgradeCard to handle it
            return UpgradeCard.chooseNextCard(
                this::useCard,
                index,
                this
            );
        }
        // } 
        // return new TowerCard(this::useCard, TowerType.ARCHER, index, 10);

    }

    // Clear card at location
    public void clearCard(int index) {
        this.held_cards.set(index, TowerCard.getEmptyCard(index));
    }

    // Use a card at index i
    public void useCard(int index) {
        // Check the type of the card + cost
        Card c = held_cards.get(index);
        int cost = c.getCost();

        if (!this.buy(cost)) { return; }
        
        if(c instanceof TowerCard tc) {
                            selected_card = index;
                this.setPlacingType(tc.getTowerType());
        } else if (c instanceof UpgradeCard uc) {
            uc.go();
            clearCard(index);
        }

        // switch (c) {
        //     case TowerCard tc -> {
        //         // Place tc if valid
        //         selected_card = index;
        //         this.setPlacingType(tc.getTowerType());
        //     }
        //     case UpgradeCard uc -> {
        //         uc.go();
        //         clearCard(index);
        //     }
        //     default -> {
        //     }
        // }
    }

    // Select next wave 
    private Wave selectWave() {
        // Select a wave for next round
        // Check if there are still waves 
        // in our wave structure
        if (this.wave < wave_structure.size()) {
            // Return the wave on the wave structure
            Pair<Wave, Integer> pwi = wave_structure.get(this.wave);
            
            // Set the level to the given level
            this.level = pwi.getSecond();
            
            // Return the wave
            return pwi.getFirst();
        }

        // Set the level to (wave / 4)
        this.level = wave / 4;

        // Return a requested wave
        return Wave.requestWave(Wave.WaveTypes.NORMAL);
    }

    /*********************
    
    CARD LOGIC
    
    *********************/

    // Increasing income
    public void increaseIncome() {
        this.per_wave++;
    }

    // Healing
    public void heal() {
        if (this.goal.getHP() < 10) {
            this.goal.changeHP(1);
        }
        else 
            Error.raise("You've gotta be stupid...");
    }

    // Upgrade CardCostThing
    public void upgradeCardCostRatio() {
        if (card_cost_level == card_cost_distros.length) {
            Error.raise("Oops! We sold too many of these cards.");
            return;
        }
        this.card_cost_level++;
    }

    // Upgrade CardNumberThing
    private static final int[] CARDMAXCOUNTS = {1, 3, 7, 15, 32};
    private int CARDMAXCOUNTSindex = 0;
    public void upgradeCardMaxCount() {
        if (CARDMAXCOUNTSindex == CARDMAXCOUNTS.length) {
            Error.raise("Oops! We sold too many of these cards.");
            return;
        }
        this.max_purchase = CARDMAXCOUNTS[++CARDMAXCOUNTSindex]; 
    }


    // Collision handler
    private void collisionEngine() {
        // Calculate collisions. O(n^2) but whatever
        // Store immutable list of entities
        List<Entity> entities = new ArrayList<>(this.board.getEntities());
        for (Entity e : entities) { // For all non-enemies...
            Rect r = e.getCollider();

            if (e instanceof Enemy) continue; 
            List<Enemy> colls = new ArrayList<>();            

            for (Entity f : entities) { // Loop through all entities...
                if (e == f) continue; // Skip self...
                if (!(f instanceof Enemy em)) continue; // Skip non-enemies...
                if (!e.mayICollide(em)) continue; // Skip if you may NOT collide...

                // Check collision
                if (Collision.intersects(r, em.getCollider())) {
                    colls.add(em);
                }
            }

            // Call onCollide ONLY if necessary
            if (!colls.isEmpty())
                e.onCollide(this, colls);
        }
    }

    // Handle unit placement
    private TowerType placing_type = null;
    private boolean can_place = false;

    // Begin placement of a unit
    public void setPlacingType(TowerType tt) {
        // Only possible if building / placing
        if (this.state != State.BUILDING && this.state != State.PLACING) return;

        // Set state first as it overrides then
        this.setState(State.PLACING);
        this.placing_type = tt;
    }

    // Cancel placement type
    public void cancelPlacing() {
        this.setState(State.BUILDING);
        this.money += held_cards.get(selected_card).getCost();
        this.selected_card = -1;
    }

    // Did someone say... CLICK???
    public void click() { this.queued_click = true; }

    private void nothing() { }

    // Click handler
    private void handleClicking() {
        // ALWAYS handle Widgets first
        for (Widget w : this.getActiveWidgets()) {
            if (w.getHovered() && w.onClick()) {
                // Press the widget
                return;
            }
        }

        // Defer to other clicks
        switch (state) {
            case PLACING -> clickPlaceSelectedCard();
            case BUILDING     -> nothing();
            default           -> nothing();
        }
    }

    // Debug: toggle pause state
    public void togglePause() {
        this.paused = !this.paused;
    }

    // Click with state as PLACING_UNIT
    private void clickPlaceSelectedCard() {
        // No placement if can't place
        if (!can_place) { 
            Error.raise("You can't place there!");
            return; 
        }
        
        // No placement if no card selected
        if (selected_card == -1) { 
            System.out.println("DEBUG - attempted to place while selected_card = -1;");
            return; 
        }

        // Selected card should be a Tower
        if (!(this.held_cards.get(selected_card) instanceof TowerCard tc)) { return; }

        // Get metadata
        int up = tc.getUpgradeAmount();

        // Check if its upgrade vs placing new
        if (this.board.getOccupied(selected_x, selected_y)) {
            Tower t = (Tower) this.board.getUnitAt(selected_x, selected_y);

            // Upgrade the tower
            t.addCard(up);
        } else {
            // Create a new unit of type placingType
            // at the focused point
            this.queryPlace(placing_type, selected_x, selected_y);

            // Upgrade it by tc.getUpgradeAmount - 1
            
            ((Tower) this.board.getUnitAt(selected_x, selected_y)).addCard(up - 1);
        }

        
        // Remove the selected_card
        clearCard(selected_card);


        // Cancel the placement
        cancelPlacing();
    }

    // Handle 1, 2, 3 pressed
    public void numsPressed(int num) {
        // Must be building
        if (this.state != State.BUILDING) return;

        // Confirm current card isn't null
        if (this.held_cards.get(num) == null) return;

        // useCard at num
        useCard(num);
    }

    // Get canPlace
    public boolean canPlace() { return this.can_place; }

    // Redrawing card logic
    public void attemptRedraw() {
        // Check if money is sufficient
        if (!this.buy(2)) return; 
        this.dealCards();
    }

    // Debugging methods
    public void giveABajillionDollars() {
        this.money = Integer.MAX_VALUE;
    }

    public void setStacksToMax() {
        this.max_purchase = 25;
    }

    // Tick updates
    public void update(double dt) {
        // DEBUG: Set type to Tree, mode to PLACING_UNIT
        // this.gameState = State.PLACING_UNIT;
        // this.placingType = UnitType.TREE;

        // Update the widgets to set hovered state
        for (Widget w : this.getActiveWidgets()) {
            w.update(this.mouse_x, this.mouse_y);
        }

        // We'll handle clicking first
        if (queued_click) {
            // Unqueue the click
            queued_click = false;
            this.handleClicking();
        }

        // Skip update if paused
        if (this.paused) return;

        // RenderStep entities
        for (Entity e : new ArrayList<>(this.board.getEntities())) {
            e._renderStep(dt);
        }

        // RenderStep units
        // Do this after in case a tower creates a 
        // projectile (so first tick doesn't immediately)
        // move the projectile

        // Also force RequestGamestates to accept a GameState
        for (Unit u : this.board.getUnits()) {
            if (u instanceof RequestsGamestates rsu) {
                rsu.setGameState(this);
            }

            if (u != null) {
                u._renderStep(dt);
            }
        }

        // Create projetiles requested by towers
        for (Unit u : this.board.getUnits()) {
            if (u instanceof Tower t) {
                for (Projectile p : t.getQueuedProjectiles()) {
                    this.board.addEntity(p);
                }
            }
        }

        // Move entities requesting movement
        for (Entity e : new ArrayList<>(this.board.getEntities())) {
            if (e.getDX() != 0 || e.getDY() != 0)
                this.resolveMovement(e);
        }

        // Destroy entities that are dead
        for (Entity e : new ArrayList<>(this.board.getEntities())) {
            if (e.getHP() <= 0 && !(e instanceof Goal)) {
                this.board.removeEntity(e);
            }
        }

        // Attempt to spawn enemies from spawners
        // if state is correct
        if (this.state == State.AUTO) {
            // Check if wave can still spawn
            if (this.current_wave.stillGoing()) {
                this.current_wave.update(dt, this);
            } else if (!this.board.stillEnemies()) {
                // Set to BUILDING
                this.setState(State.BUILDING);
            }
        }
 
        // Calculate collisions
        this.collisionEngine();

        // Refocus player
        this.focus();

        // Sort entities
        this.board.sortEntities();

        // Check if game over
        if (!goal.isAlive()) {
            this.gameOver = true;
        }
    }
}
