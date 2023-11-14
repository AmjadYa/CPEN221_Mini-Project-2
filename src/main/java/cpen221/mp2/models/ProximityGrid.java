package cpen221.mp2.models;

import cpen221.mp2.util.Util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An instance maintains Planets in a 2D rectangle and can return the closest Planet
 * to a given Point. It maintains an internal set of rectangles containing Planets
 * based on their coordinates.
 */
public class ProximityGrid implements Iterable<Planet> {

    /* The dimensions of an individual rectangle. */
    private static final int RECT_WIDTH = 64, RECT_HEIGHT = 64;
    /* a 2D array(list) representing rectangles that contain a list of Planets.
     * [0][0] is the bottom-left rectangle. rs will always have at least one
     * rectangle, and will always be rectangular (i.e. not ragged). */
    private ArrayList<ArrayList<List<Planet>>> rectangles;
    /* The minimum x- and y-values of this PlanetProximitySet. */
    private int x, y;

    /**
     * Create a ProximityMap spanning the axis-aligned rectangle
     * with bottom-left coordinates (x, y) and the given dimensions.
     *
     * @param x      the bottom-left x coordinate for the grid.
     * @param y      the bottom-left y coordinate for the grid.
     * @param width  the width of the grid.
     * @param height the height of the grid.
     */
    public ProximityGrid(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;

        int w = width / RECT_WIDTH + 1;
        int h = height / RECT_HEIGHT + 1;
        rectangles = new ArrayList<>(h);
        for (int i = 0; i < h; ++i) {
            rectangles.add(new ArrayList<>(h));
            for (int j = 0; j < w; ++j) {
                rectangles.get(i).add(new LinkedList<Planet>());
            }
        }
    }

    /**
     * Return the Planet in planets closest to (x, y) or null if planets is null/empty.
     *
     * @return the Planet in planets closest to (x, y) or null if planets is null/empty.
     */
    private static Planet closestOfList(List<Planet> planets, Point2D p) {
        if (planets.isEmpty()) {
            return null;
        }

        Planet closest = planets.get(0);
        double dist = Double.MAX_VALUE;
        for (Planet pl : planets) {
            if (pl != null) {
                double nDist = Util.distance(pl.x(), pl.y(), p.getX(), p.getY());
                if (dist > nDist) {
                    dist = nDist;
                    closest = pl;
                }
            }
        }
        return closest;
    }

    /**
     * Add Planet pl to this ProximityGrid.
     *
     * @param pl is within the bounds of this ProximityGrid.
     */
    public void addPlanet(Planet pl) {
        int ri = (pl.y() - y) / RECT_HEIGHT;
        int rj = (pl.x() - x) / RECT_WIDTH;

        rectangles.get(ri).get(rj).add(pl);
    }

    /**
     * Returns the planet closest to p.<br /><br />
     * <p><strong>requires clause:</strong> the ProximityGrid is not empty.</p>
     *
     * @return the planet closest to p.
     */
    public Planet closestPlanet(Point2D p) {
        int ri = (int) (p.getY() - y) / RECT_HEIGHT;
        int rj = (int) (p.getX() - x) / RECT_WIDTH;

        if (ri >= rows()) {
            ri = rows() - 1;
        } else if (ri < 0) {
            ri = 0;
        }

        if (rj >= cols()) {
            rj = cols() - 1;
        } else if (rj < 0) {
            rj = 0;
        }

        Planet pl = closestOfList(rectangles.get(ri).get(rj), p);

        int imin = ri - 1 >= 0 ? ri - 1 : 0;
        int imax = ri + 1 < rows() ? ri + 1 : rows() - 1;
        int jmin = rj - 1 >= 0 ? rj - 1 : 0;
        int jmax = rj + 1 < cols() ? rj + 1 : cols() - 1;
        do {
            List<Planet> ns = new LinkedList<Planet>();
            ns.add(pl);
            for (int i = imin; i <= imax; ++i) {
                ns.add(closestOfList(rectangles.get(i).get(jmin), p));
                ns.add(closestOfList(rectangles.get(i).get(jmax), p));
            }
            for (int j = jmin + 1; j < jmax; ++j) {
                ns.add(closestOfList(rectangles.get(imin).get(j), p));
                ns.add(closestOfList(rectangles.get(imax).get(j), p));
            }

            pl = closestOfList(ns, p);

            imin = imin - 1 >= 0 ? imin - 1 : imin;
            imax = imax + 1 < rows() ? imax + 1 : imax;
            jmin = jmin - 1 >= 0 ? jmin - 1 : jmin;
            jmax = jmax + 1 < cols() ? jmax + 1 : jmax;
        } while (pl == null);
        return pl;
    }

    /**
     * Return the number of rows in this grid.
     *
     * @return the number of rows in this grid.
     */
    private int rows() {
        return rectangles.size();
    }

    /**
     * Return the number of columns in this grid.
     *
     * @return the number of columns in this grid.
     */
    private int cols() {
        return rectangles.get(0).size();
    }

    @Override
    public Iterator<Planet> iterator() {
        return new MapIterator();
    }

    /**
     * An instance enumerates all Planets in this PlanetProximityGrid.
     */
    private class MapIterator implements Iterator<Planet> {

        /* The current index whose Planets are being iterated. */
        private int ri, rj;

        /* The current iterator getting Planets. */
        private Iterator<Planet> iter;

        /**
         * Constructor: a MapIterator starting at rs[0][0].
         */
        public MapIterator() {
            ri = 0;
            rj = 0;
            iter = rectangles.get(0).get(0).iterator();
            ensureTotalEnumeration();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Planet next() {
            Planet n = iter.next();
            ensureTotalEnumeration();
            return n;
        }

        /**
         * Iff iter still has more elements, do nothing. Otherwise, cycle to
         * the next List's iterator until all Planets have been enumerated.
         */
        private void ensureTotalEnumeration() {
            while (!iter.hasNext() && ri < rectangles.size()) {
                ++rj;
                if (rj < cols()) {
                    iter = rectangles.get(ri).get(rj).iterator();
                } else {
                    ++ri;
                    if (ri < rows()) {
                        rj = 0;
                        iter = rectangles.get(ri).get(rj).iterator();
                    }
                }
            }
        }
    }
}