package cpen221.mp2.models;

import cpen221.mp2.graph.Edge;
import cpen221.mp2.util.Util;

import java.util.Objects;

/**
 * Links are weighted undirected connections between two planets.
 * The length of a link is the rounded distance between both planets,
 * but always > 0.
 * <p>
 * Link implements GameElement, indicating that it is a component of
 * a game universe and has some user-facing representation.
 * <p>
 * An instance represents a link between two planets.
 */
public class Link extends Edge<Planet> implements GameElement {

    private int visits; // The number of times this cpen221.mp2.graph.Edge has been visited.

    /**
     * Constructor: An edge with end planets pl1 and pl2.
     * Precondition: pl1 and pl2 are non-null, non-equal Nodes. length > 0.
     */
    Link(Planet pl1, Planet pl2, int length) {
        super(pl1, pl2, length);
        visits = 0;
    }

    /**
     * A method to create a new link after verifying the correctness of arguments and
     * computing the link distance.
     *
     * @param p1 represents one end of the link
     * @param p2 the other end of the link
     * @return a link between p1 and p2
     */
    public static Link createLink(Planet p1, Planet p2) {
        if (Util.anyNull(p1, p2)) {
            throw new IllegalArgumentException("End points cannot be null");
        }
        double distance = Util.distance(p1.x(), p1.y(), p2.x(), p2.y());
        int length = distance <= 1 ? 1 : (int) distance;
        return new Link(p1, p2, length);
    }

    /**
     * Return true iff this link and e share a planet.
     */
    public boolean intersects(Link e) {
        return intersection(e) != null;
    }

    /**
     * Return the amount of fuel needed to travel on this link.
     *
     * @return the amount of fuel needed to travel on this link.
     */
    public int fuelNeeded() {
        return super.length();
    }

    /**
     * Return true iff: this edge and ob are the same object, or
     * if this edge and ob connect the same two Nodes.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.v1(), super.v2());
    }

    /**
     * Return the names of the planets connected to this link, delimited by the
     * String "---".
     */
    @Override
    public String toString() {
        return super.v1().name() + "---" + super.v2().name();
    }

    /**
     * Return a String to print when this object is drawn on a GUI.
     *
     * @return a name to use with this link.
     */
    public String name() {
        return String.valueOf(super.length());
    }

    /**
     * Return the x location of the center of this link.
     *
     * @return the x coordinate of the center of this link.
     */
    public int x() {
        int x1 = super.v1().x();
        int x2 = super.v2().x();
        return (int) (((x1 + x2) / 2.0) + 0.5);
    }

    /**
     * Return the y location of the center of this link.
     *
     * @return the y coordinate for the center of this link.
     */
    public int y() {
        int y1 = super.v1().y();
        int y2 = super.v2().y();
        return (int) (((y1 + y2) / 2.0) + 0.5);
    }

    /**
     * Increase the amount of times this link has been visited by 1.
     * <p>
     * modifies clause: the number of times this link has been visited is increase by 1.
     * </p>
     */
    void visit() {
        ++visits;
    }

    /**
     * Return the number of times this link has been visited.
     *
     * @return the number of times think link has been visited.
     */
    public int getVisits() {
        return visits;
    }


}
