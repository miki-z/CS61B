package hw4.puzzle;
import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Solver {
    private ArrayList<WorldState> results;
    /** Constructor which solves the puzzle, computing
     * everything necessary for moves() and solution() to
     * not have to solve the problem again. Solves the
     * puzzle using the A* algorithm. Assumes a solution exists.
     */
    public Solver(WorldState initial) {
        results = new ArrayList<>();
        MinPQ<SearchNode> searchNodes = new MinPQ<>(new NodeComparator());
        // insert an “initial search node” into the priority queue
        SearchNode initialNode = new SearchNode(initial, 0, null);
        searchNodes.insert(initialNode);

        SearchNode x = searchNodes.delMin();
        while (!x.wordState.isGoal()) {
            for (WorldState word : x.wordState.neighbors()) {
                if (x.source == null || !word.equals(x.source.wordState)) {
                    SearchNode node = new SearchNode(word, x.movesMade + 1, x);
                    searchNodes.insert(node);
                }
            }
            x = searchNodes.delMin();
        }

        while (x != null) {
            results.add(x.wordState);
            x = x.source;
        }
    }

    /** Returns the minimum number of moves to solve the puzzle starting
     * at the initial WorldState.
     */
    public int moves() {
        return results.size() - 1;
    }

    /** Returns a sequence of WorldStates from the initial WorldState
     * to the solution.
     */
    public Iterable<WorldState> solution() {
        Collections.reverse(results);
        return results;
    }

    private static class NodeComparator implements Comparator<SearchNode> {

        public int compare(SearchNode o1, SearchNode o2) {
            int priority1 = o1.distanceToGoal + o1.movesMade;
            int priority2 = o2.distanceToGoal + o2.movesMade;
            return priority1 - priority2;
        }

    }

    private static class SearchNode {
        private WorldState wordState;
        private int movesMade;
        private SearchNode source;
        private int distanceToGoal;

        private SearchNode(WorldState word, int moves, SearchNode source) {
            this.wordState = word;
            this.movesMade = moves;
            this.source = source;
            this.distanceToGoal = wordState.estimatedDistanceToGoal();
        }
    }
}
