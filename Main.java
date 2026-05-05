import javax.swing.*;

/**
 * Galaxy Shooter - 2D Space Shooter Game
 *
 * This project is a Java Swing-based arcade-style space shooter game
 * where the player controls a spaceship and progresses through multiple
 * levels of increasing difficulty.
 *
 * The player must defeat waves of enemies, avoid enemy bullets, and
 * survive boss fights to complete all levels. The game includes a scoring
 * system, multiple enemy types, boss battles, and a level progression system.
 *
 * The game is rendered using Java Swing with a 60 FPS game loop.
 * 
 * @author Dean Bar Ner
 * @author Adriel Largo
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Galaxy Shooter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}

