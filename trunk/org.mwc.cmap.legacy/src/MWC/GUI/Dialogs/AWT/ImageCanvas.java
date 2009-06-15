package MWC.GUI.Dialogs.AWT;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class ImageCanvas extends Component {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private Image image;

	public ImageCanvas() {
	}
    public ImageCanvas(Image image) {
		setImage(image);
    }
    public void paint(Graphics g) {
		if(image != null) {
        	g.drawImage(image, 0, 0, this);
		}
    }
    public void update(Graphics g) {
        paint(g);
    }
	public void setImage(Image image) {
        Util.waitForImage(this, image);
		this.image = image;

        setSize(image.getWidth(this), image.getHeight(this));

		if(isShowing()) {
			repaint();
		}
	}


	public Dimension getPreferredSize() {
		if(image != null) {
			return new Dimension(image.getWidth(this),
		                     	image.getHeight(this));
		}
		else 
			return new Dimension(0,0);
	}
}
