package eu.fpetersen.robobrain.behavior;

import java.util.List;
import java.util.StringTokenizer;

import eu.fpetersen.robobrain.color.RGBColor;
import eu.fpetersen.robobrain.color.RGBColorTable;
import eu.fpetersen.robobrain.color.RGBColorTableFactory;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.RGBLED;
import eu.fpetersen.robobrain.speech.SpeechReceiver;
import eu.fpetersen.robobrain.speech.SpeechRecognizerService;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * This Behavior reacts to speech commands. For now it changes the Robot's LED
 * color to what the user says. There are different colornames imported from a
 * long list of color names. When speech is detected, the longest possible color
 * name is matched.
 * 
 * @author Frederik Petersen
 * 
 * 
 */
public class ReactToSpeechBehavior extends Behavior implements SpeechReceiver {

	protected static final String TAG = "ReactToSpeech-Behavior";
	private RGBColorTable colorTable;
	private List<String> colorNames;

	/**
	 * Creates the colorTable upon instantiation
	 */
	public ReactToSpeechBehavior() {
		colorTable = RGBColorTableFactory.getInstance()
				.getStandardColorTableFromTextFile();
		colorNames = colorTable.getNames();
	}

	/**
	 * Evaluate speech results to look for colornames. Colornames are evaluated
	 * from most words to one word, to set a priority on longer color names. If
	 * color names are longer than one word,
	 * {@link eu.fpetersen.robobrain.behavior.ReactToSpeechBehavior#checkMultipleWordColorName(String, String)
	 * checkMultipleWordColorName()} is called.
	 * 
	 * @param results
	 *            Result String list of Speech results.
	 */
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

	/**
	 * Check if name can be matched with that colorName. All words must be found
	 * in input string
	 * 
	 * @param s
	 *            Input String for which the method checks if colorname is
	 *            included
	 * @param colorName
	 *            Colorname for which the String s is checked.
	 * @return True if colorName is in String s.
	 */
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
		setLED(results);
	}

}
