package eu.fpetersen.robobrain.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.MoreAsserts;
import android.widget.TextView;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.ui.Console;

/**
 * Unit Testing of the {@link Console} activity.
 * 
 * @author Frederik Petersen
 * 
 */
@SuppressWarnings("rawtypes")
public class ConsoleTest extends ActivityInstrumentationTestCase2 {

	private Console consoleActivity;

	@SuppressWarnings("unchecked")
	public ConsoleTest() {
		super(Console.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		consoleActivity = (Console) getActivity();
	}

	/**
	 * Test if the timestamp is correctly created and formatted
	 */
	public void testFormattedTimeStamp() {
		String date = consoleActivity.getFormattedCurrentTimestamp();
		assertNotNull(date);
		String pattern = consoleActivity
				.getString(R.string.console_timestamp_format);
		pattern = pattern.replaceAll("(\\w)", "\\\\\\d");
		pattern = pattern.replaceAll("(\\s)", "\\\\\\s");
		MoreAsserts.assertMatchesRegex(pattern, date);
	}

	/**
	 * Test if text is correctly appended to the console, and if scrolling down
	 * works.
	 */
	public void testTextAppendWithoutIntent() {
		TextView consoleTextView = (TextView) consoleActivity
				.findViewById(R.id.consoleTextView);
		String findThis = "INTHEMIDDLE";
		for (int i = 0; i < 100; i++) {
			sleepMillis(200);
			consoleActivity.appendText("Test " + i);
			if (i == 50) {
				consoleActivity.appendText(findThis);
			}
		}
		String allText = consoleTextView.getText().toString();
		assertTrue(allText.contains(findThis));

		assertTrue(consoleActivity.isScrolledDown());
	}

	/**
	 * Test if text is correctly appended to the console when sending the
	 * intent, and if scrolling down works.
	 */
	public void testTextAppendWithIntent() {
		TextView consoleTextView = (TextView) consoleActivity
				.findViewById(R.id.consoleTextView);
		String appendMe = "AppendMe";
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, appendMe);
		consoleActivity.sendBroadcast(cIntent);

		sleepMillis(500);
		String allText = consoleTextView.getText().toString();
		assertTrue(allText.contains(appendMe));

	}

	/**
	 * Set the Thread to sleep for the given amount of milli seconds
	 * 
	 * @param millis
	 *            Time to sleep in milli seconds
	 */
	private void sleepMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
