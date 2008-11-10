package MWC.GUI.Dialogs.AWT;

import java.awt.*;
import java.awt.event.*;

public class MessageDialog extends WorkDialog 
                           implements ActionListener {
	private Button       okButton;
	private MessagePanel messagePanel;

	public MessageDialog(Frame  frame, DialogClient client, 
						String title, String message,
						Image  image) {
		this(frame, client, title, message, image, false);
	}
	public MessageDialog(Frame  frame, DialogClient client, 
		String title, String       message,
		Image  image, boolean      modal) {

		super(frame, client, title, modal);

		messagePanel = new MessagePanel(image, message);
		okButton     = addButton("Ok");
		okButton.addActionListener(this);
		setWorkPanel(messagePanel);
	}
	public void doLayout() {
		okButton.requestFocus();
		super.doLayout();
	}
	public void actionPerformed(ActionEvent event) {
		dispose();
	}
	private void setMessage(String message) {
		messagePanel.setMessage(message);
	}
	private void setImage(Image image) {
		messagePanel.setImage(image);
	}
}
class MessagePanel extends Postcard {
	private Label label;

	public MessagePanel(String message) {
		this(null, message);
	}
	public MessagePanel(Image image, String message) {
		super(image, new Panel());
		getPanel().add(label = new Label(message,Label.CENTER));
	}
	public void setMessage(String message) {
		label.setText(message);
	}
}
