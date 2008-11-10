package MWC.GUI.Dialogs.AWT;

import java.awt.*;
import java.awt.event.*;

public class YesNoDialog extends WorkDialog {
	private Button      yesButton;
	private Button      noButton;
	private boolean     answer      = false;
	private ButtonPanel buttonPanel = new ButtonPanel();
	private YesNoPanel  yesNoPanel;

	public YesNoDialog(Frame  frame, DialogClient client, 
						String title, String question,
						Image  image) {
		this(frame, client, title, question, image, false);
	}
	public YesNoDialog(Frame  frame, DialogClient client, 
						String title, String       question,
						Image  image, boolean      modal) {
		super(frame, client, title, modal);

		ButtonListener buttonListener = new ButtonListener();

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
  
	public void setYesButtonLabel(String label) {
		yesButton.setLabel(label);
	}
	public void setNoButtonLabel(String label) {
		noButton.setLabel(label);
	}
	public boolean answeredYes() {
		return answer;
	}
	public void setMessage(String question) {
		yesNoPanel.setMessage(question);
	}
	public void setImage(Image image) {
		yesNoPanel.setImage(image);
	}
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() == yesButton) 	answer = true;
			else								answer = false;

			dispose();
		}
	}
}
class YesNoPanel extends Postcard {
	private Label label;
	public YesNoPanel(String question) {
		this(null, question);
	}
	public YesNoPanel(Image image, String question) {
		super(image, new Panel());
		getPanel().add(label = new Label(question,Label.CENTER));
	}
	public void setMessage(String question) {
		label.setText(question);
	}
}
