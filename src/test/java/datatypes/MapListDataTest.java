package datatypes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class MapListDataTest {
    private MapListData mapListData;
    private HashMap<String, String> map;
    private List<String> keySorted;

    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        map.put("10001", "40.7505, -73.9934");
        map.put("10002", "40.7168, -73.9861");
        map.put("10003", "40.7306, -73.9905");

        keySorted = Arrays.asList("10001", "10002", "10003");

        mapListData = new MapListData(map, keySorted);
    }

    @Test
    void testGetMap() {
        HashMap<String, String> retrievedMap = mapListData.getMap();

        Assertions.assertEquals(map, retrievedMap);
    }

    @Test
    void testGetKeySorted() {
        List<String> retrievedKeySorted = mapListData.getKeySorted();

        Assertions.assertEquals(keySorted, retrievedKeySorted);
    }

    @Test
    void testMapListData_nullMap() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new MapListData(null, keySorted);
        });
    }

    @Test
    void testMapListData_nullKeySorted() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new MapListData(map, null);
        });
    }
}