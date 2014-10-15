/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

    public void setRubberband(final Rubberband rb) {
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
	public void processMouseEvent(final MouseEvent event) {
		super.processMouseEvent(event);  // fire to listeners

		if(rubberband != null && 
		   event.getID() == MouseEvent.MOUSE_RELEASED)
			rubberbandEnded(rubberband);
	}
}
