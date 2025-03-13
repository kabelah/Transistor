package dataprocessing;

import java.util.List;

/**
 * This class is responsible for checking if a given postal code exists in a
 * sorted list of postal codes.
 */
public class CheckForPostalCodeInExcel {

    /**
     * Searches for a given postal code in a sorted list of postal codes using
     * binary search.
     *
     * @param keysSorted The sorted list of postal codes.
     * @param postCode   The postal code to search for.
     * @return True if the postal code is found, false otherwise.
     */
    public static boolean search(List<String> keysSorted, String postCode) {
        int l = 0, r = keysSorted.size() - 1;

        // Loop to implement Binary Search
        while (l <= r) {

            // Calculate mid
            int m = l + (r - l) / 2;
            int res = postCode.compareTo(keysSorted.get(m));

            // Check if postCode is present at mid
            if (res == 0)
                return true;

            // If postCode greater, ignore left half
            if (res > 0)
                l = m + 1;

            // If postCode is smaller, ignore right half
            else
                r = m - 1;
        }

        return false;
    }
    
    /**
     * Alias for the search method.
     *
     * @param keysSorted The sorted list of postal codes.
     * @param postCode   The postal code to search for.
     * @return True if the postal code is found, false otherwise.
     */
    public static boolean contains(List<String> keysSorted, String postCode) {
        return search(keysSorted, postCode);
    }
}
