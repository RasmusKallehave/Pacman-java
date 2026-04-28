import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
public class PacmanGame extends JPanel implements ActionListener, KeyListener {
    private final int tileSize = 24;
    private final int rows = 21;
    private final int cols = 19;

    private final >Timer timer;

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

    
    
}
