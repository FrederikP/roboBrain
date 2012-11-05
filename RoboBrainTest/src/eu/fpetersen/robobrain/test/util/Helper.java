package eu.fpetersen.robobrain.test.util;

/**
 * Helper class for all Test classes. A central repository for methods used
 * throughout the testing system
 * 
 * @author Frederik Petersen
 * 
 */
public class Helper {

	/**
	 * Set the Thread to sleep for the given amount of milli seconds
	 * 
	 * @param millis
	 *            Time to sleep in milli seconds
	 */
	public static void sleepMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
