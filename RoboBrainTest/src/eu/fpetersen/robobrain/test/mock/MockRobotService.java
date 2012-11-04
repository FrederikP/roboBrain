package eu.fpetersen.robobrain.test.mock;

import eu.fpetersen.robobrain.communication.RobotService;

public class MockRobotService extends RobotService {

	public void setRunning(boolean running) {
		this.running = running;
	}
}
