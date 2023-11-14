package cpen221.mp2.models;

import java.awt.geom.Point2D;
import java.util.Set;

/**
 * An instance can access a Space Gems game's state.
 */
public interface Model {

    /**
     * Return the seed used to generate this game.
     */
    public long seed();

    /**
     * Return a Set of all Nodes in this game.
     */
    public Set<Planet> planets();

    /**
     * Return a Set of all Edges in this game.
     */
    public Set<Link> edges();

    /**
     * Return the maximum separation between Nodes in the x-direction.
     */
    public int width();

    /**
     * Return the maximum separation between Nodes in the y-direction.
     */
    public int height();

    /**
     * Return the location of Earth, the starting Node.
     */
    public Planet earth();

    /**
     * Return the location of Kamino, the destination of the hunt stage.
     */
    public Planet kamino();

    /**
     * Return the closest Planet to the given Point, or null if
     * there are no Planets in the universe.
     */
    public Planet closestNode(Point2D p);

    /**
     * Return the Node that the ship is currently on or the Node
     * from which the ship has just departed.
     */
    public Planet shipNode();

    /**
     * Return the current location of the ship in this game.
     */
    public Point2D shipLocation();

    /**
     * Return the current stage of this Space Gems game.
     */
    public Stage phase();

    /**
     * Return the remaining amount of fuel.
     */
    public int fuelRemaining();

    /**
     * Return the total distance traveled since the rescue stage started.
     */
    public int fuelUsed();

    /**
     * Return the current amount of gems collected.
     */
    public int spice();

    /**
     * Return the current score of this game.
     */
    public int score();

    /**
     * Return true iff the search phase ended successfully.
     */
    public boolean huntSucceeded();

    /**
     * Return true iff the rescue phase ended successfully.
     */
    public boolean gatherSucceeded();

    /**
     * An instance describes the current phase of the model.
     */
    public static enum Stage {
        HUNT, GATHER, NONE
    }

    ;
}
