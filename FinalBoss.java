import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Represents the final boss enemy in level 4.
 *
 * The boss enters from the top of the screen and moves down until it reaches
 * a fixed position near the top. Once in position, it moves horizontally
 * across the screen, bouncing off the edges.
 *
 * The boss fires a spread of five bullets at regular intervals and has
 * significantly higher health than normal enemies.
 */

public class FinalBoss extends Enemy {
    private static final int W = 96;
    private static final int H = 80;
    private static final int SHOOT_INTERVAL = 40; // 0.67 s
    private static final int TARGET_Y = 80; // top y pos

    private int shootTimer = 0;

    private final List<Bullet> gameBullets;
    private final BufferedImage sprite;

    // Entry animation -slides down from above the screen to TARGET_Y
    private boolean inPosition = false;

    /**
     * Creates a new FinalBoss instance.
     *
     * @param x starting x position
     * @param y starting y position (typically off-screen)
     * @param gameBullets shared bullet list used to spawn projectiles
     */
    public FinalBoss(int x, int y, List<Bullet> gameBullets) {
        super(x,y);
        this.health = 30;
        this.maxHealth = 30;
        this.speed = 3;
        this.gameBullets = gameBullets;

        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResource("/images/finalBoss.png"));
        } catch (Exception e) { /* fallback drawn in draw() */}
        this.sprite = loaded;
    }


    /**
     * Updates the boss each frame.
     *
     * The boss first moves downward until it reaches its target position.
     * After that, it moves left and right across the screen and periodically fires bullets.
    */
    @Override
    public void update() {
        // Slide into position first
        if (!inPosition) {
            if (y < TARGET_Y) {
                y +=2;
            } else {
                y = TARGET_Y;
                inPosition = true;
            }
            return; // don't shoot while entering
        }

        // Bounce left and right across the 700px wide screen
        x += speed;
        if (x + W >=700 || x <=0) speed = -speed;

        // Shoot
        if (++shootTimer >= SHOOT_INTERVAL) {
            shoot();
            shootTimer = 0;
        }
    }


    /**
     * Fires a spread of five bullets downward.
     *
     * Bullets are fired at slightly different angles to create a spread pattern.
    */
    @Override
    public void shoot() {
        // 5-bullet spread 
        int cx = x + W / 2;
        int by = y + H;
        // angles -20, -10, 0, +10, +20 degrees (relative to straight down)
        int[][] offsets = { {-14, 4}, {-7, 5}, {0, 6}, {7, 5}, {14, 4} };
        for (int[] off : offsets) {
            gameBullets.add(new SpreadBullet(cx + off[0], by, off[0], off[1]));
        }
    }

    /**
     * Returns the number of points awarded for defeating this boss.
     *
     * @return 2000 points
     */
    @Override
    public int getPoints() { return 2000; }

    /**
     * Returns the sprite used to render the boss.
     *
     * @return the boss sprite, or null if loading failed
     */
    @Override
    public BufferedImage getSprite() { return sprite; }

    /**
     * Draws the boss on the screen.
     *
     * If a sprite is available, it is used. Otherwise, a fallback
     * shape-based design is drawn.
     *
     * @param g the graphics context used for rendering
     */
    @Override
    public void draw(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite, x,y,W,H,null);
        } else {
            // Fallback
            // Dark red body 
            g.setColor(new Color(160,0,0));
            g.fillRect(x,y,W,H);
            // wings
            g.setColor(new Color(200,0,0));
            g.fillRect(x-20, y+20, 20,40);
            g.fillRect(x+W, y+20, 20,40);
            // Wing tips
            g.setColor(new Color(255,60,0));
            g.fillRect(x-30,y+35,12,16);
            g.fillRect(x+W+18, y+35, 12,16);
            // Glowing core
            g.setColor(new Color(255, 100, 0, 200));
            g.fillOval(x + W / 2 - 14, y + H / 2 - 14, 28, 28);
            g.setColor(new Color(255, 220, 0));
            g.fillOval(x + W / 2 - 7, y + H / 2 - 7, 14, 14);
            // Eyes
            g.setColor(new Color(255, 0, 0));
            g.fillOval(x + 18, y + 20, 16, 16);
            g.fillOval(x + W - 34, y + 20, 16, 16);
            g.setColor(Color.BLACK);
            g.fillOval(x + 22, y + 24, 8, 8);
            g.fillOval(x + W - 30, y + 24, 8, 8);
        }
        drawHealthBar(g,x,y-12,W);
    }

    /**
     * Returns the collision bounds of the boss.
     *
     * @return a rectangle representing the boss hitbox
     */
    @Override
    public Rectangle getBounds() { return new Rectangle(x,y,W,H); }

}
