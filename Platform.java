import java.awt.Color;
import java.awt.Graphics;

public class Platform extends Entity {
    // y represents the starting height on the left.
    // endY represents the ending height on the right.
    public int endY;

    public Platform(int x, int y, int width, int height, int endY) {
        super(x, y, width, height);
        this.endY = endY;
    }

    // Calculates the Y coordinate on the surface of 
    // a slanted platform based on object’s X position
    public int getSurfaceY(int objectX) {
        if (objectX < x) return y;
        if (objectX > x + width) return endY;
        
        // Find the height along the slope
        double percent = (double)(objectX - x) / width;
        return (int)(y + percent * (endY - y));
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(225, 50, 100));
        
        // Draw a slanted shape to represent the platform
        int[] xPoints = {x, x + width, x + width, x};
        int[] yPoints = {y, endY, endY + height, y + height};
        g.fillPolygon(xPoints, yPoints, 4);
        
        // Draw border along the top edge
        g.setColor(Color.BLACK);
        g.drawLine(x, y, x + width, endY);
    }
}
