/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.borlander.rac525791.dashboard.text;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;

public class TwoLinesText implements TextDrawer {
	private final CenteredText myBottom;
	private final CenteredText myTop;
	private String myNotSplittedText;
	private boolean myNeedSplit = true;

	public TwoLinesText() {
		this(new CenteredText(), new CenteredText());
	}

	public TwoLinesText(final CenteredText top, final CenteredText bottom) {
		myTop = top;
		myBottom = bottom;
	}

	@Override
	public void drawText(final GCProxy gcFactory, final Graphics g) {
		if (myNeedSplit) {
			splitText(gcFactory);
			myNeedSplit = false;
		}
		myTop.drawText(gcFactory, g);
		myBottom.drawText(gcFactory, g);
	}

	private void requestSplit() {
		myNeedSplit = true;
		myTop.setText("");
		myBottom.setText("");
	}

	public void setBounds(final Rectangle top, final Rectangle bottom) {
		myTop.setBounds(top);
		myBottom.setBounds(bottom);
	}

	@Override
	public boolean setFont(final Font font) {
		final boolean topChanged = myTop.setFont(font);
		final boolean bottomChanged = myBottom.setFont(font);
		final boolean changed = topChanged || bottomChanged;
		if (changed) {
			requestSplit();
		}
		return changed;
	}

	@Override
	public void setText(final String text) {
		myNotSplittedText = text;
		requestSplit();
	}

	private boolean splitOnSpaces(final GCProxy gc) {
		final String[] words = myNotSplittedText.split("\\s");
		if (words.length > 1) {
			for (int i = 1; i < words.length - 1; i++) {
				final StringBuffer top = new StringBuffer();
				final StringBuffer bottom = new StringBuffer();
				for (int j = 0; j < i; j++) {
					if (top.length() > 0) {
						top.append(' ');
					}
					top.append(words[j]);
				}
				for (int j = i; j < words.length; j++) {
					if (bottom.length() > 0) {
						bottom.append(' ');
					}
					bottom.append(words[j]);
				}
				final String topText = top.toString();
				final String bottomText = bottom.toString();

				if (myTop.isFitHorizontally(gc, topText) && myBottom.isFitHorizontally(gc, bottomText)) {
					myTop.setText(topText);
					myBottom.setText(bottomText);
					return true;
				}
			}
		}
		return false;
	}

	protected void splitText(final GCProxy gc) {
		if (myTop.isFitHorizontally(gc, myNotSplittedText)) {
			myTop.setText(myNotSplittedText);
			myBottom.setText("");
			return;
		}

		if (myNotSplittedText.indexOf(' ') > -1 && splitOnSpaces(gc)) {
			return;
		}

		final int index = myTop.getLargestSubstringConfinedTo(gc, myNotSplittedText);
		myTop.setText(myNotSplittedText.substring(0, index));
		myBottom.setText(myNotSplittedText.substring(index));
	}

}