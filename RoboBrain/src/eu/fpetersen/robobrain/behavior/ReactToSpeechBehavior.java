package eu.fpetersen.robobrain.behavior;

import java.util.List;
import java.util.StringTokenizer;

import eu.fpetersen.robobrain.color.RGBColor;
import eu.fpetersen.robobrain.color.RGBColorTable;
import eu.fpetersen.robobrain.color.RGBColorTableFactory;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.RGBLED;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.speech.SpeechReceiver;
import eu.fpetersen.robobrain.speech.SpeechRecognizerService;
import eu.fpetersen.robobrain.util.RoboLog;

public class ReactToSpeechBehavior extends Behavior implements SpeechReceiver {

	protected static final String TAG = "ReactToSpeech-Behavior";
	private RGBColorTable colorTable;
	private List<String> colorNames;

	public ReactToSpeechBehavior(Robot robot, String name) {
		super(robot, name);
		colorTable = RGBColorTableFactory.getInstance()
				.getStandardColorTableFromTextFile();
		colorNames = colorTable.getNames();
	}

	protected void interpretSpeechResults(List<String> results) {
		setLED(results);
	}

	private void setLED(List<String> results) {
		RGBLED led = getRobot().getHeadColorLED();
		boolean colorMatch = false;
		for (String s : results) {
			for (String colorName : colorNames) {
				if (colorName.contains(" ")) {
					colorMatch = checkMultipleWordColorName(s, colorName);
				} else {
					if (s.contains(colorName)) {
						colorMatch = true;
					}
				}

				if (colorMatch) {
					RGBColor color = colorTable.getColorForName(colorName);
					RoboLog.log("Displaying color: " + colorName);
					led.set(color.getRed(), color.getGreen(), color.getBlue());
					break;
				}
			}
			if (colorMatch) {
				break;
			}
		}
	}

	private boolean checkMultipleWordColorName(String s, String colorName) {
		boolean match = false;
		StringTokenizer tokenizer = new StringTokenizer(colorName);
		int matchCount = 0;
		int wordCount = tokenizer.countTokens();
		while (tokenizer.hasMoreTokens()) {
			if (s.contains(tokenizer.nextToken())) {
				matchCount++;
			}
		}
		if (matchCount == wordCount) {
			match = true;
		}
		return match;
	}

	@Override
	public void startBehavior() {

		RobotService.getInstance().getDistributingSpeechReceiver()
				.addReceiver(ReactToSpeechBehavior.this);
		if (SpeechRecognizerService.getInstance() != null) {
			super.startBehavior();
		} else {
			RoboLog.log("Cannot connect to speech Recognition Service.");
			stopBehavior();
		}

	}

	@Override
	protected void behaviorLoop() {

	}

	@Override
	public void stopBehavior() {
		RobotService.getInstance().getDistributingSpeechReceiver()
				.removeReceiver(ReactToSpeechBehavior.this);
		super.stopBehavior();
	}

	public void onReceive(List<String> results) {
		interpretSpeechResults(results);
	}

}
