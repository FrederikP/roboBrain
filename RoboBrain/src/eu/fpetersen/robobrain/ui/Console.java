package eu.fpetersen.robobrain.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.ui.R;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ScrollView;
import android.widget.TextView;

public class Console extends Activity {

	// change this to your Bluetooth device address
	private static final String DEVICE_ADDRESS = "07:12:05:03:53:76";

	private TextView consoleTV;

	private ScrollView consoleScroller;
	
	CommandCenter commandCenter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_console);

		// get handles to Views defined in our layout file
		consoleTV = (TextView) findViewById(R.id.consoleTextView);
		consoleScroller = (ScrollView) findViewById(R.id.consoleScroller);
		
		commandCenter = CommandCenter.getInstance(DEVICE_ADDRESS, this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		commandCenter.connect();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		commandCenter.disconnect();
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
	
	public TextView getConsoleTV() {
		return consoleTV;
	}

}
