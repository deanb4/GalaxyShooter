
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Abstract base class for all enemies in Galaxy Shooter.
 *
 * Defines the shared state (position, health, speed) and the contract
 * (abstract methods) that every concrete enemy type must implement.
 * The game loop interacts with all enemies exclusively through this interface,
 * so adding a new enemy type only requires extending this class.
 *
 * Subclasses: {@link BasicEnemy}, {@link BossEnemy}
 */
public abstract class Enemy {

    // Fields
    // declare variables
    protected int x; // horizontal position
    protected int y; // vertical position
    protected int health; // current health points
    protected int maxHealth;
    protected int speed; // movement speed per frame

    /**
     * Initializes the enemy at the specified screen coordinates.
     * Subclasses must call {@code super(x, y)} and then set
     * their own {@link #health} and {@link #speed} values.
     *
     * @param x the initial horizontal position (pixels from left edge)
     * @param y the initial vertical position (pixels from top edge)
    */
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Abstract Methods to force subclasses to implement these classes

    
    /**
     * Updates the enemy's state for one game frame.
     * Implementations should move the enemy and advance the shoot timer.
     * Called by the game loop every frame.
    */
    public abstract void update();

    /**
     * Fires one or more {@link EnemyBullet}s into the shared bullet list.
     * Called internally by {@link #update()} when the shoot timer expires.
    */
    public abstract void shoot(); 

    /**
     * Returns the score awarded to the player for defeating this enemy.
     *
     * @return point value of this enemy
    */
    public abstract int getPoints(); // Game score awarded

    /**
     * Returns the sprite image used to draw this enemy each frame.
     * Loaded from the {@code /images/} resources folder.
     *
     * @return the enemy's {@link BufferedImage} sprite, or {@code null} if
     *         the image resource could not be found
    */
    public abstract BufferedImage getSprite(); // sprite image

    /** Draw this enemy and its health bar 
     * abstract method implemented by sublcasses
     * @param g a Graphics2D object
     * @return null
    */
    public abstract void draw(Graphics2D g);

    /**
    * Shared health bar helper available to all subclasses (protected)
    * @param g Graphics2D object 
    * @param x x position
    * @param y y position
    * @param w width
    * @return null
    */ 
    protected void drawHealthBar(Graphics2D g, int x, int y, int w) {
        if (maxHealth <=0) return;
        double ratio = Math.max(0.0, (double) health / maxHealth);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x,y,w,4);
        g.setColor(ratio > 0.5 ? new Color(0,200,0) : new Color(220,50,0));
        g.fillRect(x,y, (int)(w*ratio),4);
    }

    // Concrete Methods

    /**
     * Returns the enemy's current horizontal position.
     *
     * @return x coordinate in pixels
    */
    public int getX() { return x; }
    
    /**
     * Returns the enemy's current vertical position.
     *
     * @return y coordinate in pixels
    */
    public int getY() { return y; }

    /**
     * Returns the enemy's current health points.
     *
     * @return remaining health (0 or above)
    */
    public int getHealth() { return health; }
    
    /**
     * Returns {@code true} when the enemy's health has reached zero.
     * The game loop uses this to remove dead enemies and award points.
     *
     * @return {@code true} if health &lt;= 0, {@code false} otherwise
    */
    public boolean isDead() { return health <= 0; }
    
    /**
     * Reduces the enemy's health by the specified amount.
     * Called by the game loop when a {@link PlayerBullet} collides with
     * this enemy.
     *
     * @param amt the amount of damage to apply (should be positive)
    */
    public void takeDamage(int amt) { health -= amt; }


    /**
     * Returns the axis-aligned bounding box used for collision detection.
     * Override in subclasses that use a different size.
     *
     * @return a {@link Rectangle} at this enemy's current position
    */
    public Rectangle getBounds() {
        return new Rectangle(x, y, 48, 48);}
}


