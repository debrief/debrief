package MWC.GUI.Dialogs.AWT;

import java.awt.*;

public class ButtonPanel extends Panel {
    Panel     buttonPanel = new Panel();
    Separator separator   = new Separator();

	public ButtonPanel() {
		this(Orientation.CENTER);
	}
    public ButtonPanel(Orientation orientation) {
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
    public void add(Button button) {
        buttonPanel.add(button);
    }
    public Button add(String buttonLabel) {
        Button addMe = new Button(buttonLabel);
        buttonPanel.add(addMe);
        return addMe;
    }
    protected String paramString() {
        return super.paramString() + "buttons=" +
        getComponentCount();
    }
}
