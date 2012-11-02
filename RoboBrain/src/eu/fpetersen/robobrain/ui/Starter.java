package eu.fpetersen.robobrain.ui;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
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
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.speech.SpeechRecognizerService;

public class Starter extends Activity {

	private TextView statusTV;
	private ToggleButton toggleStatusB;
	private TableLayout robotBehaviorTable;

	// For displaying progress circle stuff
	private ProgressDialog dialog;

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
		dialog = new ProgressDialog(Starter.this);

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

	protected void handleStatusToggle(boolean isChecked) {
		if (isChecked) {
			dialog.setMessage("Starting Robobrain Service");
			dialog.setTitle("Service Starting");
			dialog.show();
			toggleStatusB.post(new Runnable() {
				public void run() {
					startService(new Intent(getApplicationContext(),
							SpeechRecognizerService.class));
					startService(new Intent(getApplicationContext(),
							RobotService.class));
				}
			});

		} else {
			dialog.setMessage("Stopping Robobrain Service");
			dialog.setTitle("Service Stopping");
			dialog.show();
			toggleStatusB.post(new Runnable() {
				public void run() {
					dialog.setMessage("Sending stop behavior signal");
					Intent intent = new Intent(
							RoboBrainIntent.ACTION_STOPALLBEHAVIORS);
					Starter.this.sendBroadcast(intent);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							dialog.setMessage("Sending service stop signals");
							stopService(new Intent(getApplicationContext(),
									RobotService.class));
							stopService(new Intent(getApplicationContext(),
									SpeechRecognizerService.class));
						}
					}, 500);

				}
			});
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateStatus();
				dialog.dismiss();
			}
		}, 3000);
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
					robotBehaviorTable.setVisibility(View.INVISIBLE);
					cleanupRobotBehaviorTable();
				}
			});

		} else {
			runOnUiThread(new Runnable() {

				public void run() {
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

	protected void cleanupRobotBehaviorTable() {
		for (int i = 1; i < robotBehaviorTable.getChildCount(); i++) {
			robotBehaviorTable.removeViewAt(i);
		}

	}

	protected void setupRobotBehaviorTable() {
		Collection<CommandCenter> ccs = CommandCenter.getAllCCs();
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

	private void setBehaviorButtonClickListener(final Button button,
			final Behavior b, final LinearLayout behaviorLayout,
			final CommandCenter cc) {
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.setMessage("Sending Behavior start signal");
				dialog.setTitle("Behavior Starting");
				dialog.show();
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
						dialog.dismiss();
					}
				}, 500);
			}
		});
	}

	protected void setStopBehaviorClickListener(
			final Button stopBehaviorButton, final Behavior b,
			final LinearLayout behaviorLayout, final CommandCenter cc) {
		stopBehaviorButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.setMessage("Sending Behavior stop signal");
				dialog.setTitle("Behavior Stopping");
				dialog.show();
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
						dialog.dismiss();
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
}
