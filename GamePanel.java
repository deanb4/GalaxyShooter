import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game panel for Galaxy Shooter.
 *
 * This class handles the game loop (60 FPS), input processing,
 * rendering, collision detection, level progression, and game state
 * management.
 *
 * The game progresses through multiple levels and ends when the
 * final boss is defeated or the player loses all lives.
 */

public class GamePanel extends JPanel implements ActionListener {
    // Screen
    public static final int WIDTH = 700;
    public static final int HEIGHT = 800;

    // Game states
    private enum State { PLAYING, LEVEL_COMPLETE, GAME_OVER, WIN}   
    private State state = State.PLAYING;

    // Frames to show the "level X Complete" screen before advancing
    private static final int LEVEL_COMPLETE_DURATION = 150; // 2.5 seconds
    private int levelCompleteTimer = 0;

    // Game objects
    private Player player;
    private List<Bullet> bullets;
    private Level level;
    private int levelNumber = 1;
    private int score = 0;
    
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean spacePressed = false;

    // Starfield
    private static final int STAR_COUNT = 90;
    private final int[] starX = new int[STAR_COUNT];
    private final int[] starY = new int[STAR_COUNT];

    // Timer
    private final Timer gameTimer = new Timer(16,this);

     /**
     * Creates the game panel and initializes the game.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = (int)(Math.random() * WIDTH);
            starY[i] = (int)(Math.random() * HEIGHT);
        }

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> leftPressed = true;
                    case KeyEvent.VK_RIGHT -> rightPressed = true;
                    case KeyEvent.VK_UP -> upPressed = true;
                    case KeyEvent.VK_DOWN -> downPressed = true;
                    case KeyEvent.VK_SPACE-> spacePressed = true;
                    case KeyEvent.VK_ENTER -> {
                        if (state == State.GAME_OVER || state == State.WIN) restartGame();
                    }
                }
            }
            @Override public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> leftPressed = false;
                    case KeyEvent.VK_RIGHT -> rightPressed = false;
                    case KeyEvent.VK_UP ->  upPressed = false;
                    case KeyEvent.VK_DOWN -> downPressed = false;
                    case KeyEvent.VK_SPACE -> spacePressed = false;

                }
            }
        });

        initGame();
        gameTimer.start();
    }


     /**
     * Initializes game objects and loads the first level.
     */
    private void initGame() {
        bullets = new ArrayList<>();
        player = new Player(WIDTH / 2 - Player.WIDTH / 2, HEIGHT - 100, 3, bullets);
        loadLevel(1);
    }

    /**
     * Loads a specific level.
     *
     * @param number level number to load
     */
    private void loadLevel(int number) {
        levelNumber = number;
        bullets.clear();
        switch(number) {
            case 1 -> level = new Level1(bullets);
            case 2 -> level = new Level2(bullets);
            case 3 -> level = new Level3(bullets);
            default -> level = new BossLevel(bullets);
        }
        level.spawnEnemies();
    }

    /**
     * Main game loop callback (60 FPS).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (state) {
            case PLAYING -> update();
            case LEVEL_COMPLETE -> tickLevelComplete();
            default -> {}
        }
        repaint();
    }

    /**
     * Handles timing for the level complete screen.
     */
    private void tickLevelComplete() {
        levelCompleteTimer++;
        if (levelCompleteTimer >= LEVEL_COMPLETE_DURATION) {
            levelCompleteTimer = 0;
            loadLevel(levelNumber + 1); // Load next level
            state = State.PLAYING; // set state to playing
        }
    }

    /**
     * Updates all game logic each frame.
     */
    private void update() {

        // 1. Update Player
        player.update(leftPressed, rightPressed, upPressed,downPressed,spacePressed,WIDTH,HEIGHT);

        // 2. Update enemies
        for (Enemy enemy : level.getEnemiesSnapshot()) {
            if (!enemy.isDead()) enemy.update(); // Draw enemies 
        }

        // 3. Update bullets
        for (Bullet b : new ArrayList<>(bullets)) {
            if (!b.isActive()) continue;
            b.update();
            if (b.getY() < -20 || b.getY() > HEIGHT + 20 ||
                b.getX() < -20 || b.getX() > WIDTH + 20) b.deactivate();
        }

        // 4. Update PlayerBullet -> Enemy Collisions (player fires at enemey)
        for (Bullet b : new ArrayList<>(bullets)) { // create a shallow copy to iterate over for safety
            if (!b.isActive() || !(b instanceof PlayerBullet)) continue; // skip iteration if bullet not active or does not belong to player skip
            for (Enemy enemy : level.getEnemiesSnapshot()) {
                if (enemy.isDead()) continue; // If enemy is dead skip iteration 

                // Check if bullet and enemy intersect aka "hit"
                if (b.getBounds().intersects(enemy.getBounds())) {
                    enemy.takeDamage(b.getDamage());
                    b.deactivate(); // remove bullet

                    // If enemy is dead
                    if (enemy.isDead()) {
                        score += enemy.getPoints(); // add to score
                        level.onEnemyKilled(); // increase killcount for level
                    }
                    break; // once found bullet that "hit" break out of loop
                }
            }
        } 

        // 5. Update EnemyBullet -> Player collisions aka enemy "hits" player
        if (player.isVulnerable()) {
            for (Bullet b : new ArrayList<>(bullets)) {
                // If bullet not active or belongs to player then skip iteration
                if (!b.isActive() || b instanceof PlayerBullet) continue;
                // If Hit
                if (b.getBounds().intersects(player.getBounds())) {
                    b.deactivate(); // remove bullet
                    player.takeDamage(b.getDamage()); // player takes damage
                    // If player is dead change game state to game over
                    if (player.isDead()) {
                        state = State.GAME_OVER;
                        gameTimer.stop();
                        return;
                    }
                    break;
                }
            }
        }

        // 6. Cleanup
        level.removeDeadEnemies(); // clear dead enemies
        bullets.removeIf(b -> !b.isActive()); // remove all deactivated bullets

        // 7. Level tick (spawn timer)
        level.update();

        // 8. Stars (background)
        for (int i = 0; i < STAR_COUNT; i++) {
            starY[i]++; // increase Y pos every frame update
            // If star goes off screen reset y position to top and give random x position
            if (starY[i] > HEIGHT) {
                starY[i] = 0;
                starX[i] = (int)(Math.random() * WIDTH);
            }
        }

        // 9. Level completion
        if (level.isComplete()) {
            bullets.clear();
            if (levelNumber >=4 ) {
                // Boss level beaten - go straight to WIN
                state = State.WIN;
                gameTimer.stop();
            } else {
                // Show "Level X complete" then auto advance
                state = State.LEVEL_COMPLETE;
                levelCompleteTimer = 0;
            }
        }
    }


    
    /**
     * Paints all game visuals.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // use parents method to clear canvas and set it up for repainting
        Graphics2D g2 = (Graphics2D) g; // downcast to graphics2d for better perf and anti alias
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // turn on anti aliasing for better quality sprites

        drawBackground(g2);
        drawScene(g2); 

        switch (state) {
            case LEVEL_COMPLETE -> drawLevelComplete(g2);
            case GAME_OVER -> drawEndOverlay(g2, "GAME OVER", new Color(255,60,60));
            case WIN -> drawEndOverlay(g2, "YOU WIN", new Color(0,220,100));
            default -> {} // Playing (no overlay)
        }
    }

    /**
     * Draws the starfield background.
     */
    private void drawBackground(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,WIDTH,HEIGHT);
        // Change brightness for each star and add to background
        for (int i = 9; i < STAR_COUNT; i++) {
            int b = 150 + (int)(Math.random() * 106);
            g.setColor(new Color(b,b,b));
            g.fillRect(starX[i], starY[i], 2,2);
        }
    }

    /**
     * Draws all game entities.
    */
    private void drawScene(Graphics2D g) {
        player.draw(g);
        for (Bullet b : new ArrayList<>(bullets)) {
            if (b.isActive()) b.draw(g);
        }   
        level.draw(g);
        drawHUD(g);
    }

     /**
     * Draws the HUD (score, lives, level, progress bar).
     */
    private void drawHUD(Graphics2D g) {
        // Background bar
        g.setColor(new Color(0,0,0, 180));
        g.fillRect(0,0,WIDTH,65);

        FontMetrics fm;

        // Score
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.setColor(new Color(0,220,255));
        g.drawString("SCORE: " + score, 12,24);

        // Level Label
        String lvlLabel = levelNumber < 4 ? "Level " + levelNumber : "Boss LEVEL"; 
        g.setColor(Color.WHITE);
        fm = g.getFontMetrics();
        g.drawString(lvlLabel, (WIDTH - fm.stringWidth(lvlLabel)) / 2, 24); 

        // Lives (circules calculated from the right edge)
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        fm = g.getFontMetrics();
        int lives = player.getLives();
        int circleSize = 14;
        int circleGap  = 4;
        int totalCircleW = lives * (circleSize + circleGap);
        String livesLabel = "LIVES: ";
        int livesLabelW = fm.stringWidth(livesLabel);
        int hudRightX = WIDTH -12; // right padding
        int circlesStartX = hudRightX - totalCircleW;
        int labelStartX = circlesStartX - livesLabelW;

        g.setColor(new Color(255,80,80));
        g.drawString(livesLabel, labelStartX, 24);
        for (int i = 0; i < lives; i++) {
            g.setColor(new Color(255,60,60));
            g.fillOval(circlesStartX + i * (circleSize + circleGap), 10, circleSize, circleSize);
        }

        // Kill progress bar
        int kills = level.getKillCount();
        int needed = level.getKillsRequired();
        double pct = Math.min(1.0, (double) kills / needed);

        int barW = 260, barX = (WIDTH - barW) / 2, barY = 34;
        g.setColor(new Color(50,50,50));
        g.fillRoundRect(barX,barY,barW,10,6,6);
        g.setColor(new Color(0,200,120));
        g.fillRoundRect(barX, barY, (int)(barW * pct), 10,6,6);

        g.setFont(new Font( "Monospaces", Font.PLAIN, 11));
        fm = g.getFontMetrics();
        g.setColor(new Color(200,200 ,200));
        String killStr = kills + " / " + needed + " kills";
        g.drawString(killStr, (WIDTH - fm.stringWidth(killStr)) / 2, barY + 24);
    }

    /**
     * level complete overlay
     * @param g Graphis2D object
    */
    private void drawLevelComplete(Graphics2D g) {
        // Dim background
        g.setColor(new Color(0,0,0, 160));
        g.fillRect(0,0,WIDTH,HEIGHT);

        // Determine the label
        String line1 = levelNumber < 4 ? "Level " + levelNumber + " COMPLETE!" : "BOSS DEFEATED!";

        g.setFont(new Font("Monospaced", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        // Gold color
        g.setColor(new Color(255,215,0));
        g.drawString(line1, (WIDTH - fm.stringWidth(line1)) / 2, HEIGHT / 2 - 30);

        String line2 = levelNumber < 4
                ? "Get ready for Level " + (levelNumber +1) + "!"
                : "You saved the galaxy!";
        
        g.setFont(new Font("Monospaced", Font.BOLD,22));
        fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString(line2, (WIDTH - fm.stringWidth(line2)) / 2, HEIGHT / 2 + 20);

        // Progress dots so players knows how long to wait
        int filled = (int)((double) levelCompleteTimer / LEVEL_COMPLETE_DURATION * 5);
        int dotX = WIDTH / 2 - 40;
        for (int i = 0; i < 5; i++) {
            g.setColor(i < filled ? new Color(255,215,0) : new Color(80,80,80));
            g.fillOval(dotX + i * 20, HEIGHT / 2 + 50, 12,12);
        }
    }

    /**
     * End Game overlay
     * @param g Graphis2D object
     * @param title
     * @param titleColor
    */
    private void drawEndOverlay(Graphics2D g, String title, Color titleColor) {
        g.setColor(new Color(0,0,0,190)); // added opacity too at 190
        g.fillRect(0,0,WIDTH,HEIGHT);

        g.setFont(new Font("Monospaced", Font.BOLD, 52));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(titleColor);
        g.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, HEIGHT / 2-50);

        g.setFont(new Font("Monospaced", Font.PLAIN, 22));
        fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String sub = "Final Score: " + score;
        g.drawString(sub, (WIDTH - fm.stringWidth(sub)) / 2, HEIGHT / 2 + 10);

        g.setFont(new Font("Monospaced", Font.PLAIN,18));
        fm = g.getFontMetrics();
        g.setColor(new Color(180,180,180));
        String hint = "Press ENTER to play again";
        g.drawString(hint, (WIDTH - fm.stringWidth(hint)) / 2, HEIGHT / 2+50);
    }
    
     /**
     * Restarts the game from level 1.
     */
    private void restartGame() {
        score = 0;
        levelCompleteTimer = 0;
        state = State.PLAYING;
        bullets.clear();
        player.reset(WIDTH / 2 - Player.WIDTH / 2, HEIGHT - 100, 3);
        loadLevel(1);
        gameTimer.restart();
        requestFocusInWindow();
    }

}