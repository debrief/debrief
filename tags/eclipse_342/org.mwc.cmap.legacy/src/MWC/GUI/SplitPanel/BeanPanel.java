 /*
 * Copyright (c) 1997 Borland International, Inc. All Rights Reserved.
 * 
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.  
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS 
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY CLAIMS
 * OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR DISTRIBUTION
 * OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES ARISING OUT OF
 * OR RESULTING FROM THE USE, MODIFICATION, OR DISTRIBUTION OF PROGRAMS
 * OR FILES CREATED FROM, BASED ON, AND/OR DERIVED FROM THIS SOURCE
 * CODE FILE.
 * 
 */

//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996, 1996 Borland International, Inc. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package MWC.GUI.SplitPanel;



import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

//import borland.jbcl.util.*;
//import borland.jbcl.model.*;

/**
 * Convenient Panel to use as a superclass for JavaBean views and controls
 *  - subdispatches focus, key and mouse events
 *  - manages action listeners
 *  - manages tab/focus awareness
 */
public class BeanPanel extends Panel
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BeanPanel() {
    enableEvents(AWTEvent.FOCUS_EVENT_MASK |
                 AWTEvent.KEY_EVENT_MASK |
                 AWTEvent.MOUSE_EVENT_MASK |
                 AWTEvent.MOUSE_MOTION_EVENT_MASK);
  }

  public BeanPanel(LayoutManager layout) {
    super.setLayout(layout);
    enableEvents(AWTEvent.FOCUS_EVENT_MASK |
                 AWTEvent.KEY_EVENT_MASK |
                 AWTEvent.MOUSE_EVENT_MASK |
                 AWTEvent.MOUSE_MOTION_EVENT_MASK);
  }

  /**
   * protected implementation of action event registration methods that a subclass
   * can expose as public methods if it sources action events
   */
  public synchronized void addActionListener(ActionListener l) {
    if (actionAdapter == null)
      actionAdapter = new ActionMulticaster();
    actionAdapter.add(l);
  }

  public synchronized void removeActionListener(ActionListener l) {
    if (actionAdapter != null)
      actionAdapter.remove(l);
  }

  /**
   * protected support for a focusAware property that a subclass
   * can expose as public if it has the potential for accepting focus
   */
  protected boolean isFocusAware() {
    return focusAware;
  }

  protected void setFocusAware(boolean aware) {
    focusAware = aware;
  }

  // General events

  protected void processEvent(AWTEvent e) {
    if (e instanceof ActionEvent)
      processActionEvent((ActionEvent)e);
    else
      super.processEvent(e);
  }

  // Action events

  protected void processActionEvent(ActionEvent e) {
    if (actionAdapter != null)
      actionAdapter.dispatch(e);
  }

  // Key events

  protected void processKeyEvent(KeyEvent e) {
    //System.err.println("processKeyEvent(" + e + ")");
//    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F6 && e.isAltDown()) {
//      System.err.println("Consuming alt-f6!");
//      e.consume();
//    }
    switch (e.getID()) {
      case KeyEvent.KEY_PRESSED:  processKeyPressed(e); break;
      case KeyEvent.KEY_TYPED:    processKeyTyped(e); break;
      case KeyEvent.KEY_RELEASED: processKeyReleased(e); break;
    }
    super.processKeyEvent(e);
  }

  protected void processKeyPressed(KeyEvent e) {
  }

  protected void processKeyTyped(KeyEvent e) {
  }

  protected void processKeyReleased(KeyEvent e) {
  }

  // Mouse events

  protected void processMouseEvent(MouseEvent e) {
    //System.err.println("processMouseEvent(" + e + ")");
    switch (e.getID()) {
      case MouseEvent.MOUSE_PRESSED:  processMousePressed(e);  break;
      case MouseEvent.MOUSE_RELEASED: processMouseReleased(e); break;
      case MouseEvent.MOUSE_CLICKED:  processMouseClicked(e);  break;
      case MouseEvent.MOUSE_ENTERED:  processMouseEntered(e);  break;
      case MouseEvent.MOUSE_EXITED:   processMouseExited(e);   break;
    }
    super.processMouseEvent(e);
  }

  protected void processMouseMotionEvent(MouseEvent e) {
    switch (e.getID()) {
      case MouseEvent.MOUSE_MOVED:    processMouseMoved(e);    break;
      case MouseEvent.MOUSE_DRAGGED:  processMouseDragged(e);  break;
    }
    super.processMouseMotionEvent(e);
  }

/*
  void showFocusOwner() {
    for (Container p = getParent(); p != null; p = p.getParent())
      if (p instanceof Window) {
        Component fo = ((Window)p).getFocusOwner();
        System.err.println("focusOwner=" + fo);
        return;
      }
  }
*/

  protected void processMousePressed(MouseEvent e) {
    if (e.getClickCount() == 1 && isFocusable()) {
      //System.err.println("processMousePress(" + e + ") requesting focus");
      requestFocus();
      //showFocusOwner();
    }
  }
  protected void processMouseReleased(MouseEvent e) {
  }
  protected void processMouseClicked(MouseEvent e) {
  }
  protected void processMouseEntered(MouseEvent e) {
  }
  protected void processMouseExited(MouseEvent e) {
  }
  protected void processMouseMoved(MouseEvent e) {
  }
  protected void processMouseDragged(MouseEvent e) {
  }

  // Focus events

  protected void processFocusEvent(FocusEvent e) {
    //System.err.println("BeanPanel.processFocusEvent e=" + e + " focusState=" + focusState + " focusAware=" + focusAware);
    if (focusAware) {
      switch (e.getID()) {
        case FocusEvent.FOCUS_GAINED: focusState = ItemPainter.FOCUSED;  /*showFocusOwner();*/ break;
        case FocusEvent.FOCUS_LOST:   focusState = ItemPainter.INACTIVE; /*showFocusOwner();*/ break;
      }
      //repaint();
    }
    super.processFocusEvent(e);
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  /**
   *
   */
  public boolean isFocusable() {
//System.err.println("BeanPanel.isFocusTraversable this=" + this + ",focusAware=" + focusAware);
    return focusAware;
  }

  protected transient ActionMulticaster actionAdapter;
  protected boolean focusAware = true;
  protected int focusState = ItemPainter.INACTIVE;
}
