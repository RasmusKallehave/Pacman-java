import javax.swing.*;

public class PacmanGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-man Game");
        GamePanel game = new GamePanel();

        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}