package MWC.GUI.RubberBanding;

import java.awt.Component;
import java.awt.Graphics;

import MWC.GUI.Rubberband;


public class NullRubberBand extends Rubberband{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NullRubberBand() {
	}
	public NullRubberBand(final Component component) {
		super(component);
	}
	public void drawLast(final Graphics graphics) {
	}
	public void drawNext(final Graphics graphics) {
	}
}
