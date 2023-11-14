package cpen221.mp2.models;

import cpen221.mp2.graph.Graph;
import cpen221.mp2.graph.ImGraph;
import cpen221.mp2.initialization.DelaunayTriangulation;
import cpen221.mp2.initialization.UEdge;
import cpen221.mp2.util.Util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * A Universe represents the physical layout of a game: it tracks Planets,
 * Edges (Links), and size of the map. Universes are randomly generated from a seed.
 */
public class Universe {

    /* Location of files for initializing a game instance */
    private static final String BOARD_GENERATION_DIRECTORY = Util.DIRECTORY
            + "/data/board_generation";
    /* The seed given to a RNG to generate this Board. */
    private final long seed;
    /* The "Earth" Node. The spaceship starts and must return here. */
    private Planet earth;
    /* The target Node that must be reached during the rescue stage. */
    private Planet target;
    /* The distance of the Node furthest from the target. */
    private double furthestNodeDistance;
    /* The minimum traveled distance from Earth to the target. */
    private int distanceToTarget;
    /* Represents the planetary universe as a graph */
    private Graph<Planet, Link> planetGraph;
    /* The proximity grid of all Planets on this Board. */
    private ProximityGrid planetGrid;
    /* The dimensions of the game viewing area, which is a rectangle. */
    private int width;
    private int height;

    /**
     * Constructor: a rectangular game board generated via RNG using a seed.
     * There are many game initialization parameters here explained in the Builder.
     *
     * @param w          is the width of the game viewing area.
     * @param h          is the height of the game viewing area.
     * @param seed       initializes the random number generator.
     * @param minPlanets is the minimum number of planets in a game instance. minPlanets >= 0.
     * @param maxPlanets is the maximum number of planets in a game instance. maxPlanets >= minPlanets.
     * @param minSpice   is the minimum spice level on a planet. minSpice >= 0.
     * @param maxSpice   is the maximum spice level on a planet. maxSpice >= minSpice.
     */
    private Universe(int w, int h, long seed, int minPlanets, int maxPlanets, int minSpice,
                     int maxSpice) {
        width = w;
        height = h;
        this.seed = seed;
        Random r = new Random(seed);
        int np = r.nextInt(maxPlanets - minPlanets + 1) + minPlanets;
        DelaunayTriangulation dt = new DelaunayTriangulation(np, r, w, h);

        // convert Points to Planets, mapping each Point to its corresponding Node
        HashMap<Point, Planet> pToN = new HashMap<Point, Planet>();
        planetGrid = new ProximityGrid(0, 0, w, h);
        planetGraph = new Graph<Planet, Link>();
        Queue<String> names = planetNames(r); // shuffled list of planet names
        int id = 0; // id of each planet
        int kaminoId = r.nextInt(dt.getVertices().size() - 1) + 1;

        for (Point location : dt.getVertices()) {
            String name;
            int spice;
            if (id == 0) {
                name = Util.EARTH_NAME;
                spice = 0;
            } else {
                if (id == kaminoId) {
                    name = Util.CRASHED_PLANET_NAME;
                    spice = 0;
                } else {
                    name = names.peek();
                    names.remove();
                    spice = spice(r, minSpice, maxSpice);
                }
            }

            Planet pl = new Planet.PlanetBuilder()
                    .pos(location.x, location.y)
                    .name(name)
                    .id(id)
                    .spice(spice)
                    .build();

            if (id == 0) {
                earth = pl;
            } else {
                if (id == kaminoId) {
                    target = pl;
                }
            }

            ++id;
            planetGrid.addPlanet(pl);
            planetGraph.addVertex(pl);
            pToN.put(location, pl);
        }

        // add the edges
        for (UEdge ue : dt.getEdges()) {
            Planet n1 = pToN.get(ue.p1());
            Planet n2 = pToN.get(ue.p2());
            Link e = Link.createLink(n1, n2);
            planetGraph.addEdge(e);
        }

        // remove an arbitrary amount of edges, while keeping connectivity
        planetGraph.pruneRandomEdges(r);

        // set the furthest distance (needed for getPing)
        double maxDistance = 0;
        for (Planet planet : planetGrid) {
            double nodeDistance = absoluteDistanceToTarget(planet);
            if (nodeDistance > maxDistance) {
                maxDistance = nodeDistance;
            }
        }
        furthestNodeDistance = maxDistance;
        distanceToTarget = planetGraph.pathLength(planetGraph.shortestPath(earth, target));
    }

    /**
     * Return the planet names listed in planets.txt, shuffled to a
     * random order using RNG r.
     * <p>
     * Precondition: planets.txt is in BOARD_GENERATION_DIRECTORY.
     * </p>
     *
     * @param r is a random number generator.
     */
    private static Queue<String> planetNames(Random r) {
        File f = new File(BOARD_GENERATION_DIRECTORY + "/planets.txt");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(f));
        }
        catch (FileNotFoundException e) {
            throw new UncheckedIOException("Cannot find planets.txt", e);
        }
        LinkedList<String> names = new LinkedList<String>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                // Strip non-ascii or null characters out of string
                line = line.replaceAll("[\uFEFF-\uFFFF \u0000]", "");
                names.add(line);
            }
            reader.close();
        }
        catch (IOException e) {
            throw new UncheckedIOException("Error reading planets.txt", e);
        }
        Collections.shuffle(names, r);
        return names;
    }

    /**
     * Return a random amount of gems set by the constraints min and max.
     * The number tends to favour lower values.
     *
     * @param r   is a random number generator.
     * @param min is the minimum amount of spice to generate.
     * @param max is the maximum amount of spice to generate.
     */
    private static int spice(Random r, int min, int max) {
        double weight = r.nextDouble();
        weight *= weight; // quadratic distribution; mean weight is 1/3
        return (int) (weight * (max - min + 1)) + min;
    }

    /**
     * Return the seed used to generate this game Universe.
     *
     * @return the seed used to generate this game Universe.
     */
    public long seed() {
        return seed;
    }

    /**
     * Return the signal strength from the target planet at planet pl. This is
     * inversely correlated with the distance between pl and the target planet.
     * <p>
     * The returned value d satisfies 0 <= d <= 1. If d = 1, n is the target
     * node. If d = 0, n is the node furthest from the target node.
     *
     * @return the signal strength from the target planet at planet pl.
     */
    public double signal(Planet pl) {
        return 1.0 - absoluteDistanceToTarget(pl) / furthestNodeDistance;
    }

    /**
     * Return the absolute distance from pl to the target.
     *
     * @return the absolute distance from pl to the target.
     */
    private double absoluteDistanceToTarget(Planet pl) {
        return Util.distance(pl.x(), pl.y(), target.x(), target.y());
    }

    /**
     * Return an Iterable containing all the Planets in this Universe.
     * Do NOT modify this Iterable or its elements in any way.
     *
     * @return an Iterable containing all the Planets in this Universe.
     */
    public Iterable<Planet> planets() {
        return planetGrid;
    }

    /**
     * Return the closest planet to the given Point, or null if
     * there are no Planets.
     *
     * @return the closest planet to the given Point.
     */
    public Planet closestPlanet(Point2D p) {
        return planetGrid.closestPlanet(p);
    }

    /**
     * Return the planet with ID id in this Universe if it exists,
     * null otherwise.
     *
     * @return the planet with ID id in this Universe if it exists.
     */
    public Planet getNode(int id) {
        for (Planet n : planetGrid) {
            if (n.id() == id) {
                return n;
            }
        }

        return null;
    }

    /**
     * Return the starting planet (Earth).
     */
    public Planet earth() {
        return earth;
    }

    /**
     * Return the unique planet that the spaceship is trying to find.
     */
    public Planet target() {
        return target;
    }

    /**
     * Return an unmodifiable Set of Edges in this Universe.
     */
    public Set<Link> links() {
        return Collections.unmodifiableSet(planetGraph.allEdges());
    }

    /**
     * Return the distance from the target planet to the planet furthest from it.
     *
     * @return the distance from the target planet to the planet furthest from it.
     */
    public double furthestNodeDistance() {
        return furthestNodeDistance;
    }

    /**
     * Return the width of this game board.
     *
     * @return the width of this game board.
     */
    public int width() {
        return width;
    }

    /**
     * Return the height of this game board.
     *
     * @return the height of this game board.
     */
    public int height() {
        return height;
    }

    public int sumLinkLengths() {
        return planetGraph.edgeLengthSum();
    }

    public Set<Planet> neighbors(Planet planet) {
        if (planetGraph.vertex(planet)) {
            return planetGraph.getNeighbours(planet).keySet();
        } else {
            throw new NoSuchElementException(planet + " is not part of this universe");
        }
    }

    public List<Planet> shortestPath(Planet source, Planet sink) {
        return planetGraph.shortestPath(source, sink);
    }

    public boolean connected(Planet pl1, Planet pl2) {
        return planetGraph.edge(pl1, pl2);
    }

    /**
     * Return the min traveled distance between the target and Earth.
     */
    public int distanceToTarget() {
        return distanceToTarget;
    }

    public ImGraph<Planet, Link> planetGraph() {
        return planetGraph;
    }

    /**
     * An instance builds a universe with the appropriate parameters.
     * It will refuse to build if a parameter is unset.
     */
    public static class UniverseBuilder {

        /* The parameters needed to build a Universe */
        private Integer width, height;
        private Long seed;
        private Integer minPlanets, maxPlanets;
        private Integer minSpice, maxSpice;

        /**
         * Set the dimensions (width x height) of this  Board.
         */
        public UniverseBuilder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Set the seed of the RNG used to generate this Board.
         */
        public UniverseBuilder seed(long seed) {
            this.seed = seed;
            return this;
        }

        /**
         * Set the min and max number of Planets in this Universe.
         */
        public UniverseBuilder planetBounds(int min, int max) {
            minPlanets = min;
            maxPlanets = max;
            return this;
        }

        /**
         * Set the min and max amount of spice per planet in this Universe.
         */
        public UniverseBuilder spiceBounds(int min, int max) {
            minSpice = min;
            maxSpice = max;
            return this;
        }

        /**
         * Build this Universe.
         * Precondition: all appropriate parameters have been set.
         */
        public Universe build() {
            if (Util.anyNull(width, height, seed, minPlanets, maxPlanets, minSpice,
                    maxSpice)) {
                throw new IllegalStateException("unset UniverseBuilder params");
            }

            return new Universe(width, height, seed, minPlanets, maxPlanets, minSpice,
                    maxSpice);
        }
    }
}
