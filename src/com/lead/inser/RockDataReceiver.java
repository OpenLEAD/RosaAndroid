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
		
		boolean inductive1_value = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE1, false);
		display.inductive1(inductive1_value);
		
		boolean inductive2_value = intent.getBooleanExtra(MonitoringDisplay.INDUCTIVE2, false);
		display.inductive2(inductive2_value);
		
		double inclination_value = intent.getDoubleExtra(MonitoringDisplay.INCLINATION, 0);
		display.inclination(inclination_value);
		
	}
}
