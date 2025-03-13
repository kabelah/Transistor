package dataprocessing;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class is responsible for writing data to an Excel file.
 */
public class ExcelWriter {

    /**
     * Writes the postal codes and their corresponding latitudes and longitudes to
     * an Excel file.
     *
     * @param map The map containing the postal codes as keys and the corresponding
     *            latitudes and longitudes as values.
     * @throws IOException If an input or output exception occurred.
     */
    public static void write(HashMap<String, String> map) throws IOException {
        final String pathName = "src/main/resources/dataprocessing/MassZipLatLon.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        int rowCount = 0;
        List<String> sortedKeys = new ArrayList<>(map.keySet());
        Collections.sort(sortedKeys);
        for (String s : sortedKeys) {
            // Splits the value to separate latitude and longitude
            String[] latLong = map.get(s).split(",");
            Row row = sheet.createRow(rowCount++);
            // Create cells for the postal code, latitude, and longitude
            Cell cell1 = row.createCell(0);
            Cell cell2 = row.createCell(1);
            Cell cell3 = row.createCell(2);
            cell1.setCellValue(s);
            cell2.setCellValue(latLong[0]);
            cell3.setCellValue(latLong[1]);
        }
        // Write the workbook to the Excel file
        FileOutputStream outputStream = new FileOutputStream(pathName);
        wb.write(outputStream);
    }
    
    /**
     * Writes the postal codes and their corresponding latitudes and longitudes to
     * an Excel file, using a provided list of sorted keys.
     *
     * @param map The map containing the postal codes as keys and the corresponding
     *            latitudes and longitudes as values.
     * @param sortedKeys A pre-sorted list of keys to use when writing to the file.
     * @throws IOException If an input or output exception occurred.
     */
    public static void write(HashMap<String, String> map, List<String> sortedKeys) throws IOException {
        final String pathName = "src/main/resources/dataprocessing/MassZipLatLon.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        int rowCount = 0;
        
        for (String s : sortedKeys) {
            if (map.containsKey(s)) {
                // Splits the value to separate latitude and longitude
                String[] latLong = map.get(s).split(",");
                Row row = sheet.createRow(rowCount++);
                // Create cells for the postal code, latitude, and longitude
                Cell cell1 = row.createCell(0);
                Cell cell2 = row.createCell(1);
                Cell cell3 = row.createCell(2);
                cell1.setCellValue(s);
                cell2.setCellValue(latLong[0]);
                cell3.setCellValue(latLong[1]);
            }
        }
        // Write the workbook to the Excel file
        FileOutputStream outputStream = new FileOutputStream(pathName);
        wb.write(outputStream);
    }
}
