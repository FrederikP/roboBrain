package eu.fpetersen.robobrain.communication;

import android.content.Intent;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;

/**
 * Holds all RoboBrain Intent String constants
 * 
 * @author Frederik Petersen
 * 
 */
public class RoboBrainIntent extends Intent {

	// -----ACTIONS-----//
	/**
	 * For outputting messages to the console activity
	 */
	public static final String ACTION_OUTPUT = "eu.fpetersen.robobrain.console.intent.action.OUTPUT";

	/**
	 * For turning behaviors on and off.
	 */
	public static final String ACTION_BEHAVIORTRIGGER = "eu.fpetersen.robobrain.console.intent.action.BEHAVIORTRIGGER";

	/**
	 * For turning off all Behaviors, for example when stopping RoboBrain
	 * Service.
	 */
	public static final String ACTION_STOPALLBEHAVIORS = "eu.fpetersen.robobrain.console.intent.actions.STOPALLBEHAVIORS";

	/**
	 * For sending speech results to a {@link DistributingSpeechReceiver}.
	 */
	public static final String ACTION_SPEECH = "eu.fpetersen.robobrain.console.intent.actions.SPEECH";

	// -----EXTRAS-----//
	/**
	 * Fill this with message to display in console activity, when sending
	 * intent with {@link RoboBrainIntent#ACTION_OUTPUT}.
	 */
	public static final String EXTRA_OUTPUT = "eu.fpetersen.robobrain.console.intent.extra.OUTPUT";

	/**
	 * Fill this with boolean to state if behavior should be started(true) or
	 * stopped(false) when sending intent with
	 * {@link RoboBrainIntent#ACTION_BEHAVIORTRIGGER}
	 */
	public static final String EXTRA_BEHAVIORSTATE = "eu.fpetersen.robobrain.console.intent.extra.BEHAVIORSTATE";

	/**
	 * Fill this with behaviors UUID to specify which behavior is to be started
	 * when sending intent with {@link RoboBrainIntent#ACTION_BEHAVIORTRIGGER}
	 */
	public static final String EXTRA_BEHAVIORUUID = "eu.fpetersen.robobrain.console.intent.extra.BEHAVIORUUID";

	/**
	 * Fill this with speech results String array when sending intent with
	 * {@link RoboBrainIntent#ACTION_SPEECH}
	 */
	public static final String EXTRA_SPEECH_RESULTS = "eu.fpetersen.robobrain.console.intent.extra.SPEECHRESULTS";
}
