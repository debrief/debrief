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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;

public class ButtonPanel extends Panel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		Panel     buttonPanel = new Panel();
    Separator separator   = new Separator();

	public ButtonPanel() {
		this(Orientation.CENTER);
	}
    public ButtonPanel(final Orientation orientation) {
		int buttonPanelOrient = FlowLayout.CENTER;
        setLayout(new BorderLayout(0,5));

		if(orientation == Orientation.CENTER)
			buttonPanelOrient = FlowLayout.CENTER;
		else if(orientation == Orientation.RIGHT)
			buttonPanelOrient = FlowLayout.RIGHT;
		else if(orientation == Orientation.LEFT)
			buttonPanelOrient = FlowLayout.LEFT;

		buttonPanel.setLayout(new FlowLayout(buttonPanelOrient));
        add(separator, "North");
        add(buttonPanel, "Center");
    }
    public void add(final Button button) {
        buttonPanel.add(button);
    }
    public Button add(final String buttonLabel) {
        final Button addMe = new Button(buttonLabel);
        buttonPanel.add(addMe);
        return addMe;
    }
    protected String paramString() {
        return super.paramString() + "buttons=" +
        getComponentCount();
    }
}
