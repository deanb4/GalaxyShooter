import java.util.List;

/**
 * Represents the second level of Galaxy Shooter.
 *
 * This level increases difficulty compared to Level1 by spawning more
 * enemies, introducing multiple rows of BasicEnemy instances, and
 * adding a BossEnemy partway through the level.
 *
 * The level is considered complete once the required number of kills
 * has been reached.
 */

public class Level2 extends Level {

    private static final int KILLS_REQUIRED = 20;
    private static final int MAX_ON_SCREEN = 6;
    private static final int SPAWN_INTERVAL = 90;
    private static final int BOSS_SPAWN_AT = 10;
    private static final int MIN_SPACING = 80;
    // reference to the game's bullet list so enemies can shoot into it
    private List<Bullet> gameBullets;
    private int killCount = 0;
    private int spawnTimer = 0;
    private boolean bossSpawned = false;

    // Constructor


    /**
     * Creates Level2.
     *
     * @param gameBullets shared bullet list used by enemies to fire projectiles
    */
    public Level2(List<Bullet> gameBullets) {
        super(2);                        // level number = 2
        this.gameBullets = gameBullets;  // store bullet list for enemy creation
    }

    /**
     * Spawns the initial wave of BasicEnemy instances.
     *
     * Enemies are distributed across multiple columns with slight randomness
     * in vertical positioning to create a staggered formation.
     */
    @Override
    public void spawnEnemies() {
       int cols = MAX_ON_SCREEN;
       int colW = (700-48) / cols;
       for (int i = 0; i < cols; i++) {
        int x = i * colW + (int)(Math.random() * (colW - 48));
        int y = -60 - i * 60;
        enemies.add(new BasicEnemy(x,y,gameBullets));
       }
    }

    /**
     * Spawns a single BasicEnemy at a safe horizontal position.
     */
    private void spawnBasic() {
        int x = findSafeX();
        enemies.add(new BasicEnemy(x,-60,gameBullets));
    }

    /**
     * Finds a safe x-coordinate for spawning an enemy.
     *
     * Ensures that the new enemy is not too close to existing ones.
     * If no valid position is found after several attempts, a random
     * position is returned as a fallback.
     *
     * @return a valid x-coordinate for spawning
     */
    private int findSafeX() {
        int attempts = 20;
        while (attempts-- > 0) {
            int candidate = (int)(Math.random() * (700-48));
            boolean clear = true;
            for (Enemy e : enemies) {
                if (Math.abs(e.getX() - candidate) < MIN_SPACING) { clear = false; break;}
            }
            if (clear) return candidate;
        }
        return (int)(Math.random() * (700-48));
    }

    /**
     * Updates the level each frame.
     *
     * Removes off-screen enemies, spawns a boss after a certain number
     * of kills, and continues spawning BasicEnemy instances until the
     * maximum on-screen limit is reached.
    */
    @Override
    public void update() {
        if (isComplete()) return;
        enemies.removeIf(e -> e.getY() > 820);

        if (!bossSpawned & killCount >= BOSS_SPAWN_AT) {
            enemies.add(new BossEnemy(268, -80, gameBullets));
            bossSpawned = true;
        }

        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            boolean bossAlive = enemies.stream().anyMatch(e -> e instanceof BossEnemy);
            int cap = bossAlive ? MAX_ON_SCREEN -1 : MAX_ON_SCREEN;
            if (enemies.size() < cap) spawnBasic();
        }
    }

    /**
     * Called when an enemy is killed.
     *
     * Increments the kill counter used to track level progress.
     */
    @Override public void onEnemyKilled() { killCount++; }

    /**
     * Returns the number of enemies killed so far.
     *
     * @return current kill count
     */
    @Override public int getKillCount() { return killCount; }

    /**
     * Returns the number of kills required to complete the level.
     *
     * @return required kill count
     */
    @Override public int getKillsRequired() { return KILLS_REQUIRED;}

     /**
     * Determines whether the level is complete.
     *
     * The level ends when the required number of kills is reached.
     *
     * @return true if the level is complete, false otherwise
     */
    @Override
    public boolean isComplete() {
        return killCount >= KILLS_REQUIRED;
    }

} 