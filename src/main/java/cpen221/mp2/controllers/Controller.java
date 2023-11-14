package cpen221.mp2.controllers;

/**
 * An instance controls a Game and can access its status.
 */
public interface Controller {

    public static final int TICKTIME = 10; // time per update, in milliseconds

    /**
     * Start the current game. If a game has already started, this will print
     * an error message and return.
     */
    public void start();

    /**
     * Reset the current game to its initial state.
     */
    public void reset();

    /**
     * Return true iff the search stage ended successfully.
     *
     * @return true if the hunt stage was successful and false otherwise
     */
    public boolean huntSucceeded();

    /**
     * Return true iff the rescue stage ended successfully.
     *
     * @return true if the gather stage was successful and false otherwise
     */
    public boolean gatherSucceeded();

    /**
     * Update the model by one tick.
     */
    public void update();

    /**
     * Create (but don't start) a new game with the long value of str
     * as a seed, or a random seed of str is not a valid long.
     *
     * @param seed is a seed for the random number generator that is used to
     *             build the game universe
     */
    public void newGame(String seed);

    /**
     * Create (but don't start) a new game with seed s.
     *
     * @param seed is a seed for the random number generator that is used to
     *             build the game universe
     */

    public void newGame(long seed);
}
