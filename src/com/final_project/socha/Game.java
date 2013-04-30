package com.final_project.socha;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class Game extends Activity {
	private int time;
	private boolean won;
	private TextView timeField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		time = 0;
		won = false;
		
		timeField = (TextView) findViewById(R.id.timeField);
		
		new CountDownTimer(3600000, 1000) {	//creates an hour-long countdown timer
		    public void onTick(long millisUntilFinished) {
		    	//convert the countdown timer into a countup timer
		        timeField.setText((3600 - (millisUntilFinished / 1000)) % 60 + ":" + (3600 - (millisUntilFinished / 1000)) / 60);
		        time = (int) (3600 - (millisUntilFinished / 1000));
		    }
	
		    public void onFinish() {
		        timeField.setText("Hour+");
		        time = 3600;
		    }
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("time",time);
        returnIntent.putExtra("won",won);
        setResult(RESULT_OK,returnIntent);        
        finish();
	}

}
