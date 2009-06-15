package MWC.GUI.Dialogs.AWT;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDialog extends WorkDialog 
                           implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
}
class MessagePanel extends Postcard {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
