import java.util.Arrays;
import java.util.Stack;

public class Board {

    private int[][] blocks;
    private int dimension;
    private int hamming;
    private int manhattan;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocksin) {
        init(blocksin);
    }

    private Board(Board b) {
        init(b.blocks);
    }

    // board dimension N
    public int dimension() {
        return dimension;
    }

    // number of blocks out of place
    public int hamming() {
        if (hamming != -1) {
            return hamming;
        }
        hamming = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                int value = blocks[row][col];
                if (value == 0) {
                    continue;
                }
                int lin = row * dimension + col + 1;
                if (lin != value) {
                    hamming++;
                }
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        if (manhattan != -1) {
            return manhattan;
        }
        manhattan = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                int value = blocks[row][col];
                if (value == 0) {
                    continue;
                }
                int rowGoal = (value - 1) / dimension;
                int colGoal = (value - 1) % dimension;
                manhattan += Math.abs(colGoal - col) + Math.abs(rowGoal - row);
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return manhattan() == 0;
    }

    // a board that is obtained by exchanging any pair of
    // blocks
    public Board twin() {
        Board twin = new Board(this);

        int r1 = 0, c1 = 0, r2 = 0, c2 = 1;
        if (twin.blocks[0][0] == 0) {
            if (dimension() == 2) {
                c1 = 1;
                r2 = 1;
                c2 = 1;
            } else {
                c1 = 1;
                c2 = 2;
            }
        } else if (twin.blocks[0][1] == 0) {
            if (dimension() == 2) {
                r2 = 1;
                c2 = 0;
            } else {
                c2 = 2;
            }
        }

        twin.exch(r1, c1, r2, c2);

        return twin;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        boolean isEqual = false;
        if (y != null && y instanceof Board) {
            Board other = (Board) y;
            if (this.dimension() == other.dimension()) {
                isEqual = true;
                for (int row = 0; row < dimension && isEqual; row++) {
                    for (int col = 0; col < dimension && isEqual; col++) {
                        isEqual = this.blocks[row][col] == other.blocks[row][col];
                    }
                }
            }

        }
        return isEqual;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        boolean isFoundZero = false;
        int row = 0;
        int col = 0;
        for (row = 0; row < dimension && !isFoundZero; row++) {
            for (col = 0; col < dimension && !isFoundZero; col++) {
                isFoundZero = blocks[row][col] == 0;
            }
        }

        row--;
        col--;

        Stack<Board> neighbors = new Stack<Board>();
        addToNeighbors(neighbors, row, col, row, col - 1);
        addToNeighbors(neighbors, row, col, row, col + 1);
        addToNeighbors(neighbors, row, col, row - 1, col);
        addToNeighbors(neighbors, row, col, row + 1, col);

        return neighbors;
    }

    private void addToNeighbors(Stack<Board> neighbors, int r1, int c1, int r2,
            int c2) {
        if (r2 >= 0 && r2 < dimension && c2 >= 0 && c2 < dimension) {
            Board b = new Board(this);
            b.exch(r1, c1, r2, c2);
            neighbors.push(b);
        }
    }

    // string representation of this board (in the
    // output format specified below)
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = dimension();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    private void init(int[][] blocksin) {

        if (blocksin == null) {
            throw new java.lang.NullPointerException("Blocks may not be null.");
        }

        dimension = blocksin.length;
        this.blocks = new int[dimension][];

        for (int row = 0; row < dimension; row++) {
            this.blocks[row] = Arrays.copyOf(blocksin[row], dimension);
        }

        hamming = -1;
        manhattan = -1;
    }

    private void exch(int r1, int c1, int r2, int c2) {
        int tmp = blocks[r1][c1];
        blocks[r1][c1] = blocks[r2][c2];
        blocks[r2][c2] = tmp;
    }

    public static void main(String[] args) // unit tests (not graded)
    {
        testDimension();
        testHamming();
        testManhattan();
        testTwin();
        testEquals();
        testNeighbors1();
        testNeighbors2();
        testToString();
    }

    private static void testToString() {
        System.out.println("TestNeighbors1");
        int[][] blocks = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);
        System.out.println(b.toString());
    }

    private static void testNeighbors1() {
        System.out.println("TestNeighbors1");

        int[][] blocks = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);

        Iterable<Board> neighbors = b.neighbors();

        int[][] blocks1 = new int[][] { new int[] { 1, 0, 2 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b1 = new Board(blocks1);

        int[][] blocks2 = new int[][] { new int[] { 1, 2, 5 },
                new int[] { 3, 4, 0 }, new int[] { 6, 7, 8 } };
        Board b2 = new Board(blocks2);

        Board[] boards = new Board[] { b1, b2 };
        int boardNum = 0;
        for (Board n : neighbors) {
            boolean isEqual = n.equals(boards[boardNum++]);
            assert isEqual;
        }

    }

    private static void testNeighbors2() {
        System.out.println("TestNeighbors2");

        int[][] blocks = new int[][] { new int[] { 1, 2, 4 },
                new int[] { 3, 0, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);

        Iterable<Board> neighbors = b.neighbors();

        int[][] blocks1 = new int[][] { new int[] { 1, 2, 4 },
                new int[] { 0, 3, 5 }, new int[] { 6, 7, 8 } };
        Board b1 = new Board(blocks1);

        int[][] blocks2 = new int[][] { new int[] { 1, 2, 4 },
                new int[] { 3, 5, 0 }, new int[] { 6, 7, 8 } };
        Board b2 = new Board(blocks2);

        int[][] blocks3 = new int[][] { new int[] { 1, 0, 4 },
                new int[] { 3, 2, 5 }, new int[] { 6, 7, 8 } };
        Board b3 = new Board(blocks3);

        int[][] blocks4 = new int[][] { new int[] { 1, 2, 4 },
                new int[] { 3, 7, 5 }, new int[] { 6, 0, 8 } };
        Board b4 = new Board(blocks4);

        Board[] boards = new Board[] { b1, b2, b3, b4 };
        int boardNum = 0;
        for (Board n : neighbors) {
            boolean isEqual = n.equals(boards[boardNum++]);
            assert isEqual;
        }

    }

    private static void testEquals() {
        System.out.println("TestEquals");

        int[][] blocks = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);

        boolean isEqual;

        //isEqual = b.equals(null);
        //assert !isEqual;

        isEqual = b.equals(new Object());
        assert !isEqual;

        Board b2 = new Board(blocks);
        isEqual = b.equals(b2);
        assert isEqual;

        int[][] blocks3 = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b3 = new Board(blocks3);
        isEqual = b.equals(b3);
        assert isEqual;

        int[][] blocks4 = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 5, 5 }, new int[] { 6, 7, 8 } };
        Board b4 = new Board(blocks4);
        isEqual = b.equals(b4);
        assert !isEqual;
    }

    private static void testTwin() {
        System.out.println("TestTwin");

        int[][] blocks = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);
        Board twin = b.twin();
        assert twin.blocks[0][0] == 2;
        assert twin.blocks[0][1] == 1;

        blocks = new int[][] { new int[] { 0, 1, 2 }, new int[] { 3, 4, 5 },
                new int[] { 6, 7, 8 } };
        b = new Board(blocks);
        twin = b.twin();
        assert twin.blocks[0][0] == 0;
        assert twin.blocks[0][1] == 2;
        assert twin.blocks[0][2] == 1;

        blocks = new int[][] { new int[] { 1, 0, 2 }, new int[] { 3, 4, 5 },
                new int[] { 6, 7, 8 } };
        b = new Board(blocks);
        twin = b.twin();
        assert twin.blocks[0][0] == 2;
        assert twin.blocks[0][1] == 0;
        assert twin.blocks[0][2] == 1;
    }

    private static void testManhattan() {
        System.out.println("TestManhattan");

        int[][] blocks = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);

        int manhattan = b.manhattan();
        assert manhattan == 3 + 1 + 1 + 3 + 1 + 1;

        blocks = new int[][] { new int[] { 1, 2, 3 }, new int[] { 4, 5, 6 },
                new int[] { 7, 8, 0 } };
        b = new Board(blocks);

        manhattan = b.manhattan();
        assert manhattan == 0;
    }

    private static void testHamming() {
        System.out.println("TestHamming");

        int[][] blocks = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);

        int hamming = b.hamming();
        assert hamming == 6;

        blocks = new int[][] { new int[] { 1, 2, 3 }, new int[] { 4, 5, 6 },
                new int[] { 7, 8, 0 } };
        b = new Board(blocks);

        hamming = b.hamming();
        assert hamming == 0;

    }

    private static void testDimension() {
        System.out.println("TestDimension");

        int[][] blocks = new int[][] { new int[] { 1, 2, 0 },
                new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 } };
        Board b = new Board(blocks);

        assert b.dimension() == 3;

        blocks = new int[][] { new int[] { 1, 2 }, new int[] { 6, 0 } };
        b = new Board(blocks);

        assert b.dimension() == 2;

    }
}
