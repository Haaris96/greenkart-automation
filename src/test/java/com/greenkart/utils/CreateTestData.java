package com.greenkart.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * One-time utility to generate the testdata.xlsx file.
 * Run this as a standalone main class when you need to regenerate test data.
 */
public class CreateTestData {

    public static void main(String[] args) throws IOException {
        String filePath = "src/test/resources/testdata/testdata.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = createHeaderStyle(workbook);

            // ── Sheet 1: Products ──────────────────────────────────────────────
            Sheet products = workbook.createSheet("Products");
            createRow(products, 0, headerStyle, "productName", "category", "expectedInCart");
            createRow(products, 1, null, "Brocolli",    "Vegetables", "true");
            createRow(products, 2, null, "Cauliflower",  "Vegetables", "true");
            createRow(products, 3, null, "Beetroot",     "Vegetables", "true");
            createRow(products, 4, null, "Carrot",       "Vegetables", "true");
            createRow(products, 5, null, "Cucumber",     "Vegetables", "true");
            createRow(products, 6, null, "Tomato",       "Vegetables", "true");

            // ── Sheet 2: Users ─────────────────────────────────────────────────
            Sheet users = workbook.createSheet("Users");
            createRow(users, 0, headerStyle, "username", "password", "role");
            createRow(users, 1, null, "testuser1", "Test@1234", "buyer");
            createRow(users, 2, null, "testuser2", "Test@5678", "buyer");

            // ── Sheet 3: Coupons ───────────────────────────────────────────────
            Sheet coupons = workbook.createSheet("Coupons");
            createRow(coupons, 0, headerStyle, "couponCode", "expectedResult", "discount");
            createRow(coupons, 1, null, "rahulshettyacademy", "valid",   "10%");
            createRow(coupons, 2, null, "INVALID123",          "invalid", "0%");
            createRow(coupons, 3, null, "SAVE10",              "invalid", "0%");

            // ── Sheet 4: Orders ────────────────────────────────────────────────
            Sheet orders = workbook.createSheet("Orders");
            createRow(orders, 0, headerStyle, "productName", "couponCode", "expectedOrderStatus");
            createRow(orders, 1, null, "Brocolli",    "rahulshettyacademy", "success");
            createRow(orders, 2, null, "Cauliflower", "",                   "success");
            createRow(orders, 3, null, "Tomato",      "rahulshettyacademy", "success");
            createRow(orders, 4, null, "Beetroot",    "",                   "success");

            // Auto-size columns
            for (Sheet sheet : new Sheet[]{products, users, coupons, orders}) {
                for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Test data file created: " + filePath);
        }
    }

    private static void createRow(Sheet sheet, int rowNum, CellStyle style, String... values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            if (style != null) cell.setCellStyle(style);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
