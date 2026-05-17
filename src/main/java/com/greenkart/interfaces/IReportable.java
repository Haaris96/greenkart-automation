package com.greenkart.interfaces;

/**
 * Interface for report logging across extent and cucumber reporters.
 */
public interface IReportable {

    void logInfo(String message);

    void logPass(String message);

    void logFail(String message);

    void logSkip(String message);

    void attachScreenshot(String screenshotPath);
}
