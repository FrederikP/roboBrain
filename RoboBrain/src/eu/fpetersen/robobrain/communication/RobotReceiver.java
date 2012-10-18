package eu.fpetersen.robobrain.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import at.abraxas.amarino.AmarinoIntent;

public class RobotReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, final Intent intent) {

		String data = null;

		// the type of data which is added to the intent
		final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE,
				-1);

		// we only expect String data though, but it is better to
		// check if
		// really string was sent
		// later Amarino will support differnt data types, so far
		// data comes
		// always as string and
		// you have to parse the data to the type you have sent from
		// Arduino, like it is shown below
		if (dataType == AmarinoIntent.STRING_EXTRA) {
			data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);

			Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
			cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, data);
			context.sendBroadcast(cIntent);
		}

	}

}
