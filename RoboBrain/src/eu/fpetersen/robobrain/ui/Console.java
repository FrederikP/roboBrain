package eu.fpetersen.robobrain.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.communication.ConsoleReceiver;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;

/**
 * For now it is more like a log then a console. It displays messages to the
 * user to show details on communication and warnings/errors.
 * 
 * Options menu allows to switch to Starter activity
 * 
 * @author Frederik Petersen
 * 
 */
public class Console extends Activity {

	private TextView consoleTV;

	private ScrollView consoleScroller;

	private ConsoleReceiver cReceiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_console);

		// get handles to Views defined in our layout file
		consoleTV = (TextView) findViewById(R.id.consoleTextView);
		consoleScroller = (ScrollView) findViewById(R.id.consoleScroller);

		cReceiver = new ConsoleReceiver(this);
		IntentFilter intentFilter = new IntentFilter(
				RoboBrainIntent.ACTION_OUTPUT);
		registerReceiver(cReceiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (cReceiver != null)
			unregisterReceiver(cReceiver);
	}

	/**
	 * 
	 * @return Formatted timestamp as a string in this format:
	 *         "HH:mm:ss dd:MM:yyyy --> "
	 */
	public String getFormattedCurrentTimestamp() {
		Calendar cal = Calendar.getInstance();
		Date timestamp = cal.getTime();
		String pattern = getString(R.string.console_timestamp_format);
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(timestamp);
	}

	/**
	 * Scroll to the bottom of the console text view.
	 */
	private void scrollToBottom() {
		consoleScroller.post(new Runnable() {
			public void run() {
				consoleScroller.smoothScrollTo(0, consoleTV.getBottom());
			}
		});
	}

	/**
	 * Append text to the console text view and scrolls down if applicable
	 * 
	 * @param text
	 *            To be appended to the console text view.
	 */
	public void appendText(String text) {
		if (text != null) {
			String consoleText = consoleTV.getText().toString();
			final String consoleTextToAppend = consoleText + "\n"
					+ getFormattedCurrentTimestamp() + text;
			runOnUiThread(new Runnable() {
				public void run() {

					// Make sure that it's only scrolled automatically if user
					// isn't scrolling around.
					// ->Only Scrolls automatically if it was scrolled down
					// completely before adding new line
					boolean scrollDownAfterAppend = isScrolledDown();

					consoleTV.setText(consoleTextToAppend);

					if (scrollDownAfterAppend)
						scrollToBottom();
				}
			});

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_console, menu);

		MenuItem starter = menu.findItem(R.id.starter_menu_item);
		final Intent cIntent = new Intent(this, Starter.class);
		cIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		starter.setIntent(cIntent);
		starter.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				Console.this.startActivity(cIntent);
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 
	 * @return True if completely scrolled down, false if not
	 */
	public boolean isScrolledDown() {
		boolean scrollDownAfterAppend = true;
		int scrollY = consoleScroller.getScrollY()
				+ consoleScroller.getHeight() + 20;
		int cBottom = consoleTV.getBottom();
		if (scrollY + 20 < cBottom)
			scrollDownAfterAppend = false;
		return scrollDownAfterAppend;
	}

}
