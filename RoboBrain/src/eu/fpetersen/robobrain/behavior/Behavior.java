package eu.fpetersen.robobrain.behavior;

import java.util.UUID;

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
		turnedOn = true;

		while (turnedOn) {
			behaviorLoop();
		}

		robot.stop();
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

}
