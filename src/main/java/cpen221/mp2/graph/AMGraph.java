package cpen221.mp2.graph;

import java.util.*;

public class AMGraph<V extends Vertex, E extends Edge<V>> implements MGraph<V, E> {

    private Edge<V>[][] vertexConnections;
    private Map<Vertex, Integer> vertexToIndex;
    private int nextIndex;
    private final int maxVertices;

    //TODO: fix your broken code eric (.values() stop relying so much on keyset)

    /**
     * Create an empty graph with an upper-bound on the number of vertices
     * @param maxVertices is greater than 1
     */
    public AMGraph(int maxVertices) {
        this.maxVertices = maxVertices;
        this.nextIndex = 0;
        this.vertexConnections = new Edge[maxVertices][maxVertices];
        this.vertexToIndex = new HashMap<>();
    }

    /**
     * Assigns a new vertex an index to the array of edges.
     *
     * @param v vertex to add
     * @return true if added, false if already inside/ could not do it
     * @throws IllegalArgumentException if the size would exceed max indices in array
     */
    public boolean addVertex(V v) {
        if(vertex(v) || hasID(v)){
            return false;
        }
        if(vertexToIndex.size() + 1 > maxVertices) {
            throw new IllegalArgumentException();
        }
        vertexToIndex.put(v, nextIndex);
        nextIndex++;
        return true;
    }

    /**
     * Checks if the vertex we want to add already exists within the graph
     *
     * @param v vertex to add
     * @return true if vertex already exists within the list, false otherwise
     */
    private boolean hasID(Vertex v) {
        for (Vertex vertex: vertexToIndex.keySet()) {
            if (vertex.id() == v.id()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the vertex is within the map of indices.
     *
     * @param v vertex to check in the graph
     * @return true if present, false otherwise
     */
    public boolean vertex(V v) {
        return vertexToIndex.containsKey(v);
    }

    /**
     * Adds an edge that didn't exist previously.
     *
     * @param e the edge to add to the graph
     * @return true if successfully added, false otherwise
     */
    public boolean addEdge(E e) {
        if(edge(e) || !vertex(e.v1()) || !vertex(e.v2())){
            return false;
        }

        vertexConnections[vertexToIndex.get(e.v1())][vertexToIndex.get(e.v2())] = e;
        vertexConnections[vertexToIndex.get(e.v2())][vertexToIndex.get(e.v1())] = e;

        return true;
    }

    /**
     * Check if the edge exists within the graph.
     *
     * @param e the edge to check in the graph
     * @return true if within, false otherwise
     */
    public boolean edge(E e) {
        return edge(e.v1(), e.v2());
    }

    /**
     * Check if there is an edge between two given vertices.
     *
     * @param v1 the first vertex of the edge
     * @param v2 the second vertex of the edge
     * @return true if within, false otherwise
     */
    public boolean edge(V v1, V v2) {
        return vertexConnections[vertexToIndex.get(v1)][vertexToIndex.get(v2)] != null;
    }

    /**
     * Given two vertices, finds the distance between them.
     *
     * @param v1 the first vertex of the edge
     * @param v2 the second vertex of the edge
     * @return the length of the edge
     * @throws IllegalArgumentException if the two vertices aren't connected
     */
    public int edgeLength(V v1, V v2) {
        Edge<V> edge = vertexConnections[vertexToIndex.get(v1)][vertexToIndex.get(v2)];
        if (edge != null) {
            return edge.length();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the sum of the lengths of all edges in the graph.
     *
     * @return the sum of the lengths of all edges in the graph
     */
    public int edgeLengthSum() {
        int sum = 0;

        //traverse the upper right triangle of the matrix
        for(int i = 0; i < maxVertices; i++){
            for (int j = i + 1; j < maxVertices; j++) {
                if(vertexConnections[i][j] != null){
                    sum += vertexConnections[i][j].length();
                }

            }
        }

        return sum;
    }

    /**
     * Remove an edge from the graph.
     *
     * @param e the edge to remove
     * @return true if e was successfully removed, false otherwise
     */
    public boolean remove(E e) {
        if (!edge(e)) {
            return false;
        } else {
            vertexConnections[vertexToIndex.get(e.v1())][vertexToIndex.get(e.v2())] = null;
            vertexConnections[vertexToIndex.get(e.v2())][vertexToIndex.get(e.v1())] = null;
            return true;
        }
    }

    /**
     * Remove a vertex from the graph.
     *
     * @param v the vertex to remove
     * @return true if v was successfully removed, false otherwise
     */
    public boolean remove(V v) {
        if (!vertex(v)) {
            return false;
        } else {
            //TODO: check this
            Edge<V>[][] newMatrix = new Edge[maxVertices][maxVertices];
            int newMatrixIndex = 0;
            for (int i = 0; i < maxVertices; i++) {
                if (vertexToIndex.get(v) != i) {
                    newMatrix[newMatrixIndex] = vertexConnections[i];
                    newMatrixIndex++;
                }
            }
            nextIndex = newMatrixIndex;
            vertexToIndex.remove(v);
            return true;
        }
    }

    /**
     * Obtain a set of all vertices in the list.
     *
     * @return a set of all vertices in the list
     */
    public Set<V> allVertices() {
        Set<V> allV = new HashSet<>();
        for (Vertex v: vertexToIndex.keySet()) {
            allV.add((V) v.clone());
        }
        return allV;
    }

    /**
     * Obtain a set of all edges connected to v.
     *
     * @param v the vertex of interest
     * @return all edges connected to v
     */
    public Set<E> allEdges(V v) {
        Set<E> allEdges = new HashSet<>();
        for(Edge edge: vertexConnections[vertexToIndex.get(v)]){
            if (edge != null) {
                allEdges.add((E) edge.clone());
            }
        }
        return allEdges;
    }

    /**
     * Obtain a set of all edges in the graph.
     *
     * @return all edges in the graph
     */
    public Set<E> allEdges() {
        //traverse the upper right triangle of the matrix
        Set<E> allEdges = new HashSet<>();
        for(int i = 0; i < maxVertices; i++){
            for (int j = i + 1; j < maxVertices; j++) {
                if(vertexConnections[i][j] != null){
                    allEdges.add((E) vertexConnections[i][j].clone());
                }
            }
        }
        return allEdges;
    }

    /**
     * Obtain all the neighbours of vertex v.
     *
     * @param v is the vertex of interest, v has to exist within the graph
     * @return a map containing each vertex w that neighbors v and the edge between v and w,
     * null if v does not exist within the graph
     */
    public Map<V, E> getNeighbours(V v) {
        Map<V, E> vertexEdge = new HashMap<>();
        for (int i = 0; i < maxVertices; i++) {
            Edge edge = vertexConnections[vertexToIndex.get(v)][i];
            if (edge != null) {
                Vertex otherVertex;
                if (edge.v1().equals(v)) {
                    otherVertex = edge.v2();
                } else {
                    otherVertex = edge.v1();
                }
                vertexEdge.put((V) otherVertex.clone(), (E) edge.clone());
            }
        }
        return vertexEdge;
    }
}
