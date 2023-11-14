package cpen221.mp2.views;

import cpen221.mp2.controllers.Controller;
import cpen221.mp2.models.Model;

import static cpen221.mp2.models.Model.Stage;

/**
 * An instance is minimum requirements of a view of the Space Gem game.
 */
public interface View {

    /**
     * Initialize the view with the given Controller and Model. If the view
     * has already been initialized, this will simply overwrite the previous
     * initialization.
     */
    public void init(Controller c, Model m);

    /**
     * Begin a particular stage. The view will autonomously update and display
     * the model until told to stop updating this particular stage.
     */
    public void beginStage(Stage s);

    /**
     * Signal the end of a particular stage; the view will stop updating,
     * leaving a static displayed state.
     * Precondition: this stage has begun.
     */
    public void endStage(Stage s);

    /**
     * Signal that the game has ended with score score.
     */
    public void endGame(int score);

    /**
     * Print s as a regular message.
     */
    public default void outprint(String s) {
        System.out.print(s);
        System.out.flush();
    }

    /**
     * Print s as an error message.
     */
    public default void errprint(String s) {
        System.err.print(s);
        System.err.flush();
    }

    /**
     * Print s terminated by a newline as a regular message.
     */
    public default void outprintln(String s) {
        outprint(s + '\n');
    }

    /**
     * Print s terminated by a newline as an error message.
     */
    public default void errprintln(String s) {
        errprint(s + '\n');
    }
}
