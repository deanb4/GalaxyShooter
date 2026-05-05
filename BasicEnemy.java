
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * A standard enemy in Galaxy Shooter.
 *
 * Moves straight down the screen at a constant speed and fires a single
 * {@link EnemyBullet} downward at a fixed interval. Requires two player
 * hits to destroy and awards 100 points.
 *
 * Sprite: {@code /images/basicEnemy.png} 
 *
 */
public class BasicEnemy extends Enemy{

    // shoot timer — controls how often this enemy fires back
    private int shootTimer = 0;

    /**
     * Number of frames between consecutive shots.
     * At 60 FPS this enemy fires roughly every 1.5 seconds.
    */
    private static final int SHOOT_INTERVAL = 90;

    // reference to the game's bullet list so shoot() can add to it
    private final List<Bullet> gameBullets;

    // Enemy sprite
    private final BufferedImage sprite;


    /**
     * Creates a BasicEnemy at the specified position.
     *
     * @param x           initial horizontal position (pixels from left edge)
     * @param y           initial vertical position (pixels from top edge)
     * @param gameBullets the shared bullet list from {@code GamePanel};
     *                    bullets fired by this enemy are added here
    */
    public BasicEnemy(int x, int y, List<Bullet> gameBullets) {
        super(x, y);
        this.health      = 2;    // takes 2 player bullets to kill
        this.maxHealth   = 2;
        this.speed       = 2;    // moves 2 pixels downward per frame
        this.gameBullets = gameBullets;

        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResource("/images/basicEnemy.png"));
        } catch (Exception e) {
            // fallback
        }
        this.sprite = loaded;
    }

// ── Abstract Method Implementations ──────────────────────────────────────

/**
 * Advances the enemy one game frame.
 *
 * Moves the enemy straight down by {@link #speed} pixels, then
 * increments the shoot timer. When the timer reaches
 * {@link #SHOOT_INTERVAL} a bullet is fired and the timer resets.
*/
@Override
public void update() {
    // move logic — BasicEnemy moves straight down toward the player
    y += speed;

    // increment shoot timer and fire when interval is reached
    shootTimer++;
    if (shootTimer >= SHOOT_INTERVAL) {
        shoot();
        shootTimer = 0;  // reset after firing
    }
}


/**
 * Fires a single {@link EnemyBullet} centered below this enemy.
 *
 * The bullet is spawned at {@code (x + 21, y + 48)} so that it
 * emerges from the horizontal center of the 48 px wide sprite and
 * just below its bottom edge.
 * x + 21 centers the 6px wide bullet inside the 48px wide enemy
 * y + 48 spawns it just below the bottom edge of the enemy sprite
*/
@Override
public void shoot() {
    gameBullets.add(new EnemyBullet(x + 21, y + 48));
}


/**
 * Returns the score awarded to the player for destroying this enemy.
 *
 * @return {@code 100} points
 */
@Override
public int getPoints() {
    return 100;  // player earns 100 points for killing a BasicEnemy
}

/**
 * Loads and returns the sprite image for this enemy.
 * The image is read fresh each call from the classpath resource
 * {@code /images/basicEnemy.png}.
 *
 * @return the enemy sprite, or {@code null} if the resource
 * cannot be located
 */
@Override
public BufferedImage getSprite() {
    // loads the enemy sprite image from the /images/ resources folder
    return sprite;
}

/**
 * Draws the enemey if sprite exists
 * If no image found it fallsback to drawing basic enemy
 * @param g Graphics2D object
 */
@Override
public void draw(Graphics2D g) {
    if (sprite != null) {
        g.drawImage(sprite,x,y,48,48,null);
    } else {
        g.setColor(new Color(180,0,220));
        g.fillRect(x,y,48,48);
        g.setColor(new Color(255,50,50));
        g.fillOval(x + 10, y + 14, 10, 10);
        g.fillOval(x + 28, y + 14, 10, 10);
    }
    drawHealthBar(g,x,y-8,48);
}

/**
 * Returns the axis-aligned bounding box for collision detection.
 * Matches the 48&times;48 pixel size of the sprite.
 *
 * @return a {@link Rectangle} at this enemy's current position
 */
@Override
public Rectangle getBounds() {
    // 48x48 hitbox matches the basicEnemy.png sprite size
    return new Rectangle(x, y, 48, 48);
}
}
