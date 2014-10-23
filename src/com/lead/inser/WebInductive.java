package com.lead.inser;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.http.client.methods.HttpGet;

import android.content.Intent;

import com.lead.sensor.InductiveJson;

public class WebInductive extends WebSensor {
	private InductiveJson[] inductive;

	public WebInductive(HttpGet linkarg, String sensorarg, int counterarg,
			int base_frequencyarg) {
		super(linkarg, sensorarg, counterarg, base_frequencyarg);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void writeOnIntent(BufferedReader reader, Intent i) throws IllegalStateException, IOException {
		inductive = gson.fromJson(reader,InductiveJson[].class);
		i.putExtra(sensor,inductive[0].value.data);
	}

}
