package org.selenium.cms.common;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;

public class UnitBase {

	public static WebDriver driver;
	public WebDriver newDriver;
	public boolean isDriver = true;
	public final int ACTION_REPEAT = 5;

	protected int DEFAULT_TIMEOUT = 600000; // milliseconds = 60 seconds
	protected int WAIT_INTERVAL = 1000; // milliseconds
	public int loopCount = 0;

	public static String DEFAULT_DRIVERPATH_WIN = getAbsoluteFilePath("\\src\\Drivers\\chromedriver.exe");
	public static String DEFAULT_DRIVERPATH_UBUNTU = getAbsoluteFilePath("\\src\\Drivers\\chromedriver");
	public static String DEFAULT_FILEIMAGE_PATH = getAbsoluteFilePath("\\src\\Images\\mypicture.jpg");
	public final String DEFAULT_BASEURL = "http://localhost:2368/ghost";

	/**
	 * Get element
	 * 
	 * @param locator
	 * @param opParams
	 * @return an element
	 */
	public WebElement getElement(Object locator, Object... opParams) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		WebDriver wDriver;
		if (isDriver)
			wDriver = (WebDriver) (opParams.length > 0 ? opParams[0] : driver);
		else
			wDriver = (WebDriver) (opParams.length > 0 ? opParams[0]
					: newDriver);
		WebElement elem = null;
		try {
			elem = wDriver.findElement(by);
		} catch (NoSuchElementException e) {

		}
		return elem;
	}

	/**
	 * get an element
	 * 
	 * @param locator
	 * @param opParams
	 * @return null
	 */
	public WebElement getDisplayedElement(Object locator, Object... opParams) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		WebDriver wDriver;
		if (isDriver)
			wDriver = (WebDriver) (opParams.length > 0 ? opParams[0] : driver);
		else
			wDriver = (WebDriver) (opParams.length > 0 ? opParams[0]
					: newDriver);
		WebElement e = null;
		try {
			if (by != null)
				e = wDriver.findElement(by);
			if (e != null) {
				if (isDisplay(by))
					return e;
			}
		} catch (NoSuchElementException ex) {
		} catch (StaleElementReferenceException ex) {
			checkCycling(ex, 10);
			getDisplayedElement(locator);
		} finally {
			loopCount = 0;
		}
		return null;
	}

	/**
	 * Get element
	 * 
	 * @param locator
	 *            locator of element
	 * @param opParams
	 *            opPram[0]: timeout opPram[1]: 0,1 0: No Assert 1: Assert
	 * @return an element
	 */
	public WebElement waitForAndGetElement(Object locator, Object... opParams) {
		WebElement elem = null;
		int timeout = (Integer) (opParams.length > 0 ? opParams[0]
				: DEFAULT_TIMEOUT);
		int isAssert = (Integer) (opParams.length > 1 ? opParams[1] : 1);
		int notDisplayE = (Integer) (opParams.length > 2 ? opParams[2] : 0);
		WebDriver wDriver;
		if (isDriver)
			wDriver = (WebDriver) (opParams.length > 3 ? opParams[3] : driver);
		else
			wDriver = (WebDriver) (opParams.length > 3 ? opParams[3]
					: newDriver);
		for (int tick = 0; tick < timeout / WAIT_INTERVAL; tick++) {
			if (notDisplayE == 2) {
				elem = getElement(locator, wDriver);
			} else {
				elem = getDisplayedElement(locator, wDriver);
			}
			if (null != elem) {
				return elem;
			}
		}
		if (isAssert == 1) {
			assert false : ("Timeout after " + timeout
					+ "ms waiting for element present: " + locator);
		}
		return null;
	}

	/**
	 * Get element
	 * 
	 * @param locator
	 *            locator of element
	 * @param opParams
	 *            opPram[0]: timeout opPram[1]: 0,1 0: No Assert 1: Assert
	 * @return an element
	 */
	public WebElement waitForElementNotPresent(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		int isAssert = opParams.length > 1 ? opParams[1] : 1;
		int notDisplayE = opParams.length > 2 ? opParams[2] : 0;

		for (int tick = 0; tick < timeout / WAIT_INTERVAL; tick++) {
			if (notDisplayE == 2) {
				elem = getElement(locator);
			} else {
				elem = getDisplayedElement(locator);
			}
			if (null == elem) {
				return null;
			}
		}

		if (isAssert == 1) {
			assert false : ("Timeout after " + timeout
					+ "ms waiting for element not present: " + locator);
		}
		return elem;
	}

	/**
	 * Click by using javascript
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void clickByJavascript(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		WebElement e = null;
		if (locator instanceof WebElement) {
			e = (WebElement) locator;
		} else {
			e = waitForAndGetElement(locator, DEFAULT_TIMEOUT, 1, notDisplay);
		}
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", e);
	}

	/**
	 * click action
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void click(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		WebElement element = null;
		Actions actions = new Actions(driver);
		try {
			if (!locator.getClass().getName().contains("WebElement")) {
				element = waitForAndGetElement(locator, DEFAULT_TIMEOUT, 1,
						notDisplay);
			} else {
				element = (WebElement) locator;
			}
			if (element.isEnabled()) {
				actions.click(element).perform();
			} else {
				clickByJavascript(locator, notDisplay);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			clickByJavascript(locator, notDisplay);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			clickByJavascript(locator, notDisplay);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * get value attribute
	 * 
	 * @param locator
	 * @return value of element
	 */
	public String getValue(Object locator) {
		try {
			return waitForAndGetElement(locator).getAttribute("value");
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			return getValue(locator);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * type to textbox
	 * 
	 * @param locator
	 * @param value
	 * @param validate
	 * @param opParams
	 */
	public void type(Object locator, String value, boolean validate,
			Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		try {
			for (int loop = 1;; loop++) {
				if (loop >= ACTION_REPEAT) {
					Assert.fail("Timeout at type: " + value + " into "
							+ locator);
				}
				WebElement element = waitForAndGetElement(locator,
						DEFAULT_TIMEOUT, 1, notDisplay);
				if (element != null) {
					if (validate)
						element.clear();
					element.click();
					element.sendKeys(value);
					if (!validate || value.equals(getValue(locator))) {
						break;
					}
				}
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			type(locator, value, validate, opParams);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			type(locator, value, validate, opParams);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * checkCycling
	 * 
	 * @param e
	 * @param loopCountAllowed
	 */
	public void checkCycling(Exception e, int loopCountAllowed) {
		if (loopCount > loopCountAllowed) {
			Assert.fail("Cycled: " + e.getMessage());
		}
		loopCount++;
	}

	/**
	 * check element displays or net
	 * 
	 * @param locator
	 * @return true if element displays false if element doesn't display
	 */
	public boolean isDisplay(Object locator) {
		boolean bool = false;
		WebElement e = getElement(locator);
		try {
			if (e != null)
				bool = e.isDisplayed();
		} catch (StaleElementReferenceException ex) {
			checkCycling(ex, 10);
			isDisplay(locator);
		} finally {
			loopCount = 0;
		}
		return bool;
	}

	/**
	 * get random string
	 * 
	 * @return random string
	 */
	public String getRandomString() {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * get a list of random numbers
	 * 
	 * @return random numbers
	 */
	public String getRandomNumber() {
		char[] chars = "0123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Scroll to a element on the website
	 * 
	 * @param element
	 * @param driver
	 */
	public static void scrollToElement(WebElement element, WebDriver driver) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	/**
	 * Scroll to bottom of the page of website
	 * 
	 * @param driver
	 */
	public static void scrollToBottomPage(WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,"
				+ "document.body.scrollHeight,document.documentElement.clientHeight));");
	}

	/**
	 * This function returns a absolute path from a relative path
	 * 
	 * @param relativeFilePath
	 * @return string
	 */
	public static String getAbsoluteFilePath(String relativeFilePath) {
		String fs = File.separator;
		String curDir = System.getProperty("user.dir");
		String absolutePath = curDir + relativeFilePath;
		absolutePath = absolutePath.replace("/", fs).replace("\\", fs);
		return absolutePath;
	}

	/**
	 * uploadFileUsingRobot
	 * 
	 * @param fileLocation
	 */
	public void uploadFileUsingRobot(String fileLocation) {
		Utils.pause(3000);
		try {
			Robot robot = new Robot();
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_DOWN);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_A);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_A);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_X);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_X);
			// Setting clipboard with file location
			setClipboardData(fileLocation);
			// native key strokes for CTRL, V and ENTER keys

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			Utils.pause(1000);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	/**
	 * setClipboardData
	 * 
	 * @param string
	 */
	public static void setClipboardData(String string) {
		// StringSelection is a class that can be used for copy and paste
		// operations.
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(stringSelection, null);
	}

}
