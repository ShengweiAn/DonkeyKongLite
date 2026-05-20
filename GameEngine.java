import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class GameEngine extends JPanel implements ActionListener {
    private enum GameState {
        MENU,
        GAME,
        LEADERBOARD
    }
    private GameState currentState = GameState.MENU; 

    private Timer gameTimer;
    private Player player;
    private ArrayList<Obstacle> barrels;
    private ArrayList<Platform> platforms;
    private ArrayList<Ladder> ladders;
    private ArrayList<Hammer> hammers; 
    private Boss donkeyKong; 
    
    private int score = 0;
    private int secondsElapsed = 0;
    private boolean gameOver = false;
    private boolean gameWon = false; 
    
    private boolean left, right, up, down;
    
    private JButton newGameButton;
    private JButton leaderboardButton;
    private JButton backToMenuButton;
    private JButton restartButton; 

    public GameEngine(JButton restartButton) {
        this.restartButton = restartButton;
        setFocusable(true);
        setLayout(null); 

        if (this.restartButton != null) {
            this.restartButton.setBounds(300, 380, 200, 40); 
            this.restartButton.setVisible(false);
            this.restartButton.addActionListener(e -> initGame());
            add(this.restartButton);
        }

        initMenuButtons();
        
        gameTimer = new Timer(20, this);
        gameTimer.start();
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (currentState != GameState.GAME) return; 
                if (e.getKeyCode() == KeyEvent.VK_A) left = true;
                if (e.getKeyCode() == KeyEvent.VK_D) right = true;
                if (e.getKeyCode() == KeyEvent.VK_W) up = true;
                if (e.getKeyCode() == KeyEvent.VK_S) down = true;
            }
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) left = false;
                if (e.getKeyCode() == KeyEvent.VK_D) right = false;
                if (e.getKeyCode() == KeyEvent.VK_W) up = false;
                if (e.getKeyCode() == KeyEvent.VK_S) down = false;
            }
        });
    }

    private void initMenuButtons() {
        newGameButton = new JButton("NEW GAME");
        newGameButton.setBounds(300, 200, 200, 50); // Shifted up slightly to accommodate the new details section
        newGameButton.addActionListener(e -> startNewGame());
        add(newGameButton);

        leaderboardButton = new JButton("LEADERBOARD");
        leaderboardButton.setBounds(300, 270, 200, 50);
        leaderboardButton.addActionListener(e -> showLeaderboardView());
        add(leaderboardButton);

        backToMenuButton = new JButton("BACK TO MENU");
        backToMenuButton.setBounds(300, 435, 200, 40); 
        backToMenuButton.setVisible(false);
        backToMenuButton.addActionListener(e -> showMainMenuView());
        add(backToMenuButton);
    }

    private void startNewGame() {
        left = false;
        right = false;
        up = false;
        down = false;
        newGameButton.setVisible(false);
        leaderboardButton.setVisible(false);
        backToMenuButton.setVisible(false);
        if (restartButton != null) restartButton.setVisible(false);

        player = new Player(50, 480); 
        barrels = new ArrayList<>();
        platforms = new ArrayList<>();
        ladders = new ArrayList<>();
        score = 0;
        secondsElapsed = 0;
        gameOver = false;
        gameWon = false; 
        
        platforms.add(new Platform(0, 210, 700, 15, 245)); 
        platforms.add(new Platform(100, 355, 700, 15, 320)); 
        platforms.add(new Platform(0, 430, 700, 15, 465)); 
        platforms.add(new Platform(0, 540, 800, 20, 540));

        ladders.add(new Ladder(500, 340, 30, 80)); 

        hammers = new ArrayList<>();
        hammers.add(new Hammer(200, 400)); 

        donkeyKong = new Boss(30, 160); 

        currentState = GameState.GAME;
        requestFocusInWindow(); 
    }

    public void initGame() {
        startNewGame();
    }

    private void showLeaderboardView() {
        currentState = GameState.LEADERBOARD;
        newGameButton.setVisible(false);
        leaderboardButton.setVisible(false);
        
        // move backtomenu button down slightly in the leaderboard screen to avoid overlap
        if (backToMenuButton != null) {
            backToMenuButton.setBounds(300, 480, 200, 40);
            backToMenuButton.setVisible(true);
        }
        
        if (restartButton != null) restartButton.setVisible(false);
        repaint();
    }

    private void showMainMenuView() {
        currentState = GameState.MENU;
        newGameButton.setVisible(true);
        leaderboardButton.setVisible(true);
        
        // move backtomenu button to its original position in the main menu
        if (backToMenuButton != null) {
            backToMenuButton.setBounds(300, 435, 200, 40);
            backToMenuButton.setVisible(false);
        }
        
        if (restartButton != null) restartButton.setVisible(false);
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState != GameState.GAME || gameOver) return;

        secondsElapsed++;
        
        boolean overlappingLadder = false;
        for (Ladder l : ladders) {
            if (player.getBounds().intersects(l.getBounds())) {
                overlappingLadder = true;
                break;
            }
        }

        if (!overlappingLadder) {
            player.isClimbing = false;
        } else if (up || down) {
            player.isClimbing = true; 
        }

        player.update(left, right, up, down);

        for (Hammer h : hammers) {
            if (!h.collected) {
                if (player.getBounds().intersects(h.getBounds())) {
                    h.collected = true;
                    h.respawnTimer = 750; 
                    player.hasHammer = true;
                    player.hammerTimer = 250; 
                }
            } else {
                h.respawnTimer--;
                if (h.respawnTimer <= 0) {
                    h.collected = false; 
                }
            }
        }

        if (player.x < 0) player.x = 0; 
        if (player.x > 800 - player.width - 14) {
            player.x = 800 - player.width - 14; 
        }

        if (donkeyKong != null && player.getBounds().intersects(donkeyKong.getBounds())) {
            triggerWin();
            return;
        }

        if (!player.isClimbing) {
            for (Platform p : platforms) {
                int playerCenterX = player.x + player.width / 2;
                if (playerCenterX >= p.x && playerCenterX <= p.x + p.width) {
                    int surfaceY = p.getSurfaceY(playerCenterX);
                    if (player.getDy() > 0) {
                        if (player.y + player.height >= surfaceY - 5 && player.y + player.height - player.getDy() <= surfaceY + 15) {
                            player.landOn(surfaceY);
                            break;
                        }
                    }
                }
            }
        }

        int difficultyLevel = (secondsElapsed / 500) + 1;
        score += difficultyLevel;

        if (secondsElapsed % Math.max(25, (120 - (difficultyLevel * 5))) == 0) {
            Obstacle newBarrel = new Obstacle(20, 175, 3 + difficultyLevel);
            newBarrel.dx = 3 + difficultyLevel; 
            barrels.add(newBarrel);
        }

        for (int i = 0; i < barrels.size(); i++) {
            Obstacle b = barrels.get(i);
            b.update();
            
            boolean barrelTouchedPlatform = false;
            for (Platform p : platforms) {
                int barrelCenterX = b.x + b.width / 2;
                if (barrelCenterX >= p.x && barrelCenterX <= p.x + p.width) {
                    int surfaceY = p.getSurfaceY(barrelCenterX);
                    if (b.y + b.height >= surfaceY - 5 && b.y + b.height - b.dy <= surfaceY + 15) {
                        b.landOn(surfaceY);
                        barrelTouchedPlatform = true;
                        
                        if (p.y == p.endY) {
                            b.dx = -Math.abs(b.dx); 
                        } else if (p.endY > p.y) {
                            b.dx = Math.abs(b.dx);  
                        } else if (p.endY < p.y) {
                            b.dx = -Math.abs(b.dx); 
                        }
                    }
                }
            }
            if (!barrelTouchedPlatform) b.onPlatform = false; 
            
            if (b.getBounds().intersects(player.getBounds())) {
                if (player.hasHammer) {
                    score += 500; 
                    barrels.remove(i);
                    i--; 
                    continue;
                } else {
                    endGame();
                }
            }
            
            if (b.x < -40 || b.x > 840 || b.y > 600) barrels.remove(i);
        }
        repaint();
    }

    private void endGame() {
        gameOver = true;
        if (restartButton != null) restartButton.setVisible(true);
        if (backToMenuButton != null) {
            backToMenuButton.setBounds(300, 435, 200, 40); // Force default position normally
            backToMenuButton.setVisible(true); 
        }
        
        paintImmediately(0, 0, getWidth(), getHeight());
        
        String initials = JOptionPane.showInputDialog(
            this, 
            "Game Over! Score: " + score + "\nEnter Initials:", 
            "Save Score", 
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (initials != null && !initials.isEmpty()) {
            ScoreManager scoreManager = new ScoreManager();
            scoreManager.saveScore(initials.toUpperCase(), score);
        }
        repaint();
    }

    private void triggerWin() {
        gameWon = true;
        gameOver = true; 
        score += 10000;
        
        if (restartButton != null) restartButton.setVisible(true);
        if (backToMenuButton != null) {
            backToMenuButton.setBounds(300, 435, 200, 40); // Same as endGame(), force default position
            backToMenuButton.setVisible(true); 
        }
        
        paintImmediately(0, 0, getWidth(), getHeight());
        
        String initials = JOptionPane.showInputDialog(
            this, 
            "VICTORY! You Beat Donkey Kong!\nFinal Score: " + score + "\nEnter Initials:", 
            "Save Winner Score", 
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (initials != null && !initials.isEmpty()) {
            ScoreManager scoreManager = new ScoreManager();
            scoreManager.saveScore(initials.toUpperCase(), score);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (currentState) {
            case MENU:
                drawMenuScreen(g);
                break;
            case LEADERBOARD:
                drawLeaderboardScreen(g);
                break;
            case GAME:
                drawActiveGameScreen(g);
                break;
        }
    }

    private void drawMenuScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Game Title
        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        g.setColor(Color.RED);
        g.drawString("DONKEY KONG LITE", 160, 120);
        
        // Details & Controls (instructions)
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        int infoY = 360;
        
        g.drawString("HOW TO PLAY & CONTROLS", 310, infoY);
        
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.setColor(Color.GRAY);
        g.drawString("• Movement: [A] Left  |  [D] Right  |  [W] Jump / Climb Up  |  [S] Climb Down", 160, infoY + 25);
        
        g.setColor(new Color(255, 215, 0)); // Gold color for hammer specs
        g.drawString("• Hammer Powerup: Smashes active barrels. Lasts 5 seconds. Respawns after 15 seconds.", 140, infoY + 50);
        
        g.setColor(new Color(135, 206, 250)); // Light blue color for goal specs
        g.drawString("• Scoring: Smash Barrel: +500 PTS  |  Reach Boss at Top Platform: VICTORY +10,000 PTS!", 130, infoY + 75);
    }

    private void drawLeaderboardScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        g.setColor(Color.ORANGE);
        g.drawString("HIGH SCORES", 280, 80);
        
        g.setFont(new Font("Monospaced", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        
        ScoreManager scoreManager = new ScoreManager();
        ArrayList<String> highScores = scoreManager.getHighScores();
        
        int drawY = 150;
        if (highScores == null || highScores.isEmpty()) {
            g.drawString("No records logged yet!", 280, drawY);
        } else {
            int listedCount = 0;
            for (int i = 0; i < highScores.size(); i++) {
                if (listedCount >= 10) break;
                String scoreEntry = highScores.get(i).trim();
                if (!scoreEntry.isEmpty() && scoreEntry.contains(" - ")) {
                    g.drawString((listedCount + 1) + ". " + scoreEntry, 300, drawY);
                    drawY += 32;
                    listedCount++;
                }
            }
        }
    }

    private void drawActiveGameScreen(Graphics g) {
        if (gameOver) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setFont(new Font("Monospaced", Font.BOLD, 50));
            if (gameWon) {
                g.setColor(Color.GREEN);
                g.drawString("VICTORY!", 290, 190);
            } else {
                g.setColor(Color.RED);
                g.drawString("GAME OVER", 270, 190);
            }
            
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString("Final Score: " + score, 335, 250);
            
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(Color.GRAY);
            g.drawString("Choose an option below to continue:", 285, 330);
            return; 
        }

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Score: " + score, 20, 20);
        g.drawString("Time Multiplier: x" + (secondsElapsed / 500 + 1), 20, 40);
        
        for (Platform p : platforms) p.draw(g);
        for (Ladder l : ladders) l.draw(g); 
        for (Hammer h : hammers) h.draw(g); 
        if (donkeyKong != null) donkeyKong.draw(g); 
        player.draw(g);
        for (Obstacle b : barrels) b.draw(g);
    }
}