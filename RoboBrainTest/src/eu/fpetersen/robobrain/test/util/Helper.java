package eu.fpetersen.robobrain.test.util;

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
