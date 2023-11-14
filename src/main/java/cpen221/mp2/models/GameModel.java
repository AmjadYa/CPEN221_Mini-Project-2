package cpen221.mp2.models;

import cpen221.mp2.controllers.GathererStage;
import cpen221.mp2.controllers.HunterStage;
import cpen221.mp2.graph.ImGraph;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import static cpen221.mp2.models.Model.Stage.*;

/**
 * An instance maintains the current state of a Kamino game.
 */
public class GameModel implements Model, Controllable {

    private static final int BASE_SPEED = 100; // Base speed of ship (per second)
    private final Universe universe; // The Universe associated with this instance
    private final ImGraph<Planet, Link> planetGraph; // the graph representation of the universe
    private Stage stage; // The current phase of the model
    private Planet shipPlanet; // The Node that the ship is on or has last visited.
    private Point2D.Double shipLocation; // The current location of the ship.
    private Link shipLink; // The link on which the ship is traveling. null if still.
    private double lerpT; // linear interpolation parameter (shipNode to next)
    private int edgeDist; // current distance traveled on this edge
    private int fuelRemaining; // Fuel left for rescue. < 0 => failed solution.
    private int fuelUsed; // Fuel used by the ship
    private int spice; // The current amount of spice that the ship holds; >= 0
    private int score; // The cumulative score

    private String failMessage; // Iff failed, contains message; else null
    private boolean abort; // True if a game has aborted
    private boolean huntSuccessful; // True if rescue ended successfully
    private boolean gatherSuccessful; // True if return ended successfully

    /**
     * Create a new game with a given universe.
     *
     * @param univ represents the universe for the new game.
     */
    public GameModel(Universe univ) {
        stage = NONE;
        universe = univ;
        planetGraph = univ.planetGraph();

        shipPlanet = universe.earth();
        shipLocation = new Point2D.Double(shipPlanet.x(), shipPlanet.y());
        shipLink = null;
        lerpT = 0d;

        fuelUsed = 0;
        fuelRemaining = 0;

        spice = 0;
        score = 0;

        failMessage = null;
        abort = false;
        huntSuccessful = false;
        gatherSuccessful = false;
    }

    @Override
    public int width() {
        return universe.width();
    }

    @Override
    public int height() {
        return universe.height();
    }

    @Override
    public long seed() {
        return universe.seed();
    }

    @Override
    public Set<Planet> planets() {
        HashSet<Planet> planets = new HashSet<>();
        for (Planet planet : universe.planets()) {
            planets.add(planet);
        }
        return planets;
    }

    @Override
    public Set<Link> edges() {
        return universe.links();
    }

    @Override
    public Planet closestNode(Point2D p) {
        return universe.closestPlanet(p);
    }

    @Override
    public Planet shipNode() {
        return shipPlanet;
    }

    @Override
    public Point2D shipLocation() {
        return shipLocation;
    }

    @Override
    public Stage phase() {
        return stage;
    }

    @Override
    public int fuelRemaining() {
        return fuelRemaining - edgeDist;
    }

    @Override
    public int fuelUsed() {
        return fuelUsed + edgeDist;
    }

    @Override
    public int score() {
        if (stage == HUNT) {
            int tmp = score - edgeDist;
            return tmp > 0 ? tmp : 0;
        } else {
            return score;
        }
    }

    @Override
    public synchronized void update(int tick) throws SolutionFailedException {
        if (failMessage != null) {
            throw new SolutionFailedException(failMessage);
        }

        if (shipLink != null) {
            Planet shipNext = shipLink.distinctVertex(shipPlanet);
            double travelDist = BASE_SPEED * (tick / 1e3);
            lerpT += travelDist / shipLink.length();
            if (lerpT > 1d) {
                shipArrive();
                notifyAll();
            } else {
                edgeDist = (int) (lerpT * shipLink.length() + 0.5d);
                shipLocation.x = (1 - lerpT) * shipPlanet.x() + lerpT * shipNext.x();
                shipLocation.y = (1 - lerpT) * shipPlanet.y() + lerpT * shipNext.y();
            }

            if (stage == GATHER && fuelRemaining() < 0) {
                failMessage = "ran out of fuel and can no longer travel.";
                score = 0;
                throw new SolutionFailedException(failMessage);
            }
        }
    }

    /**
     * Make the ship arrive to its next destination.
     * Precondition: the ship is moving between two Nodes.
     */
    private void shipArrive() {
        shipPlanet = shipLink.distinctVertex(shipPlanet);
        shipLocation.x = shipPlanet.x();
        shipLocation.y = shipPlanet.y();
        lerpT = 0d;
        edgeDist = 0;
        fuelUsed += shipLink.length();
        if (stage == HUNT) {
            score -= shipLink.length();
            if (score < 0) {
                score = 0;
            }
        } else if (stage == GATHER) {
            fuelRemaining -= shipLink.length();
        }
        shipLink = null;
    }

    @Override
    public int currentID() {
        return shipPlanet.id();
    }

    @Override
    public double signal() {
        return universe.signal(shipPlanet);
    }

    @Override
    public PlanetStatus[] neighbors() {
        Set<Planet> planets = universe.neighbors(shipPlanet);
        PlanetStatus[] ps = new PlanetStatus[planets.size()];
        int i = 0;
        for (Planet planet : planets) {
            ps[i] = new PlanetStatus(planet.id(), planet.name(), universe.signal(planet));
            ++i;
        }
        return ps;
    }

    @Override
    public boolean onKamino() {
        return shipPlanet == universe.target();
    }

    @Override
    public Planet currentPlanet() {
        return shipPlanet;
    }

    @Override
    public Planet earth() {
        return universe.earth();
    }

    @Override
    public Planet kamino() {
        return universe.target();
    }

    /**
     * When called, blocks until the ship has moved from shipNode to planet.
     */
    private synchronized void waitUntilMoved(Planet planet) {
        shipLink = planetGraph.getEdge(shipPlanet, planet);
        while (shipLink != null) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
    }

    @Override
    public synchronized void moveTo(int id) {
        if (abort) {
            throw new AbortException();
        }
        if (failMessage != null) {
            waitUntilAbort();
        }

        for (Planet n : universe.neighbors(shipPlanet)) {
            if (n.id() == id) {
                waitUntilMoved(n);
                return;
            }
        }
        failMessage = "tried to call moveTo to a non-adjacent ID.";
        waitUntilAbort();
    }

    @Override
    public synchronized void moveTo(Planet planet) {
        if (abort) {
            throw new AbortException();
        }
        if (failMessage != null) {
            waitUntilAbort();
        }

        if (!universe.connected(shipPlanet, planet)) {
            failMessage = "tried to call moveTo to a non-adjacent Node.";
            waitUntilAbort();
        }

        waitUntilMoved(planet);

        int s = planet.takeSpice();
        spice += s;
        score += s;
    }

    @Override
    public int spice() {
        return spice;
    }

    @Override
    public ImGraph<Planet, Link> planetGraph() {
        return planetGraph;
    }

    @Override
    public HunterStage beginHuntStage() {
        stage = HUNT;
        score = universe.distanceToTarget() * 2;

        return new HunterStage() {
            @Override
            public int currentID() {
                return GameModel.this.currentID();
            }

            @Override
            public double signal() {
                return GameModel.this.signal();
            }

            @Override
            public PlanetStatus[] neighbors() {
                return GameModel.this.neighbors();
            }

            @Override
            public boolean onKamino() {
                return GameModel.this.onKamino();
            }

            @Override
            public void moveTo(int id) {
                GameModel.this.moveTo(id);
            }

        };
    }

    @Override
    public GathererStage beginGatherStage() {
        stage = GATHER;
        fuelRemaining = universe.sumLinkLengths() / 2 + universe.distanceToTarget();

        return new GathererStage() {
            @Override
            public Planet currentPlanet() {
                return GameModel.this.currentPlanet();
            }

            @Override
            public Planet earth() {
                return GameModel.this.earth();
            }

            @Override
            public Set<Planet> planets() {
                return GameModel.this.planets();
            }

            @Override
            public void moveTo(Planet planet) {
                GameModel.this.moveTo(planet);
            }

            @Override
            public int fuelRemaining() {
                return GameModel.this.fuelRemaining();
            }

            @Override
            public ImGraph<Planet, Link> planetGraph() {
                return GameModel.this.planetGraph;
            }
        };
    }

    @Override
    public boolean endHuntStage() {
        if (stage != HUNT) {
            throw new IllegalStateException(
                    "error: not in rescue stage; can't end rescue stage");
        }

        stage = NONE;
        huntSuccessful = shipPlanet == universe.target();
        if (!huntSuccessful) {
            score = 0;
        }
        return huntSuccessful;
    }

    @Override
    public boolean endGatherStage() {
        if (stage != GATHER) {
            throw new IllegalStateException(
                    "error: not in return stage; can't end return stage");
        }

        stage = NONE;
        gatherSuccessful = shipPlanet == universe.earth();
        if (!gatherSuccessful) {
            score = 0;
        }
        return gatherSuccessful;
    }

    @Override
    public boolean huntSucceeded() {
        return huntSuccessful;
    }

    @Override
    public boolean gatherSucceeded() {
        return gatherSuccessful;
    }

    @Override
    public void setShipLocation(Planet planet) {
        shipPlanet = planet;
    }

    /**
     * Block until the game is aborted, then throws an AbortException.
     */
    private synchronized void waitUntilAbort() throws AbortException {
        while (!abort) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
        throw new AbortException();
    }

    @Override
    public synchronized void abort() {
        abort = true;

        // If the ship was moving, forcibly stop it
        if (shipLink != null) {
            shipArrive();
            notifyAll();
        }
    }
}
