package cpen221.mp2.views;

import cpen221.mp2.controllers.Controller;
import cpen221.mp2.models.Model;
import cpen221.mp2.models.Model.Stage;

/**
 * An instance is a view of the Space Gems Game that immediately
 * starts one game and outputs nothing.
 */
public class QuietView implements View {

    /* True iff the game is still running. */
    protected boolean running;

    @Override
    public void init(Controller c, Model m) {
        c.start();
        running = true;
        while (running) {
            c.update();
        }
    }

    @Override
    public void beginStage(Stage s) {
    }

    @Override
    public void endStage(Stage s) {
    }

    @Override
    public void endGame(int score) {
        running = false;
    }

    @Override
    public void outprint(String s) {
    }

    @Override
    public void errprint(String s) {
    }
}
