package cpen221.mp2.util;

/**
 * Contains various static utility functions and constants.
 */
public abstract class Util {
    public static final String EARTH_NAME = "Earth";
    public static final String CRASHED_PLANET_NAME = "Kamino";
    public static final String DIRECTORY = System.getProperty("user.dir");

    /**
     * Return true iff any of the given arguments are null.
     *
     * @param objects list of objects to check if any is null.
     * @return true if one or more objects is null and false otherwise.
     */
    public static boolean anyNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the Euclidean distance between (x1, y1) and (x2, y2).
     *
     * @param x1 the first x-coordinate
     * @param y1 the first y-coordinate
     * @param x2 the second x-coordinate
     * @param y2 the second y-coordinate
     * @return the Euclidean distance between (x1, y1) and (x2, y2)
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
    }
}
