package com.lead.inser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class WebSensor {
	protected static final HttpClient httpclient = new DefaultHttpClient();
	protected static final Gson gson = new Gson();
	public int counter;
	protected HttpGet link;
	protected String sensor;
	private int base_frequency;
	
	protected abstract void writeOnIntent(BufferedReader reader, Intent i) throws IllegalStateException, IOException;
		
	public boolean ready(){
		return ((counter-=1)<=0);
	}
	
	public void reset(){
		counter = base_frequency;
	}
	
	public boolean readToIntent(Intent i) throws IllegalStateException, IOException{
		HttpResponse response = httpclient.execute(link);
		HttpEntity entity = response.getEntity();
		
		if (entity != null) {
			InputStreamReader instream = new InputStreamReader(entity.getContent(),"UTF-8");
			
			final BufferedReader reader = new BufferedReader(instream);
			
			try {
				writeOnIntent(reader,i);
			} catch (JsonSyntaxException e) {
				Log.e("Bad Json",
						"Assuming error, rising sample period.", e);
				e.printStackTrace();

				Random n = new Random();
				counter = base_frequency + 10 + n.nextInt(10);
				return false;
			}
			
			reset();
			return true;
		}
		return false;
		
	}
	
	
	
	public String getSensor(){
		return sensor;
	}
	
	WebSensor(){
	}
	
	WebSensor(HttpGet linkarg, String sensorarg, int counterarg, int base_frequencyarg){
		counter = counterarg;
		link = linkarg;
		sensor = sensorarg;
		base_frequency = base_frequencyarg;
	}
}
