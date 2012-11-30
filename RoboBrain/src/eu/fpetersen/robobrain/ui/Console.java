/*******************************************************************************
 * RoboBrain - Control your Arduino Robots per Android Device
 * Copyright (c) 2012 Frederik Petersen.
 * All rights reserved.
 * 
 * This file is part of RoboBrain.
 * 
 *     RoboBrain is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     RoboBrain is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with RoboBrain.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Frederik Petersen - Project Owner, initial Implementation
 ******************************************************************************/
package eu.fpetersen.robobrain.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

	private TextView mConsoleTV;

	private ScrollView mConsoleScroller;

	private ConsoleReceiver mConsoleReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_console);

		// get handles to Views defined in our layout file
		mConsoleTV = (TextView) findViewById(R.id.consoleTextView);
		mConsoleScroller = (ScrollView) findViewById(R.id.consoleScroller);

		mConsoleReceiver = new ConsoleReceiver(this);
		IntentFilter intentFilter = new IntentFilter(RoboBrainIntent.ACTION_OUTPUT);
		registerReceiver(mConsoleReceiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mConsoleReceiver != null)
			unregisterReceiver(mConsoleReceiver);
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
		mConsoleScroller.post(new Runnable() {
			public void run() {
				mConsoleScroller.smoothScrollTo(0, mConsoleTV.getBottom());
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
			String consoleText = mConsoleTV.getText().toString();
			final String consoleTextToAppend = consoleText + "\n" + getFormattedCurrentTimestamp()
					+ text;
			runOnUiThread(new Runnable() {
				public void run() {

					// Make sure that it's only scrolled automatically if user
					// isn't scrolling around.
					// ->Only Scrolls automatically if it was scrolled down
					// completely before adding new line
					boolean scrollDownAfterAppend = isScrolledDown();

					mConsoleTV.setText(consoleTextToAppend);

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
		MenuHelper menuHelper = new MenuHelper();
		menuHelper.addStarterMenuClickListener(menu, Console.this);
		menuHelper.addAboutMenuClickListener(menu, Console.this);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 
	 * @return True if completely scrolled down, false if not
	 */
	public boolean isScrolledDown() {
		int scrollY = mConsoleScroller.getScrollY();
		int childHeight = mConsoleScroller.getChildAt(0).getMeasuredHeight();
		int height = mConsoleScroller.getHeight();
		Log.d("Console", "scrollY: " + scrollY + "; height: " + height + "; childHeight: "
				+ childHeight);
		if (childHeight <= scrollY + height) {
			return true;
		} else {
			return false;
		}
	}
}
