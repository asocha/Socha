package com.final_project.socha;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements Button.OnClickListener{
	private Button start, scores, about, quit, easy, medium, hard;
	private ArrayList<Integer> times = new ArrayList<Integer>();
	private int mostWon, currentWon;
	private boolean continueGame;
	private RelativeLayout difficulties;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		start = (Button) findViewById(R.id.start);
		scores = (Button) findViewById(R.id.scores);
		about = (Button) findViewById(R.id.about);
		quit = (Button) findViewById(R.id.quit);
		easy = (Button) findViewById(R.id.easy);
		medium = (Button) findViewById(R.id.medium);
		hard = (Button) findViewById(R.id.hard);
		
		start.setOnClickListener(this);
		scores.setOnClickListener(this);
		about.setOnClickListener(this);
		quit.setOnClickListener(this);
		easy.setOnClickListener(this);
		medium.setOnClickListener(this);
		hard.setOnClickListener(this);
		
		difficulties = (RelativeLayout) findViewById(R.id.difficulties);
		
		//load best times, win streak, saved game
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		times.add(settings.getInt("time1", 3600));	//set to 1 hour if not loaded
		times.add(settings.getInt("time2", 3600));
		times.add(settings.getInt("time3", 3600));
		times.add(settings.getInt("time4", 3600));
		times.add(settings.getInt("time5", 3600));
		
		currentWon = settings.getInt("currentWon", 0);
		mostWon = settings.getInt("mostWon", 0);
		continueGame = settings.getBoolean("continueGame", false);
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
			if (continueGame){
				Intent intent = new Intent(this, Game.class);
				intent.putExtra(Game.KEY_DIFFICULTY, Game.DIFFICULTY_CONTINUE);
				startActivityForResult(intent, 1);
			}
			else{
				start.setVisibility(View.GONE);
				difficulties.setVisibility(View.VISIBLE);
			}
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
		else if (v == easy){
			Intent intent = new Intent(this, Game.class);
			intent.putExtra(Game.KEY_DIFFICULTY, Game.DIFFICULTY_EASY);
			startActivityForResult(intent, 1);
		}
		else if (v == medium){
			Intent intent = new Intent(this, Game.class);
			intent.putExtra(Game.KEY_DIFFICULTY, Game.DIFFICULTY_MEDIUM);
			startActivityForResult(intent, 1);
		}
		else if (v == hard){
			Intent intent = new Intent(this, Game.class);
			intent.putExtra(Game.KEY_DIFFICULTY, Game.DIFFICULTY_HARD);
			startActivityForResult(intent, 1);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1 && resultCode == RESULT_OK) {
			start.setVisibility(View.VISIBLE);
			difficulties.setVisibility(View.GONE);
			
			continueGame = data.getBooleanExtra("continueGame", false);
			boolean won = data.getBooleanExtra("won", false);
			int time = data.getIntExtra("time", 0);
			if (won && time != 0){ //player won game, update stats
				times.add(time);
				Collections.sort(times); //sort best times
				currentWon++;
				if (currentWon > mostWon) mostWon++;
			}
			else if (!continueGame){ //player lost game
				currentWon = 0;
			}
			//do nothing if player saved game
		}
	}
	
	@Override
    protected void onPause(){
		super.onPause();

		//save best times, win streaks, and saved game
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putInt("time1", times.get(0));
		editor.putInt("time2", times.get(1));
		editor.putInt("time3", times.get(2));
		editor.putInt("time4", times.get(3));
		editor.putInt("time5", times.get(4));
		
		editor.putInt("currentWon", currentWon);
		editor.putInt("mostWon", mostWon);
		editor.putBoolean("continueGame", continueGame);
		
		editor.commit();
    }

}
