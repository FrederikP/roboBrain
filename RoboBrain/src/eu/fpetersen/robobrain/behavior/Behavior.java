package eu.fpetersen.robobrain.behavior;

import java.util.UUID;

import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.Robot;

//TODO: Requirement-Management for behaviors and robots.

/**
 * @author Frederik Petersen
 * 
 *         The Behavior class should be extended for implementing a specific new
 *         Robot Behavior. For now the Robot is able to run one behavior at a
 *         time. N behaviors can be created for each robot. It's important to
 *         make sure, that the specific robot is compatible with the
 *         requirements of the behavior.
 * 
 *         When implementing a Behavior class, please make sure to supply a
 *         default constructor (i.e. don't specify any constructor). For now
 *         there is no way to supply more parameters on initialization. This
 *         will change soon.
 * 
 */
public abstract class Behavior {

	private boolean turnedOn = false;
	private Robot robot;
	private String name;
	private UUID id;

	public void initialize(Robot robot, String name) {
		this.robot = robot;
		this.name = name;
	}

	protected Behavior() {
		id = UUID.randomUUID();
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

	public Robot getRobot() {
		return robot;
	}

	protected abstract void behaviorLoop();

}
