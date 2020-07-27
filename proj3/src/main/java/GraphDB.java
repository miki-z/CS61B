import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;


/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    private final Map<Long, Node> graph = new LinkedHashMap<>();
    private final Map<String, String> originalNames = new HashMap<>();
    private final Map<String, Set<Node>> locations = new HashMap<>();
    private final Trie trie = new Trie();
    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {

        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        List<Long> nodesToRemove = new ArrayList<>();
        for (Long nodeID : graph.keySet()) {
            if (this.graph.get(nodeID).neighbors.isEmpty()) {
                nodesToRemove.add(nodeID);
            }
        }
        for (Long id : nodesToRemove) {
            this.graph.remove(id);
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return graph.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
//        Node node = graph.get(v);
//        List<Long> res = new ArrayList<>();
//        for (Long id: node.neighbors.keySet()) {
//            res.add(id);
//        }
        return this.graph.get(v).neighbors.keySet();
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double minDistance = Double.MAX_VALUE;
        Long closestNode = -Long.MAX_VALUE;
        for (Long nodeID :graph.keySet()) {
            double distance = distance(lon(nodeID), lat(nodeID), lon, lat);
            if (distance <= minDistance) {
                minDistance = distance;
                closestNode = nodeID;
            }
        }
        return closestNode;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return graph.get(v).longitude;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return graph.get(v).latitude;
    }

    /**
     * A node(vertex)
     */
    static class Node {
        double longitude;
        double latitude;
        long id;
        String name;
        Map<Long, String> neighbors;
        // Map<String, String> extraInfo;

        Node(long id, double longitude, double latitude) {
            this.id = id;
            this.longitude = longitude;
            this.latitude = latitude;
            this.neighbors = new HashMap<>();
            //this.extraInfo = new HashMap<>();
        }
    }

    /**
     * Add a node to the graph.
     * @param node the node to be added.
     */
    void addNode(Node node) {
        this.graph.put(node.id, node);
    }

    /**
     * Adds the edge v-w to this graph (if it is not already an edge).
     *
     * @param  v one vertex in the edge
     * @param  w the other vertex in the edge
     */
    void addEdge(long v, long w, String name) {
        graph.get(v).neighbors.put(w, name);
        graph.get(w).neighbors.put(v, name);
    }

    Map<Long, String> ways(Long v) {
        return graph.get(v).neighbors;
    }

    static class Trie {
        Child root;

        Trie() {
            root = new Child();
        }

        static class Child {
            boolean isKey;
            Map<Character, Child> next;

            Child(boolean b) {
                this.isKey = b;
                this.next = new HashMap<>();
            }

            Child() {
                this(false);
            }

        }

        void add(String name) {
            Child cur = root;
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (cur.next.get(c) == null) {
                    cur.next.put(c, new Child());
                }
                cur = cur.next.get(c);
            }

            if (!cur.isKey) {
                cur.isKey = true;
            }
        }


    }

    void addLocation(String location, Node node) {
        node.name = location;
        String cleanedName = cleanString(location);
        this.trie.add(cleanedName);
        Set<Node> nodes = this.locations.get(cleanedName);
        if (nodes == null) {
            nodes = new HashSet<>();
        }
        nodes.add(node);
        this.locations.put(cleanedName, nodes);
        this.originalNames.put(cleanedName, location);
    }

    Trie.Child searchPrefix(String prefix) {
        GraphDB.Trie.Child cur = this.trie.root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (cur.next.get(c) == null) {
                return null;
            }
            cur = cur.next.get(c);
        }
        return cur;
    }

    void helpGetLocation(String s, List<String> res, Trie.Child c) {
        if (c.isKey) {
            res.add(this.originalNames.get(s));
        }
        for (char ch : c.next.keySet()) {
            helpGetLocation(s + ch, res, c.next.get(ch));
        }
    }

    List<String> getLocationsByPrefix(String prefix) {
        List<String> res = new LinkedList<>();
        /* Search for prefix */
        Trie.Child cur = this.searchPrefix(prefix);
        if (cur == null) {
            return res;
        }
        this.helpGetLocation(prefix, res, cur);
        return res;
    }

    Iterable<Node> locations(String name) {
        return locations.get(cleanString(name));
    }

    List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> res = new LinkedList<>();
        for (GraphDB.Node n : this.locations(locationName)) {
            Map<String, Object> params = new HashMap<>();
            params.put("lat", n.latitude);
            params.put("lon", n.longitude);
            params.put("name", n.name);
            params.put("id", n.id);
            res.add(params);
        }
        return res;
    }

}
