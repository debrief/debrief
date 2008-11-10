package MWC.GUI.Dialogs.AWT;

import java.awt.*;
import java.awt.event.*;

public class WorkDialog extends GJTDialog { 
	private ButtonPanel buttonPanel;
	private Panel       workPanel;

	public WorkDialog(Frame        frame,
					  DialogClient client,
	                  String       title) {
		this(frame, client, title, 
		     null, Orientation.CENTER, false);
	}
	public WorkDialog(Frame        frame,
					  DialogClient client,
	                  String       title,
					  boolean      modal) {
		this(frame, client, title, 
		     null, Orientation.CENTER, modal);
	}
	public WorkDialog(Frame        frame,
					  DialogClient client,
	                  String       title,
					  Orientation  buttonOrientation,
					  boolean      modal) {
		this(frame, client, title, 
		     null, buttonOrientation, modal);
	}
	public WorkDialog(Frame        frame,
					  DialogClient client,
	                  String       title,
					  Panel        workPanel,
					  Orientation  buttonOrientation,
					  boolean      modal) {
		super(frame, title, client, modal);
		this.workPanel = workPanel;
		
		setLayout(new BorderLayout(0,2));

		if(workPanel != null)
			add(workPanel, "Center");

		add("South", buttonPanel = 
					 new ButtonPanel(buttonOrientation));
	}
	public void setWorkPanel(Panel workPanel) {
		if(workPanel != null)
			remove(workPanel);

		this.workPanel = workPanel;
		add(workPanel, "Center");

		if(isShowing()) 
			validate();
	}
	public Button addButton(String string) {
		return buttonPanel.add(string);
	}
	public void addButton(Button button) {
		buttonPanel.add(button);
	}
}
