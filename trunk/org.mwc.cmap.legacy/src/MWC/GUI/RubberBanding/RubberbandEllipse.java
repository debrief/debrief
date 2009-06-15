package MWC.GUI.RubberBanding;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

import MWC.GUI.Rubberband;

public class RubberbandEllipse extends Rubberband  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private final int startAngle = 0;
    private final int endAngle   = 360;

	public RubberbandEllipse() {
	}
    public RubberbandEllipse(Component component) {
        super(component);
    }
    public void drawLast(Graphics graphics) {
        Rectangle r = lastBounds();
        graphics.drawArc(r.x, r.y, 
            r.width, r.height, startAngle, endAngle);
    }
    public void drawNext(Graphics graphics) {
        Rectangle r = getBounds();
        graphics.drawArc(r.x, r.y, 
            r.width, r.height, startAngle, endAngle);
    }
}
