import java.util.List;

/**
 * Represents the first level of Galaxy Shooter.
 *
 * This level introduces the player to basic gameplay by spawning only
 * BasicEnemy instances. It begins with a row of enemies at the top of
 * the screen and continues spawning additional enemies over time.
 *
 * The level is completed once the player reaches the required number
 * of kills.
 */

public class Level1 extends Level {
    private static final int KILLS_REQUIRED = 1;
    private static final int MAX_ON_SCREEN = 5;
    private static final int SPAWN_INTERVAL = 120;
    private static final int MIN_SPACING = 90; // min px between enemies
    
    // reference to the game's bullet list so enemies can shoot into it
    private List<Bullet> gameBullets;
    private int killCount = 0;
    private int spawnTimer = 0;

    // Constructor

    /**
     * Creates Level1.
     *
     * @param gameBullets the shared bullet list used by enemies to fire projectiles
    */
    public Level1(List<Bullet> gameBullets) {
        super(1);                        // level number = 1
        this.gameBullets = gameBullets;  // store bullet list for enemy creation
    }

    // Abstract Metho

    /**
     * Spawns the initial set of enemies.
     *
     * Five BasicEnemy instances are placed in a horizontal row
     * near the top of the screen.
    */
    @Override
    public void spawnEnemies() {
        for (int i = 0; i < 5; i++) {
            int spawnX = 80 + i * 100;  // space enemies 100px apart across the screen
            int spawnY = 60;            // near the top of the 800px tall screen
            // each enemy gets the shared bullet list so their shoot() works
            enemies.add(new BasicEnemy(spawnX, spawnY, gameBullets));
        }
    }

     /**
     * Spawns a single enemy at a safe horizontal position.
     *
     * The position is chosen to maintain spacing between existing enemies.
     */
    private void spawnOne() {
        int x = findSafeX();
        enemies.add(new BasicEnemy(x,-60,gameBullets));
    }

    /**
     * Finds a safe horizontal position for spawning a new enemy.
     *
     * Attempts to find a position that is at least MIN_SPACING pixels away
     * from all existing enemies. If no suitable position is found after
     * several attempts, a random position is used as a fallback.
     *
     * @return a valid x-coordinate for spawning an enemy
     */
    private int findSafeX() {
        int attempts = 20;
        while (attempts-- > 0) {
            int candidate = (int)(Math.random() * (700-48));
            boolean clear = true;
            for (Enemy e : enemies) {
                if (Math.abs(e.getX() - candidate) < MIN_SPACING) {
                    clear = false;
                    break;
                }
            }
            if (clear) return candidate;
        }

        // Fallback just pick a random pos
        return (int)(Math.random() * (700-48));
    }

    /**
     * Updates the level each frame.
     *
     * Removes enemies that move off-screen and periodically spawns new ones
     * if the number of active enemies is below the maximum allowed.
    */
    @Override
    public void update() {
        if (isComplete()) return;
        enemies.removeIf(e -> e.getY() > 820);
        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL){
            spawnTimer = 0;
            if (enemies.size() < MAX_ON_SCREEN) spawnOne();
        }
    }


    /**
     * Called when an enemy is killed.
     *
     * Increments the kill count used to track level progress.
     */
    @Override public void onEnemyKilled() { killCount++; }

    /**
     * Returns the current number of kills.
     *
     * @return the number of enemies defeated
     */
    @Override public int getKillCount() { return killCount; }

    /**
     * Returns the number of kills required to complete the level.
     *
     * @return the required kill count
     */
    @Override public int getKillsRequired() {return KILLS_REQUIRED; }


    /**
     * Determines whether the level is complete.
     *
     * The level is complete when the player reaches the required number of kills.
     *
     * @return true if the level is complete, false otherwise
     */
    @Override
    public boolean isComplete() {
        // stream through all enemies — level is complete when every one isDead()
        return killCount >= KILLS_REQUIRED;
    }

} // end Level1
 