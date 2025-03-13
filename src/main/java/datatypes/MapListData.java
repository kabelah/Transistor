package datatypes;

import java.util.HashMap;
import java.util.List;

/**
 * This class is responsible for storing a map of postal codes to their
 * corresponding latitudes and longitudes,
 * and a sorted list of the postal codes.
 */
public class MapListData {
    private final HashMap<String, String> map;
    private final List<String> keySorted;

    /**
     * Constructs a new MapListData object with the given map and sorted list of
     * keys.
     *
     * @param map       The map containing the postal codes as keys and the
     *                  corresponding latitudes and longitudes as values.
     * @param keySorted The sorted list of postal codes.
     */
    public MapListData(HashMap<String, String> map, List<String> keySorted) {
        if (map == null) {
            throw new NullPointerException("Map cannot be null");
        }
        if (keySorted == null) {
            throw new NullPointerException("Key sorted list cannot be null");
        }
        this.map = map;
        this.keySorted = keySorted;
    }

    /**
     * Returns the map of postal codes to their corresponding latitudes and
     * longitudes.
     *
     * @return The map of postal codes to their corresponding latitudes and
     *         longitudes.
     */
    public HashMap<String, String> getMap() {
        return map;
    }

    /**
     * Returns the sorted list of postal codes.
     *
     * @return The sorted list of postal codes.
     */
    public List<String> getKeySorted() {
        return keySorted;
    }
}
