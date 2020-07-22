import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Comparator;
import java.util.Collections;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        /* Key: the id of an vertex, Value: best know distance from start location. */
        Map<Long, Double> bestDistance = new HashMap<>();
        /* Key: the id of an vertex, Value: the id of the best parent vertex */
        Map<Long, Long> bestParent = new HashMap<>();
        Set<Long> marked = new HashSet<>();
        long startNode = g.closest(stlon, stlat);
        long desNode = g.closest(destlon, destlat);

        /* Compare two nodes based on their best estimate distance to the destination */
        class NodeComparator implements Comparator<Long> {
            @Override
            public int compare(Long o1, Long o2) {
                double bestEstimate1 = g.distance(o1, desNode) + bestDistance.get(o1);
                double bestEstimate2 = g.distance(o2, desNode) + bestDistance.get(o2);
                if (bestEstimate1 - bestEstimate2 > 0) {
                    return 1;
                } else if (bestEstimate1 - bestEstimate2 < 0) {
                    return -1;
                }
                return 0;
            }
        }
        List<Long> res = new ArrayList<>();
        Queue<Long> fringe = new PriorityQueue<>(new NodeComparator());


        /* Initialize the best distance of all nodes to infinity */
        for (Long nodeID : g.vertices()) {
            bestDistance.put(nodeID, Double.POSITIVE_INFINITY);
        }
        /* Handle the start node */
        fringe.add(startNode);
        bestDistance.put(startNode, 0.0);
        Long curNode = fringe.poll();
        marked.add(curNode);
        /* Walk through edges */
        while (curNode != null && !curNode.equals(desNode)) {
            double startToCur = bestDistance.get(curNode);
            /* Add sources to fringe */
            for (Long neighbor : g.adjacent(curNode)) {
                if (!marked.contains(neighbor)) {
                    double disToParent = g.distance(curNode, neighbor);
                    double startToNeighbor = startToCur + disToParent;
                    /* Update the best and fringe */
                    if (startToNeighbor < bestDistance.get(neighbor)) {
                        bestDistance.put(neighbor, startToNeighbor);
                        bestParent.put(neighbor, curNode);
                        fringe.add(neighbor);
                    }
                }
            }
            curNode = fringe.poll();
            marked.add(curNode);
        }

        /* Generate the shortest path */
        while (curNode != null && !curNode.equals(startNode)) {
            res.add(curNode);
            curNode = bestParent.get(curNode);
        }
        res.add(curNode);
        Collections.reverse(res);

        return res;
    }



    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        return null; // FIXME
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
