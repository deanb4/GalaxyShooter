// TODO: LEFT OFF HERE
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all levels in Galaxy Shooter.
 *
 * Manages the list of enemies for a level and exposes the two-method
 * contract that the game loop depends on: {@link #spawnEnemies()} to
 * populate the level at load time, and {@link #isComplete()} to check
 * the win condition each frame
 *
 * The game loop only ever calls {@link #getEnemies()} and
 * {@link #isComplete()}, keeping level logic cleanly separated from
 * rendering and input handling.
 *
 * Subclasses: {@link Level1}, {@link Level2}, {@link level3}, {@lik Bosslevel}
 */
public abstract class Level {

    // Fields
    protected List<Enemy> enemies;    // all enemies active in this level
    protected int levelNumber;        // which level this is (1, 2, 3...)

    // Constructor

    
    /**
     * Initializes the level with an empty enemy list and stores the
     * level number. Subclasses call {@code super(levelNumber)}.
     *
     * @param levelNumber the sequential number of this level
    */
    public Level(int levelNumber) {
        this.levelNumber = levelNumber;         // store which level this is (1, 2, 3...)
        this.enemies     = new ArrayList<>();   // start empty — spawnEnemies() fills this
    }

    // Abstract methods

   // Populates the enemies list with the enemies for this level.
   // Called ONCE by the game when this level starts loading.
   // Each subclass (Level1, Level2) adds its own Enemy types and positions here.

    /**
     * Populates {@link #enemies} with the enemies for this level.
     *
     * Called once by the game when this level starts loading.
     * Each subclass adds its own mix of {@link BasicEnemy} and
     * {@link BossEnemy} instances at appropriate screen positions.
    */
    public abstract void spawnEnemies();

    /**
     * Abstract method implemented by subclasses
     * Called once per frame by GamePanel (after collision + dead-enemy removal).
     * Responsible for: culling off-screen enemies, ticking the spawn timer,
     * and adding new enemies when the timer fires.
     */
    public abstract void update();

    /**
     * Notifies the level that an enemy has been killed.
     *
     * Called by the game loop whenever a kill is confirmed.
     * Used to track progress toward level completion.
     */
    public abstract void onEnemyKilled();

    /**
     * Returns the number of enemies killed so far.
     *
     * Used by the HUD to display player progress
     *
     * @return the current kill count
    */
    public abstract int getKillCount();

    /**
     * Determines whether the level is complete.
     *
     * Called every frame by the game loop to check if the player
     * has met the win condition.
     *
     * @return true if the level is complete, false otherwise
    */
    public abstract boolean isComplete();

    /**
     * Returns the total number of kills required to complete the level.
     *
     * Used by the HUD progress bar.
     *
     * @return the required number of kills
     */
    public abstract int getKillsRequired();


    /**
     * Draws all enemies in this level.
     *
     * A snapshot copy of the enemy list is used to prevent errors
     * if the list is modified during iteration in the same frame.
     *
     * @param g the graphics context used for drawing
     */
    public void draw(Graphics2D g) {
        List<Enemy> snapshot = new ArrayList<>(enemies); // shallow copy
        for (Enemy e : snapshot) {
            e.draw(g);
        }
    }

    // Concrete methods shared by all levels

    /**
     * Returns a copy of the current enemy list.
     *
     * The game loop iterates over this copy instead of the original list,
     * allowing safe modification during the same frame.
     *
     * @return a copy of the enemy list
    */
    public List<Enemy> getEnemiesSnapshot() { return new ArrayList<>(enemies); }


    /**
     * Removes all enemies that are marked as dead.
     *
     * Typically called once per frame after collision handling.
     */
    public void removeDeadEnemies() {
        enemies.removeIf(Enemy::isDead);
    }

    
    /**
     * Returns the level number.
     *
     * @return the level number (1, 2, 3, etc.)
    */
    public int getLevelNumber() { return levelNumber; }
}