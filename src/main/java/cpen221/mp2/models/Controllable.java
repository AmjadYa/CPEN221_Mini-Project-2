package cpen221.mp2.models;

import cpen221.mp2.controllers.HunterStage;
import cpen221.mp2.controllers.GathererStage;

/** An instance is controllable by some Controller. */
public interface Controllable extends HunterStage, GathererStage {

    /** Start the search phase and return an instance that
     *  purely implements HuntStage. */
    public HunterStage beginHuntStage();

    /** Start the gather stage and returns an instance that
     *  purely implements GatherStage. */
    public GathererStage beginGatherStage();

    /** End the hunt phase, returning true iff it was successful
     * and false otherwise.
     * Precondition: the game is in the search phase. */
    public boolean endHuntStage();

    /** End the rescue phase, returning true iff it was successful and
     * false otherwise. Precondition: the game is in the rescue phase. */
    public boolean endGatherStage();

    /** Advance the simulation by one tick.
     * Throw SolutionFailedException if the update causes a failure. */
    public void update(int tick) throws SolutionFailedException;

    /** Abort the current game. Any attempts to change the state (i.e. by
     * moveTo) will result in an exception. This allows the game to instantly
     * end a solution, rather than stepping through the entire solution. */
    public void abort();

    /** Set the ship's current location to n. */
    public void setShipLocation(Planet planet);

    /** An instance indicates that the game has aborted. */
    @SuppressWarnings("serial")
    public static class AbortException extends RuntimeException {}

    /** An instance contains a message detailing how a solution failed.  */
    @SuppressWarnings("serial")
    public static class SolutionFailedException extends Exception {
        public SolutionFailedException(String msg) {
            super(msg);
        }
    }
}
