package dataprocessing;

import datatypes.MapListData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible for reading data from an Excel file.
 */
public class ExcelReader {

    /**
     * Reads postal codes, latitudes, and longitudes from an Excel file and returns
     * it as a MapListData object.
     *
     * @return A MapListData object containing the postal codes as keys and the
     *         corresponding latitudes and longitudes as values.
     * @throws IOException If an input or output exception occurred.
     */
    public static MapListData read() throws IOException {
        String postCode, latitude, longitude;
        List<String> keys = new ArrayList<>();
        final String pathName = "src/main/resources/dataprocessing/MassZipLatLon.xlsx";
        
        // Check if the file exists first
        File excelFilePath = new File(pathName);
        if (!excelFilePath.exists()) {
            System.out.println("Excel file not found at: " + pathName);
            // Return an empty MapListData object
            return new MapListData(new HashMap<>(), new ArrayList<>());
        }
        
        FileInputStream excelFile = new FileInputStream(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(excelFile);
        HashMap<String, String> map = new HashMap<>();
        XSSFSheet sheet = wb.getSheetAt(0);

        // Iterate through the rows of the Excel file
        for (Row row : sheet) {
            try {
                // Read the postal code, latitude, and longitude from the row
                postCode = row.getCell(0).toString();
                keys.add(postCode);
                latitude = row.getCell(1).toString();
                longitude = row.getCell(2).toString();
                // Add the postal code and its corresponding latitude and longitude to the map
                // Use regular string concatenation instead of String Template (STR)
                map.put(postCode, latitude + "," + longitude);
            } catch (Exception e) {
                System.out.println("Error reading row in Excel file: " + e.getMessage());
                // Continue with the next row
            }
        }
        
        // Close resources
        wb.close();
        excelFile.close();
        
        // Return a new MapListData object containing the map and the list of keys
        return new MapListData(map, keys);
    }

}
