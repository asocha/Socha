package com.final_project.socha;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements Button.OnClickListener{
	private Button start, scores, about, quit;
	private ArrayList<Integer> times = new ArrayList<Integer>();
	private int mostWon, currentWon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		start = (Button) findViewById(R.id.start);
		scores = (Button) findViewById(R.id.scores);
		about = (Button) findViewById(R.id.about);
		quit = (Button) findViewById(R.id.quit);
		
		start.setOnClickListener(this);
		scores.setOnClickListener(this);
		about.setOnClickListener(this);
		quit.setOnClickListener(this);
		
		//load times and win streak
		SharedPreferences settings = getPreferences(0);
		times.add(settings.getInt("time1", 3600));	//set to 1 hour if not loaded
		times.add(settings.getInt("time2", 3600));
		times.add(settings.getInt("time3", 3600));
		times.add(settings.getInt("time4", 3600));
		times.add(settings.getInt("time5", 3600));
		
		currentWon = settings.getInt("currentWon", 0);
		mostWon = settings.getInt("mostWon", 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == start){
			Intent intent = new Intent(this, Game.class);
			startActivityForResult(intent, 1);
		}
		else if (v == scores){
			Intent intent = new Intent(this, Scores.class);
			intent.putExtra("times", times);
			intent.putExtra("currentWon", currentWon);
			intent.putExtra("mostWon", mostWon);
			startActivity(intent);
		}
		else if (v == about){
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
		}
		else if (v == quit){
			finish();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1 && resultCode == RESULT_OK) {
			boolean won = data.getBooleanExtra("won", false);
			int time = data.getIntExtra("time", 0);
			if (won && time != 0){
				times.add(time);
				currentWon++;
				if (currentWon > mostWon) mostWon++;
			}
			else{
				currentWon = 0;
			}
		}
	}
	
	@Override
    protected void onPause(){
		super.onPause();
		
		//sort best times
		Collections.sort(times);

		//save best times and win streak
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putInt("time1", times.get(0));
		editor.putInt("time2", times.get(1));
		editor.putInt("time3", times.get(2));
		editor.putInt("time4", times.get(3));
		editor.putInt("time5", times.get(4));
		
		editor.putInt("currentWon", 0);
		editor.putInt("mostWon", 0);
		
		editor.commit();
    }

}
