// Copyright MWC 1999
// $RCSfile: MouseRotateX.java,v $
// $Author: Ian.Mayo $
// $Log: MouseRotateX.java,v $
// Revision 1.2  2005/12/13 09:04:44  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:41  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.2  2003-03-19 15:37:17+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.1  2002-10-28 08:14:10+00  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-28 09:11:50+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:17+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:28+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:41  ianmayo
// initial import of files
//
// Revision 1.2  2000-08-07 12:23:52+01  ian_mayo
// general improvements
//

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
 * MouseRotateX is a Java3D behavior object that lets users control the 
 * rotation of an object via a mouse.
 * <p>
 * To use this utility, first create a transform group that this 
 * rotate behavior will operate on. Then,
 *<blockquote><pre>
 * 
 *   MouseRotateX behavior = new MouseRotateX();
 *   behavior.setTransformGroup(objTrans);
 *   objTrans.addChild(behavior);
 *   behavior.setSchedulingBounds(bounds);
 *
 *</pre></blockquote>
 * The above code will add the rotate behavior to the transfor
 * group. The user can rotate any object attached to the objTrans.
 */

final class MouseRotateX extends MouseBehavior {
	
	/** constant value
	 */
	public static final int ROTATE_ABOUT_X =0;
	/** constant value
	 */
	public static final int ROTATE_ABOUT_Y =1;
	/** constant value
	 */
	public static final int ROTATE_ABOUT_Z =2;
	

	
  /** turn factor
	 */
	private double x_angle;
  /** the y factor
	 */
	private double x_factor;
	
	/** the current direction for the mouse drag operations
	 */
	private int _currentDirection =ROTATE_ABOUT_Y;


  /**
   * Creates a default mouse rotate behavior.
   **/
  public MouseRotateX() {
      super(0);
   }

  /** setup the local variables
	 */
	public final void initialize() {
		super.initialize();
		x_angle = 0;
		x_factor = .03;
		if ((flags & INVERT_INPUT) == INVERT_INPUT) {
			invert = true;
			x_factor *= -1;
		}
	}
	
	
	/** set which direction the mouse-drag operates in
	 * @param direction one of a list of directions which may be used
	 */
	public final void setRotationDirection (int direction)
	{
		_currentDirection = direction;
	}
	
	
	/** get the current rotation direction
	 * @return the current rotation direction
	 */
	public final int getRotationDirection ()
	{
		return _currentDirection;
	}	

  /** event handler
	 * @param criteria the event in progress
	 */
	@SuppressWarnings("unchecked")
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
							y = ((MouseEvent)event[i]).getY();

							dx = x - x_last;
								
							
							if (!reset){
								
								// a rotation about x it is then
								x_angle = dx * x_factor;
								
								switch(_currentDirection)
								{
									case ROTATE_ABOUT_X:
										transformY.rotX(x_angle);
										break;
									case ROTATE_ABOUT_Y:
										transformY.rotY(x_angle);
										break;
									case ROTATE_ABOUT_Z:
										transformY.rotZ(x_angle);
										break;										
								}
								
//								transformY.rotY(x_angle);
//								transformY.rotX(x_angle);

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
								Vector3d translation = new	Vector3d(mat.m03, mat.m13, mat.m23);
								currXform.setTranslation(translation);

								// Update xform
								transformGroup.setTransform(currXform);
							}
							else {
								reset = false;
							}

							x_last = x;
							y_last = y;
						}
						else if (id == MouseEvent.MOUSE_PRESSED) {
							x_last = ((MouseEvent)event[i]).getX();
							y_last = ((MouseEvent)event[i]).getY();
						}
					}
				}
			}
		}

		wakeupOn (mouseCriterion);

	}
}
