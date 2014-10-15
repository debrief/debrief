/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Dialogs.AWT;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dialog;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YesNoDialogTest extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() {
		setLayout(new BorderLayout());
		add(new YesNoDialogLauncher(this), "Center");
	}
}
class YesNoDialogLauncher extends Panel 
						implements DialogClient, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Applet         applet;
	private YesNoDialog    yesNoDialog;
	private int            cupCnt = 0;
	private final int coffeeLimit = 3;
	private final Checkbox       modal = new Checkbox("modal");

	private Button yesNoDialogButton;

	public YesNoDialogLauncher(final Applet applet) {
		this.applet = applet;

		add(modal);
		add(yesNoDialogButton = 
					new Button("Launch YesNo Dialog"));

		yesNoDialogButton.addActionListener(this);
	}
	public void actionPerformed(final ActionEvent event) {
		String question = "How about a cup of Java?"; 
		Image image = applet.getImage(applet.getCodeBase(),
									"gifs/question.gif");

		if(cupCnt >= 0 && cupCnt < coffeeLimit) {
			question += "  You've had " + cupCnt;

			if(cupCnt == 1) question += " cup already.";
			else            question += " cups already.";
		}
		else {
			question = 
					"Are you sick and tired of coffee analogies?";
		}
		if(cupCnt >= 0 && cupCnt < coffeeLimit) {
			image = applet.getImage(applet.getCodeBase(),
										"gifs/questionMark.gif");
		}
		else {
			image = applet.getImage(applet.getCodeBase(),
									"gifs/punch.gif");
		}
		if(yesNoDialog == null) {
			yesNoDialog = new YesNoDialog(Util.getFrame(this), 
								this, "Example YesNo Dialog",
								question, image, modal.getState());
		}
		else {
			if(modal.getState()) yesNoDialog.setModal(true);
			else     			 yesNoDialog.setModal(false);

			yesNoDialog.setImage(image);
			yesNoDialog.setMessage(question);
		}
		yesNoDialog.setVisible(true);
	}
	public void dialogDismissed(final Dialog d) {
		if(yesNoDialog.answeredYes()) {
			++cupCnt;

		if(cupCnt <= coffeeLimit)
			applet.showStatus("Cups Of Coffee:  " + cupCnt);
		else
			applet.showStatus("Me too");
		}
		else {
			if(cupCnt == 0)
				applet.showStatus("No coffee yet.");
			else if(cupCnt >= coffeeLimit)
				applet.showStatus("Me too");
		}
	}
	public void dialogCancelled(final Dialog d) {
		applet.showStatus("Yes No Dialog Cancelled");
	}
}
