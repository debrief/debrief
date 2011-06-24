package MWC.GUI.RubberBanding;

import java.awt.Container;
import java.awt.event.MouseEvent;

import MWC.GUI.Rubberband;


abstract public class RubberbandPanel extends Container {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rubberband rubberband;

	abstract public void rubberbandEnded(Rubberband rb);

    public void setRubberband(Rubberband rb) {
		if(rubberband != null) {
			rubberband.setActive(false);
		}
		rubberband = rb;

		if(rubberband != null) {
			rubberband.setActive(true);
			rubberband.setComponent(this);
		}
    }
	public Rubberband getRubberband() {
		return rubberband;
	}
	public void processMouseEvent(MouseEvent event) {
		super.processMouseEvent(event);  // fire to listeners

		if(rubberband != null && 
		   event.getID() == MouseEvent.MOUSE_RELEASED)
			rubberbandEnded(rubberband);
	}
}
