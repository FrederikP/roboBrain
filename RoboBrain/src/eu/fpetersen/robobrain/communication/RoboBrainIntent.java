package eu.fpetersen.robobrain.communication;

import android.content.Intent;

public class RoboBrainIntent extends Intent {
	public static final String ACTION_OUTPUT = "eu.fpetersen.robobrain.console.intent.action.OUTPUT";
	public static final String ACTION_BEHAVIORTRIGGER = "eu.fpetersen.robobrain.console.intent.action.BEHAVIORTRIGGER";
	public static final String EXTRA_OUTPUT = "eu.fpetersen.robobrain.console.intent.extra.OUTPUT";
	public static final String EXTRA_BEHAVIORSTATE = "eu.fpetersen.robobrain.console.intent.extra.BEHAVIORSTATE";
	public static final String EXTRA_BEHAVIORUUID = "eu.fpetersen.robobrain.console.intent.extra.BEHAVIORUUID";
	public static final String ACTION_STOPALLBEHAVIORS = "eu.fpetersen.robobrain.console.intent.actions.STOPALLBEHAVIORS";
}
