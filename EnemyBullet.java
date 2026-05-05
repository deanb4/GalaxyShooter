import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * A projectile fired by enemy units in Galaxy Shooter.
 *
 * Concrete subclass of {@link Bullet}. Spawned by {@link BasicEnemy}
 * and {@link BossEnemy} via their {@code shoot()} methods. Travels
 * downward the screen toward the player at 5 pixels per frame
 * and deals 1 point of damage on impact.
 *
 * Inherits from {@link Bullet}: {@code x}, {@code y}, {@code speed},
 * {@code damage}, {@code active}, {@link Bullet#getX()},
 * {@link Bullet#getY()}, {@link Bullet#getDamage()},
 * {@link Bullet#isActive()}, {@link Bullet#deactivate()},
 * {@link Bullet#getBounds()}.
 *
 * Sprite: {@code /images/enemyBullet.png}
 *
 * @see Bullet
 * @see PlayerBullet
 */

public class EnemyBullet extends Bullet {

    private final BufferedImage sprite;
   
    /**
     * Creates an EnemyBullet at the specified position.
     *
     * Typically spawned just below the firing enemy's sprite so the
     * bullet appears to emerge from the enemy's underside. For example,
     * {@link BasicEnemy#shoot()} passes {@code (x + 21, y + 48)}
     *
     * @param x horizontal spawn position (pixels from left edge)
     * @param y vertical spawn position (pixels from top edge)
     */
    public EnemyBullet(int x, int y) {
        super(x, y);        // calls Bullet constructor — sets x, y, active = true
        this.speed  = 5;    // moves 5 pixels down per frame — slower than player bullets
        this.damage = 1;    // deals 1 damage to the player on hit

        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResource("/images/enemyBullet.png"));
        } catch (Exception e) {
            // fallback will be used in draw()
        }
        this.sprite = loaded;
    }

    //Abstract Methods


    
    /**
     * Moves the bullet down the screen by {@link #speed} pixels.
     *
     * In Java Swing, positive y values move toward the bottom of the
     * window, so adding to {@code y} advances the bullet toward the player.
     * Called by the game loop every frame. The game loop should call
     * {@link #deactivate()} when {@code y} exceeds the screen height
     */
    @Override
    public void update() {
        y += speed;   // moves DOWN toward the player
    }



    /**
     * Loads and returns the sprite image for this bullet.
     * The image is read fresh each call from the classpath resource
     * {@code /images/enemyBullet.png}.
     *
     * @return the enemy bullet sprite as a {@link BufferedImage}, or
     * {@code null} if the image resource cannot be located
    */
    @Override
    public BufferedImage getSprite() {
      return sprite;
    }
    
    /**
     * Draws the bullet at current location
     * @param g Graphics2D object
     */
    @Override
    public void draw(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, 6, 16, null);
        } else {
            // Orange-red bullet with a soft glow
            g.setColor(new Color(255, 140, 0, 80));
            g.fillRect(x - 2, y - 2, 10, 20);
            g.setColor(new Color(255, 80, 0));
            g.fillRect(x, y, 6, 16);
        }
    }
}

