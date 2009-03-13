package com.borlander.rac525791.dashboard.text;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;

public class CenteredText implements TextDrawer {
	private String myText;

	private final Rectangle myBounds = new Rectangle();

	private final Rectangle TEMP = new Rectangle();

	private Font myFont;

	private Point myCachedTextSize;

	public CenteredText(Font font, String text, int x, int y, int w, int h) {
		setFont(font);
		setText(text);
		setBounds(x, y, w, h);
	}

	public CenteredText(Font font, int x, int y, int width, int height) {
		this(font, "", x, y, width, height);
	}

	public CenteredText(Font font, Rectangle rect) {
		this(font, "", rect.x, rect.y, rect.width, rect.height);
	}

	public CenteredText(Font font, String text) {
		this(font, text, 0, 0, 0, 0);
	}

	public CenteredText(Font font) {
		this(font, "");
	}

	/**
	 * Constructs not initialized instance. At least <code>setFont()</code>
	 * method should be called to complete initialziation.
	 */
	public CenteredText() {
		this(null, "");
	}

	public void drawText(GCProxy gcFactory, Graphics g) {
		if (myFont == null){
			return;
		}
		placeText(gcFactory, TEMP);
		g.pushState();
		g.setFont(myFont);
		g.drawText(myText, TEMP.x, TEMP.y);
		
//		g.setForegroundColor(ColorConstants.red);
//		g.drawRectangle(TEMP);
		
		g.popState();
	}

	public void setText(String text) {
		myText = text;
		if (myText == null){
			myText = "";
		}
		myCachedTextSize = null;
	}
	
	public boolean setFont(Font font){
		boolean changed = (myFont != font);
		if (changed){
			myFont = font;
			myCachedTextSize = null;
		}
		return changed;
	}

	public void setBounds(Rectangle rectangle) {
		myBounds.setBounds(rectangle);
	}

	public void setBounds(int x, int y, int width, int height) {
		myBounds.setLocation(x, y);
		myBounds.setSize(width, height);
	}

	public boolean isFitHorizontally(GCProxy gc, String text) {
		Point textSize = gc.getExtent(text, myFont);
		return textSize.x <= myBounds.width;
	}

	private void placeText(GCProxy gc, Rectangle output) {
		if (myCachedTextSize == null) {
			myCachedTextSize = gc.getExtent(myText, myFont);
		}

		int centerX = myBounds.x + myBounds.width / 2;
		int centerY = myBounds.y + myBounds.height / 2;

		int x = centerX - myCachedTextSize.x / 2;
		int y = centerY - myCachedTextSize.y / 2;

		output.setLocation(x, y);
		output.setSize(myCachedTextSize.x, myCachedTextSize.y);
	}

	public int getLargestSubstringConfinedTo(GCProxy gc, String s) {
		return getLargestSubstringConfinedTo(gc, s, myFont, myBounds.width);
	}

	/**
	 * @see FigureUtilities#getLargestSubstringConfinedTo
	 */
	public static int getLargestSubstringConfinedTo(GCProxy gc, String s,
			Font font, int availableWidth) {

		int min, max;
		float avg = gc.getAverageCharWidth(font);
		min = 0;
		max = s.length() + 1;

		// The size of the current guess
		int guess = 0, guessSize = 0;
		while ((max - min) > 1) {
			// Pick a new guess size
			// New guess is the last guess plus the missing width in pixels
			// divided by the average character size in pixels
			guess = guess + (int) ((availableWidth - guessSize) / avg);

			if (guess >= max)
				guess = max - 1;
			if (guess <= min)
				guess = min + 1;

			// Measure the current guess
			guessSize = gc.getExtent(s.substring(0, guess), font).x;

			if (guessSize < availableWidth)
				// We did not use the available width
				min = guess;
			else
				// We exceeded the available width
				max = guess;
		}
		return min;

	}

}