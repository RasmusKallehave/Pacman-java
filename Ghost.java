import java.awt.*;
import java.util.Random;

public class Ghost {
    public enum Personality {
        CHASER,
        AMBUSHER,
        RANDOM
    }

    private final int tileSize;
    private final int rows;
    private final int cols;

    private int row;
    private int col;

    private double x;
    private double y;

    private int targetRow;
    private int targetCol;

    private int directionRow = 0;
    private int directionCol = 1;

    private boolean isMoving = false;

    private final double speed;
    private final Color color;
    private final Personality personality;

    private final Random random = new Random();

    public Ghost(int startRow, int startCol, int tileSize, int rows, int cols, double speed, Color color, Personality personality) {
        this.row = startRow;
        this.col = startCol;
        this.tileSize = tileSize;
        this.rows = rows;
        this.cols = cols;
        this.speed = speed;
        this.color = color;
        this.personality = personality;

        this.x = startCol * tileSize;
        this.y = startRow * tileSize;

        this.targetRow = startRow;
        this.targetCol = startCol;
    }

    public void update(int pacmanRow, int pacmanCol, int pacmanDirectionRow, int pacmanDirectionCol) {
        if (isMoving) {
            moveTowardsTarget();
        } else {
            chooseNewDirection(pacmanRow, pacmanCol, pacmanDirectionRow, pacmanDirectionCol);
        }
    }

    public void draw(Graphics g) {
        int drawX = (int) x + 2;
        int drawY = (int) y + 2;
        int size = tileSize - 4;

        g.setColor(color);

        // Ghost head
        g.fillArc(drawX, drawY, size, size, 0, 180);

        // Ghost body
        g.fillRect(drawX, drawY + size / 2, size, size / 2);

        // Small feet
        int footWidth = size / 4;
        g.fillOval(drawX, drawY + size - 6, footWidth, 8);
        g.fillOval(drawX + footWidth, drawY + size - 6, footWidth, 8);
        g.fillOval(drawX + footWidth * 2, drawY + size - 6, footWidth, 8);
        g.fillOval(drawX + footWidth * 3, drawY + size - 6, footWidth, 8);

        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(drawX + 5, drawY + 7, 6, 8);
        g.fillOval(drawX + size - 11, drawY + 7, 6, 8);

        g.setColor(Color.BLUE);
        g.fillOval(drawX + 7, drawY + 10, 3, 3);
        g.fillOval(drawX + size - 9, drawY + 10, 3, 3);
    }

    private void chooseNewDirection(int pacmanRow, int pacmanCol, int pacmanDirectionRow, int pacmanDirectionCol) {
        int targetPersonalityRow = pacmanRow;
        int targetPersonalityCol = pacmanCol;

        if (personality == Personality.AMBUSHER) {
            targetPersonalityRow = pacmanRow + pacmanDirectionRow * 4;
            targetPersonalityCol = pacmanCol + pacmanDirectionCol * 4;
        } else if (personality == Personality.RANDOM) {
            chooseRandomDirection();
            return;
        }

        chooseDirectionTowards(targetPersonalityRow, targetPersonalityCol);
    }

    private void chooseDirectionTowards(int wantedRow, int wantedCol) {
        int[][] possibleDirections = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        int bestDirectionRow = 0;
        int bestDirectionCol = 0;
        double bestDistance = Double.MAX_VALUE;

        for (int[] direction : possibleDirections) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (!isWall(newRow, newCol) && !isOppositeDirection(direction[0], direction[1])) {
                double distance = distanceToTarget(newRow, newCol, wantedRow, wantedCol);

                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestDirectionRow = direction[0];
                    bestDirectionCol = direction[1];
                }
            }
        }

        if (bestDistance == Double.MAX_VALUE) {
            chooseRandomDirection();
            return;
        }

        startMoving(bestDirectionRow, bestDirectionCol);
    }

    private void chooseRandomDirection() {
        int[][] possibleDirections = {
                {-1, 0},
                {1, 0},
                {0,-1},
                {0, 1}
        };
    
        for (int i = 0; i < 10; i++) {
            int[] direction = possibleDirections[random.nextInt(possibleDirections.length)];

            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (!isWall(newRow, newCol) && !isOppositeDirection(direction[0], direction[1])) {
                startMoving(direction[0], direction[1]);
                return;
            }
        }
            
            for (int[] direction : possibleDirections) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];

                if (!isWall(newRow, newCol)) {
                    startMoving(direction[0], direction[1]);
                    return;
                }
            }
        }   
        
        private void startMoving(int newDirectionRow, int newDirectionCol) {
            directionRow = newDirectionRow;
            directionCol = newDirectionCol;

            targetRow = row + directionRow;
            targetCol = col + directionCol;
            isMoving = true;
        }

        private boolean isOppositeDirection(int newDirectionRow, int newDirectionCol) {
            return newDirectionRow == -directionRow && newDirectionCol == -directionCol;
        }

        private double distanceToTarget(int fromRow, int fromCol, int wantedRow, int wantedCol) {
            int rowDistance = fromRow - wantedRow;
            int colDistance = fromCol - wantedCol;

            return Math.sqrt(rowDistance * rowDistance + colDistance * colDistance);
        }

    private void moveTowardsTarget() {
        double targetX = targetCol * tileSize;
        double targetY = targetRow * tileSize;

        if (x < targetX) {
            x = Math.min(x + speed, targetX);
        } else if (x > targetX) {
            x = Math.max(x - speed, targetX);
        }

        if (y < targetY) {
            y = Math.min(y + speed, targetY);
        } else if (y > targetY) {
            y = Math.max(y - speed, targetY);
        }

        if (x == targetX && y == targetY) {
            row = targetRow;
            col = targetCol;
            isMoving = false;
        }
    }

    private boolean isWall(int checkRow, int checkCol) {
        if (checkRow < 0 || checkRow >= rows) {
            return true;
        }

        if (checkCol < 0 || checkCol >= cols) {
            return false;
        }

        return GameMap.MAP[checkRow].charAt(checkCol) == '#';
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
