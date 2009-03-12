package MWC.GUI.RubberBanding;

import java.awt.*;
import MWC.GUI.*;
import java.io.*;


public class RubberbandLine extends Rubberband  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RubberbandLine() {
	}
	public RubberbandLine(Component component) {
		super(component);
	}
	public void drawLast(Graphics graphics) {
		graphics.drawLine(anchorPt.x, anchorPt.y, 
							lastPt.x,   lastPt.y);
	}
	public void drawNext(Graphics graphics) {
		graphics.drawLine(anchorPt.x,    anchorPt.y, 
							stretchedPt.x, stretchedPt.y);
	}
}
