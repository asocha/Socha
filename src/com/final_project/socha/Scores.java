package com.final_project.socha;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Scores extends Activity implements Button.OnClickListener{
	private Button back;
	private ArrayList<Integer> times = new ArrayList<Integer>();
	private TextView time1, time2, time3, time4, time5, current, most;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);
		
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		
		time1 = (TextView) findViewById(R.id.time1);
		time2 = (TextView) findViewById(R.id.time2);
		time3 = (TextView) findViewById(R.id.time3);
		time4 = (TextView) findViewById(R.id.time4);
		time5 = (TextView) findViewById(R.id.time5);
		current = (TextView) findViewById(R.id.currentWins);
		most = (TextView) findViewById(R.id.mostWins);
		
		times = getIntent().getExtras().getIntegerArrayList("times");
		current.setText(String.valueOf(getIntent().getExtras().getInt("currentWon", 0)));
		most.setText(String.valueOf(getIntent().getExtras().getInt("mostWon", 0)));
		
		time1.setText("1. " + times.get(0) % 60 + " minutes, " + times.get(1) / 60 + " seconds");
		time2.setText("2. " + times.get(1) % 60 + " minutes, " + times.get(1) / 60 + " seconds");
		time3.setText("3. " + times.get(2) % 60 + " minutes, " + times.get(1) / 60 + " seconds");
		time4.setText("4. " + times.get(3) % 60 + " minutes, " + times.get(1) / 60 + " seconds");
		time5.setText("5. " + times.get(4) % 60 + " minutes, " + times.get(1) / 60 + " seconds");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scores, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == back) finish();
	}

}
