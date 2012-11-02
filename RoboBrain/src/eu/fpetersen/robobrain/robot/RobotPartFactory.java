package eu.fpetersen.robobrain.robot;

import eu.fpetersen.robobrain.util.RoboLog;

public class RobotPartFactory {

	private static RobotPartFactory instance;

	private RobotPartFactory() {

	}

	public static RobotPartFactory getInstance() {
		if (instance == null) {
			instance = new RobotPartFactory();
		}
		return instance;
	}

	/**
	 * Creates a RobotPart instance for the specified RobotPart type. The
	 * RobotPart is expected to be in the package "eu.fpetersen.robobrain.robot"
	 * and the class should be called: TYPE.java . In this case TYPE would be
	 * passed to the method to instantiate the class.
	 * 
	 * @param type
	 *            Name of the RobotPart implementation.
	 * @param robot
	 *            The robot this part is supposed to be created for.
	 * @return RobotPart instance or null if something went wrong. If something
	 *         is amiss, check log.
	 */
	public RobotPart createRobotPart(String type, Robot robot) {
		String partClassName = "eu.fpetersen.robobrain.robot." + type;
		RobotPart part = null;
		try {
			part = (RobotPart) Class.forName(partClassName).newInstance();
			part.initialize(robot);
		} catch (InstantiationException e) {
			RoboLog.log("RobotPart class " + type
					+ " could not be instantiated");
		} catch (IllegalAccessException e) {
			RoboLog.log("RobotPart class "
					+ type
					+ " could not be instantiated due to the constructor having restricted access");
		} catch (ClassNotFoundException e) {
			RoboLog.log("RobotPart class " + type + " could not be found");
		}
		return part;
	}

}
