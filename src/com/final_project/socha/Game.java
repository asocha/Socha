/***
 * Sudoku board based on example from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * http://www.pragmaticprogrammer.com/titles/eband3
 ***/

package com.final_project.socha;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends Activity {
	private static final String TAG = "Sudoku";
	public static final String KEY_DIFFICULTY = "com.example.sudoku.difficulty";
	private static final String PREF_PUZZLE = "puzzle" ;
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	protected static final int DIFFICULTY_CONTINUE = -1;
	private int puzzle[];
	private PuzzleView puzzleView;

	private final String easyPuzzle = "360000000004230800000004200"
		+ "070460003820000014500013020" + "001900000007048300000000045";
	private final String mediumPuzzle = "650000070000506000014000005"
		+ "007009000002314700000700800" + "500000630000201000030000097";
	private final String hardPuzzle = "009000000080605020501078000"
		+ "000000700706040102004000000" + "000720903090301080000000600";
	
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
		    @Override
			public void onTick(long millisUntilFinished) {
		    	//convert the countdown timer into a countup timer
		        timeField.setText((3600 - (millisUntilFinished / 1000)) / 60 + "m " + (3600 - (millisUntilFinished / 1000)) % 60 + "s");
		        time = (int)(3600 - (millisUntilFinished / 1000));
		    }
	
		    @Override
			public void onFinish() {
		        timeField.setText("Hour+");
		        time = 3600;
		    }
		}.start();
		
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		puzzle = getPuzzle(diff);
		calculateUsedTiles();

		puzzleView = new PuzzleView(this);
		LinearLayout game = (LinearLayout) findViewById(R.id.game);
		game.addView(puzzleView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
		puzzleView.requestFocus();

		// If the activity is restarted, do a continue next time
		getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
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

	@Override
	protected void onResume() {
		super.onResume();
		//Music.play(this, R.raw.game);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		//Music.stop(this);
		// Save the current puzzle
		getPreferences(MODE_PRIVATE).edit()
				.putString(PREF_PUZZLE, toPuzzleString(puzzle)).commit();
	}

	/** Given a difficulty level, come up with a new puzzle */
	private int[] getPuzzle(int diff) {
		String puz;
		switch (diff) {
		case DIFFICULTY_CONTINUE:
			puz = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE,
					easyPuzzle);
			break;
		case DIFFICULTY_HARD:
			puz = hardPuzzle;
			break;
		case DIFFICULTY_MEDIUM:
			puz = mediumPuzzle;
			break;
		case DIFFICULTY_EASY:
		default:
			puz = easyPuzzle;
			break;
		}

		return fromPuzzleString(puz);
	}

	/** Convert an array into a puzzle string */
	static private String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}

	/** Convert a puzzle string into an array */
	static protected int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}

	/** Return the tile at the given coordinates */
	private int getTile(int x, int y) {
		return puzzle[y * 9 + x];
	}

	/** Change the tile at the given coordinates */
	protected void setTile(int x, int y, int value) {
		puzzle[y * 9 + x] = value;
		calculateUsedTiles();
	}

	/** Return a string for the tile at the given coordinates */
	protected String getTileString(int x, int y) {
		int v = getTile(x, y);
		if (v == 0)
			return "";
		else
			return String.valueOf(v);
	}

	/** Open the keypad if there are any valid moves */
	protected void showKeypadOrError(int x, int y) {
		int tiles[] = getUsedTiles(x, y);
		if (tiles.length == 9) {
			Toast toast = Toast.makeText(this, R.string.no_moves_label,
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else if (getTile(x, y) == 0){
			Log.d(TAG, "showKeypad: used=" + toPuzzleString(tiles));
			Dialog v = new Keypad(this, tiles, puzzleView);
			v.show();
		}
	}

	/** Cache of used tiles */
	private final int used[][][] = new int[9][9][];

	/** Return cached used tiles visible from the given coords */
	protected int[] getUsedTiles(int x, int y) {
		return used[x][y];
	}

	/** Compute the two dimensional array of used tiles */
	private void calculateUsedTiles() {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				used[x][y] = calculateUsedTiles(x, y);
				// Log.d(TAG, "used[" + x + "][" + y + "] = " + toPuzzleString(used[x][y]));
			}
		}
	}

	/** Compute the used tiles visible from this position */
	private int[] calculateUsedTiles(int x, int y) {
		int c[] = new int[9];
		// horizontal
		for (int i = 0; i < 9; i++) {
			if (i == x)
				continue;
			int t = getTile(i, y);
			if (t != 0)
				c[t - 1] = t;
		}
		// vertical
		for (int i = 0; i < 9; i++) {
			if (i == y)
				continue;
			int t = getTile(x, i);
			if (t != 0)
				c[t - 1] = t;
		}
		// same cell block
		int startx = (x / 3) * 3;
		int starty = (y / 3) * 3;
		for (int i = startx; i < startx + 3; i++) {
			for (int j = starty; j < starty + 3; j++) {
				if (i == x && j == y)
					continue;
				int t = getTile(i, j);
				if (t != 0)
					c[t - 1] = t;
			}
		}
		// compress
		int nused = 0;
		for (int t : c) {
			if (t != 0)
				nused++;
		}
		int c1[] = new int[nused];
		nused = 0;
		for (int t : c) {
			if (t != 0)
				c1[nused++] = t;
		}
		return c1;
	}

}
