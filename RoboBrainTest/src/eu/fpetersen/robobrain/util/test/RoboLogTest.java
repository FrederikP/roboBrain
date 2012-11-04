package eu.fpetersen.robobrain.util.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.test.util.Helper;
import eu.fpetersen.robobrain.ui.Console;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Unit Testing for the RoboLog Helper class. This is also using the Console
 * acitivity to check for the output
 * 
 * @author Frederik Petersen
 * 
 */
@SuppressWarnings("rawtypes")
public class RoboLogTest extends ActivityInstrumentationTestCase2 {

	private Console consoleActivity;

	@SuppressWarnings("unchecked")
	public RoboLogTest() {
		super(Console.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		consoleActivity = (Console) getActivity();
	}

	/**
	 * Test if RoboLog message is correctly logged to Console UI activity
	 */
	public void testRoboLogLogging() {
		TextView consoleTextView = (TextView) consoleActivity
				.findViewById(R.id.consoleTextView);
		String findThis = "Logged!";

		RoboLog.log(consoleActivity, findThis);

		Helper.sleepMillis(200);

		String allText = consoleTextView.getText().toString();
		assertTrue(allText.contains(findThis));
	}

}