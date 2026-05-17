package com.greenkart.interfaces;

import org.openqa.selenium.WebElement;

/**
 * Interface defining wait strategy contract.
 * Concrete implementations provide implicit, explicit, and fluent wait behaviours.
 */
public interface IWaitStrategy {

    WebElement waitForVisibility(WebElement element);

    WebElement waitForClickability(WebElement element);

    boolean waitForInvisibility(WebElement element);

    WebElement waitForPresence(String cssSelector);

    void waitForPageLoad();

    void hardWait(long milliseconds);
}
