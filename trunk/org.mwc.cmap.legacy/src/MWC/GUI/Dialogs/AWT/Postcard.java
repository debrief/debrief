package MWC.GUI.Dialogs.AWT;

import java.awt.*;

public class Postcard extends Panel {
	private Panel       panel, panelContainer = new Panel();
	private ImageCanvas canvas = new ImageCanvas();

	public Postcard(Image image, Panel panel) {
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
	public void setImage(Image image) {
		Util.waitForImage(this, image);
		canvas.setImage(image);
	}
	public void setPanel(Panel panel) {
		if(panelContainer.getComponentCount() == 1) {
			panelContainer.remove(getComponent(0));
		}
		this.panel = panel;
		panelContainer.add(panel);
	}
    public Insets getInsets() {
        return new Insets(10,10,10,10);
    }
}
