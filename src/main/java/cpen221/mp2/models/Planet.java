package cpen221.mp2.models;

import cpen221.mp2.graph.Vertex;
import cpen221.mp2.util.Util;

/**
 * A Planet (vertex) represents a Planet. Each Planet maintains:
 * 1. a set of edges that exit it,
 * 2. a modifier that affects the ship's speed upon visiting it,
 * 3. the amount of spice on the planet and the rate at which they are lost.
 */
public class Planet extends Vertex implements GameElement {
    int spice; // Current amount of spice on this planet; always >= 0
    private int x, y; // Logical x, y coordinates of this Planet

    /**
     * Constructor: a Planet named name with ID id with no edges
     * leaving it, the given x and y coordinates, speed modifier sm, initial
     * amount of spice g, and loss rate lr.
     */
    private Planet(int x, int y, String name, int id, int spice) {
        super(id, name);
        this.x = x;
        this.y = y;
        this.spice = spice;
    }

    @Override
    public Planet clone() {
        return new Planet(this.x, this.y, super.name(), super.id(), this.spice);
    }

    /**
     * Return the current amount of spice on this planet.
     *
     * @return the current amount of spice on this planet.
     */
    public int spice() {
        return spice;
    }

    /**
     * Return the current number of spice on this planet and set
     * the number of spice to 0.<br/><br/>
     * <p><strong>modifies clause:</strong> reduces the amount of spice on the planet.</p>
     *
     * @return the amount of spice taken from this planet.
     */
    int takeSpice() {
        int ret = spice;
        spice = 0;
        return ret;
    }

    /**
     * Return true iff ob and this point to the same Planet, or
     * if ob is a Planet with the same ID as this Planet.
     * Precondition: all Planets have unique IDs.
     */
    @Override
    public boolean equals(Object ob) {
        if (ob == this) {
            return true;
        }
        if (ob == null || getClass() != ob.getClass()) {
            return false;
        }
        return super.equals(ob);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Return a String containing the name and coordinates of this Planet.
     *
     * @return a String containing the name and coordinates of this Planet.
     */
    @Override
    public String toString() {
        return String.format("%s: (%s, %s)", super.name(), x, y);
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    /**
     * An instance builds a Planet with the appropriate parameters.
     * It will refuse to build if a parameter is unset.
     */
    static class PlanetBuilder {
        /* The parameters needed to build a Planet */
        private Integer x, y;
        private String name;
        private Integer id;
        private Integer spice;

        /**
         * Set the position (x, y) of this Planet and return the node.
         */
        public PlanetBuilder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Set the name of this Planet to name and return the node.
         */
        public PlanetBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the id to id, a unique identifier for this Planet and
         * return the node.
         */
        public PlanetBuilder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Set the initial number of spice on this Planet to 'spice' and
         * return this Planet.
         */
        public PlanetBuilder spice(int spice) {
            this.spice = spice;
            return this;
        }

        /**
         * Build this Planet.
         * Precondition: all appropriate parameters have been set.
         */
        public Planet build() {
            if (Util.anyNull(x, y, name, id, spice)) {
                throw new IllegalStateException("unset PlanetBuilder params");
            }

            return new Planet(x, y, name, id, spice);
        }
    }
}
