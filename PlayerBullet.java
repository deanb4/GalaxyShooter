import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * A projectile fired by the player in Galaxy Shooter.
 *
 * Concrete subclass of {@link Bullet}. Spawned when the player presses
 * the spacebar. Travels upward the screen toward enemies at
 * 8 pixels per frame and deals 1 point of damage on impact.
 *
 * Inherits from {@link Bullet}: {@code x}, {@code y}, {@code speed},
 * {@code damage}, {@code active}, {@link Bullet#getX()},
 * {@link Bullet#getY()}, {@link Bullet#getDamage()},
 * {@link Bullet#isActive()}, {@link Bullet#deactivate()},
 * {@link Bullet#getBounds()}.
 *
 * Sprite: {@code /images/playerBullet.png}
 *
 * @see Bullet
 * @see EnemyBullet
 */
public class PlayerBullet extends Bullet {

    private final BufferedImage sprite;
    
    
    // Constructor
    
    /**
     * Creates a PlayerBullet at the specified position.
     *
     * Should be spawned just above the center of the player sprite so
     * the bullet appears to launch from the ship's cannon.
     *
     * @param x horizontal spawn position (pixels from left edge);
     *          typically the horizontal center of the player
     * @param y vertical spawn position (pixels from top edge);
     *          typically just above the top of the player sprite
     */
    public PlayerBullet(int x, int y) {
        super(x, y);        // calls Bullet constructor — sets x, y, active = true
        this.speed  = 8;    // moves 8 pixels up per frame — fast and snappy
        this.damage = 1;    // deals 1 damage to enemy on hit

        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResource("/images/playerBullter.png"));
        } catch (Exception e) {
            // fallback
        }
        this.sprite = loaded;
    }

    // Abstract methods

    /**
     * Moves the bullet up the screen by {@link #speed} pixels.
     *
     * In Java Swing, negative y values move toward the top of the
     * window, so subtracting from {@code y} advances the bullet toward
     * the enemies. Called by the game loop every frame. The game loop
     * should call {@link #deactivate()} when {@code y} drops below 0.
    */
    @Override
    public void update() {
        y -= speed;   // moves UP toward enemies
    }


    /**
     * Loads and returns the sprite image for this bullet.
     * The image is read fresh each call from the classpath resource
     *
     * @return the player bullet sprite as a {@link BufferedImage}, or
     * {@code null} if the image resource cannot be located
     */
    @Override
    public BufferedImage getSprite() {
       return sprite;
    }


    /**
     * Draws player bullet at location
     * @param g Graphics2D object
     */
    @Override
    public void draw(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite,x,y, 6,16,null);
        } else {
            g.setColor(new Color(255,255,150,80));
            g.fillRect(x-2,y-2,10,20);
            g.setColor(new Color(255,255,0));
            g.fillRect(x,y,6,16);
        }
    }

}

