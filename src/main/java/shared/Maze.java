package shared;

import java.util.Random;

public class Maze {
    private final int[][] maze;
    private final int rows;
    private final int cols;
    private final Random random;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new int[rows][cols];
        this.random = new Random();
        generateMaze();
    }

    private void generateMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = 1;
            }
        }

        Random random = new Random();
        carvePath(1, 1, random);
        placeItems(4, 1);
        createMultiplePathsToTrophy();
        placeTrapsAlongPaths(20);
        placeItems(2, 30);
    }

    private void createMultiplePathsToTrophy() {
        int trophyX = -1, trophyY = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j] == 4) {
                    trophyX = i;
                    trophyY = j;
                    break;
                }
            }
        }
        if (trophyX == -1 || trophyY == -1) return;
        for (int i = 0; i < 3; i++) {
            int startX, startY;
            do {
                startX = random.nextInt(rows);
                startY = random.nextInt(cols);
            } while (!isInBounds(startX, startY) || maze[startX][startY] != 1);

            carveIndependentPath(startX, startY, trophyX, trophyY);
        }
    }

    private void carveIndependentPath(int startX, int startY, int endX, int endY) {
        int x = startX;
        int y = startY;
        while (x != endX || y != endY) {
            maze[x][y] = 0;

            if (random.nextBoolean()) {
                x += Integer.compare(endX, x);
            } else {
                y += Integer.compare(endY, y);
            }
            if (!isInBounds(x, y) || maze[x][y] == 0) break;
        }
    }

    private void placeTrapsAlongPaths(int count) {
        int placed = 0;
        while (placed < count) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            // Размещаем ловушку только на пути
            if (maze[x][y] == 0) {
                maze[x][y] = 3;
                placed++;
            }
        }
    }

    private void carvePath(int x, int y, Random random) {
        maze[x][y] = 0;
        int[] directions = {0, 1, 2, 3};
        shuffleArray(directions, random);

        for (int direction : directions) {
            int nx = x, ny = y;
            switch (direction) {
                case 0:
                    nx = x - 2;
                    break;
                case 1:
                    ny = y + 2;
                    break;
                case 2:
                    nx = x + 2;
                    break;
                case 3:
                    ny = y - 2;
                    break;
            }
            if (isInBounds(nx, ny) && maze[nx][ny] == 1) {
                maze[x + (nx - x) / 2][y + (ny - y) / 2] = 0;
                carvePath(nx, ny, random);
            }
        }
    }

    public boolean placeTrap(int x, int y) {
        if (maze[x][y] == 0) {
            maze[x][y] = 3;
            return true;
        }
        return false;
    }

    public boolean isCoin(int x, int y) {
        return maze[y][x] == 2;
    }

    public boolean isTrap(int x, int y) {
        return maze[y][x] == 3;
    }

    public boolean isTrophy(int x, int y) {
        return maze[y][x] == 4;
    }

    public void clearCell(int x, int y) {
        maze[y][x] = 0;
    }

    private void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public boolean isWalkable(int x, int y) {
        return isInBounds(y, x) && maze[y][x] == 0 || maze[y][x] == 2 || maze[y][x] == 3 || maze[y][x] == 4;
    }

    private boolean isInBounds(int x, int y) {
        return x > 0 && x < rows - 1 && y > 0 && y < cols - 1;
    }

    public int getStartX() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j] == 0) return i;
            }
        }
        return 0;
    }

    public int getStartY() {
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                if (maze[i][j] == 0) return j;
            }
        }
        return 0;
    }

    private void placeItems(int itemType, int count) {
        int placed = 0;
        while (placed < count) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);
            if (maze[x][y] == 0) {
                maze[x][y] = itemType;
                placed++;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : maze) {
            for (int cell : row) {
                if (cell == 1) {
                    sb.append("#");
                }
                else if (cell == 0) {
                    sb.append(".");
                }
                else if (cell == 2) {
                    sb.append("M");
                }
                else if (cell == 3) {
                    sb.append("T");
                }
                else if (cell == 4) {
                    sb.append("C");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
