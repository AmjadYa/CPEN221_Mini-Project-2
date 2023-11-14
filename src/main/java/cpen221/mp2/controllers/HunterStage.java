package cpen221.mp2.controllers;

import cpen221.mp2.models.PlanetStatus;

/**
 * In the search stage, find and reach the planet with the missing
 * spaceship. You have access only to the planets you can directly
 * reach, as well as a signal whose strength varies depending on how
 * far you are from the missing ship.
 * <p>
 * An instance provides all necessary methods to move through the
 * galaxy and find the missing spaceship.
 */
public interface HunterStage {
    /**
     * Return the unique ID of the spaceship's current planet.
     *
     * @return the planet id of the spaceship's current location.
     */
    public int currentID();

    /**
     * Return the strength of the signal from the missing ship's
     * current location.
     *
     * @return the signal strength from the missing ship.
     */
    public double signal();

    /**
     * Return an unordered array of PlanetStatus objects associated with all direct
     * neighbors of the current location. Each status contains the ID of the
     * neighboring node as well as the strength of the signal from the missing
     * spaceship at that node.<br>
     * <br>
     * (NB: This is NOT the distance in the graph; it is a number between 0 and
     * 1, where 0 is the farthest away, and 1 is the signal strength on the planet that
     * has the missing spaceship.)<br>
     * <br>
     * It is possible to move directly to any node in this array.
     */
    public PlanetStatus[] neighbors();

    /**
     * Return true iff the ship is on Kamino --- where the missing
     * spaceship is.
     */
    public boolean onKamino();

    /**
     * Change the current location to the planet given by id.
     */
    public void moveTo(int id);
}
