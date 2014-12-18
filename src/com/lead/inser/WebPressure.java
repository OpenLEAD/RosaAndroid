package com.lead.inser;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.http.client.methods.HttpGet;

import android.content.Intent;

import com.lead.sensor.PressureJson;

public class WebPressure extends WebSensor {
	private PressureJson[] pressure;

	public WebPressure(HttpGet linkarg, String sensorarg, int counterarg,
			int base_frequencyarg) {
		super(linkarg, sensorarg, counterarg, base_frequencyarg);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void writeOnIntent(BufferedReader reader, Intent i)
			throws IllegalStateException, IOException {

		pressure = gson.fromJson(reader, PressureJson[].class);
		i.putExtra(sensor, pressure[0].value.pascal);

	}

}
