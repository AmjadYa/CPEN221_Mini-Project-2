package cpen221.mp2;

import cpen221.mp2.graph.*;
import org.junit.jupiter.api.Test;
import cpen221.mp2.graph.Edge;
import cpen221.mp2.graph.Vertex;

import static org.junit.jupiter.api.Assertions.*;

public class Task2Smokes {

    @Test
    public void testEdgeExists1() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);

        MGraph<Vertex, Edge<Vertex>> g = new ALGraph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addEdge(e1);

        g.remove(e1);

        assertFalse(g.edge(e1));
    }

    @Test
    public void testVertexExists1() {
        Vertex v1 = new Vertex(1, "A");

        MGraph<Vertex, Edge<Vertex>> g = new ALGraph<>();
        g.addVertex(v1);
        g.remove(v1);

        assertFalse(g.vertex(v1));
    }

}
