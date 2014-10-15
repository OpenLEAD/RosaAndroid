package com.lead.inser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.lead.sensor.AngleJson;
import com.lead.sensor.InductiveJson;

public class BGJloop extends Service {

	private Looper mServiceLooper;
	private boolean runningFlag;
	private ServiceHandler mServiceHandler;
	static final int JASON_FROM_URL = 1;

	String urlString;
	ArrayList<Pair<HttpGet, String>> httpGetpair;

	@Override
	public void onCreate() {

		HandlerThread thread = new HandlerThread("BGJloopThread",
				android.os.Process.THREAD_PRIORITY_BACKGROUND);

		thread.start();
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		urlString = intent.getStringExtra("url");
		intent.removeExtra("url");

		Bundle sensors_bundle = intent.getExtras();

		httpGetpair = new ArrayList<Pair<HttpGet, String>>();

		for (String sensor : sensors_bundle.keySet()) {
			Object address = sensors_bundle.get(sensor);
			httpGetpair.add(new Pair<HttpGet, String>(new HttpGet(urlString
					+ address.toString()), sensor));
		}

		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.arg2 = JASON_FROM_URL;
		runningFlag = true;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Toast.makeText(this, "service destroying",
		// Toast.LENGTH_SHORT).show();
		runningFlag = false;
		mServiceLooper.quit();
	}

	@SuppressWarnings("unused")
	private double decodeInclinometer(InputStream instream) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(instream,
				"UTF-8"));
		reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();

			while (reader.hasNext()) {
				String propertyname = reader.nextName();

				if (propertyname.equals("value")) {

					reader.beginObject();

					while (reader.hasNext()) {
						String valuename = reader.nextName();

						if (valuename.equals("rad")) {
							double value = reader.nextDouble();
							reader.close();
							return value;
						} else {
							reader.skipValue();

						}

					}

					reader.endObject();

				} else {
					reader.skipValue();

				}

			}

			reader.endObject();
		}

		reader.endArray();

		reader.close();

		throw new IllegalArgumentException("No boolean data to read.");

	}

	@SuppressWarnings("unused")
	private boolean decodeInductive(InputStream instream) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(instream,
				"UTF-8"));
		reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();

			while (reader.hasNext()) {
				String propertyname = reader.nextName();

				if (propertyname.equals("value")) {

					reader.beginObject();

					while (reader.hasNext()) {
						String valuename = reader.nextName();

						if (valuename.equals("data")) {
							boolean value = reader.nextBoolean();
							reader.close();
							return value;
						} else {
							reader.skipValue();

						}

					}

					reader.endObject();

				} else {
					reader.skipValue();

				}

			}

			reader.endObject();
		}

		reader.endArray();

		reader.close();

		throw new IllegalArgumentException("No boolean data to read.");

	}

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			final HttpClient httpclient = new DefaultHttpClient();
			final Gson gson = new Gson();

			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
			if (msg.arg2 == JASON_FROM_URL)
				while (runningFlag) {
					Intent localIntent = new Intent(
							MonitoringDisplay.NEW_MONITOR_DATA);

					for (Pair<HttpGet, String> httpsensor : httpGetpair) {

						try {
							HttpResponse response = httpclient
									.execute(httpsensor.first);

							HttpEntity entity = response.getEntity();
							if (entity != null) {
								InputStream instream = entity.getContent();
								final BufferedReader reader = new BufferedReader(
										new InputStreamReader(instream));

								switch (httpsensor.second) {

								case MonitoringDisplay.INDUCTIVE1:
									InductiveJson[] inductive1 = gson.fromJson(
											reader, InductiveJson[].class);
									localIntent.putExtra(
											MonitoringDisplay.INDUCTIVE1,
											inductive1[0].value.data);
									break;

								case MonitoringDisplay.INDUCTIVE2:
									InductiveJson[] inductive2 = gson.fromJson(
											reader, InductiveJson[].class);
									localIntent.putExtra(
											MonitoringDisplay.INDUCTIVE2,
											inductive2[0].value.data);
									break;

								case MonitoringDisplay.INCLINATION:
									AngleJson[] inclination = gson.fromJson(
											reader, AngleJson[].class);
									localIntent.putExtra(
											MonitoringDisplay.INCLINATION,
											inclination[0].value.rad);
									break;

								default:
									break;

								}
							}

						} catch (Exception e) {
							Log.e("Rosa WebService",
									"Cannot access/read webservice.", e);
							e.printStackTrace();
						}
					}

					LocalBroadcastManager.getInstance(BGJloop.this)
							.sendBroadcast(localIntent);

				}
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
