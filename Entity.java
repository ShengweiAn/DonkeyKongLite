import java.awt.*;
import java.awt.event.KeyEvent;

// Base class for game objects
abstract class Entity {
    protected int x, y, width, height;

    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
