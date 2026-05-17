package com.greenkart.dataproviders;

import com.greenkart.constants.Constants;
import com.greenkart.utils.ExcelUtils;
import org.testng.annotations.DataProvider;

import java.util.Map;

/**
 * TestNG DataProviders pulling data from Excel sheets.
 * Tests annotated with @Test(dataProvider="...", dataProviderClass=TestDataProvider.class)
 */
public class TestDataProvider {

    @DataProvider(name = "productData", parallel = false)
    public static Object[][] productData() {
        return ExcelUtils.getDataAsObjectArray(Constants.TEST_DATA_FILE, Constants.SHEET_PRODUCTS);
    }

    @DataProvider(name = "productDataParallel", parallel = true)
    public static Object[][] productDataParallel() {
        return ExcelUtils.getDataAsObjectArray(Constants.TEST_DATA_FILE, Constants.SHEET_PRODUCTS);
    }

    @DataProvider(name = "couponData", parallel = false)
    public static Object[][] couponData() {
        return ExcelUtils.getDataAsObjectArray(Constants.TEST_DATA_FILE, Constants.SHEET_COUPONS);
    }

    @DataProvider(name = "orderData", parallel = false)
    public static Object[][] orderData() {
        return ExcelUtils.getDataAsObjectArray(Constants.TEST_DATA_FILE, Constants.SHEET_ORDERS);
    }

    // Inline data provider (not from Excel) – for simple smoke tests
    @DataProvider(name = "searchTerms")
    public static Object[][] searchTerms() {
        return new Object[][]{
            {"Broc", 1},
            {"Cauli", 1},
            {"Tom", 1},
            {"Car", 1},
            {"Cuc", 1}
        };
    }

    @DataProvider(name = "products")
    public static Object[][] products() {
        return new Object[][]{
            {"Brocolli"},
            {"Cauliflower"},
            {"Beetroot"},
            {"Carrot"},
            {"Cucumber"},
            {"Tomato"}
        };
    }
}
