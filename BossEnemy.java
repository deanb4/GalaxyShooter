
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * 
 * The end-level boss enemy in Galaxy Shooter.
 *
 * Unlike {@link BasicEnemy}, the BossEnemy moves horizontally across
 * the screen, bouncing off the left and right edges. It fires a spread
 * shot of two {@link EnemyBullet}s at a faster interval than the basic
 * enemy, requires 10 hits to destroy, and awards 500 points.
 *
 * Sprite: {@code /images/bossEnemy.png} 
 *
 */
public class BossEnemy extends Enemy{

    // shoot timer — boss shoots faster than BasicEnemy
    private static final int W = 64;
    private static final int H = 64;
    private int shootTimer = 0;
    private static final int SHOOT_INTERVAL = 50; // fires every 50 frames

    // reference to the game's bullet list so shoot() can add to it
    private List<Bullet> gameBullets;
    private final BufferedImage sprite;

    /**
     * Creates a BossEnemy at the specified position.
     *
     * @param x           initial horizontal position (pixels from left edge)
     * @param y           initial vertical position (pixels from top edge)
     * @param gameBullets the shared bullet list from {@code GamePanel};
     *                    bullets fired by this enemy are added here
    */
    public BossEnemy(int x, int y, List<Bullet> gameBullets) {
        super(x, y);                     // calls Enemy(int x, int y) constructor
        this.health      = 10;           // inherited from Enemy — takes 10 hits to kill
        this.maxHealth   = 10;
        this.speed       = 2;            // inherited from Enemy — moves 2px side to side
        this.gameBullets = gameBullets;  // shared bullet list passed in from GamePanel

        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResource("/images/bossEnemy.png"));
        } catch (Exception e) {
            // fallback
        }
        this.sprite = loaded;
    }

    // Abstract

    /**
     * Advances the boss one game frame.
     *
     * Moves the boss horizontally by {@link #speed} pixels. When the
     * boss reaches either screen edge the speed is
     * negated to reverse direction.
     *
     * Also increments the shoot timer and fires when
     * {@link #SHOOT_INTERVAL} is reached.
    */
    @Override
    public void update() {
        // move logic — boss moves side to side across the screen
        x += speed;  // x and speed inherited from Enemy

        // reverse direction when hitting screen edges
        if (x + W >= 700 || x <= 0) speed = -speed;

        // increment shoot timer every frame
        shootTimer++;
        if (shootTimer >= SHOOT_INTERVAL) {
            shoot();         // fire bullets
            shootTimer = 0;  // reset timer after firing
        }
    }

    /**
     * Fires a spread shot of two {@link EnemyBullet}s from the boss.
     *
     * The left bullet originates at {@code (x + 8, y + 48)} and the
     * right bullet at {@code (x + 34, y + 48)}, producing a side-by-side
     * pair that covers more horizontal area than a single shot.
    */
    @Override
    public void shoot() {
        // boss fires two EnemyBullets side by side — spread shot
        // left bullet — x + 8 offsets from left side of boss sprite
        gameBullets.add(new EnemyBullet(x + 8,  y + H));
        gameBullets.add(new EnemyBullet(x + W - 14, y + H));
    }

    /**
     * Returns the score awarded to the player for destroying the boss.
     *
     * @return {@code 500} points
    */
    @Override
    public int getPoints() {
        return 500;  // 500 points awarded <--- we can change this
    }

    /**
     * Loads and returns the sprite image for the boss.
     * The image is read fresh each call from the classpath resource
     * {@code /images/bossEnemy.png}.
     *
     * @return the boss sprite, or {@code null} if the resource
     * cannot be located
    */
    @Override
    public BufferedImage getSprite() {
        return sprite;
    }

    /**
     * Draws the Boss Enemy if sprite available 
     * Or falls back to drawing enemy from shapes
     * adds health bar underneath
     * @param g Graphics2D object
     */
    @Override
    public void draw(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, W, H, null);
        } else {
            // Red body with wings and yellow eyes
            g.setColor(new Color(220, 0, 0));
            g.fillRect(x, y, W, H);
            g.setColor(new Color(255, 80, 80));
            g.fillRect(x - 14, y + 20, 14, 24); // left wing
            g.fillRect(x + W,  y + 20, 14, 24); // right wing
            g.setColor(new Color(255, 220, 0));
            g.fillOval(x + 12, y + 18, 14, 14);
            g.fillOval(x + 38, y + 18, 14, 14);
            g.setColor(Color.BLACK);
            g.fillOval(x + 16, y + 22, 6, 6);
            g.fillOval(x + 42, y + 22, 6, 6);
        }
        drawHealthBar(g, x, y - 10, W);
    }

    /**
     * Returns the axis-aligned bounding box for collision detection.
     * The boss uses a larger 64&times;64 hitbox to match its bigger sprite.
     *
     * @return a {@link Rectangle} at this boss's current position
     */
    @Override
    public Rectangle getBounds() { return new Rectangle(x, y, W, H); }

}












