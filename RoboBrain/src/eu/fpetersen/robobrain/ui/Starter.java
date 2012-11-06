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

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.speech.SpeechRecognizerService;

/**
 * Gives the user the option to turn the RoboBrain services on and off. When
 * Service is started shows the Robots and their behaviors and Stop buttons of
 * Behavior is started.
 * 
 * Options menu allows to switch to Console activity
 * 
 * @author Frederik Petersen
 * 
 */
public class Starter extends Activity {

	private TextView statusTV;
	private ToggleButton toggleStatusB;
	private TableLayout robotBehaviorTable;

	// For displaying progress circle stuff
	private ProgressDialog progressDialog;

	private RobotService robotService;

	private static Starter instance;

	public static Starter getInstance() {
		return instance;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_starter);

		// get handles to Views defined in our layout file
		statusTV = (TextView) findViewById(R.id.status_textview);
		toggleStatusB = (ToggleButton) findViewById(R.id.togglestatus_button);
		robotBehaviorTable = (TableLayout) findViewById(R.id.robot_behavior_table);
		progressDialog = new ProgressDialog(Starter.this);

		toggleStatusB.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				boolean isChecked = toggleStatusB.isChecked();
				handleStatusToggle(isChecked);
			}
		});

		// Update the status every 2 secs. For debugging ill state management
		/*
		 * Timer timer = new Timer(); timer.schedule(new TimerTask() {
		 * 
		 * @Override public void run() { updateStatus(); } }, 2000, 2000);
		 */

	}

	/**
	 * Called to start or stop services. Also displays progress dialog.
	 * 
	 * @param isChecked
	 *            True if service is supposed to be turned on, False if service
	 *            is supposed to be turned off
	 */
	protected void handleStatusToggle(boolean isChecked) {
		if (isChecked) {
			progressDialog.setMessage("Starting Robobrain Service");
			progressDialog.setTitle("Service Starting");
			progressDialog.show();
			toggleStatusB.post(new Runnable() {
				public void run() {
					startService(new Intent(getApplicationContext(),
							SpeechRecognizerService.class));
					startService(new Intent(getApplicationContext(),
							RobotService.class));
				}
			});

		} else {
			progressDialog.setMessage("Stopping Robobrain Service");
			progressDialog.setTitle("Service Stopping");
			progressDialog.show();
			toggleStatusB.post(new Runnable() {
				public void run() {
					progressDialog.setMessage("Sending stop behavior signal");
					Intent intent = new Intent(
							RoboBrainIntent.ACTION_STOPALLBEHAVIORS);
					Starter.this.sendBroadcast(intent);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							progressDialog
									.setMessage("Sending service stop signals");
							stopService(new Intent(getApplicationContext(),
									RobotService.class));
							stopService(new Intent(getApplicationContext(),
									SpeechRecognizerService.class));
						}
					}, 500);

				}
			});
		}
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

	/**
	 * Updates UI to display correct service status and display correct
	 * information about robots and their behaviors
	 */
	private void updateStatus() {
		if (robotService == null || !robotService.isRunning()) {
			runOnUiThread(new Runnable() {

				public void run() {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					statusTV.setText("Stopped!");
					statusTV.setTextColor(Color.RED);
					toggleStatusB.setChecked(false);
					robotBehaviorTable.setVisibility(View.INVISIBLE);
					cleanupRobotBehaviorTable();
				}
			});

		} else {
			runOnUiThread(new Runnable() {

				public void run() {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					statusTV.setText("Started!");
					statusTV.setTextColor(Color.GREEN);
					toggleStatusB.setChecked(true);
					if (robotBehaviorTable.getVisibility() == View.INVISIBLE) {
						robotBehaviorTable.setVisibility(View.VISIBLE);
					}
					cleanupRobotBehaviorTable();
					setupRobotBehaviorTable();
				}
			});

		}
	}

	/**
	 * Removes all robots and behavior information from the UI.
	 */
	protected void cleanupRobotBehaviorTable() {
		for (int i = 1; i < robotBehaviorTable.getChildCount(); i++) {
			robotBehaviorTable.removeViewAt(i);
		}

	}

	/**
	 * Return the table that holds robots and behaviors
	 * 
	 * @return table that holds robots and behaviors
	 */
	public TableLayout getRobotBehaviorTable() {
		return robotBehaviorTable;
	}

	/**
	 * Sets up Robot and Behavior information table
	 */
	protected void setupRobotBehaviorTable() {
		Collection<CommandCenter> ccs = robotService.getAllCCs();
		for (CommandCenter cc : ccs) {
			TableRow row = new TableRow(this);
			TextView nameView = new TextView(this);
			nameView.setText(cc.getRobot().getName());
			nameView.setPadding(5, 5, 5, 5);
			LinearLayout behaviorLayout = new LinearLayout(this);
			behaviorLayout.setOrientation(LinearLayout.VERTICAL);
			addBehaviorButtons(behaviorLayout, cc);
			row.addView(nameView);
			row.addView(behaviorLayout);

			robotBehaviorTable.addView(row);
		}
	}

	/**
	 * Sets up the Click Listener for the Behavior Button
	 * 
	 * @param button
	 *            Button the Listener is set for
	 * @param b
	 *            Behavior that the button represents
	 * @param behaviorLayout
	 *            Layout the button is placed in
	 * @param cc
	 *            CommandCenter of the robot
	 */
	private void setBehaviorButtonClickListener(final Button button,
			final Behavior b, final LinearLayout behaviorLayout,
			final CommandCenter cc) {
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				progressDialog.setMessage("Sending Behavior start signal");
				progressDialog.setTitle("Behavior Starting");
				progressDialog.show();
				Runnable behavior = new Runnable() {
					public void run() {
						Intent intent = new Intent(
								RoboBrainIntent.ACTION_BEHAVIORTRIGGER);
						intent.putExtra(RoboBrainIntent.EXTRA_BEHAVIORSTATE,
								true);
						intent.putExtra(RoboBrainIntent.EXTRA_BEHAVIORUUID,
								b.getId());
						Starter.this.sendBroadcast(intent);
					}
				};
				Thread thread = new Thread(behavior);
				thread.start();
				behaviorLayout.removeAllViews();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						addBehaviorButtons(behaviorLayout, cc);
						progressDialog.dismiss();
					}
				}, 500);
			}
		});
	}

	/**
	 * Sets the Click Listener for the Behavior Stop Button.
	 * 
	 * @param stopBehaviorButton
	 *            Button the Listener is set for
	 * @param b
	 *            Behavior that is to be stopped by click
	 * @param behaviorLayout
	 *            Layout in which the button resides
	 * @param cc
	 *            CommandCenter of the robot.
	 */
	protected void setStopBehaviorClickListener(
			final Button stopBehaviorButton, final Behavior b,
			final LinearLayout behaviorLayout, final CommandCenter cc) {
		stopBehaviorButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				progressDialog.setMessage("Sending Behavior stop signal");
				progressDialog.setTitle("Behavior Stopping");
				progressDialog.show();
				Runnable behaviorStopper = new Runnable() {
					public void run() {
						Intent intent = new Intent(
								RoboBrainIntent.ACTION_BEHAVIORTRIGGER);
						intent.putExtra(RoboBrainIntent.EXTRA_BEHAVIORSTATE,
								false);
						intent.putExtra(RoboBrainIntent.EXTRA_BEHAVIORUUID,
								b.getId());
						Starter.this.sendBroadcast(intent);
					}
				};
				Thread thread = new Thread(behaviorStopper);
				thread.start();
				behaviorLayout.removeAllViews();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						addBehaviorButtons(behaviorLayout, cc);
						progressDialog.dismiss();
					}
				}, 500);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_starter, menu);

		MenuItem console = menu.findItem(R.id.console_menu_item);
		final Intent cIntent = new Intent(this, Console.class);
		cIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		console.setIntent(cIntent);
		console.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				Starter.this.startActivity(cIntent);
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Add robot's behaviors to the layout
	 * 
	 * @param behaviorLayout
	 *            Layout the behaviors are added to
	 * @param cc
	 *            CommandCenter of the robot, containing all it's behaviors
	 */
	protected void addBehaviorButtons(final LinearLayout behaviorLayout,
			final CommandCenter cc) {
		boolean behaviorRunning = false;
		Behavior rb = null;
		for (Behavior b : cc.getBehaviors()) {
			if (b.isTurnedOn()) {
				behaviorRunning = true;
				rb = b;
				break;
			}
		}
		final Behavior runningBehavior = rb;
		if (!behaviorRunning) {
			for (final Behavior b : cc.getBehaviors()) {

				runOnUiThread(new Runnable() {

					public void run() {
						Button button = new Button(Starter.this);
						button.setText(b.getName());
						behaviorLayout.addView(button);
						setBehaviorButtonClickListener(button, b,
								behaviorLayout, cc);
					}
				});

			}

		} else {
			runOnUiThread(new Runnable() {

				public void run() {
					Button stopBehaviorButton = new Button(Starter.this);
					stopBehaviorButton.setText("Stop Behavior");
					setStopBehaviorClickListener(stopBehaviorButton,
							runningBehavior, behaviorLayout, cc);
					behaviorLayout.addView(stopBehaviorButton);
				}
			});

		}

	}

	/**
	 * Sets robot service and updates it's status
	 * 
	 * @param service
	 *            RobotService the Starter is controlling. Set to null if
	 *            Service is destroyed.
	 */
	public void setRobotService(RobotService service) {
		this.robotService = service;
		updateStatus();
	}

	/**
	 * Displays an error/warning dialog to show the user that something went
	 * wrong
	 * 
	 * @param title
	 *            Title of the dialog message
	 * @param message
	 *            Message to display
	 */
	public void showAlertDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Starter.this);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage(message).setTitle(title);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();

		dialog.show();

	}
}
