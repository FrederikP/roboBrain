package eu.fpetersen.robobrain.behavior;

import java.util.UUID;

import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.Robot;

//TODO: Requirement-Management for behaviors and robots.

/**
 * The Behavior class should be extended for implementing a specific new Robot
 * Behavior. For now the Robot is able to run one behavior at a time. N
 * behaviors can be created for each robot. It's important to make sure, that
 * the specific robot is compatible with the requirements of the behavior.
 * 
 * When implementing a Behavior class, please make sure to supply a default
 * constructor (i.e. don't specify any constructor). For now there is no way to
 * supply more parameters on initialization. This will change soon.
 * 
 * @author Frederik Petersen
 * 
 */
public abstract class Behavior {

	private boolean turnedOn = false;
	private Robot robot;
	private String name;
	private UUID id;

	/**
	 * Is to be called right after creating a new instance of this class. It
	 * basically has constructor functionality to allow dynamic contruction of
	 * the object (which needs the standard contructor).
	 * 
	 * @param robot
	 *            The robot this behavior is created for.
	 * @param name
	 *            The name of the behavior to be displayed in the UI.
	 */
	public void initialize(Robot robot, String name) {
		this.robot = robot;
		this.name = name;
	}

	/**
	 * Standard constructor to allow dynamically creating different subclasses.
	 * Creates a UUID to identify the behavior, making it easily addressable by
	 * the UI.
	 */
	protected Behavior() {
		id = UUID.randomUUID();
	}

	/**
	 * Starts the behavior, starting it's main loop.
	 */
	public void startBehavior() {
		if (turnedOn == false) {

			turnedOn = true;

			while (turnedOn) {
				behaviorLoop();
			}

			robot.stop();
		}
	}

	/**
	 * Turns the behavior off, stopping it's main loop.
	 */
	public void stopBehavior() {
		this.turnedOn = false;
	}

	/**
	 * 
	 * @return Name to be displayed by UI, as given in xml config
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The identifying id of this behavior
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * 
	 * @return True if beahvior is turned on, else false
	 */
	public boolean isTurnedOn() {
		return turnedOn;
	}

	/**
	 * Sends an intent to the Console UI, which is then able to display the
	 * message to the user
	 * 
	 * @param message
	 *            Message that is to be displayed in the Console UI
	 */
	public void toConsole(String message) {
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, robot.getName() + "-"
				+ name + ":" + message);
		RobotService.getInstance().sendBroadcast(cIntent);
	}

	/**
	 * 
	 * @return The robot that owns this behavior
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * This is the main loop of the behavior. All the logic happens here. Try to
	 * avoid making the thread sleep that's running this loop. Look for examples
	 * in existing behaviors
	 */
	protected abstract void behaviorLoop();

}
