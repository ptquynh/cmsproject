package org.selenium.cms.common;

public class Utils {

	/**
	 * Pause
	 * 
	 * @param timeInMillis
	 */
	public static void pause(long timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}