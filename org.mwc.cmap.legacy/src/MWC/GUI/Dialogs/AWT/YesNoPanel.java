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

package MWC.GUI.Dialogs.AWT;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class YesNoPanel extends Postcard {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Label label;

	public YesNoPanel(final Image image, final String question) {
		super(image, new Panel());
		getPanel().add(label = new Label(question, Label.CENTER));
	}

	public YesNoPanel(final String question) {
		this(null, question);
	}

	public void setMessage(final String question) {
		label.setText(question);
	}
}
