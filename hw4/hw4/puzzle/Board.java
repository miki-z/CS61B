package hw4.puzzle;
import edu.princeton.cs.algs4.Queue;
import java.util.Arrays;

public class Board implements WorldState {
    private int[][] board;
    /** Constructs a board from an N-by-N array of tiles where
     * tiles[i][j] = tile at row i, column j*/
    public Board(int[][] tiles) {
        board = new int[tiles.length][];
        for (int i = 0; i < tiles.length; i++) {
            board[i] = Arrays.copyOf(tiles[i], tiles.length);
        }
    }

    /** Returns value of tile at row i, column j (or 0 if blank) */
    public int tileAt(int i, int j) {
        if (i < 0 || j < 0 || i > size() - 1 || j > size() - 1) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        return board[i][j];
    }

    /** Returns the board size N */
    public int size() {
        return board.length;
    }

    /** Returns the neighbors of the current board */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == 0) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = 0;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = 0;
                }
            }
        }
        return neighbors;
    }

    /** The number of tiles in the wrong position. */
    public int hamming() {
        int hamDistance = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                int goal = i * size() + j + 1;
                if (board[i][j] != 0 && board[i][j] != goal) {
                    hamDistance++;
                }
            }
        }
        return hamDistance;
    }

    /** The sum of the Manhattan distances (sum of the vertical and
     * horizontal distance) from the tiles to their goal positions.*/
    public int manhattan() {
        int manhattanDis = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                int currentTile = board[i][j];
                if (currentTile != 0) {
                    int goalRow = (currentTile - 1) / size();
                    int goalCol = (currentTile - 1) % size();
                    manhattanDis += Math.abs(goalCol - j) + Math.abs(goalRow - i);
                }
            }
        }
        return manhattanDis;
    }

    /** Estimated distance to goal. */
    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    /** Returns true if this board's tile values are the same
     * position as y's */
    public boolean equals(Object y) {
        if (y == null || y.getClass() != this.getClass()) {
            return false;
        }
        Board other = (Board) y;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (board[i][j] != other.tileAt(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                result = 31 * result + board[i][j];
            }
        }
        return result;
    }

    /** Returns the string representation of the board.
      * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
