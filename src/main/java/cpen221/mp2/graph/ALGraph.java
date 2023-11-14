package cpen221.mp2.graph;

import java.util.*;

public class ALGraph<V extends Vertex, E extends Edge<V>> implements MGraph<V, E> {

    private Map<Vertex, Map<Vertex, Edge>> mapVertices; //TODO: should this be public??
    public ALGraph(){
        mapVertices = new HashMap<>();
    }

    /**
     * Creates a new index in the nested mapVertices map.
     *
     * @param v vertex to add
     * @return true if added, false if already inside/ could not do it
     */
    public boolean addVertex(V v) {
        if(vertex(v) || hasID(v)){
            return false;
        }
        mapVertices.put(v, new HashMap<>());
        return true;
    }
    /**
     * Checks if the vertex we want to add already exists within the map.
     *
     * @param v vertex to add
     * @return true if vertex already exists within the map, false otherwise
     */
    private boolean hasID(Vertex v) {
        for (Vertex vertex: mapVertices.keySet()) {
            if (vertex.id() == v.id()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the id of the vertex is within the indices of the map.
     *
     * @param v vertex to check in the graph
     * @return true if present, false otherwise
     */
    public boolean vertex(V v) {
        return mapVertices.containsKey(v);
    }

    /**
     * Adds an edge to the map of vertices.
     *
     * @param e the edge to add to the graph
     * @return true if the addition was successful, false otherwise
     */
    public boolean addEdge(E e) {

        if(edge(e) || !vertex(e.v1()) || !vertex(e.v2())){
            return false;
        }

        //on the index of the v1 vertex, place the v2 vertex with the edge value
        mapVertices.get(e.v1()).put(e.v2(),e);
        //on the index of the v2 vertex, place the v1 vertex with the edge value
        mapVertices.get(e.v2()).put(e.v1(),e);
        return true;
    }

    /**
     * Calls a function to check if an edge exists in the graph.
     *
     * @param e the edge to check in the graph
     * @return true if the edge exists, false otherwise
     */

    public boolean edge(E e) {
        return edge(e.v1(), e.v2());
    }

    /**
     * Check if two vertices have a connection.
     *
     * @param v1 the first vertex of the edge
     * @param v2 the second vertex of the edge
     * @return whether there is a connection between these two vertices
     */
    public boolean edge(V v1, V v2) {
        return mapVertices.get(v1).containsKey(v2);
        //TODO: fails if the value is null
    }

    /**
     * Returns the length of the edge between two vertices if the connection exists.
     *
     * @param v1 the first vertex of the edge
     * @param v2 the second vertex of the edge
     * @return the length of the edge
     * @throws IllegalArgumentException if the edge does not exist.
     */
    public int edgeLength(V v1, V v2) {
        Edge edge = mapVertices.get(v1).get(v2);
        if(edge != null){
            return edge.length();
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Calculate the sum of the lengths of all edges in the map.
     *
     * @return the sum of the lengths of all edges in the map
     */
    public int edgeLengthSum() {
        int sumTwice = 0;
        for (Vertex vertex: mapVertices.keySet()) {
            Map<Vertex, Edge> connectionsMap = mapVertices.get(vertex);
            for (Vertex v: connectionsMap.keySet()) {
                sumTwice += connectionsMap.get(v).length();
            }
        }
        return sumTwice / 2;
    }

    /**
     * Remove an edge from the map.
     *
     * @param e the edge to remove
     * @return true if e was successfully removed, false otherwise
     */
    public boolean remove(E e) {
        if (!edge(e)) {
            return false;
        } else {
            mapVertices.get(e.v1()).remove(e.v2());
            mapVertices.get(e.v2()).remove(e.v1());
            return true;
        }
    }

    /**
     * Remove a vertex from the map.
     *
     * @param v the vertex to remove
     * @return true if v was successfully removed, false otherwise
     */
    public boolean remove(V v) {
        if (!vertex(v)) {
            return false;
        } else {
            mapVertices.remove(v);
            for (Vertex vertex: mapVertices.keySet()) {
                mapVertices.get(vertex).remove(v);
            }
            return true;
        }
    }

    /**
     * Obtain a set of all vertices in the map.
     *
     * @return a set of all vertices in the map
     */
    public Set<V> allVertices() {
        Set<V> allVertices = new HashSet<>();
        for(Vertex vertex: mapVertices.keySet()){
            allVertices.add((V)vertex.clone());
        }
        return allVertices;
    }

    /**
     * Obtain a set of all edges connected to v.
     *
     * @param v the vertex of interest
     * @return all edges connected to v
     */
    public Set<E> allEdges(V v) {
        Set<E> set = new HashSet<>();
        for (Edge edge: mapVertices.get(v).values()) {
            set.add((E) edge.clone());
        }
        return set;
    }

    /**
     * Obtain a set of all edges in the list.
     *
     * @return all edges in the list
     */
    public Set<E> allEdges() {
        Set<E> allEdges = new HashSet<>();
        for (Vertex vertex: mapVertices.keySet()) {
            for (Edge edge: mapVertices.get(vertex).values()) {
                allEdges.add((E) edge.clone());
            }
        }
        return allEdges;
    }

    /**
     * Obtain all the neighbours of vertex v.
     *
     * @param v is the vertex of interest, v has to exist within the graph
     * @return a map containing each vertex w that neighbors v and the edge
     * between v and w, null if v does not exist within the list
     */
    public Map<V, E> getNeighbours(V v) {
        Map<V, E> neighbours = new HashMap<>();
        for (Vertex vertex: mapVertices.get(v).keySet()) {
            neighbours.put((V) vertex.clone(), (E) mapVertices.get(v).get(vertex).clone());
        }
        return neighbours;
    }
}
