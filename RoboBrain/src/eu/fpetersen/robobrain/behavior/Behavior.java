package eu.fpetersen.robobrain.behavior;

import java.util.UUID;

import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.Robot;

public abstract class Behavior {

	private boolean turnedOn = false;
	private Robot robot;
	private String name;
	private UUID id;

	public Behavior(Robot robot, String name) {
		id = UUID.randomUUID();
		this.robot = robot;
		this.name = name;
	}

	private void turnOff() {
		this.turnedOn = false;
	}

	public void startBehavior() {
		if (turnedOn == false) {

			turnedOn = true;

			while (turnedOn) {
				behaviorLoop();
			}

			robot.stop();
		}
	}

	public void stopBehavior() {
		turnOff();
	}

	protected abstract void behaviorLoop();

	public String getName() {
		return name;
	}

	public UUID getId() {
		return id;
	}

	public boolean isTurnedOn() {
		return turnedOn;
	}

	public void toConsole(String message) {
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, robot.getName() + "-"
				+ name + ":" + message);
		RobotService.getInstance().sendBroadcast(cIntent);
	}

}
