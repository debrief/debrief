package com.borlander.rac525791.dashboard.text;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;

public class TwoLinesText implements TextDrawer {
	private final CenteredText myBottom;
	private final CenteredText myTop;
	private String myNotSplittedText;
	private boolean myNeedSplit = true;

	public TwoLinesText(){
		this(new CenteredText(), new CenteredText());
	}

	public TwoLinesText(CenteredText top, CenteredText bottom){
		myTop = top;
		myBottom = bottom;
	}
	
	public void drawText(GCProxy gcFactory, Graphics g) {
		if (myNeedSplit){
			splitText(gcFactory);
			myNeedSplit = false;
		}
		myTop.drawText(gcFactory, g);
		myBottom.drawText(gcFactory, g);
	}
	
	public void setBounds(Rectangle top, Rectangle bottom){
		myTop.setBounds(top);
		myBottom.setBounds(bottom);
	}
	
	public void setText(String text) {
		myNotSplittedText = text;
		requestSplit();
	}
	
	public boolean setFont(Font font){
		boolean topChanged = myTop.setFont(font);
		boolean bottomChanged = myBottom.setFont(font);
		boolean changed = topChanged || bottomChanged;
		if (changed){
			requestSplit();
		}
		return changed;
	}
	
	protected void splitText(GCProxy gc) {
		if (myTop.isFitHorizontally(gc, myNotSplittedText)){
			myTop.setText(myNotSplittedText);
			myBottom.setText("");
			return;
		}

		if (myNotSplittedText.indexOf(' ') > -1 && splitOnSpaces(gc)){
			return;
		}
		
		int index = myTop.getLargestSubstringConfinedTo(gc, myNotSplittedText);
		myTop.setText(myNotSplittedText.substring(0, index));
		myBottom.setText(myNotSplittedText.substring(index));
	}
	
	private boolean splitOnSpaces(GCProxy gc){
		String[] words = myNotSplittedText.split("\\s");
		if (words.length > 1){
			for (int i = 1; i < words.length - 1; i++){
				StringBuffer top = new StringBuffer();
				StringBuffer bottom = new StringBuffer();
				for (int j = 0; j < i; j++){
					if (top.length() > 0){
						top.append(' ');
					}
					top.append(words[j]);
				}
				for (int j = i; j < words.length; j++){
					if (bottom.length() > 0){
						bottom.append(' ');
					}
					bottom.append(words[j]);
				}
				String topText = top.toString();
				String bottomText = bottom.toString();
				
				if (myTop.isFitHorizontally(gc, topText) && myBottom.isFitHorizontally(gc, bottomText)){
					myTop.setText(topText);
					myBottom.setText(bottomText);
					return true;
				}
			}
		}
		return false;
	}
	
	private void requestSplit(){
		myNeedSplit = true;
		myTop.setText("");
		myBottom.setText("");
	}
	
}