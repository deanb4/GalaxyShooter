import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Represents the player's ship in Galaxy Shooter.
 *
 * The player can move in four directions, shoot bullets, take damage,
 * and temporarily become invincible after being hit.
 */

public class Player {
    
    public static final int WIDTH = 48;
    public static final int HEIGHT = 48;

    private static final int SPEED = 5;
    private static final int SHOOT_DELAY = 15;
    private static final int INVINCIBLE_FRAMES = 90;

    private static final double UPPER_BOUND_PCT = 0.35;

    private int x,y;
    private int lives;
    private int shootCooldown = 0;
    private int invincibleFrames = 0;

    private final BufferedImage sprite;  /** Player sprite image */
    private final List<Bullet> bullets; /** Shared bullet list used to store player bullets */


    /**
     * Creates a new Player instance.
     *
     * @param x starting x position
     * @param y starting y position
     * @param lives starting number of lives
     * @param bullets shared bullet list used for shooting
     */
    public Player(int x, int y, int lives, List<Bullet> bullets) {
        this.x = x;
        this.y = y;
        this.lives = lives;
        this.bullets = bullets;

        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResource("/images/player.png"));
        } catch (Exception e) { /* Fallback */ }
        this.sprite = loaded;
    }

    // update
    
   /**
     * Updates the player each frame.
     *
     * Handles movement, shooting, and invincibility timing.
     *
     * @param left whether left movement key is pressed
     * @param right whether right movement key is pressed
     * @param up whether up movement key is pressed
     * @param down whether down movement key is pressed
     * @param shoot whether shoot key is pressed
     * @param screenW width of the game screen
     * @param screenH height of the game screen
     */
    public void update(boolean left, boolean right, boolean up, boolean down, 
                        boolean shoot, int screenW, int screenH) {
        
        // Hoitzontal - clamp to screen edges
        if (left && x > 0) x -= SPEED; // 
        if (right && x < screenW - WIDTH) x += SPEED;

        // Vertical - unsure if to use
        int minY = Math.max(65, (int)(screenH * UPPER_BOUND_PCT));
        int maxY = screenH - HEIGHT;
        if (up && y > minY) y -= SPEED;
        if (down && y < maxY) y += SPEED;

        // Shooting
        if (shootCooldown > 0) shootCooldown--;
        if (shoot && shootCooldown == 0) {
            bullets.add(new PlayerBullet(x + WIDTH / 2 - 3, y));
            shootCooldown = SHOOT_DELAY;
        }

        if (invincibleFrames > 0) invincibleFrames--;
    }

      /**
     * Draws the player on the screen.
     *
     * If the player is invincible, a blinking effect is applied.
     * If no sprite is available, a fallback shape is drawn.
     *
     * @param g graphics context used for rendering
     */
    public void draw(Graphics2D g) {
        if (invincibleFrames > 0 && (invincibleFrames % 6) >=3) return;

        if (sprite != null) {
            g.drawImage(sprite,x,y,WIDTH, HEIGHT, null);
        } else {
            g.setColor(new Color(0,220,255));
            int[] xs = { x + WIDTH / 2, x, x + WIDTH};
            int[] ys = { y, y + HEIGHT, y + HEIGHT};
            g.fillPolygon(xs,ys,3);
            g.setColor(new Color(0,100,255,150));
            g.fillOval(x + WIDTH / 2-8, y + HEIGHT -6, 16,12);
        }
    }

     /**
     * Returns whether the player is currently vulnerable to damage.
     *
     * @return true if player can take damage, false otherwise
     */
    public boolean isVulnerable() { return invincibleFrames == 0; }

    /**
     * Applies damage to the player and activates invincibility frames.
     *
     * @param amount amount of damage taken
     */
    public void takeDamage(int amount) {
        lives -= amount;
        invincibleFrames = INVINCIBLE_FRAMES;
    }

    /**
     * Returns whether the player has no remaining lives.
     *
     * @return true if dead, false otherwise
     */
    public boolean isDead() { return lives <= 0; }


    /**
     * Returns the number of remaining lives.
     *
     * @return current lives
     */
    public int getLives() { return lives; }

    /**
     * Returns the player's x position.
     *
     * @return x coordinate
     */
    public int getX() { return x; }

    /**
     * Returns the player's y position.
     *
     * @return y coordinate
     */
    public int getY() { return y; }

    /**
     * Returns the collision bounds of the player.
     *
     * @return rectangle representing hitbox
     */
    public Rectangle getBounds() { return new Rectangle(x,y,WIDTH, HEIGHT); }

    /**
     * Resets the player to a starting state.
     *
     * @param startX starting x position
     * @param startY starting y position
     * @param startLives starting number of lives
     */
    public void reset(int startX, int startY, int startLives) {
        this.x = startX;
        this.y = startY;
        this.lives = startLives;
        this.shootCooldown = 0;
        this.invincibleFrames = 0;
    }
    
}
