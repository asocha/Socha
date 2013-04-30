package com.final_project.socha;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{
	private Button start, scores, about, quit;
	private ArrayList<Integer> times = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		start = (Button) findViewById(R.id.start);
		scores = (Button) findViewById(R.id.scores);
		about = (Button) findViewById(R.id.about);
		quit = (Button) findViewById(R.id.quit);
		
		//load times
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
			startActivity(intent);
		}
		else if (v == about){
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
		}
		else if (v == quit){
			//save times
			finish();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1 && resultCode == RESULT_OK) {
			boolean won = data.getBooleanExtra("won", false);
			int time = data.getIntExtra("time", 0);
			if (won && time != 0) times.add(time);
		}
	}

}
