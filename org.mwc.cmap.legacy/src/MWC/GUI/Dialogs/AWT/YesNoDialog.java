/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Dialogs.AWT;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YesNoDialog extends WorkDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Button      yesButton;
	private final Button      noButton;
	boolean     answer      = false;
	private YesNoPanel  yesNoPanel;

	public YesNoDialog(final Frame  frame, final DialogClient client, 
						final String title, final String question,
						final Image  image) {
		this(frame, client, title, question, image, false);
	}
	public YesNoDialog(final Frame  frame, final DialogClient client, 
						final String title, final String       question,
						final Image  image, final boolean      modal) {
		super(frame, client, title, modal);

		final ButtonListener buttonListener = new ButtonListener();

		yesButton = addButton("Yes");
		noButton  = addButton("No");

		yesButton.addActionListener(buttonListener);
		noButton.addActionListener(buttonListener);

		setWorkPanel(yesNoPanel = new YesNoPanel(image,question));

		if(image != null)
			setImage(image);
	}
  
	public void doLayout() {
		yesButton.requestFocus();
		super.doLayout(); 
	}
  
	public void setYesButtonLabel(final String label) {
		yesButton.setLabel(label);
	}
	public void setNoButtonLabel(final String label) {
		noButton.setLabel(label);
	}
	public boolean answeredYes() {
		return answer;
	}
	public void setMessage(final String question) {
		yesNoPanel.setMessage(question);
	}
	public void setImage(final Image image) {
		yesNoPanel.setImage(image);
	}
	class ButtonListener implements ActionListener {
		public void actionPerformed(final ActionEvent event) {
			if(event.getSource() == yesButton) 	answer = true;
			else								answer = false;

			dispose();
		}
	}
}
class YesNoPanel extends Postcard {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label label;
	public YesNoPanel(final String question) {
		this(null, question);
	}
	public YesNoPanel(final Image image, final String question) {
		super(image, new Panel());
		getPanel().add(label = new Label(question,Label.CENTER));
	}
	public void setMessage(final String question) {
		label.setText(question);
	}
}
