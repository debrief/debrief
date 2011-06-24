package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import com.borlander.rac525791.dashboard.layout.DashboardFonts;

public class DashboardFontsImpl implements DashboardFonts {
	private Display myDisplay;
	private Font myValueFont;
	private Font myUnitsFont;
	private Font myTextFont;
	
	private final int myTextSize; 
	private final int myValueSize;
	private final int myUnitsSize;
	private boolean myTextBold = true;

	public DashboardFontsImpl(int text, int value, int units){
		myTextSize = text;
		myValueSize = value;
		myUnitsSize = units;
	}
	
	public Font getTextFont() {
		if (myTextFont == null || myTextFont.isDisposed()){
			myTextFont = new Font(getDisplay(), "Arial", myTextSize, myTextBold ? SWT.BOLD : SWT.NORMAL);
		}
		return myTextFont;
	}
	
	public Font getValueFont() {
		if (myValueFont == null || myValueFont.isDisposed()){
			myValueFont = new Font(getDisplay(), "Arial", myValueSize, SWT.BOLD);
		}
		return myValueFont;
	}
	
	public Font getUnitsFont() {
		if (myUnitsFont == null || myUnitsFont.isDisposed()){
			myUnitsFont = new Font(getDisplay(), "Arial", myUnitsSize, SWT.NORMAL);
		}
		return myUnitsFont;
	}
	
	private Display getDisplay(){
		if (myDisplay == null || myDisplay.isDisposed()){
			myDisplay = Display.getCurrent();
			myDisplay.disposeExec(new Runnable() {
				public void run() {
					DashboardFontsImpl.this.dispose();
				}
			});
		}
		return myDisplay;
	}
	
	public void dispose(){
		if (myTextFont != null){
			myTextFont.dispose();
			myTextFont = null;
		}
		if (myValueFont != null){
			myValueFont.dispose();
			myValueFont = null;
		}
		if (myUnitsFont != null){
			myUnitsFont.dispose();
			myUnitsFont = null;
		}
		myDisplay = null;
	}

}
