package com.greenkart.base;

import com.greenkart.constants.Constants;
import com.greenkart.interfaces.IPageActions;
import com.greenkart.interfaces.IWaitStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Abstract base page that every page object extends.
 * Implements IPageActions and IWaitStrategy, providing:
 *   - Explicit wait helpers
 *   - Fluent wait helpers
 *   - Common actions (click, type, scroll, hover …)
 * Child classes inherit these helpers automatically (inheritance + abstraction).
 */
public abstract class BasePage implements IPageActions, IWaitStrategy {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait fluentWait;
    protected final Actions actions;
    private static final Logger log = LogManager.getLogger(BasePage.class);

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        int explicitSec = Integer.parseInt(
            System.getProperty("explicit.wait", String.valueOf(Constants.EXPLICIT_WAIT))
        );
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitSec));

        // Fluent wait – polls every 500 ms, ignores NoSuchElementException
        this.fluentWait = new WebDriverWait(driver, Duration.ofSeconds(Constants.FLUENT_WAIT),
                Duration.ofMillis(Constants.FLUENT_POLLING));

        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    // ─── IPageActions ───────────────────────────────────────────────────────────

    @Override
    public void click(WebElement element) {
        waitForClickability(element);
        log.debug("Clicking element: {}", element);
        element.click();
    }

    @Override
    public void type(WebElement element, String text) {
        waitForVisibility(element);
        log.debug("Typing '{}' into element", text);
        element.sendKeys(text);
    }

    @Override
    public void clearAndType(WebElement element, String text) {
        waitForVisibility(element);
        element.clear();
        element.sendKeys(text);
        log.debug("Cleared and typed '{}' into element", text);
    }

    @Override
    public String getText(WebElement element) {
        waitForVisibility(element);
        return element.getText().trim();
    }

    @Override
    public boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    @Override
    public boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public void selectDropdownByVisibleText(WebElement element, String text) {
        waitForVisibility(element);
        new Select(element).selectByVisibleText(text);
        log.debug("Selected '{}' from dropdown", text);
    }

    @Override
    public void hover(WebElement element) {
        waitForVisibility(element);
        actions.moveToElement(element).perform();
        log.debug("Hovering over element");
    }

    @Override
    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        log.debug("Scrolled to element");
    }

    // ─── IWaitStrategy ──────────────────────────────────────────────────────────

    @Override
    public WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    @Override
    public WebElement waitForClickability(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    @Override
    public boolean waitForInvisibility(WebElement element) {
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    @Override
    public WebElement waitForPresence(String cssSelector) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
    }

    @Override
    public void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
    }

    @Override
    public void hardWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ─── Fluent wait helpers ─────────────────────────────────────────────────

    /**
     * Waits using fluent wait with custom polling – ignores NoSuchElementException.
     */
    public WebElement fluentWaitForElement(By locator) {
        Wait<WebDriver> fw = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(Constants.FLUENT_WAIT))
                .pollingEvery(Duration.ofMillis(Constants.FLUENT_POLLING))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return fw.until(d -> d.findElement(locator));
    }

    public <T> T fluentWait(Function<WebDriver, T> condition) {
        Wait<WebDriver> fw = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(Constants.FLUENT_WAIT))
                .pollingEvery(Duration.ofMillis(Constants.FLUENT_POLLING))
                .ignoring(NoSuchElementException.class);
        return fw.until(condition);
    }

    // ─── Extra helpers ────────────────────────────────────────────────────────

    public void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        log.debug("JS-clicked element");
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    public void switchToAlert() {
        wait.until(ExpectedConditions.alertIsPresent());
    }

    public void acceptAlert() {
        switchToAlert();
        driver.switchTo().alert().accept();
    }

    public String getAlertText() {
        switchToAlert();
        return driver.switchTo().alert().getText();
    }

    // Abstract method – every page must verify it has loaded
    public abstract boolean isPageLoaded();
}
