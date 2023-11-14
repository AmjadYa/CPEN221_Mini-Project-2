package cpen221.mp2.views;

import cpen221.mp2.controllers.Controller;
import cpen221.mp2.gui.GUI;
import cpen221.mp2.models.Model;
import cpen221.mp2.models.Model.Stage;

import javax.swing.*;

/**
 * An instance represents a graphical view.
 */
public class GUIView implements View {

    private GUI gui; // The GUI rendering this game
    private Controller ctrlr; // The controller of this game's model

    /**
     * Constructor: a GUIView with a blank GUI.
     */
    public GUIView() {
        gui = new GUI();
        addTopMenuListeners();
    }

    /**
     * Add listeners to the top menu to relay user input to the presenter.
     */
    private void addTopMenuListeners() {
        gui.addStartListener(e -> ctrlr.start());
        gui.addResetListener(e -> ctrlr.reset());
        gui.addNewMapListener(e -> ctrlr.newGame(JOptionPane.showInputDialog(
                "Enter either a valid seed, or anything else to get a random seed.")));
    }

    @Override
    public void init(Controller c, Model m) {
        ctrlr = c;
        gui.setStartEnabled(true);
        gui.init(c, m);
        outprintln("Seed: " + m.seed());
    }

    @Override
    public void beginStage(Stage s) {
        gui.beginStage(s);
        gui.setStartEnabled(false);
    }

    @Override
    public void endStage(Stage s) {
        gui.endPhase(s);
        switch (s) {
            case HUNT:
                if (ctrlr.huntSucceeded()) {
                    outprintln("Hunt Stage ended successfully!");
                }
                break;

            case GATHER:
                if (ctrlr.gatherSucceeded()) {
                    outprintln("Gather Stage ended successfully!");
                }
                break;

            default:
        }
    }

    @Override
    public void endGame(int score) {
        gui.pause();
        if (ctrlr.gatherSucceeded()) {
            outprintln("Score: " + score);
        } else {
            errprintln("Score: " + score);
        }
    }

    @Override
    public void errprint(String s) {
        View.super.errprint(s);
        gui.errprint(s);
    }

    @Override
    public void outprint(String s) {
        View.super.outprint(s);
        gui.outprint(s);
    }
}
