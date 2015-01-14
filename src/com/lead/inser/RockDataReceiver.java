package com.lead.inser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RockDataReceiver extends BroadcastReceiver {
	private MonitoringDisplay display;

	private final double DUMMY_ANGLE = 200;

	public RockDataReceiver(MonitoringDisplay d) {
		display = d;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		/* 1 */
		boolean inductive_left_status = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_LEFT + MonitoringDisplay.STATUS,	false);
		boolean inductive_left_value = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_LEFT, false);
		display.inductive_left(inductive_left_value, inductive_left_status); 

		/* 2 */
		boolean inductive_right_status = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_RIGHT + MonitoringDisplay.STATUS,false);
		boolean inductive_right_value = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_RIGHT, false);
		display.inductive_right(inductive_right_value, inductive_right_status); 

		/* 3 */
		boolean inductive_key_attached_status = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_KEY_ATTACHED + MonitoringDisplay.STATUS, false);
		boolean inductive_key_attached_value = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_KEY_ATTACHED, false);
		display.inductive_key_attached(inductive_key_attached_value, inductive_key_attached_status); 

		/* 4 */
		boolean inductive_key_detached_status = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_KEY_DETACHED	+ MonitoringDisplay.STATUS, false);
		boolean inductive_key_detached_value = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_KEY_DETACHED, false);
		display.inductive_key_detached(inductive_key_detached_value, inductive_key_detached_status); 

		/* 5 */
		boolean inclination_body_status = intent.getBooleanExtra(MonitoringDisplay.INCLINATION_BODY + MonitoringDisplay.STATUS,false);
		double inclination_body_value = intent.getDoubleExtra(MonitoringDisplay.INCLINATION_BODY, DUMMY_ANGLE);
		display.inclination_body(inclination_body_value,inclination_body_status); 

		/* 6 */
		boolean pressure_status = intent.getBooleanExtra(MonitoringDisplay.PRESSURE + MonitoringDisplay.STATUS, false);
		float pressure_value = intent.getFloatExtra(MonitoringDisplay.PRESSURE, -1);
		display.pressure(pressure_value,pressure_status);
 
		display.end_connection();

	}
}
