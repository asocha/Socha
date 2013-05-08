/***
 * Sudoku board based on example from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * http://www.pragmaticprogrammer.com/titles/eband3
 ***/

package com.final_project.socha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View {
	private float width; // width of one tile
	private float height; // height of one tile
	protected int selX; // X index of selection
	protected int selY; // Y index of selection
	private final Rect selRect = new Rect();
	
	private Rect r = new Rect();
	private Paint paint = new Paint();	//general (background, hints, selected, etc.)
	private Paint dark = new Paint();	//large grid lines
	private Paint light = new Paint();	//small grid lines
	private Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);	//numbers
	
	protected boolean hintsEnabled;
	
	private final Game game;

	public PuzzleView(Context context) {
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		hintsEnabled = false;
		
		// Define colors for the grid lines
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		// Define color and style for numbers
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = (w-1) / 9f;
		height = (h-1) / 9f;
		getRect(selX, selY, selRect);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the background...
		paint.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

		// Draw the board...
		
		// Draw the hints...

		// Pick a hint color based on #moves left
		if (hintsEnabled){
			int c[] = {getResources().getColor(R.color.puzzle_hint_0),
				getResources().getColor(R.color.puzzle_hint_1),
				getResources().getColor(R.color.puzzle_hint_2)};
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (game.getTile(i, j) != 0) continue;
					int movesleft = 9 - game.getUsedTiles(i, j).length;
					if (movesleft <= c.length) {
						getRect(i, j, r);
						paint.setColor(c[movesleft-1]);
						canvas.drawRect(r, paint);
					}
				}
			}
		}
		
		// Draw the selection...

		paint.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, paint);
		
		// Draw the minor grid lines
		for (int i = 1; i < 9; i++) {
			if (i % 3 == 0)
				continue;
			canvas.drawLine(0, i * height, getWidth(), i * height, light);
			canvas.drawLine(i * width, 0, i * width, getHeight(), light);
		}
		// Draw the major grid lines
		for (int i = 0; i <= 9; i+=3) {
			canvas.drawLine(0, i * height, getWidth(), i * height, dark);
			canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
		}

		// Draw the numbers...

		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		// Draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		select((int) (event.getX() / width), (int) (event.getY() / height));
		game.showKeypadOrError(selX, selY);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX, selY - 1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX, selY + 1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX - 1, selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX + 1, selY);
			break;

		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE:
			setSelectedTile(0);
			break;
		case KeyEvent.KEYCODE_1:
			setSelectedTile(1);
			break;
		case KeyEvent.KEYCODE_2:
			setSelectedTile(2);
			break;
		case KeyEvent.KEYCODE_3:
			setSelectedTile(3);
			break;
		case KeyEvent.KEYCODE_4:
			setSelectedTile(4);
			break;
		case KeyEvent.KEYCODE_5:
			setSelectedTile(5);
			break;
		case KeyEvent.KEYCODE_6:
			setSelectedTile(6);
			break;
		case KeyEvent.KEYCODE_7:
			setSelectedTile(7);
			break;
		case KeyEvent.KEYCODE_8:
			setSelectedTile(8);
			break;
		case KeyEvent.KEYCODE_9:
			setSelectedTile(9);
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			game.showKeypadOrError(selX, selY);
			break;

		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	public void setSelectedTile(int tile) {
		if (game.getTile(selX, selY) == 0){
			game.setTile(selX, selY, tile);
			invalidate(); //may change hints
		}
	}

	private void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}

	private void getRect(int x, int y, Rect rect) {
		rect.set((int)(x * width), (int)(y * height), (int)(x * width + width), (int)(y * height + height));
	}

}