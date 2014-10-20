package com.lead.rosa;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

import com.lead.inser.InsertActivity;
import com.lead.rosa.R;
import com.lead.sonar.SonarActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void inser(View view) {
		startActivity(new Intent(this, InsertActivity.class));
	}

	public void remo(View view) {
		startActivity(new Intent(this, InsertActivity.class));
	}

	public void sonar(View view) {
		startActivity(new Intent(this, SonarActivity.class));
	}
}