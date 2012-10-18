package eu.fpetersen.robobrain.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import eu.fpetersen.robobrain.communication.RobotService;

public class Starter extends Activity {

	private TextView statusTV;
	private ToggleButton toggleStatusB;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_starter);

		// get handles to Views defined in our layout file
		statusTV = (TextView) findViewById(R.id.status_textview);
		toggleStatusB = (ToggleButton) findViewById(R.id.togglestatus_button);

		toggleStatusB.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				boolean isChecked = toggleStatusB.isChecked();
				handleStatusToggle(isChecked);
			}
		});

	}

	protected void handleStatusToggle(boolean isChecked) {
		if (isChecked) {
			toggleStatusB.post(new Runnable() {
				public void run() {
					startService(new Intent(Starter.this, RobotService.class));
				}
			});

		} else {
			toggleStatusB.post(new Runnable() {
				public void run() {
					stopService(new Intent(Starter.this, RobotService.class));
				}
			});
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateStatus();
			}
		}, 1000);
	}

	@Override
	public void onStart() {
		super.onStart();
		updateStatus();
	}

	@Override
	protected void onResume() {
		updateStatus();
		super.onResume();
	}

	private void updateStatus() {
		RobotService rService = RobotService.getInstance();
		if (rService == null || !rService.isRunning()) {
			runOnUiThread(new Runnable() {

				public void run() {
					statusTV.setText("Stopped!");
					statusTV.setTextColor(Color.RED);
					toggleStatusB.setChecked(false);
				}
			});

		} else {
			runOnUiThread(new Runnable() {

				public void run() {
					statusTV.setText("Started!");
					statusTV.setTextColor(Color.GREEN);
					toggleStatusB.setChecked(true);
				}
			});

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_starter, menu);

		MenuItem console = menu.findItem(R.id.console_menu_item);
		Intent cIntent = new Intent(this, Console.class);
		console.setIntent(cIntent);
		return super.onCreateOptionsMenu(menu);
	}
}
