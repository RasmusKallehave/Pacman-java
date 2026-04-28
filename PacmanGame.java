import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class PacmanGame extends JPanel implements ActionListener, KeyListener {
    private final int tileSize = 24;
    private final int rows = 21;
    private final int cols = 19;

    private final Timer timer;

    private int pacmanRow = 15;
    private int pacmanCol = 9;

    private int directionRow = 0;
    private int directionCol = 0;

    private int nextDirectionRow = 0;
    private int nextDirectionCol = 0;

    private int score = 0;
    private boolean gameWon = false;

    private final String[] map = {

            "###################",
            "#........#........#",
            "#.##.###.#.###.##.#",
            "#.................#",
            "#.##.#.#####.#.##.#",
            "#....#...#...#....#",
            "####.### # ###.####",
            "   #.#       #.#   ",
            "####.# ## ## #.####",
            "    .  #   #  .    ",
            "####.# ##### #.####",
            "   #.#       #.#   ",
            "####.# ##### #.####",
            "#........#........#",
            "#.##.###.#.###.##.#",
            "#..#.....P.....#..#",
            "##.#.#.#####.#.#.##",
            "#....#...#...#....#",
            "#.######.#.######.#",
            "#.................#",
            "###################"
    };

    private final Set<Point> dots = new HashSet<>();

    public PacmanGame() {
        setPreferredSize(new Dimension(cols * tileSize, rows * tileSize * 40));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        loadDots();

        timer = new Timer(120, this);
        timer.start();
    }

    private void loadDots() {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length(); col++) {
                if (map[row].charAt(col) == '.') {
                    dots.add(new Point(col, row));
                }
            }
        }
    }

    private boolean isWall(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true;
        }

        return map[row].charAt(col) == '#';
    }

    private void movePacman() {
        if (gameWon) {
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
            pacmanRow = newRow;
            pacmanCol = newCol;
        }

        Point currentPosition = new Point(pacmanCol, pacmanRow);

        if (dots.remove(currentPosition)) {
            score += 10;
        }

        if (dots.isEmpty()) {
            gameWon = true;
            timer.stop();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawMaze(g);
        drawDots(g);
        drawPacman(g);
        drawScore(g);

        if (gameWon) {
            drawWinMessage(g);
        }
    }

    private void drawMaze(Graphics g) {
        g.setColor(new Color(20, 40, 200));

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length(); col++) {
                if (map[row].charAt(col) == '#') {
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

    private void drawPacman(Graphics g) {
        int x = pacmanCol * tileSize + 2;
        int y = pacmanRow * tileSize + 2;

        g.setColor(Color.YELLOW);

        int mouthStartAngle;

        if (directionCol == 1) {
            mouthStartAngle = 35;
        } else if (directionCol == -1) {
            mouthStartAngle = 215;
        } else if (directionRow == -1) {
            mouthStartAngle = 125;
        } else if (directionRow == 1) {
            mouthStartAngle = 305;
        } else {
            mouthStartAngle = 35;
        }

        g.fillArc(x, y, tileSize - 4, tileSize - 4, mouthStartAngle, 290);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, rows * tileSize + 28);
    }

    private void drawWinMessage(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("YOU WIN!", 135, 260);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePacman();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) {
            nextDirectionRow = -1;
            nextDirectionCol = 0;
        } else if (key == KeyEvent.VK_DOWN) {
            nextDirectionRow = 1;
            nextDirectionCol = 0;
        } else if (key == KeyEvent.VK_LEFT) {
            nextDirectionRow = 0;
            nextDirectionCol = -1;
        } else if (key == KeyEvent.VK_RIGHT) {
            nextDirectionRow = 0;
            nextDirectionCol = 1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-man Game");
        PacmanGame game = new PacmanGame();

        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
