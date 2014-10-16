package com.lead.inser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RockDataReceiver extends BroadcastReceiver {
	private MonitoringDisplay display;

	public RockDataReceiver(MonitoringDisplay d) {
		display = d;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean inductive_left_value = intent.getBooleanExtra(
				MonitoringDisplay.INDUCTIVE_LEFT, false);
		display.inductive_left(inductive_left_value);

		boolean inductive_right_value = intent.getBooleanExtra(
				MonitoringDisplay.INDUCTIVE_RIGHT, false);
		display.inductive_right(inductive_right_value);

		boolean inductive_key_value = intent.getBooleanExtra(
				MonitoringDisplay.INDUCTIVE_KEY, false);
		display.inductive_key(inductive_key_value);

		double inclination_body_value = intent.getDoubleExtra(
				MonitoringDisplay.INCLINATION_BODY, 0);
		display.inclination_body(inclination_body_value);

		double inclination_right_value = intent.getDoubleExtra(
				MonitoringDisplay.INCLINATION_RIGHT, 0);
		display.inclination_right(inclination_right_value);

		double inclination_key_value = intent.getDoubleExtra(
				MonitoringDisplay.INCLINATION_KEY, 0);
		display.inclination_key(inclination_key_value);

		double pressure_value = intent.getDoubleExtra(
				MonitoringDisplay.PRESSURE, 100000);
		display.pressure(pressure_value);

	}
}
