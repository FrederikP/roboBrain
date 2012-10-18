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
import android.widget.ScrollView;
import android.widget.TextView;
import eu.fpetersen.robobrain.communication.ConsoleReceiver;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;

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

	public String getFormattedCurrentTimestamp() {
		Calendar cal = Calendar.getInstance();
		Date timestamp = cal.getTime();
		String pattern = "HH:mm:ss dd:MM:yyyy --> ";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(timestamp);
	}

	private void scrollToBottom() {
		consoleScroller.post(new Runnable() {
			public void run() {
				consoleScroller.smoothScrollTo(0, consoleTV.getBottom());
			}
		});
	}

	public void appendText(String text) {
		if (text != null) {
			String consoleText = consoleTV.getText().toString();
			final String consoleTextToAppend = consoleText + "\n"
					+ getFormattedCurrentTimestamp() + text;
			runOnUiThread(new Runnable() {
				public void run() {
					consoleTV.setText(consoleTextToAppend);
					scrollToBottom();
				}
			});

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_starter, menu);

		MenuItem starter = menu.findItem(R.id.starter_menu_item);
		Intent cIntent = new Intent(this, Starter.class);
		starter.setIntent(cIntent);
		return super.onCreateOptionsMenu(menu);
	}

}
