import java.util.Comparator;
import java.util.LinkedList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private boolean isSolvable;
    private SearchNode solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {

        if (initial == null) {
            throw new java.lang.NullPointerException(
                    "Initial board may not be null.");
        }

        isSolvable = false;
        solution = null;

        SearchNode tree1 = new SearchNode();
        tree1.board = initial;
        tree1.prev = null;
        tree1.moves = 0;
        tree1.priority = -1;

        SearchNode tree2 = new SearchNode();
        tree2.board = initial.twin();
        tree2.prev = null;
        tree2.moves = 0;
        tree2.priority = -1;

        MinPQ<SearchNode> q1 = new MinPQ<SearchNode>(new BoardComparator());
        MinPQ<SearchNode> q2 = new MinPQ<SearchNode>(new BoardComparator());

        q1.insert(tree1);
        q2.insert(tree2);

        boolean isTwin = false;

        while (!q1.isEmpty() || !q2.isEmpty()) {
            if (isTwin) {
                SearchNode twin = q2.delMin();
                if (twin.board.isGoal()) {
                    isSolvable = false;
                    return;
                }
                getNeighbors(twin, q2);
            } else {
                SearchNode curr = q1.delMin();
                if (curr.board.isGoal()) {
                    isSolvable = true;
                    solution = curr;
                    return;
                }
                getNeighbors(curr, q1);
            }
            isTwin = !isTwin;
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (isSolvable()) {
            return solution.moves;
        } else {
            return -1;
        }
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (isSolvable()) {
            LinkedList<Board> stack = new LinkedList<Board>();
            SearchNode tmp = solution;
            while (tmp != null) {
                stack.addFirst(tmp.board);
                tmp = tmp.prev;
            }
            return stack;
        } else {
            return null;
        }
    }

    private void getNeighbors(SearchNode node, MinPQ<SearchNode> pq) {
        Board board = node.board;
        Board prev = null;

        if (node.prev != null) {
            prev = node.prev.board;
        }
        Iterable<Board> i = board.neighbors();
        for (Board n : i) {
            if (prev == null || !n.equals(prev)) {
                SearchNode child = new SearchNode();
                child.board = n;
                child.prev = node;
                child.moves = node.moves + 1;
                child.priority = -1;
                pq.insert(child);
            }
        }
    }

    private class BoardComparator implements Comparator<SearchNode> {

        @Override
        public int compare(SearchNode arg0, SearchNode arg1) {
            if (arg0.priority == -1) {
                arg0.priority = arg0.moves + arg0.board.manhattan();
            }

            if (arg1.priority == -1) {
                arg1.priority = arg1.moves + arg1.board.manhattan();
            }

            return arg0.priority - arg1.priority;
        }
    }

    private class SearchNode {
        private SearchNode prev;
        private Board board;
        private int moves;
        private int priority;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
