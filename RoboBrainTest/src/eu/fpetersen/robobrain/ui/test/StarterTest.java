package eu.fpetersen.robobrain.ui.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.util.Helper;
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
		robotService = new MockRobotService(starterActivity);
		super.setUp();
	}

	/**
	 * Test toggling the RoboBrain Service from the UI
	 */
	public void testStatusToggle() {
		assertFalse(robotService.isRunning());
		final TextView statusTV = (TextView) starterActivity
				.findViewById(R.id.status_textview);
		final ToggleButton toggleButton = (ToggleButton) starterActivity
				.findViewById(R.id.togglestatus_button);
		assertFalse(toggleButton.isChecked());
		assertEquals("Stopped!", statusTV.getText());

		// ----Turn on service----
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				assertTrue(toggleButton.performClick());
			}
		});
		// Wait a while to make sure any real service can be started
		Helper.sleepMillis(5000);

		// set mock service to running
		robotService.setRunning(true);
		assertTrue(robotService.isRunning());

		// Overwrite real Service with Mock, because we only want to check this
		// Activity
		starterActivity.setRobotService(robotService);

		Helper.sleepMillis(500);

		assertTrue(robotService.isRunning());
		assertEquals("Started!", statusTV.getText());

		int tableChilds = starterActivity.getRobotBehaviorTable()
				.getChildCount();

		assertSame(2, tableChilds);

		TextView robotNameView = (TextView) ((TableRow) starterActivity
				.getRobotBehaviorTable().getChildAt(1)).getChildAt(0);
		assertSame("TestBot", robotNameView.getText());

		// ----Turn off service----
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				assertTrue(toggleButton.performClick());
			}
		});
		// Wait a while to make sure any real service can be started
		Helper.sleepMillis(3000);

		// set mock service to running
		robotService.setRunning(false);

		// Overwrite real Service with Mock, because we only want to check this
		// Activity
		starterActivity.setRobotService(robotService);

		Helper.sleepMillis(500);

		assertEquals("Stopped!", statusTV.getText());
		assertFalse(robotService.isRunning());

		tableChilds = starterActivity.getRobotBehaviorTable().getChildCount();

		assertSame(1, tableChilds);

		assertFalse(starterActivity.getRobotBehaviorTable().isShown());

	}
}
