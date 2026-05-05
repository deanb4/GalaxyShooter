import java.util.List;

/**
 * Represents the third level of Galaxy Shooter.
 *
 * This level increases difficulty by introducing faster enemy spawning
 * and two BossEnemy encounters during the wave. BasicEnemy units spawn
 * continuously while bosses appear at specific kill thresholds.
 *
 * The level is completed once the required number of kills is reached.
 */
public class Level3 extends Level {

    // static fields
    private static final int KILLS_REQUIRED = 30;
    private static final int MAX_ON_SCREEN = 7;
    private static final int SPAWN_INTERVAL = 60;
    private static final int BOSS1_SPAWN_AT = 10;
    private static final int BOSS2_SPAWN_AT = 20;
    private static final int MIN_SPACING = 70;

    private final List<Bullet> gameBullets;  /** Shared bullet list used by enemies */
    private int killCount = 0;
    private int spawnTimer = 0;
    private boolean boss1Spawned = false;
    private boolean boss2Spawned = false;


    /**
     * Creates Level3.
     *
     * @param gameBullets shared bullet list used by enemies to fire projectiles
     */
    public Level3(List<Bullet> gameBullets) {
        super(3);
        this.gameBullets = gameBullets;
    }

    /**
     * Spawns the initial wave of BasicEnemy instances.
     *
     * Enemies are distributed across the screen with slight vertical staggering
     * to create a more dynamic formation.
     */
    @Override
    public void spawnEnemies() {
        int cols = MAX_ON_SCREEN;
        int colW = (700 - 48) / cols;
        for (int i = 0; i < cols; i++) {
            int x = i * colW + (int)(Math.random() * Math.max(1,colW - 48));
            int y = -60 - i * 50;
            enemies.add(new BasicEnemy(x, y, gameBullets));
        }
    }

     /**
     * Spawns a single BasicEnemy at a safe horizontal position.
     */
    private void spawnBasic() {
        int x = findSafeX();
        enemies.add(new BasicEnemy(x, -60, gameBullets));
    }

    /**
     * Finds a safe x-coordinate for spawning an enemy.
     *
     * Attempts to avoid placing enemies too close to each other.
     * If no valid position is found, a random position is returned.
     *
     * @return a valid x-coordinate for spawning an enemy
     */
    private int findSafeX() {
        int attempts = 20;
        while (attempts-- > 0) {
            int candidate = (int)(Math.random() * (700 - 48));
            boolean clear = true;
            for (Enemy e : enemies) {
                if (Math.abs(e.getX() - candidate) < MIN_SPACING) { 
                    clear = false; break; 
                }
            }
            if (clear) return candidate;
        }
        return (int)(Math.random() * (700 - 48));
    }

    /**
     * Updates the level each frame.
     *
     * Removes off-screen enemies, spawns two bosses at different kill thresholds,
     * and continues spawning BasicEnemy instances while respecting on-screen limits.
     */
    @Override
    public void update() {
        if (isComplete()) return;
        enemies.removeIf(e -> e.getY() > 820);

        // First boss
        if (!boss1Spawned && killCount >= BOSS1_SPAWN_AT) {
            enemies.add(new BossEnemy(100,-80,gameBullets));
            boss1Spawned = true;
        }
        // Second boss (other side)
        if (!boss2Spawned && killCount >= BOSS2_SPAWN_AT) {
            enemies.add(new BossEnemy(400, -80, gameBullets));
            boss2Spawned = true;
        }

        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            long bossCount = enemies.stream().filter(e -> e instanceof BossEnemy).count();
            int cap = MAX_ON_SCREEN - (int) bossCount;
            long basicAlive = enemies.stream().filter(e -> e instanceof BasicEnemy).count();
            if (basicAlive < cap) spawnBasic();
        }
    }


    /**
     * Called when an enemy is killed.
     *
     * Increments the kill counter used to track level progress.
     */
    @Override public void onEnemyKilled() { killCount++; }

    /**
     * Determines whether the level is complete.
     *
     * The level ends when the required number of kills is reached.
     *
     * @return true if the level is complete, false otherwise
     */
    @Override public boolean isComplete() { return killCount >= KILLS_REQUIRED; }


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
    @Override public int getKillsRequired() { return KILLS_REQUIRED; }
}
