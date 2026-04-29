import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int tileSize = 24;
    private final int rows = 21;
    private final int cols = 19;

    private final Timer timer;

    private int pacmanRow = 15;
    private int pacmanCol = 9;

    private double pacmanX = pacmanCol * tileSize;
    private double pacmanY = pacmanRow * tileSize;
    private int targetRow = pacmanRow;
    private int targetCol = pacmanCol;
    private boolean pacmanIsMoving = false;

    private int directionRow = 0;
    private int directionCol = 0;

    private int nextDirectionRow = 0;
    private int nextDirectionCol = 0;

    private int score = 0;
    private boolean gameStarted = false;
    private boolean gamePaused = false;
    private boolean gameWon = false;

    private final int gameUpdateDelay = 16; // About 60 frames per second
    private final double pacmanSpeed = 2.0; // pixels pac-man moves per frame (speed)

    private int mouthSize = 35; 
    private int mouthChange = 3;

    private final Set<Point> dots = new HashSet<>();
    private final List<Ghost> ghosts = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(cols * tileSize, rows * tileSize + 40)); //Size of the window
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        loadDots();
        loadGhosts();

        timer = new Timer(gameUpdateDelay, this); 
        timer.start();
    }


    private void loadDots() {
        for (int row = 0; row < GameMap.MAP.length; row++) {
            for (int col = 0; col < GameMap.MAP[row].length(); col++) {
                if (GameMap.MAP[row].charAt(col) == '.') {
                    dots.add(new Point(col, row));
                }
            }
        }
    }

    private void loadGhosts() {
        ghosts.add(new Ghost(9, 9, tileSize, rows, cols, 1.5, Color.RED, Ghost.Personality.CHASER));
        ghosts.add(new Ghost(9, 8, tileSize, rows, cols, 1.3, Color.PINK,Ghost.Personality.AMBUSHER ));
        ghosts.add(new Ghost(9, 10, tileSize, rows, cols, 1.0, Color.CYAN, Ghost.Personality.RANDOM));
    }

    private boolean isWall(int row, int col) {
        if (row < 0 || row >= rows) {
            return true;
        }

        if (col < 0 || col >= cols) {
            return false;
        }

        return GameMap.MAP[row].charAt(col) == '#';
    }

    private void movePacman() {
        if (!gameStarted || gameWon || gamePaused) {
            return;
        }

        if (pacmanIsMoving) {
            movePacmanTowardsTarget();
            return;
        }
        
        int wantedRow = pacmanRow + nextDirectionRow;
        int wantedCol = pacmanCol + nextDirectionCol;

        if (!isWall(wantedRow, wantedCol)) {
            directionRow = nextDirectionRow;
            directionCol = nextDirectionCol;
        }

        int newRow = pacmanRow + directionRow;
        int newCol = pacmanCol + directionCol;

        if (!isWall(newRow, newCol)) {
            if (newCol < 0) {
                pacmanCol = cols - 1;
                pacmanX = pacmanCol * tileSize;
                targetRow = pacmanRow;
                targetCol = pacmanCol;
            } else if (newCol >= cols) {
                pacmanCol = 0;
                pacmanX = pacmanCol * tileSize;
                targetRow = pacmanRow;
                targetCol = pacmanCol;
            } else {
                targetRow = newRow;
                targetCol = newCol;
                pacmanIsMoving = true;
            }
        }
    }

    private void movePacmanTowardsTarget() {
        double targetX = targetCol * tileSize;
        double targetY = targetRow * tileSize;

        if (pacmanX < targetX) {
            pacmanX = Math.min(pacmanX + pacmanSpeed, targetX);
        } else if (pacmanX > targetX) {
            pacmanX = Math.max(pacmanX - pacmanSpeed, targetX);
        }

        if (pacmanY < targetY) {
            pacmanY = Math.min(pacmanY + pacmanSpeed, targetY);
        } else if (pacmanY > targetY) {
            pacmanY = Math.max(pacmanY - pacmanSpeed, targetY);
        }

        if (pacmanX == targetX && pacmanY == targetY) {
            pacmanRow = targetRow;
            pacmanCol = targetCol;
            pacmanIsMoving = false;

            Point currentPosition = new Point(pacmanCol, pacmanRow);

            if (dots.remove(currentPosition)) {
                score += 10;
            }

            if (dots.isEmpty()) {
                gameWon = true;
            }
        }
    }

    private void changePacmanDirection(int requestsRow, int requestedCol) {
        nextDirectionRow = requestsRow;
        nextDirectionCol = requestedCol;

        boolean requestedOppositeDirection = requestsRow == -directionRow && requestedCol == -directionCol;

        if (pacmanIsMoving && requestedOppositeDirection) {
            directionRow = requestsRow;
            directionCol = requestedCol;
            targetRow = pacmanRow;
            targetCol = pacmanCol;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawMaze(g);
        drawDots(g);
        drawGhosts(g);
        drawPacman(g);
        drawScore(g);

        if (!gameStarted) {
            drawStartScreen(g);
        }

        if (gamePaused) {
            drawPauseScreen(g);
        }

        if (gameWon) {
            drawWinMessage(g);
        }
    }

    private void drawMaze(Graphics g) {
        g.setColor(new Color(20, 40, 200));

        for (int row = 0; row < GameMap.MAP.length; row++) {
            for (int col = 0; col < GameMap.MAP[row].length(); col++) {
                if (GameMap.MAP[row].charAt(col) == '#') {
                    int x = col * tileSize;
                    int y = row * tileSize;

                    g.fillRoundRect(x + 2, y + 2, tileSize - 4, tileSize -4, 8, 8);
                }
            }
        }
    }

    private void drawDots(Graphics g) {
        g.setColor(Color.WHITE);

        for (Point dot : dots) { 
            int x = dot.x * tileSize + tileSize / 2 - 3;
            int y = dot.y * tileSize + tileSize / 2 - 3;

            g.fillOval(x, y, 6, 6);
        }
    }

    private void drawGhosts(Graphics g) {
        for (Ghost ghost : ghosts) {
            ghost.draw(g);
        }
    }

    private void drawPacman(Graphics g) {
        int x = (int) pacmanX + 2;
        int y = (int) pacmanY + 2;

        g.setColor(Color.YELLOW);

        int mouthStartAngle;

        if (directionCol == 1) {
            mouthStartAngle = mouthSize;
        } else if (directionCol == -1) {
            mouthStartAngle = 180 + mouthSize;
        } else if (directionRow == -1) {
            mouthStartAngle = 90 + mouthSize;
        } else if (directionRow == 1) {
            mouthStartAngle = 270 + mouthSize;
        } else {
            mouthStartAngle = mouthSize;
        }

        int arcAngle = 360 - mouthSize * 2;
        g.fillArc(x, y, tileSize - 4, tileSize - 4, mouthStartAngle, arcAngle);    
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, rows * tileSize + 28);
    }

    private void drawStartScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, cols * tileSize, rows * tileSize + 40);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("START GAME", 105, 240);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Press ENTER or SPACE", 130, 275);
    }

    private void drawPauseScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, cols * tileSize, rows * tileSize + 40);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("PAUSED", 150, 240);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Press P or ESC to continue", 120, 275);
    }


    private void drawWinMessage(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("YOU WIN!", 135, 260);
    }

    private void updatePacmanMouth() {
        mouthSize += mouthChange;

        if (mouthSize >= 40 || mouthSize <= 5) {
            mouthChange *= -1;
        }
    }

    private void updateGhosts() {
        for (Ghost ghost : ghosts) {
            ghost.update(pacmanRow, pacmanCol, directionRow, directionCol);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gamePaused && !gameWon) {
            updatePacmanMouth();
            updateGhosts();
        }
        movePacman();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!gameStarted && (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE)) {
            gameStarted = true;
            gamePaused = false;
            directionRow = 0;
            directionCol = 1;
            nextDirectionRow = 0;
            nextDirectionCol = 1;
            return;
        }

        if (gameStarted && !gameWon && (key == KeyEvent.VK_ESCAPE)) {
            gamePaused = !gamePaused;
            return ;
        }

        if (gamePaused) {
            return;
        }

        if (key == KeyEvent.VK_UP) {
            changePacmanDirection(-1, 0);
        } else if (key == KeyEvent.VK_DOWN) {
            changePacmanDirection(1, 0);
        } else if (key == KeyEvent.VK_LEFT) {
            changePacmanDirection(0, -1);
        } else if (key == KeyEvent.VK_RIGHT) {
            changePacmanDirection(0, 1);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
