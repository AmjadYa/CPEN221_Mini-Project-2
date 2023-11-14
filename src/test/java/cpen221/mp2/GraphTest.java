package cpen221.mp2;

import cpen221.mp2.graph.Edge;
import cpen221.mp2.graph.Graph;
import cpen221.mp2.graph.ImGraph;
import cpen221.mp2.graph.Vertex;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTest {

    @Test
    public void testCreateGraph() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 9);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);

        assertEquals(e2, g.getEdge(v2, v3));
        assertEquals(21, g.pathLength(g.shortestPath(v3, v4)));
    }

    @Test
    public void testMultiplePaths() {
        //https://www.youtube.com/watch?v=pVfj6mxhdMw
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 6);
        Edge<Vertex> e2 = new Edge<>(v1, v4, 1);
        Edge<Vertex> e3 = new Edge<>(v2, v3, 5);
        Edge<Vertex> e4 = new Edge<>(v2, v4, 2);
        Edge<Vertex> e5 = new Edge<>(v2, v5, 2);
        Edge<Vertex> e6 = new Edge<>(v3, v5, 5);
        Edge<Vertex> e7 = new Edge<>(v4, v5, 1);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);

        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);

        assertEquals(0, g.pathLength(g.shortestPath(v1, v1)));
        assertEquals(3, g.pathLength(g.shortestPath(v1, v2)));
        assertEquals(7, g.pathLength(g.shortestPath(v1, v3)));
        assertEquals(1, g.pathLength(g.shortestPath(v1, v4)));
        assertEquals(2, g.pathLength(g.shortestPath(v1, v5)));

        assertEquals(Map.of(v4, e2), g.getNeighbours(v1, 1));
        assertEquals(Map.of(v2, e4, v4, e2, v5, e7), g.getNeighbours(v1, 3));
    }

    @Test
    public void testComplicatedPaths() {
        //https://www.youtube.com/watch?v=5GT5hYzjNoo
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");
        Vertex v7 = new Vertex(7, "G");
        Vertex v8 = new Vertex(8, "H");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 8);
        Edge<Vertex> e2 = new Edge<>(v1, v3, 2);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 5);
        Edge<Vertex> e4 = new Edge<>(v2, v4, 2);
        Edge<Vertex> e5 = new Edge<>(v2, v6, 13);
        Edge<Vertex> e6 = new Edge<>(v3, v4, 2);
        Edge<Vertex> e7 = new Edge<>(v3, v5, 5);
        Edge<Vertex> e8 = new Edge<>(v4, v5, 1);
        Edge<Vertex> e9 = new Edge<>(v4, v6, 6);
        Edge<Vertex> e10 = new Edge<>(v4, v7, 3);
        Edge<Vertex> e11 = new Edge<>(v5, v7, 1);
        Edge<Vertex> e12 = new Edge<>(v6, v7, 2);
        Edge<Vertex> e13 = new Edge<>(v6, v8, 3);
        Edge<Vertex> e14 = new Edge<>(v7, v8, 6);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addVertex(v7);
        g.addVertex(v8);

        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);
        g.addEdge(e8);
        g.addEdge(e9);
        g.addEdge(e10);
        g.addEdge(e11);
        g.addEdge(e12);
        g.addEdge(e13);
        g.addEdge(e14);

        assertEquals(0, g.pathLength(g.shortestPath(v1, v1)));
        assertEquals(6, g.pathLength(g.shortestPath(v1, v2)));
        assertEquals(2, g.pathLength(g.shortestPath(v1, v3)));
        assertEquals(4, g.pathLength(g.shortestPath(v1, v4)));
        assertEquals(5, g.pathLength(g.shortestPath(v1, v5)));
        assertEquals(8, g.pathLength(g.shortestPath(v1, v6)));
        assertEquals(6, g.pathLength(g.shortestPath(v1, v7)));
        assertEquals(11, g.pathLength(g.shortestPath(v1, v8)));

        assertEquals(Map.of(v3, e2, v4, e6, v5, e8), g.getNeighbours(v1, 5));
        assertEquals(Map.of(v2, e4, v3, e2, v4, e6, v5, e8, v6, e12, v7, e11, v8, e13), g.getNeighbours(v1, 12));
    }

    @Test
    public void testSmallDiameter() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);
        Edge<Vertex> e2 = new Edge<>(v1, v3, 2);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 9);
        Edge<Vertex> e4 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e5 = new Edge<>(v3, v4, 4);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);

        assertEquals(11, g.diameter());
        assertEquals(v1, g.getCenter());
    }

    @Test
    public void testDisconnectedDiameter() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);
        Edge<Vertex> e2 = new Edge<>(v1, v3, 2);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 9);
        Edge<Vertex> e4 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e5 = new Edge<>(v3, v4, 4);
        Edge<Vertex> e6 = new Edge<>(v5, v6, 14);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);

        assertEquals(11, g.diameter());
        assertEquals(v1, g.getCenter());
    }

    @Test
    public void testNoCycle() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 9);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);

        assertEquals(g.hasCycle(v1), false);
    }

    @Test
    public void testCycle() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 9);
        Edge<Vertex> e4 = new Edge<>(v3, v4, 2);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);

        assertEquals(g.hasCycle(v1), true);
    }

    @Test
    public void testMultipleBranchesCycle() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 9);
        Edge<Vertex> e4 = new Edge<>(v1, v3, 1);
        Edge<Vertex> e5 = new Edge<>(v1, v5, 6);
        Edge<Vertex> e6 = new Edge<>(v5, v6, 4);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);

        assertEquals(g.hasCycle(v1), true);
        assertEquals(g.hasCycle(v2), true);
        assertEquals(g.hasCycle(v3), true);
        assertEquals(g.hasCycle(v4), false);
        assertEquals(g.hasCycle(v5), false);
        assertEquals(g.hasCycle(v6), false);
    }

    @Test
    public void mst1() {
        //https://www.youtube.com/watch?v=ivcbaIhrcsE
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");
        Vertex v7 = new Vertex(7, "G");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 2);
        Edge<Vertex> e2 = new Edge<>(v1, v4, 4);
        Edge<Vertex> e3 = new Edge<>(v1, v6, 5);
        Edge<Vertex> e4 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e5 = new Edge<>(v2, v4, 1);
        Edge<Vertex> e6 = new Edge<>(v2, v5, 3);
        Edge<Vertex> e7 = new Edge<>(v2, v6, 8);
        Edge<Vertex> e8 = new Edge<>(v2, v7, 4);
        Edge<Vertex> e9 = new Edge<>(v3, v5, 10);
        Edge<Vertex> e10 = new Edge<>(v3, v7, 6);
        Edge<Vertex> e11 = new Edge<>(v4, v5, 2);
        Edge<Vertex> e12 = new Edge<>(v6, v7, 1);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addVertex(v7);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);
        g.addEdge(e8);
        g.addEdge(e9);
        g.addEdge(e10);
        g.addEdge(e11);
        g.addEdge(e12);

        Set<ImGraph<Vertex, Edge<Vertex>>> expected = new HashSet<>();
        Graph<Vertex, Edge<Vertex>> mst = new Graph<>();
        mst.addVertex(v1);
        mst.addVertex(v2);
        mst.addVertex(v3);
        mst.addVertex(v4);
        mst.addVertex(v5);
        mst.addVertex(v6);
        mst.addVertex(v7);
        mst.addEdge(e1);
        mst.addEdge(e5);
        mst.addEdge(e8);
        mst.addEdge(e10);
        mst.addEdge(e11);
        mst.addEdge(e12);

        expected.add(mst);

        Set<ImGraph<Vertex, Edge<Vertex>>> actual = g.minimumSpanningComponents(1);

        assertEquals(expected, actual);
    }

    @Test
    public void mst2() {
        //https://www.youtube.com/watch?v=ivcbaIhrcsE
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");
        Vertex v7 = new Vertex(7, "G");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 2);
        Edge<Vertex> e2 = new Edge<>(v1, v4, 4);
        Edge<Vertex> e3 = new Edge<>(v1, v6, 5);
        Edge<Vertex> e4 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e5 = new Edge<>(v2, v4, 1);
        Edge<Vertex> e6 = new Edge<>(v2, v5, 3);
        Edge<Vertex> e7 = new Edge<>(v2, v6, 8);
        Edge<Vertex> e8 = new Edge<>(v2, v7, 4);
        Edge<Vertex> e9 = new Edge<>(v3, v5, 10);
        Edge<Vertex> e10 = new Edge<>(v3, v7, 6);
        Edge<Vertex> e11 = new Edge<>(v4, v5, 2);
        Edge<Vertex> e12 = new Edge<>(v6, v7, 1);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addVertex(v7);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);
        g.addEdge(e8);
        g.addEdge(e9);
        g.addEdge(e10);
        g.addEdge(e11);
        g.addEdge(e12);

        Set<ImGraph<Vertex, Edge<Vertex>>> expected = new HashSet<>();
        Graph<Vertex, Edge<Vertex>> graph1 = new Graph<>();
        graph1.addVertex(v1);

        Graph<Vertex, Edge<Vertex>> graph2 = new Graph<>();
        graph2.addVertex(v2);
        graph2.addVertex(v4);
        graph2.addEdge(e5);

        Graph<Vertex, Edge<Vertex>> graph3 = new Graph<>();
        graph3.addVertex(v3);

        Graph<Vertex, Edge<Vertex>> graph4 = new Graph<>();
        graph4.addVertex(v5);

        Graph<Vertex, Edge<Vertex>> graph5 = new Graph<>();

        graph5.addVertex(v6);
        graph5.addVertex(v7);
        graph5.addEdge(e12);

        expected.add(graph1);
        expected.add(graph2);
        expected.add(graph3);
        expected.add(graph4);
        expected.add(graph5);

        Set<ImGraph<Vertex, Edge<Vertex>>> actual = g.minimumSpanningComponents(5);

        assertEquals(expected, actual);
    }

    @Test
    public void connectedComponents1() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 2);
        Edge<Vertex> e2 = new Edge<>(v3, v4, 4);
        Edge<Vertex> e3 = new Edge<>(v3, v5, 2);
        Edge<Vertex> e4 = new Edge<>(v4, v5, 1);
        Edge<Vertex> e5 = new Edge<>(v4, v6, 6);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);

        Graph<Vertex, Edge<Vertex>> graph1 = new Graph<>();
        graph1.addVertex(v1);
        graph1.addVertex(v2);
        graph1.addEdge(e1);

        Graph<Vertex, Edge<Vertex>> graph2 = new Graph<>();
        graph2.addVertex(v3);
        graph2.addVertex(v4);
        graph2.addVertex(v5);
        graph2.addVertex(v6);
        graph2.addEdge(e2);
        graph2.addEdge(e3);
        graph2.addEdge(e4);
        graph2.addEdge(e5);

        Set<Graph<Vertex, Edge<Vertex>>> expected = new HashSet<>();
        expected.add(graph1);
        expected.add(graph2);

        assertEquals(g.connectedComponents(g), expected);
        System.out.println("W");

    }

    @Test
    public void basicGraphStuff() {
        Vertex v1 = new Vertex(1, "A");
        Vertex v2 = new Vertex(2, "B");
        Vertex v3 = new Vertex(3, "C");
        Vertex v4 = new Vertex(4, "D");
        Vertex v5 = new Vertex(5, "E");
        Vertex v6 = new Vertex(6, "F");
        Vertex v7 = new Vertex(7, "G");

        Edge<Vertex> e1 = new Edge<>(v1, v2, 2);
        Edge<Vertex> e2 = new Edge<>(v3, v4, 4);
        Edge<Vertex> e3 = new Edge<>(v3, v5, 2);
        Edge<Vertex> e4 = new Edge<>(v4, v5, 1);
        Edge<Vertex> e5 = new Edge<>(v4, v6, 6);
        Edge<Vertex> e6 = new Edge<>(v1, v6, 6);

        Graph<Vertex, Edge<Vertex>> g = new Graph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);
        g.addVertex(v6);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);

        assertEquals(g.vertex(v1), true);
        assertEquals(g.vertex(v7), false);
        assertEquals(g.edge(e2), true);
        assertEquals(g.edge(v3, v4), true);
        assertEquals(g.edge(e6), false);
        assertEquals(g.edge(v1, v6), false);
        assertEquals(g.edgeLengthSum(), 15);

        g.remove(v3);
        assertEquals(g.vertex(v3), false);

    }
    // note we couldn't make a test for pruneEdges, random() didn't work
}
