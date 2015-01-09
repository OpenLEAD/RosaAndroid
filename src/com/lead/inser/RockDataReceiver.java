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

		int error_number = 0;
		
		
		
		if (intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_LEFT + MonitoringDisplay.STATUS,
				false)) {
			boolean inductive_left_value = intent.getBooleanExtra(
					MonitoringDisplay.INDUCTIVE_LEFT, false);
			display.inductive_left(inductive_left_value);
		}
		else
			error_number += 1; /*1*/

		if (intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_RIGHT + MonitoringDisplay.STATUS,
				false)) {
			boolean inductive_right_value = intent.getBooleanExtra(
					MonitoringDisplay.INDUCTIVE_RIGHT, false);
			display.inductive_right(inductive_right_value);
		}
		else
			error_number += 1; /*2*/

		if (intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_KEY_ATTACHED + MonitoringDisplay.STATUS,
				false)) {
			boolean inductive_key_attached_value = intent.getBooleanExtra(
					MonitoringDisplay.INDUCTIVE_KEY_ATTACHED, false);
			display.inductive_key_attached(inductive_key_attached_value);
		}
		else
			error_number += 1; /*3*/

		if (intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE_KEY_DETACHED + MonitoringDisplay.STATUS,
				false)) {
			boolean inductive_key_detached_value = intent.getBooleanExtra(
					MonitoringDisplay.INDUCTIVE_KEY_DETACHED, false);
			display.inductive_key_detached(inductive_key_detached_value);
		}
		else
			error_number += 1; /*4*/

		if (intent.getBooleanExtra(MonitoringDisplay.INCLINATION_BODY + MonitoringDisplay.STATUS,
				false)) {
			double inclination_body_value = intent.getDoubleExtra(
					MonitoringDisplay.INCLINATION_BODY, DUMMY_ANGLE);
			if (inclination_body_value != DUMMY_ANGLE)
				display.inclination_body(inclination_body_value);
		}
		else
			error_number += 1; /*5*/

		if (intent.getBooleanExtra(MonitoringDisplay.PRESSURE + MonitoringDisplay.STATUS,
				false)) {
			float pressure_value = intent.getFloatExtra(
					MonitoringDisplay.PRESSURE, -1);
			if (pressure_value > 0)
				display.pressure(pressure_value);
		}
		else
			error_number += 1; /*6*/
		
		if(error_number>0)
			display.bad_connection(error_number);
		
	}
}
