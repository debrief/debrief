package com.borlander.rac525791.dashboard.text;

import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Font;

public interface TextDrawer {
	public void setText(String text);
	/**
	 * @return true if font is actually changed
	 */
	public boolean setFont(Font font);
	public void drawText(GCProxy gcFactory, Graphics g);
}