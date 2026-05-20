import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Boss extends Entity {
    private Image bossSprite;

    public Boss(int x, int y) {
        // Boss size of 50x50 pixels
        super(x, y, 50, 50);
        
        // Load sprite from png file
        bossSprite = new ImageIcon("boss.png").getImage();
    }

    @Override
    public void draw(Graphics g) {
        // Verify the image file loaded successfully and has a valid dimension
        if (bossSprite != null && bossSprite.getWidth(null) > 0) {
            g.drawImage(bossSprite, x, y, width, height, null);
        } else {
            // Failsafe: If boss.png is missing or misnamed, draw a placeholder image in place of the boss
            g.setColor(new Color(115, 53, 0)); // Classic arcade brown
            g.fillRect(x, y, width, height);
            
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            
            g.setColor(Color.WHITE);
            g.drawString("BOSS", x + 10, y + 30);
        }
    }
}