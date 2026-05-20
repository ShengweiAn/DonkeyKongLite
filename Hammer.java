import java.awt.Color;
import java.awt.Graphics;

public class Hammer extends Entity {
    public boolean collected = false;
    public int respawnTimer = 0; //Track the frames before the hammer respawns (if the player picks it up)

    public Hammer(int x, int y) {
        // Size: 20x20 pixels
        super(x, y, 20, 20);
    }

    @Override
    public void draw(Graphics g) {
        if (collected) return;

        // Draw Hammer Handle (Wood Brown)
        g.setColor(new Color(139, 69, 19));
        g.fillRect(x + 8, y + 8, 4, 12);

        // Draw Hammer Head (Iron Gray)
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + 2, y + 2, 16, 6);
        
        g.setColor(Color.BLACK);
        g.drawRect(x + 2, y + 2, 16, 6);
    }
}