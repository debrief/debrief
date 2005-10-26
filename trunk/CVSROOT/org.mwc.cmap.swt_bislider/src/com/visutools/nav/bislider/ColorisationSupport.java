package com.visutools.nav.bislider;

import java.util.Vector;

/**
 * The support object to store registered listeners and fire events.
 * <br><br>
 * <table border=1 width = "90%">
 *   <tr>
 *     <td>
 *       Copyright 1997-2005 Frederic Vernier. All Rights Reserved.<br>
 *       <br>
 *       Permission to use, copy, modify and distribute this software and its documentation for educational, research and
 *       non-profit purposes, without fee, and without a written agreement is hereby granted, provided that the above copyright
 *       notice and the following three paragraphs appear in all copies.<br>
 *       <br>
 *       To request Permission to incorporate this software into commercial products contact Frederic Vernier, 19 butte aux
 *       cailles street, Paris, 75013, France. Tel: (+33) 871 747 387. eMail: Frederic.Vernier@laposte.net / Web site: http://vernier.frederic.free.fr
 *       <br>
 *       IN NO EVENT SHALL FREDERIC VERNIER BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 *       DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF FREDERIC
 *       VERNIER HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.<br>
 *       <br>
 *       FREDERIC VERNIER SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *       MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HERE UNDER IS ON AN "AS IS" BASIS, AND
 *       FREDERIC VERNIER HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.<br>
 *     </td>
 *   </tr>
 * </table>
 * <br>
 * <b>Project related :</b> FiCell, FieldExplorer<br>
 * <br>
 * <b>Dates:</b> <br>
 * <li> Creation : XXX<br>
 * <li> Format : 15/02/2004<br>
 * <li> Last Modif : 15/02/2004<br>
 * <br>
 * <b>Bugs:</b> <br>
 * <li> <br>
 * <br>
 * <b>To Do:</b> <br>
 * <li> splitting between range slider events and color events<br>
 * <br>
 *
 * @author    Frederic Vernier, Frederic.Vernier@laposte.net
 * @version   1.4.1
 */

public class ColorisationSupport implements java.io.Serializable {
  private  Vector  colorisationListeners  = new Vector();

  static final long serialVersionUID = -1497023291489642695L;

  /**
   * Adds a feature to the ColorisationListener attribute of the ColorisationSupport object
   *
   * @param l  The feature to be added to the ColorisationListener attribute
   */
  public synchronized void addColorisationListener(ColorisationListener l) {
    // add a listener if it is not already registered
    if (!colorisationListeners.contains(l))
      colorisationListeners.addElement(l);
  }// addColorisationListener()


  /**
   * Description of the Method
   *
   * @param l  Description of the Parameter
   */
  public synchronized void removeColorisationListener(ColorisationListener l) {
    // remove it if it is registered
    if (colorisationListeners.contains(l))
      colorisationListeners.removeElement(l);
  }// removeColorisationListener()


  /**
   * fire the evcent asynchronously
   *
   * @param Source_Arg      Source of the event. Should be the BiSlider
   * @param ColorArray_Arg  Array describing the color correspondances.
   */
  public void fireAsyncNewColors(
    final Object Source_Arg,
    final double[][] ColorArray_Arg) {
    Thread  Thread1  = new Thread() {
      public void run() {
        internFireAsyncNewColors(Source_Arg, ColorArray_Arg);
      }
    };
    Thread1.start();
  }// fireAsyncNewColors()


  /**
   * fire the event asynchronously
   *
   * @param Source_Arg      Source of the event. Should be the BiSlider
   * @param ColorArray_Arg  Array describing the color correspondances.
   */
  private void internFireAsyncNewColors(
    final Object Source_Arg,
    final double[][] ColorArray_Arg) {
    Vector  v;
    synchronized (this) {
      v = (Vector)colorisationListeners.clone();
    }

    // Fire the event to all listeners.
    int     count  = v.size();
    for (int i = 0; i < count; i++) {
      ColorisationListener  listener  = (ColorisationListener)v.elementAt(i);
      listener.newColors(new ColorisationEvent(Source_Arg, ColorArray_Arg));
    }
  }// internFireAsyncNewColors()


  /**
   * Description of the Method
   *
   * @param Source_Arg      Source of the event. Should be the BiSlider
   * @param ColorArray_Arg  Array describing the color correspondances.
   */
  public void fireNewColors(Object Source_Arg, double[][] ColorArray_Arg) {
    // Make a copy of the listener object vector so that
    // it cannot be changed while we are firing events.
    Vector  v;
    synchronized (this) {
      v = (Vector)colorisationListeners.clone();
    }

    // Fire the event to all listeners.
    int     count  = v.size();
    for (int i = 0; i < count; i++) {
      ColorisationListener  listener  = (ColorisationListener)v.elementAt(i);
      listener.newColors(new ColorisationEvent(Source_Arg, ColorArray_Arg));
    }
  }// fireNewColors()


  /**
   * Create a ColorisationEvent event
   *
   * @param Source_Arg      Source of the event. Should be the BiSlider
   * @param ColorArray_Arg  Array describing the color correspondances.
   * @return A colorizer object for the given parameters
   */
  public Colorizer createColorisationEvent(
    Object Source_Arg,
    double[][] ColorArray_Arg) {
      return new ColorisationEvent(Source_Arg, ColorArray_Arg);
    }// createColorisationEvent()
}

