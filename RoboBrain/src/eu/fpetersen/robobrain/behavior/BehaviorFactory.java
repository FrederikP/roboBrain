package eu.fpetersen.robobrain.behavior;

import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.util.RoboLog;

public class BehaviorFactory {

	private static BehaviorFactory instance;

	private BehaviorFactory() {

	}

	public static BehaviorFactory getInstance() {
		if (instance == null) {
			instance = new BehaviorFactory();
		}
		return instance;
	}

	/**
	 * Creates a Behavior instance for the specified Behavior name. The behavior
	 * is expected to be in the package "eu.fpetersen.robobrain.behavior" and
	 * the class should be called: NAME.java . In this case NAME would be passed
	 * to the method to instantiate the class.
	 * 
	 * @param name
	 *            Name of the Behavior class.
	 * @param robot
	 *            The robot this behavior is supposed to be created for.
	 * @return Behavior instance or null if something went wrong. If something
	 *         is amiss, check log.
	 */
	public Behavior createBehavior(String name, Robot robot) {
		String behaviorClassName = "eu.fpetersen.robobrain.behavior." + name;
		Behavior behavior = null;
		try {
			behavior = (Behavior) Class.forName(behaviorClassName)
					.newInstance();
			behavior.initialize(robot, name);
		} catch (InstantiationException e) {
			RoboLog.log("Behavior class " + name + " could not be instantiated");
		} catch (IllegalAccessException e) {
			RoboLog.log("Behavior class "
					+ name
					+ " could not be instantiated due to the constructor having restricted access");
		} catch (ClassNotFoundException e) {
			RoboLog.log("Behavior class " + name + " could not be found");
		}
		return behavior;
	}

}
