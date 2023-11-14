package cpen221.mp2.controllers;

import cpen221.mp2.graph.ImGraph;
import cpen221.mp2.models.Link;
import cpen221.mp2.models.Planet;

import java.util.Set;

/**
 * Return to Earth on time while collecting as much spice as possible.
 * The rescued spaceship has information on the entire galaxy.
 * Spice on a planet is collected automatically when the planet is reached.
 * <p>
 * N.B.: There are many other methods in other classes that you will also
 * probably want to use, such as those in Graph.
 * <p>
 * An instance provides all the necessary methods to move through the galaxy,
 * collect speed upgrades, and reach Earth.
 */
public interface GathererStage {
    /**
     * Return the Node on which the ship is.
     */
    public Planet currentPlanet();

    /**
     * Return node Earth Node. You MUST return at this planet to succeed.
     */
    public Planet earth();

    /**
     * Return the set of all planets.
     */
    public Set<Planet> planets();

    /**
     * Move the space ship to a planet
     * An exception occurs if the ship's current planet is not adjacent to planet.
     *
     * @throws Exception if the ship cannot move to **planet** in one step.
     */
    public void moveTo(Planet planet);

    /**
     * Return the remaining amount of distance that your ship can travel.
     * Your solution must end before this becomes negative.
     *
     * @return the amount of fuel that remains in the ship
     */
    public int fuelRemaining();

    /**
     * Return an immutable graph representation of the universe.
     *
     * @return the immutable graph representation of the universe.
     */
    public ImGraph<Planet, Link> planetGraph();
}
