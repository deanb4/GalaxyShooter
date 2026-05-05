import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Represents a bullet that travels in a diagonal direction.
 *
 * This bullet type is used by the FinalBoss to create spread-shot attacks.
 * It moves both horizontally and vertically each frame based on its velocity.
 */

public class SpreadBullet extends Bullet {

    // Fields
    private final int dx;
    private final int dy;
    private final BufferedImage sprite;

    /**
     * Creates a new SpreadBullet.
     *
     * @param x starting x position
     * @param y starting y position
     * @param dx horizontal velocity per frame
     * @param dy vertical velocity per frame
     */
    public SpreadBullet(int x, int y, int dx, int dy) {
        super(x,y);
        this.dx = dx;
        this.dy = dy;
        this.damage = 1;

        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResource("/images/enemyBullet.png"));
        } catch (Exception e) { /* fallback */}
        this.sprite = loaded;
    }

    /**
     * Updates the bullet position each frame.
     *
     * Moves diagonally based on its velocity components.
     */
    @Override
    public void update() {
        x += dx;
        y += dy;
    }

    /**
     * Returns the sprite used to render the bullet.
     *
     * @return bullet sprite image, or null if not loaded
     */
    @Override 
    public BufferedImage getSprite() { return sprite; }

    /**
     * Draws the bullet on the screen.
     *
     * If a sprite is available, it is used. Otherwise, a fallback
     * visual representation is drawn.
     *
     * @param g graphics context used for rendering
     */
    @Override 
    public void draw(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite, x,y,8,14,null);
        } else {
            // Orange yellow angled bullet
            g.setColor(new Color(255,160,0,80));
            g.fillOval(x-3,y-3,14,14);
            g.setColor(new Color(255,120,0));
            g.fillOval(x,y,8,8);
        }
    }

    /**
     * Returns the collision bounds of the bullet.
     *
     * @return rectangle representing hitbox
     */
    @Override
    public Rectangle getBounds() { return new Rectangle(x,y,8,8); }

}
