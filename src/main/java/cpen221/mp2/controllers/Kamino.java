package cpen221.mp2.controllers;

import cpen221.mp2.models.Controllable.AbortException;
import cpen221.mp2.models.Controllable.SolutionFailedException;
import cpen221.mp2.models.GameModel;
import cpen221.mp2.models.Universe;
import cpen221.mp2.spaceship.MillenniumFalcon;
import cpen221.mp2.views.*;

import java.util.Random;
import java.util.function.Supplier;

import static cpen221.mp2.models.Model.Stage.GATHER;
import static cpen221.mp2.models.Model.Stage.HUNT;

/**
 * An instance runs the game, linking the state to the user interface.
 */
public class Kamino implements Controller {

    /* Tunable map generation parameters. */
    public static final int MIN_NODES = 5;
    public static final int MAX_NODES = 750;
    public static final int MIN_SPICE = 0;
    public static final int MAX_SPICE = 5000;
    public static final int WIDTH = 4096;
    public static final int HEIGHT = 4096;
    private static final Random RNG = new Random(); // used for random seed generation.

    /* A Spaceship supplier used to get new Spaceships (e.g. for restarting). */
    private static Supplier<Spaceship> ships;
    protected long seed; // The seed used to generate this game.
    protected Spaceship spaceship; // The solution implementing this game.
    protected boolean started; // True iff this game has started.
    protected boolean failed; // True iff this game's solution failed.
    protected GameModel model; // The controllable model for this game.
    protected View view; // The view for this game.
    /* Separate thread used to prevent the model from blocking the view */
    protected ModelThread thread;

    /**
     * Constructor: a game with seed s, spaceship sp, and View v.
     */
    public Kamino(long seed, Spaceship spaceship, View view) {
        this.view = view;
        ships = () -> {
            try {
                return spaceship.getClass().newInstance();
            }
            catch (Exception e) {
                System.err.println("fatal error: failed to create new Spaceship");
                System.exit(1);
            }
            return null;
        };

        init(seed, spaceship);
    }

    /**
     * Run Kamino. Without any options, this defaults to an instance
     * with a random seed using a GUI view.
     * <p>
     * -s, --seed=-8876742922144960503  Run this game using the seed SEED
     * -g, --gui        Use the GUI (graphical user interface) view
     * -c, --cli        Use the CLI (command-line interface) view
     * -b, --benchmark  Use a benchmark view, which will give statistics
     * of your solution when run on multiple seeds
     * -q, --quiet      Use a quiet view, which outputs nothing.
     */
    public static void main(String[] argv) {
        // parse arguments
        View view = null;
        Long seed = null;
        for (int i = 0; i < argv.length; ++i) {
            try {
                if (argv[i].equals("-g") || argv[i].equals("--gui")) {
                    if (view != null) {
                        System.err.println(
                                "Error: cannot specify more than " + "one view option");
                        return;
                    } else {
                        view = new GUIView();
                    }
                } else if (argv[i].equals("-c") || argv[i].equals("--cli")) {
                    if (view != null) {
                        System.err.println(
                                "Error: cannot specify more than " + "one view option");
                        return;
                    } else {
                        view = new CLIView();
                    }
                } else if (argv[i].equals("-q") || argv[i].equals("--quiet")) {
                    if (view != null) {
                        System.err.println(
                                "Error: cannot specify more than " + "one view option");
                        return;
                    } else {
                        view = new QuietView();
                    }
                } else if (argv[i].equals("-b") || argv[i].equals("--benchmark")) {
                    if (view != null) {
                        System.err.println(
                                "Error: cannot specify more than " + "one view option");
                        return;
                    } else {
                        view = new BenchmarkView();
                    }
                } else if (argv[i].length() > 7
                        && argv[i].substring(0, 7).equals("--seed=")) {
                    seed = Long.parseLong(argv[i].substring(7));
                } else if (argv[i].equals("-s")) {
                    if (i + 1 < argv.length) {
                        ++i;
                        seed = Long.parseLong(argv[i]);
                    } else {
                        System.err.println("Error: no seed specified.");
                        return;
                    }
                } else {
                    System.err.println("Error: invalid argument \"" + argv[i] + '"');
                    return;
                }
            }
            catch (NumberFormatException e) {
                System.err.println("Invalid seed \"" + argv[i] + '"');
                return;
            }
        }
        if (seed == null) {
            seed = RNG.nextLong(); // avoid burning RNG; only generate if needed
        }

        // begin the game with the appropriate parameters
        if (view == null) {
            view = new GUIView();
        }
        new Kamino(seed, new MillenniumFalcon(), view);
    }

    /**
     * Initialize the game with seed s and spaceship sp. If this game has already
     * been initialized, this overwrites the previous initialization.
     */
    protected void init(long seed, Spaceship ship) {
        // stop the old thread, if it exists
        if (thread != null) {
            thread.kill();
        }

        started = false;
        failed = false;

        this.seed = seed;
        spaceship = ship;
        Universe u = new Universe.UniverseBuilder().size(WIDTH, HEIGHT).seed(seed)
                .planetBounds(MIN_NODES, MAX_NODES).spiceBounds(MIN_SPICE, MAX_SPICE).build();
        model = new GameModel(u);
        thread = new ModelThread();
        view.init(this, model);
    }

    @Override
    public void newGame(String seed) {
        if (seed == null) {
            return;
        }
        try {
            init(Long.valueOf(seed), ships.get());
        }
        catch (NumberFormatException ex) {
            init(RNG.nextLong(), ships.get());
        }
    }

    @Override
    public void newGame(long seed) {
        init(seed, ships.get());
    }

    @Override
    public void reset() {
        init(seed, ships.get());
    }

    @Override
    public void start() {
        if (started) {
            view.errprintln("Game has already started");
            return;
        }
        started = true;
        thread.start();
    }

    /**
     * Take the appropriate actions when a solution fails.
     */
    private void fail(SolutionFailedException e) {
        failed = true;
        view.endStage(model.phase());
        view.errprintln("Solution failed with reason: " + e.getMessage());
        view.endGame(model.score());
    }

    @Override
    public synchronized void update() {
        try {
            model.update(TICKTIME);
        }
        catch (SolutionFailedException e) {
            if (!failed) {
                fail(e);
            }
        }
    }

    /**
     * Run ship's hunt method.
     * Throw a SolutionFailedException if the hunt fails.
     */
    protected void hunt() throws SolutionFailedException {
        view.beginStage(HUNT);
        spaceship.hunt(model.beginHuntStage());
        boolean success = model.endHuntStage();
        view.endStage(HUNT);
        if (success) {
            return;
        }

        throw new SolutionFailedException(
                "Your solution to search() returned at the wrong location.");
    }

    /**
     * Run ship's gather method.
     * Throw a SolutionFailedException if the gather stage fails.
     */
    protected void gather() throws SolutionFailedException {
        view.beginStage(GATHER);
        spaceship.gather(model.beginGatherStage());
        boolean success = model.endGatherStage();
        view.endStage(GATHER);
        if (success) {
            return;
        }

        throw new SolutionFailedException(
                "Your solution to rescue() returned at the wrong location.");
    }

    @Override
    public boolean huntSucceeded() {
        return model.huntSucceeded();
    }

    @Override
    public boolean gatherSucceeded() {
        return model.gatherSucceeded();
    }

    /**
     * An instance runs a model in a separate thread.
     * It can be killed by calling kill().
     */
    protected class ModelThread extends Thread {
        /**
         * Run through the game until it finishes, fails, or is aborted.
         */
        @Override
        public void run() {
            try {
                hunt();
                gather();
                view.endGame(model.score());
            }
            catch (SolutionFailedException e) {
                fail(e);
            }
            catch (AbortException e) {
            }
        }

        /**
         * Kill this model thread by aborting the underlying model.
         */
        public void kill() {
            model.abort();
        }
    }
}
