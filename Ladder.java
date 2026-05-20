import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Ladder {
    public int x, y, width, height;

    public Ladder(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(139,69,19)); // "arcade brown color for the ladder(s)"
        // Vertical sides
        g.fillRect(x, y, 4, height);
        g.fillRect(x + width - 4, y, 4, height);
        
        // Horizontal rungs
        for (int rungY = y; rungY < y + height; rungY += 15) {
            g.fillRect(x, rungY, width, 4);
        }
    }
}
