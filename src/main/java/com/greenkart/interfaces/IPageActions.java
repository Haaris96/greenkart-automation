package com.greenkart.interfaces;

import org.openqa.selenium.WebElement;

/**
 * Interface defining standard page actions contract.
 * All page objects must implement these core interactions.
 */
public interface IPageActions {

    void click(WebElement element);

    void type(WebElement element, String text);

    String getText(WebElement element);

    boolean isDisplayed(WebElement element);

    boolean isEnabled(WebElement element);

    void selectDropdownByVisibleText(WebElement element, String text);

    void hover(WebElement element);

    void scrollToElement(WebElement element);

    void clearAndType(WebElement element, String text);
}
