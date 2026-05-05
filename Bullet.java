
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Abstract base class for all projectiles in Galaxy Shooter.
 *
 * Encapsulates the shared state (position, speed, damage, active flag)
 * and the contract that every concrete bullet type must satisfy. The game
 * loop iterates over a single {@code List<Bullet>} that holds both
 * {@link PlayerBullet}, {@link SpreadBullet} 
 * and {@link EnemyBullet} instances, so all bullet
 * logic is accessed through this common interface.
 *
 * Subclasses: {@link PlayerBullet}, {@link EnemyBullet}, {@link SpreadBullet}
 */
public abstract class Bullet {

    // Fields
    // declare variables
    protected int x;           // horizontal position
    protected int y;           // vertical position
    protected int speed;       // pixels moved per frame
    protected int damage;      // health removed on hit
    protected boolean active;  // false = remove from game

    // ── Constructor — exactly as specified in the PDF ─────────────────
    // Sets starting position and marks bullet as active.
     // Subclasses call super(x, y) then set their own speed and damage.

    /**
     * Initializes the bullet at the given position and marks it active.
     * Subclasses call {@code super(x, y)} and then set their own
     * {@link #speed} and {@link #damage} values.
     *
     * @param x the initial horizontal position (pixels from left edge)
     * @param y the initial vertical position (pixels from top edge)
    */
    public Bullet(int x, int y) {
        this.x      = x; // starting horizontal position
        this.y      = y; // starting vetical position
        this.active = true;    // PDF specifies active = true in constructor
    }

    // ── Abstract methods — implement in each subclass ─────────────────
    // PDF specifies exactly these two as abstract.

     /**
     * Moves the bullet by {@link #speed} pixels each game frame.
     * Direction (up or down) is determined by the subclass implementation.
     * Called by the game loop every frame.
    */
    public abstract void update();

    /** 
    * Abstract class 
    * draw this bullet at its current position
    * @param g Graphics2D object
    */
    public abstract void draw(Graphics2D g);

     /**
     * Returns the sprite image used to draw this bullet each frame.
     * Loaded from the {@code /images/} resources folder by each subclass.
     *
     * @return the bullet's {@link BufferedImage} sprite, or {@code null}
     *         if the image resource could not be found
    */
    public abstract BufferedImage getSprite();

    // Concrete Methods

    /**
     * Returns the bullet's current horizontal position.
     *
     * @return x coordinate in pixels
    */
    public int getX() { return x; }

    /**
     * Returns the bullet's current vertical position.
     *
     * @return y coordinate in pixels
    */
    public int getY() { return y; }

 
     /**
     * Returns the amount of damage this bullet deals on impact.
     * The game loop passes this value to the target's damage handler
     * after a collision is detected.
     *
     * @return damage amount (positive integer)
    */
    public int getDamage() { return damage; }

    /**
     * Returns {@code true} while the bullet is still in play.
     * The game loop removes bullets for which this returns {@code false}.
     *
     * @return {@code true} if the bullet has not yet been deactivated
     */
    public boolean isActive() { return active; }


    /**
     * Marks this bullet as inactive.
     * Called by the game loop when the bullet hits a target or travels
     * off the edge of the screen.
    */
    public void deactivate() { active = false; }

    /**
     * Returns the axis-aligned bounding box used for collision detection.
     *
     * @return a {@link Rectangle} at this bullet's current position
    */
    public Rectangle getBounds() {
        return new Rectangle(x, y, 6, 16);
    }
}