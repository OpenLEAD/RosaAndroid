package com.lead.inser;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.http.client.methods.HttpGet;

import android.content.Intent;

import com.lead.sensor.AngleJson;

public class WebInclination extends WebSensor {
	public WebInclination(HttpGet linkarg, String sensorarg, int counterarg,
			int base_frequencyarg) {
		super(linkarg, sensorarg, counterarg, base_frequencyarg);
		// TODO Auto-generated constructor stub
	}

	private AngleJson[] inclination;

	@Override
	protected void writeOnIntent(BufferedReader reader, Intent i)
			throws IllegalStateException, IOException {
		inclination = gson.fromJson(reader, AngleJson[].class);
		i.putExtra(sensor, inclination[0].value.rad);
	}

}
