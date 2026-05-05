import java.util.List;

/**
 * Represents the final boss level in Galaxy Shooter.
 *
 * This level contains a single FinalBoss enemy that appears at the top
 * of the screen and moves horizontally. No regular enemies are spawned.
 *
 * The level is completed when the boss is defeated. The kill count is
 * either 0 or 1 and is primarily used for HUD display.
 */
public class BossLevel extends Level {

    /** Reference to the shared bullet list used by the game */
    private final List<Bullet> gameBullets;
    private boolean bossDefeated = false;
    private int killCount = 0;

    /**
     * Creates the boss level and initializes required resources.
     *
     * @param gameBullets the shared list of bullets used by enemies
     */
    public BossLevel(List<Bullet> gameBullets) {
        super(4);
        this.gameBullets = gameBullets;
    }

    /**
     * Spawns the final boss enemy.
     *
     * The boss is positioned horizontally centered at the top of the screen,
     * slightly off-screen so it enters smoothly.
     */
    @Override
    public void spawnEnemies() {
        // Center the boss in the screen
        int x = (700 / 2) - (96 / 2) ;
        enemies.add(new FinalBoss(x, -100, gameBullets));
    }


    /**
     * Updates the boss level each frame.
     *
     * No additional enemies are spawned in this level.
     * Once the boss is defeated, the level stops updating.
     */
    @Override
    public void update() {
        if (isComplete()) return;
    }

    /**
     * Called when the boss is killed.
     *
     * Updates the kill count and marks the level as complete.
     */
    @Override
    public void onEnemyKilled() {
        killCount++;
        bossDefeated = true;
    }

    /**
     * Determines whether the level is complete.
     *
     * @return true if the boss has been defeated, false otherwise
     */
    @Override public boolean isComplete() { return bossDefeated; }


    /**
     * Returns the current kill count.
     *
     * @return the number of enemies killed (0 or 1)
    */
    @Override public int getKillCount() { return killCount; }

    /**
     * Returns the number of kills required to complete the level.
     *
     * The value is always 1 since only the boss needs to be defeated.
     *
     * @return 1
     */
    @Override public int getKillsRequired() { return 1; }

}
