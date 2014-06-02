package com.lead.sonar;

import com.lead.inser.InsertActivity;
import com.lead.rosa.R;
import com.lead.rosa.R.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SonarActivity extends Activity {

	private FrameLayout display;
	private FrameLayout water;
	private ObjectAnimator submerge1;
	private ObjectAnimator submerge2;
	private AnimatorSet submerge;
	private ImageView sonar;
	private ImageView sonarbtn;
	private ProgressBar sonar_progress;
	private TextView sonar_loading_text;
	private RelativeLayout viga;

    
    // handler for the background updating
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
        	sonar_progress.incrementProgressBy(1);
        	sonar_loading_text.setText("CARREGANDO "+String.valueOf(sonar_progress.getProgress())+"%");
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sonar);
		sonarbtn = (ImageView) findViewById(R.id.imageView3);
		display = (FrameLayout) findViewById(R.id.animationFrame);
		water = (FrameLayout) findViewById(R.id.water);
		sonar = (ImageView) findViewById(R.id.imageView5);
		viga = (RelativeLayout) findViewById(R.id.relativelayoutgarra);
		sonar_progress = (ProgressBar) findViewById(R.id.progressBar1);
		sonar_loading_text = (TextView) findViewById(R.id.carregando);
		
		submerge1 = ObjectAnimator.ofFloat(water, "ScaleY",1).setDuration(2000);
		submerge2 = ObjectAnimator.ofFloat(water, "TranslationY",0).setDuration(2000);
		
		submerge = new AnimatorSet();
		submerge.play(submerge1).with(submerge2);
	}
	
	public void activate(View view){
		viga.animate().alpha(0.15f).setDuration(400).start();
		sonar_progress.animate().alpha(1).setDuration(200).start();
		sonar_loading_text.animate().alpha(1).setDuration(200).setListener(new AnimatorListenerAdapter(){

			@Override
			public void onAnimationEnd(Animator animation) {
			     // create a thread for updating the progress bar
		        Thread background = new Thread (new Runnable() {
		           public void run() {
		               try {
		                   // enter the code to be run while displaying the progressbar.
		                   //
		                   // This example is just going to increment the progress bar:
		                   // So keep running until the progress value reaches maximum value
		                   while (sonar_progress.getProgress()< sonar_progress.getMax()) {
								Thread.sleep(40);
		                       progressHandler.sendMessage(progressHandler.obtainMessage());
		                   }
		               } catch (java.lang.InterruptedException e) {
		                   // if something fails do something smart
		               }
		            sonar_progress.setAlpha(0);
		            sonar_progress.setProgress(0);
		           	sonar_loading_text.setText("CARREGANDO 0%");
           	    	startActivity(new Intent (SonarActivity.this, SonarViewActivity.class));
		           }
		        });
		         
		        // start the background thread
		        background.start();
				
			}
			
		}).start();
		
	}
	
	
	public void submerge(View view){
		
		if(submerge.isRunning())
			submerge.cancel();
		
		if(water.getTag().equals("full")){
			submerge1.setFloatValues(1);
			submerge2.setFloatValues(0);
			water.setTag("empty");
			
		}
		else{
			submerge1.setFloatValues(display.getMeasuredHeight()/2);
			submerge2.setFloatValues(-display.getMeasuredHeight()/2);
			water.setTag("full");
		}
		
		submerge.start();
		submerge.addListener(new AnimatorListenerAdapter(){

			@Override
			public void onAnimationEnd(Animator animation) {
				if(water.getTag().equals("full")){
					sonar.setImageResource(R.drawable.sonar_ativo);
					sonarbtn.animate().alpha(1).setDuration(200).start();
				}
				else{
					sonar.setImageResource(R.drawable.sonar_inativo);
					sonarbtn.animate().alpha(0).setDuration(200).start();
				}
			}
			
		});
		
		
	}
}
