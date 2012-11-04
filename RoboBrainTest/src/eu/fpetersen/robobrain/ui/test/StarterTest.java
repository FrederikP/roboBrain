package eu.fpetersen.robobrain.ui.test;

import android.test.ActivityInstrumentationTestCase2;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.ui.Starter;

/**
 * Unit testing if the {@link Starter} activity.
 * 
 * @author Frederik Petersen
 * 
 */
public class StarterTest extends ActivityInstrumentationTestCase2<Starter> {

	private Starter starterActivity;

	private MockRobotService robotService;

	public StarterTest() {
		super(Starter.class);
	}

	@Override
	protected void setUp() throws Exception {
		starterActivity = getActivity();
		robotService = new MockRobotService();
		starterActivity.setRobotService(robotService);
		super.setUp();
	}

	/**
	 * Test toggling the RoboBrain Service from the UI
	 */
	/*
	 * public void testStatusToggle() { assertFalse(robotService.isRunning());
	 * ToggleButton toggleButton = (ToggleButton) starterActivity
	 * .findViewById(R.id.togglestatus_button);
	 * assertFalse(toggleButton.isChecked());
	 * 
	 * assertTrue(toggleButton.performClick()); // set mock service to running
	 * robotService.setRunning(true); assertTrue(robotService.isRunning()); }
	 */

}
