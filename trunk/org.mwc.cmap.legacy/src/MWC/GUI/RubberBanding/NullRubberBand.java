package MWC.GUI.RubberBanding;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import MWC.GUI.Rubberband;


public class NullRubberBand extends Rubberband implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NullRubberBand() {
	}
	public NullRubberBand(Component component) {
		super(component);
	}
	public void drawLast(Graphics graphics) {
	}
	public void drawNext(Graphics graphics) {
	}
}
