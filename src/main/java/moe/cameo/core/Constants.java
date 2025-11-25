package moe.cameo.core;

public class Constants {
    public static final int TILE_SIZE = 32;

    public static final int GAME_COLUMNS = 32;
    public static final int GAME_ROWS = 24;

    public static final int FPS = 60;

    public static final int PLAYER_ACCELERATION = 240;
    public static final int PLAYER_DECCELERATION = 480;
    public static final int PLAYER_MAX_SPEED = 120;

    public static final int GRACE_TIME_PER_ATTACK = 3;

    // Calculate other data ---- DON'T CHANGE PAST THESE
    // POINTS AS THEY'RE NOT CONFIGURATIONS
    public static final int SCREEN_X = GAME_COLUMNS * TILE_SIZE;
    public static final int SCREEN_Y = GAME_ROWS * TILE_SIZE;

    public static final int FRAME_TIME_MS = 1_000 / FPS;
}
