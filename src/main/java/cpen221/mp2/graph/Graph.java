package cpen221.mp2.graph;

import java.util.*;

/**
 * Represents a graph with vertices of type V.
 *
 * @param <V> represents a vertex type
 */
public class Graph<V extends Vertex, E extends Edge<V>> implements ImGraph<V, E>, MGraph<V, E> {
    ALGraph<Vertex, Edge<Vertex>> graph;

    public Graph() {
        this.graph = new ALGraph<>();
    }

    @Override
    public E getEdge(V v1, V v2) {
        int length = edgeLength(v1, v2);
        return (E) new Edge<>(v1, v2, length);
    }

    @Override
    public List<V> shortestPath(V source, V sink) {
        Map<Vertex, dijkstraInfo> pathTable = dijkstra(source);

        List<V> reversedOrder = new ArrayList<>();
        Vertex currentVertex = sink;
        while (!currentVertex.equals(source)) {
            reversedOrder.add((V) currentVertex);
            currentVertex = pathTable.get(currentVertex).prevVertex;
        }
        reversedOrder.add(source);
        Collections.reverse(reversedOrder);

        return reversedOrder;
    }

    /**
     * Use Dijkstra's algorithm to compute the shortest path
     * length from a source to all other vertices
     * @param source starting vertex, must exist
     * @return a map mapping each vertex in the graph to its dijkstraInfo
     */
    private Map<Vertex, dijkstraInfo> dijkstra(Vertex source) {
        //dijkstra's algorithm
        Set<Vertex> allVertices = (Set<Vertex>) allVertices();
        List<Vertex> unvisited = new ArrayList<>();

        Map<Vertex, dijkstraInfo> table = new HashMap<>();

        for (Vertex v: allVertices) {
            unvisited.add(v);
            if (v.equals(source)) {
                table.put(v, new dijkstraInfo(null, 0));
            } else {
                table.put(v, new dijkstraInfo(null, Integer.MAX_VALUE));
            }
        }

        Vertex currentVertex = source;
        while (!unvisited.isEmpty()) {
            Map<Vertex, Edge<Vertex>> neighbours = graph.getNeighbours(currentVertex);
            for (Vertex vertex: neighbours.keySet()) {
                if (!unvisited.contains(vertex)) {
                    continue;
                }
                int currentDistance = neighbours.get(vertex).length();
                if (currentDistance + table.get(currentVertex).pathLength < table.get(vertex).pathLength) {
                    table.get(vertex).pathLength = currentDistance + table.get(currentVertex).pathLength;
                    table.get(vertex).prevVertex = currentVertex;
                }
            }
            unvisited.remove(currentVertex);
            Vertex leastDistanceVertex = null;
            int leastDistance = Integer.MAX_VALUE;
            for (Vertex vertex: unvisited) {
                if (table.get(vertex).pathLength < leastDistance) {
                    leastDistance = table.get(vertex).pathLength;
                    leastDistanceVertex = vertex;
                }
            }
            currentVertex = leastDistanceVertex;
        }
        return table;
    }

    class dijkstraInfo {
        Vertex prevVertex;
        int pathLength;
        public dijkstraInfo (Vertex prevVertex, int pathLength) {
            this.prevVertex = prevVertex;
            this.pathLength = pathLength;
        }
    }

    @Override
    public int pathLength(List<V> path) {
        int sum = 0;
        //don't iterate onto the last item
        for (int i = 0; i < path.size() - 1; i++) {
            sum += getEdge(path.get(i), path.get(i+1)).length();
        }
        return sum;
    }

    @Override
    public Map<V, E> getNeighbours(V v, int range) {
        Map<V, E> neighbours = new HashMap<>();
        for (Vertex vertex: allVertices()) {
            List<V> shortestPath = shortestPath(v, (V) vertex);
            if (pathLength(shortestPath) <= range && !vertex.equals(v)) {
                neighbours.put((V) vertex,
                        getEdge(shortestPath.get(shortestPath.size() - 2), shortestPath.get(shortestPath.size() - 1)));
            }
        }
        return neighbours;
    }

    @Override
    public Set<ImGraph<V, E>> minimumSpanningComponents(int k) {
        //use kruskal's algorithm
        List<Edge> edgeList = new ArrayList<>(allEdges());
        edgeList.sort(Comparator.comparingInt(Edge::length));

        Set<ImGraph<V, E>> graphSet = new HashSet<>();

        int numVertices = 0;
        Graph<V, E> curGraph = new Graph<>();
        for (Vertex vertex: allVertices()) {
            curGraph.addVertex((V) vertex);
            numVertices++;
        }

        int numEdges = 0;
        for (Edge edge: edgeList) {
            curGraph.addEdge((E) edge);
            numEdges++;
            if (curGraph.hasCycle(edge.v1())) {
                curGraph.remove((E) edge);
                numEdges--;
            }
            graphSet = connectedComponents(curGraph);
            if ((numEdges == numVertices - 1) || (connectedComponents(curGraph).size() == k)) {
                break;
            }
        }

        return graphSet;
    }

    /**
     * Detects if there is a cycle in the graph containing the vertex start
     * @param start the vertex to test for cycles
     * @return true if there is a cycle involving vertex start, false otherwise
     */
    public boolean hasCycle(Vertex start) {
        Map<Vertex, Boolean> visited = new HashMap<>();
        for (Vertex vertex: allVertices()) {
            if (vertex.equals(start)) {
                visited.put(vertex, true);
            } else {
                visited.put(vertex, false);
            }
        }
        for (Vertex vertex: getNeighbours((V) start).keySet()) {
            if (hasCycleUtil(vertex, visited, start)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function for recursion in hasCycle
     * @param vertex current vertex being visited
     * @param visited the list of vertex already visited
     * @param start start vertex
     * @return true if a cycle with start has been found, false otherwise
     */
    private boolean hasCycleUtil(Vertex vertex, Map<Vertex, Boolean> visited, Vertex start){
        for (Vertex v: getNeighbours((V) vertex).keySet()) {
            if (visited.get(v) == true) {
                continue;
            }
            if (getNeighbours((V) v).keySet().contains(start)) {
                return true;
            }
            visited.put(vertex, true);
            if (hasCycleUtil(v, visited, start)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all connected subgraphs from the original possibly disconnected graph
     * @param original the graph to find subgraphs in
     * @return a set of all connected subgraphs
     */
    public Set<ImGraph<V, E>> connectedComponents(Graph<V, E> original) {
        Set<ImGraph<V, E>> graphs = new HashSet<>();
        Map<Vertex, Boolean> visited = new HashMap<>();

        for (Vertex v: original.allVertices()) {
            visited.put(v, false);
        }

        for (Vertex v: original.allVertices()) {
            if (!visited.get(v)) {
                Graph<V, E> connected = new Graph<>();
                graphs.add(connectedUtil(v, visited, connected, original));
            }
        }

        return graphs;
    }

    /**
     * Helper function for recursion in connectedComponents
     * @param v current vertex being visited
     * @param visited map of vertices already visited
     * @param connected subgraph being appended to
     * @param original original graph
     * @return connected subgraph in original
     */
    private Graph<V, E> connectedUtil(Vertex v, Map<Vertex, Boolean> visited, Graph<V, E> connected, Graph<V, E> original) {
        visited.put(v, true);
        connected.addVertex((V) v);
        for (Vertex vertex: original.getNeighbours((V) v).keySet()) {
            connected.addVertex((V) vertex);
            connected.addEdge(original.getNeighbours((V) v).get(vertex));
        }

        for (Vertex vertex: original.getNeighbours((V) v).keySet()) {
            if (!visited.get(vertex)) {
                connectedUtil(vertex, visited, connected, original);
            }
        }
        return connected;
    }

    /**
     * Computes the eccentricity of a vertex in the graph
     * @param vertex the vertex to compute eccentricity for
     * @return the eccentricity of vertex
     */
    private int eccentricity(Vertex vertex) {
        int eccentricity = 0;
        Map<Vertex, dijkstraInfo> table = dijkstra(vertex);
        for (dijkstraInfo info: table.values()) {
            eccentricity = Math.max(info.pathLength, eccentricity);
        }
        return eccentricity;
    }

    @Override
    public int diameter() {
        Graph<V, E> biggestGraph = getBiggestSubgraph();

        int diameter = 0;
        for (Vertex vertex: biggestGraph.allVertices()) {
            diameter = Math.max(biggestGraph.eccentricity(vertex), diameter); //TODO: do this math.max thing more often
        }
        return diameter;
    }

    @Override
    public V getCenter() {
        Graph<V, E> biggestGraph = getBiggestSubgraph();

        int minEccentricity = Integer.MAX_VALUE;
        V bestGirl = null;
        for (Vertex vertex: biggestGraph.allVertices()) {
            if (biggestGraph.eccentricity(vertex) < minEccentricity) {
                minEccentricity = biggestGraph.eccentricity(vertex);
                bestGirl = (V) vertex;
            }
        }
        return bestGirl;
    }

    /**
     * @return The biggest connected subgraph within the graph
     */
    private Graph<V, E> getBiggestSubgraph() {
        Graph<V, E> biggestGraph = this;
        int biggestSize = 0;
        for (ImGraph<V, E> graph: connectedComponents(this)) {
            Graph<V, E> imgraph = (Graph) graph;
            int curSize = imgraph.allVertices().size();
            if (curSize > biggestSize) {
                biggestGraph = imgraph;
                biggestSize = curSize;
            }
        }
        return biggestGraph;
    }

    @Override
    public boolean addVertex(V v) {
        return graph.addVertex(v);
    }

    @Override
    public boolean vertex(V v) {
        return graph.vertex(v);
    }

    @Override
    public boolean addEdge(E e) {
        return graph.addEdge((Edge) e);
    }

    @Override
    public boolean edge(E e) {
        return graph.edge((Edge) e);
    }

    @Override
    public boolean edge(V v1, V v2) {
        return graph.edge(v1, v2);
    }

    @Override
    public int edgeLength(V v1, V v2) {
        return graph.edgeLength(v1, v2);
    }

    @Override
    public int edgeLengthSum() {
        return graph.edgeLengthSum();
    }

    @Override
    public boolean remove(E e) {
        return graph.remove((Edge) e);
    }

    @Override
    public boolean remove(V v) {
        return graph.remove(v);
    }

    @Override
    public Set<V> allVertices() {
        return (Set<V>) graph.allVertices();
    }

    @Override
    public Set<E> allEdges(V v) {
        return (Set<E>) graph.allEdges(v);
    }

    @Override
    public Set<E> allEdges() {
        return (Set<E>) graph.allEdges();
    }

    @Override
    public Map<V, E> getNeighbours(V v) {
        return (Map<V, E>) graph.getNeighbours(v);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Graph<?, ?>) {
            Graph<?, ?> other = (Graph<?, ?>) o;
            if (other.allVertices().equals(allVertices()) && other.allEdges().equals(allEdges())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return allVertices().hashCode();
    }

    //// add all new code above this line ////

    /**
     * This method removes some edges at random while preserving connectivity
     * <p>
     * DO NOT CHANGE THIS METHOD
     * </p>
     * <p>
     * You will need to implement allVertices() and allEdges(V v) for this
     * method to run correctly
     *</p>
     * <p><strong>requires:</strong> this graph is connected</p>
     *
     * @param rng random number generator to select edges at random
     */
    public void pruneRandomEdges(Random rng) {
        class VEPair {
            V v;
            E e;

            public VEPair(V v, E e) {
                this.v = v;
                this.e = e;
            }
        }
        /* Visited Nodes */
        Set<V> visited = new HashSet<>();
        /* Nodes to visit and the cpen221.mp2.graph.Edge used to reach them */
        Deque<VEPair> stack = new LinkedList<VEPair>();
        /* Edges that could be removed */
        ArrayList<E> candidates = new ArrayList<>();
        /* Edges that must be kept to maintain connectivity */
        Set<E> keep = new HashSet<>();

        V start = null;
        for (V v : this.allVertices()) {
            start = v;
            break;
        }
        if (start == null) {
            // nothing to do
            return;
        }
        stack.push(new VEPair(start, null));
        while (!stack.isEmpty()) {
            VEPair pair = stack.pop();
            if (visited.add(pair.v)) {
                keep.add(pair.e);
                for (E e : this.allEdges(pair.v)) {
                    stack.push(new VEPair(e.distinctVertex(pair.v), e));
                }
            } else if (!keep.contains(pair.e)) {
                candidates.add(pair.e);
            }
        }
        // randomly trim some candidate edges
        int iterations = rng.nextInt(candidates.size());
        for (int count = 0; count < iterations; ++count) {
            int end = candidates.size() - 1;
            int index = rng.nextInt(candidates.size());
            E trim = candidates.get(index);
            candidates.set(index, candidates.get(end));
            candidates.remove(end);
            remove(trim);
        }
    }
}
