import javax.swing.JFrame;
import javax.swing.JButton;

public class DonkeyKongLite extends JFrame {

    public DonkeyKongLite() {
        setTitle("Donkey Kong Lite");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create button without adding to frame, will appear on the game end screen
        JButton restartButton = new JButton("RESTART GAME");
        
        GameEngine engine = new GameEngine(restartButton);
        add(engine);

        setVisible(true);
    }

    public static void main(String[] args) {
        new DonkeyKongLite();
    }
}
