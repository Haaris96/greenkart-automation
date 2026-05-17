package com.greenkart.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility for reading/writing Excel (.xlsx) files via Apache POI.
 * Used for data-driven testing.
 */
public class ExcelUtils {

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    private ExcelUtils() {}

    /**
     * Returns all rows of a sheet as a list of column→value maps.
     * First row is treated as header.
     */
    public static List<Map<String, String>> getSheetData(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                log.error("Sheet '{}' not found in {}", sheetName, filePath);
                return data;
            }

            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, String> rowMap = new HashMap<>();
                for (int c = 0; c < colCount; c++) {
                    String header = getCellValue(headerRow.getCell(c));
                    String value  = getCellValue(row.getCell(c));
                    rowMap.put(header, value);
                }
                data.add(rowMap);
            }
            log.info("Read {} data rows from sheet '{}'", data.size(), sheetName);
        } catch (IOException e) {
            log.error("Failed to read Excel file: {}", filePath, e);
        }
        return data;
    }

    /**
     * Returns sheet data as a 2-D Object array suitable for TestNG @DataProvider.
     */
    public static Object[][] getDataAsObjectArray(String filePath, String sheetName) {
        List<Map<String, String>> rows = getSheetData(filePath, sheetName);
        Object[][] result = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            result[i][0] = rows.get(i);
        }
        return result;
    }

    /**
     * Reads a single cell value.
     */
    public static String getCellData(String filePath, String sheetName, int rowNum, int colNum) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(colNum);
            return getCellValue(cell);
        } catch (IOException e) {
            log.error("getCellData failed", e);
            return "";
        }
    }

    /**
     * Writes a value to a specific cell and saves the file.
     */
    public static void setCellData(String filePath, String sheetName, int rowNum, int colNum, String value) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            if (row == null) row = sheet.createRow(rowNum);
            Cell cell = row.getCell(colNum);
            if (cell == null) cell = row.createCell(colNum);
            cell.setCellValue(value);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            log.info("Written '{}' to [{},{}] in sheet '{}'", value, rowNum, colNum, sheetName);
        } catch (IOException e) {
            log.error("setCellData failed", e);
        }
    }

    /**
     * Returns the row count (excluding header) in a sheet.
     */
    public static int getRowCount(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            return sheet == null ? 0 : sheet.getLastRowNum();
        } catch (IOException e) {
            log.error("getRowCount failed", e);
            return 0;
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
