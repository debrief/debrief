/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Dialogs.AWT;

import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;

public class Postcard extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Panel       panelContainer = new Panel();
	private final ImageCanvas canvas = new ImageCanvas();

	public Postcard(final Image image, final Panel panel) {
		if(image != null) setImage(image);
		if(panel != null) setPanel(panel);

		setLayout(new RowLayout());
		add(canvas);
		add(panelContainer);
	}
	public Panel getPanel() {
		if(panelContainer.getComponentCount() == 1) 
			return (Panel)panelContainer.getComponent(0);
		else
			return null;
	}
	public void setImage(final Image image) {
		Util.waitForImage(this, image);
		canvas.setImage(image);
	}
	public void setPanel(final Panel panel) {
		if(panelContainer.getComponentCount() == 1) {
			panelContainer.remove(getComponent(0));
		}
		panelContainer.add(panel);
	}
    public Insets getInsets() {
        return new Insets(10,10,10,10);
    }
}
