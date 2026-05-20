import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Player extends Entity {
    private int dy = 0;
    private final int GRAVITY = 1;
    private final int JUMP_STRENGTH = -12;
    public boolean onPlatform = false; 
    public boolean isClimbing = false; 
    public boolean hasHammer = false;
    public int hammerTimer = 0; //Tracks duration of hammer powerup in frames

    private Image spriteLeft;
    private Image spriteRight;
    private Image jumpLeft;
    private Image jumpRight;

    private boolean facingRight = true;

    public Player(int x, int y) {
        super(x, y, 30, 40);
        spriteRight = new ImageIcon("player_right.png").getImage();
        spriteLeft = new ImageIcon("player_left.png").getImage();
        jumpRight = new ImageIcon("player_jump_right.png").getImage();
        jumpLeft = new ImageIcon("player_jump_left.png").getImage();
    }

    public int getDy() {
        return this.dy;
    }

    public void update(boolean left, boolean right, boolean up, boolean down) {
        if (hasHammer) {
            hammerTimer--;
            if (hammerTimer <= 0) {
                hasHammer = false;
            }
        }
        if (isClimbing) {
            // Ladder mechanic: vertical tracking, ignore gravity
            if (up) {
                dy = -4;
            } else if (down) {
                dy = 4;
            } else {
                dy = 0; 
            }

            if (left) {
                x -= 3;
                facingRight = false;
            }
            if (right) {
                x += 3;
                facingRight = true;
            }

            y += dy;
            onPlatform = false; 
            
        } else {
            // Platforming mechanics: movement and jumping
            if (left) {
                x -= 5;
                facingRight = false;
            }
            if (right) {
                x += 5;
                facingRight = true;
            }

            if (up && onPlatform) {
                dy = JUMP_STRENGTH;
                onPlatform = false;
            }

            dy += GRAVITY;
            y += dy;
            onPlatform = false; 
        }
    }

    public void landOn(int groundY) {
        this.y = groundY - this.height;
        this.dy = 0;
        this.onPlatform = true;
        this.isClimbing = false; 
    }

    @Override
    public void draw(Graphics g) {
        Image currentSprite;

        if (isClimbing || !onPlatform) {
            currentSprite = facingRight ? jumpRight : jumpLeft;
        } else {
            currentSprite = facingRight ? spriteRight : spriteLeft;
        }

        if (currentSprite != null && currentSprite.getWidth(null) > 0) {
            g.drawImage(currentSprite, x, y, width, height, null);
            
            // Overlay a transparent gold aura if hammer powerup is active
            if (hasHammer) {
                g.setColor(new Color(255, 215, 0, 100)); // Gold color
                g.fillRect(x, y, width, height);
            }
        } else {
            if (hasHammer) {
                // Flash bright gold/yellow if images aren't loaded
                g.setColor(hammerTimer % 6 < 3 ? Color.YELLOW : Color.ORANGE);
            } else if (isClimbing) {
                g.setColor(Color.GREEN); 
            } else {
                g.setColor(onPlatform ? Color.RED : Color.BLUE);
            }
            g.fillRect(x, y, width, height);
        }
    }
}
