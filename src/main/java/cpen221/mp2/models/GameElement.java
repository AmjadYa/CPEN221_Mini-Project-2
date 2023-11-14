package cpen221.mp2.models;

/**
 * An instance represents a viewable entity attached to a game.
 */
public interface GameElement {

    /*** Return the name of this GameElement when drawn on the board. */
    public String name();

    /**
     * Return the x coordinate of this GameElement.
     */
    public int x();

    /**
     * Return the y coordinate of this GameElement.
     */
    public int y();
}
