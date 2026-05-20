import java.awt.Graphics;
import java.awt.Color;

public class Obstacle extends Entity {
    public int dx; // Horizontal speed
    public int dy = 0; // Vertical speed
    private final int GRAVITY = 1;
    public boolean onPlatform = false;

    public Obstacle(int x, int y, int speed) {
        super(x, y, 20, 20);
        this.dx = -speed; // Start moving left
    }

    public void update() {
        // Apply horizontal movement
        x += dx;

        // Apply gravity if jumping/midair
        if (!onPlatform) {
            dy += GRAVITY;
            y += dy;
        }
        
        // Reset state so GameEngine continuously checks the airborne parameter
        onPlatform = false;
    }

    public void landOn(int surfaceY) {
        this.y = surfaceY - this.height;
        this.dy = 0;
        this.onPlatform = true;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(184, 91, 13)); 
        g.fillOval(x, y, width, height);

        g.setColor(Color.BLACK);
        g.drawOval(x, y, width, height);

        g.setColor(new Color(60, 60, 60)); 
        g.drawLine(x + 2, y + 5, x + width - 3, y + 5);
        g.drawLine(x + 1, y + 15, x + width - 2, y + 15);

        g.setColor(new Color(115, 53, 0)); 
        g.drawLine(x + 6, y + 5, x + 6, y + 15);
        g.drawLine(x + 10, y + 2, x + 10, y + 18);
        g.drawLine(x + 14, y + 5, x + 14, y + 15);
    }
}
