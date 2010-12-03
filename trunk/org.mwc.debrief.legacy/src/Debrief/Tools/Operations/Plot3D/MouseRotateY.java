package Debrief.Tools.Operations.Plot3D;
/*
 *	@(#)MouseRotateY.java 1.3 00/02/10 13:13:43
 *
 * Copyright (c) 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;

/**
 * MouseRotateY is a Java3D behavior object that lets users control the 
 * rotation of an object via a mouse.
 * <p>
 * To use this utility, first create a transform group that this 
 * rotate behavior will operate on. Then,
 *<blockquote><pre>
 * 
 *   MouseRotateY behavior = new MouseRotateY();
 *   behavior.setTransformGroup(objTrans);
 *   objTrans.addChild(behavior);
 *   behavior.setSchedulingBounds(bounds);
 *
 *</pre></blockquote>
 * The above code will add the rotate behavior to the transform
 * group. The user can rotate any object attached to the objTrans.
 */

public final class MouseRotateY extends MouseBehavior {
  /** turn factor
	 */
  private double y_angle;
  /** the y factor
	 */
  private double y_factor;

  /**
   * Creates a rotate behavior given the transform group.
   * @param transformGroup The transformGroup to operate on.
   */
  public MouseRotateY(TransformGroup transformGroup) {
    super(transformGroup);
  }

  /**
   * Creates a default mouse rotate behavior.
   **/
  public MouseRotateY() {
      super(0);
   }

  /**
   * Creates a rotate behavior.
   * Note that this behavior still needs a transform
   * group to work on (use setTransformGroup(tg)) and
   * the transform group must add this behavior.
   * @param flags interesting flags (wakeup conditions).
   */
  public MouseRotateY(int flags) {
      super(flags);
   }

  /** setup the local variables
	 */
	public final void initialize() {
		super.initialize();
		y_angle = 0;
		y_factor = .03;
		if ((flags & INVERT_INPUT) == INVERT_INPUT) {
			invert = true;
			y_factor *= -1;
		}
	}

  /** return the y factor
	 * @return the y factor
	 */
	public final double getYFactor() {
		return y_factor;
	}
  
  /** set the y factor
	 * @param factor the new y factor
	 */
	public final void setFactor(double factor) {
		y_factor = factor;
	}
  

  /** event handler
	 * @param criteria the event in progress
	 */
	@SuppressWarnings("rawtypes")
	public final void processStimulus(Enumeration criteria) {
		WakeupCriterion wakeup;
		AWTEvent[] event;
		int id;
		int  dx;

		while (criteria.hasMoreElements()) {
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
				for (int i=0; i<event.length; i++) {
					processMouseEvent((MouseEvent) event[i]);

					if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
					((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))){

						id = event[i].getID();
						if ((id == MouseEvent.MOUSE_DRAGGED) &&
						!((MouseEvent)event[i]).isMetaDown() &&
						!((MouseEvent)event[i]).isAltDown()){

							x = ((MouseEvent)event[i]).getX();

							dx = x - x_last;

							if (!reset){
								y_angle = dx * y_factor;

								transformY.rotY(y_angle);

								transformGroup.getTransform(currXform);

								//Vector3d translation = new Vector3d();
								//Matrix3f rotation = new Matrix3f();
								Matrix4d mat = new Matrix4d();

								// Remember old matrix
								currXform.get(mat);

								// Translate to origin
								currXform.setTranslation(new Vector3d(0.0,0.0,0.0));
								if (invert) {
									currXform.mul(currXform, transformX);
									currXform.mul(currXform, transformY);
								} else {
									currXform.mul(transformX, currXform);
									currXform.mul(transformY, currXform);
								}

								// Set old translation back
								Vector3d translation = new
								Vector3d(mat.m03, mat.m13, mat.m23);
								currXform.setTranslation(translation);

								// Update xform
								transformGroup.setTransform(currXform);
							}
							else {
								reset = false;
							}

							x_last = x;
						}
						else if (id == MouseEvent.MOUSE_PRESSED) {
							x_last = ((MouseEvent)event[i]).getX();
						}
					}
				}
			}
		}

		wakeupOn (mouseCriterion);

	}
}
