package com.borlander.rac525791.dashboard.text;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class GCProxy {
	private GC myGC;
	
	public Point getExtent(String text, Font font){
		return getGC(font).stringExtent(text);
	}
	
	public int getAverageCharWidth(Font font){
		return getGC(font).getFontMetrics().getAverageCharWidth();
	}

	public void dispose() {
		if (myGC != null) {
			myGC.dispose();
			myGC = null;
		}
	}

	private GC getGC(Font font) {
		if (myGC == null || myGC.isDisposed()) {
			myGC = new GC(new Shell());
	//		System.out.println("creating new GC");
		}
		myGC.setFont(font);
		return myGC;
	}
	
}