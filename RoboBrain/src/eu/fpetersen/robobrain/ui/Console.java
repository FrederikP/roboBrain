package eu.fpetersen.robobrain.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.fpetersen.robobrain.ui.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ScrollView;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class Console extends Activity {

	// change this to your Bluetooth device address
	private static final String DEVICE_ADDRESS = "07:12:05:03:53:76";

	private TextView consoleTV;

	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();

	private ScrollView consoleScroller;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_console);

		// get handles to Views defined in our layout file
		consoleTV = (TextView) findViewById(R.id.consoleTextView);
		consoleScroller = (ScrollView) findViewById(R.id.consoleScroller);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our
		// receiver
		registerReceiver(arduinoReceiver, new IntentFilter(
				AmarinoIntent.ACTION_RECEIVED));

		// this is how you tell Amarino to connect to a specific BT device from
		// within your own code
		Amarino.connect(this, DEVICE_ADDRESS);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// if you connect in onStart() you must not forget to disconnect when
		// your app is closed
		Amarino.disconnect(this, DEVICE_ADDRESS);

		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}

	/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino events.
	 * 
	 * It extracts data from the intent and updates the graph accordingly.
	 */
	public class ArduinoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {
			consoleTV.post(new Runnable() {

				public void run() {
					String data = null;

					// the type of data which is added to the intent
					final int dataType = intent.getIntExtra(
							AmarinoIntent.EXTRA_DATA_TYPE, -1);

					// we only expect String data though, but it is better to
					// check if
					// really string was sent
					// later Amarino will support differnt data types, so far
					// data comes
					// always as string and
					// you have to parse the data to the type you have sent from
					// Arduino, like it is shown below
					if (dataType == AmarinoIntent.STRING_EXTRA) {
						data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
						appendText(data);
					}
				}
			});

		}

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

	private void appendText(String text) {
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

}
