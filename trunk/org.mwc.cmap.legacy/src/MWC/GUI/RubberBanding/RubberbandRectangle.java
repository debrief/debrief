package MWC.GUI.RubberBanding;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import MWC.GUI.Rubberband;


public class RubberbandRectangle extends Rubberband{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RubberbandRectangle() {
	}
    public RubberbandRectangle(final Component component) {
        super(component);
    }
    public void drawLast(final Graphics graphics) {
        final Rectangle rect = lastBounds();
        graphics.drawRect(rect.x, rect.y, 
                          rect.width, rect.height);
    }
    public void drawNext(final Graphics graphics) {
        final Rectangle rect = getBounds();
        graphics.drawRect(rect.x, rect.y, 
                          rect.width, rect.height);
    }
}
