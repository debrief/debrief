/*
* @(#)BasicToolBarUI.java	1.59 00/02/02
*
* Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
*
* This software is the proprietary information of Sun Microsystems, Inc.
* Use is subject to license terms.
*
*/

package MWC.GUI.Tools.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import MWC.GUI.Properties.Swing.SwingPropertiesPanel;

/**
 * A Basic L&F implementation of ToolBarUI.  This implementation
 * is a "combined" view/controller.
 * <p>
 *
 * @version 1.59 02/02/00
 * @author Georges Saab
 * @author Jeff Shapiro
 */
public class MyBasicToolBarUI extends javax.swing.plaf.basic.BasicToolBarUI implements SwingConstants
{
  protected JToolBar toolBar;
  private boolean floating;
  private int floatingX;
  private int floatingY;
  private JDialog floatingFrame;
  protected DragWindow dragWindow;
  private Container dockingSource;
  private int dockingSensitivity = 0;
  protected int focusedCompIndex = -1;

  protected Color dockingColor = null;
  protected Color floatingColor = null;
  protected Color dockingBorderColor = null;
  protected Color floatingBorderColor = null;

  protected MouseInputListener dockingListener;
  protected PropertyChangeListener propertyListener;

  protected ContainerListener toolBarContListener;
  protected FocusListener toolBarFocusListener;

  /**
   * As of Java 2 platform v1.3 this previously undocumented field is no
   * longer used.
   * Key bindings are now defined by the LookAndFeel, please refer to
   * the key bindings specification for further details.
   *
   * @deprecated As of Java 2 platform v1.3.
   */
  protected KeyStroke upKey;
  /**
   * As of Java 2 platform v1.3 this previously undocumented field is no
   * longer used.
   * Key bindings are now defined by the LookAndFeel, please refer to
   * the key bindings specification for further details.
   *
   * @deprecated As of Java 2 platform v1.3.
   */
  protected KeyStroke downKey;
  /**
   * As of Java 2 platform v1.3 this previously undocumented field is no
   * longer used.
   * Key bindings are now defined by the LookAndFeel, please refer to
   * the key bindings specification for further details.
   *
   * @deprecated As of Java 2 platform v1.3.
   */
  protected KeyStroke leftKey;
  /**
   * As of Java 2 platform v1.3 this previously undocumented field is no
   * longer used.
   * Key bindings are now defined by the LookAndFeel, please refer to
   * the key bindings specification for further details.
   *
   * @deprecated As of Java 2 platform v1.3.
   */
  protected KeyStroke rightKey;


  private static String FOCUSED_COMP_INDEX = "JToolBar.focusedCompIndex";

  public static ComponentUI createUI( JComponent c )
  {
    return new MyBasicToolBarUI();
  }

  public void installUI( JComponent c )
  {
    toolBar = (JToolBar) c;

    // Set defaults
    installDefaults();
    installComponents();
    installListeners();
    installKeyboardActions();

    // Initialize instance vars
    dockingSensitivity = 0;
    floating = false;
    floatingX = floatingY = 0;
    floatingFrame = null;

    setOrientation( toolBar.getOrientation() );
    c.setOpaque(true);

    if ( c.getClientProperty( FOCUSED_COMP_INDEX ) != null )
    {
      focusedCompIndex = ( (Integer) ( c.getClientProperty( FOCUSED_COMP_INDEX ) ) ).intValue();
    }
  }

  public void uninstallUI( JComponent c )
  {

    // Clear defaults
    uninstallDefaults();
    uninstallComponents();
    uninstallListeners();
    uninstallKeyboardActions();

    // Clear instance vars
    if (isFloating() == true)
      setFloating(false, null);

    floatingFrame = null;
    dragWindow = null;
    dockingSource = null;

    c.putClientProperty( FOCUSED_COMP_INDEX, new Integer( focusedCompIndex ) );
  }

  protected void installDefaults( )
  {
    LookAndFeel.installBorder(toolBar,"ToolBar.border");
    LookAndFeel.installColorsAndFont(toolBar,
                                     "ToolBar.background",
                                     "ToolBar.foreground",
                                     "ToolBar.font");
    // ToolBar specific defaults
    if ( dockingColor == null || dockingColor instanceof UIResource )
      dockingColor = UIManager.getColor("ToolBar.dockingBackground");
    if ( floatingColor == null || floatingColor instanceof UIResource )
      floatingColor = UIManager.getColor("ToolBar.floatingBackground");
    if ( dockingBorderColor == null ||
            dockingBorderColor instanceof UIResource )
      dockingBorderColor = UIManager.getColor("ToolBar.dockingForeground");
    if ( floatingBorderColor == null ||
            floatingBorderColor instanceof UIResource )
      floatingBorderColor = UIManager.getColor("ToolBar.floatingForeground");
  }

  protected void uninstallDefaults( )
  {
    LookAndFeel.uninstallBorder(toolBar);
    dockingColor = null;
    floatingColor = null;
    dockingBorderColor = null;
    floatingBorderColor = null;
  }

  protected void installComponents( )
  {
  }

  protected void uninstallComponents( )
  {
  }

  protected void installListeners( )
  {
    dockingListener = createDockingListener( );

    if ( dockingListener != null )
    {
      toolBar.addMouseMotionListener( dockingListener );
      toolBar.addMouseListener( dockingListener );
    }

    propertyListener = createPropertyListener();  // added in setFloating

    toolBarContListener = createToolBarContListener();

    if ( toolBarContListener != null )
    {
      toolBar.addContainerListener( toolBarContListener );
    }

    toolBarFocusListener = createToolBarFocusListener();

    if ( toolBarFocusListener != null )
    {
      // Put focus listener on all components in toolbar
      Component[] components = toolBar.getComponents();

      for ( int i = 0; i < components.length; ++i )
      {
        components[ i ].addFocusListener( toolBarFocusListener );
      }
    }
  }

  protected void uninstallListeners( )
  {
    if ( dockingListener != null )
    {
      toolBar.removeMouseMotionListener(dockingListener);
      toolBar.removeMouseListener(dockingListener);

      dockingListener = null;
    }

    if ( propertyListener != null )
    {
      propertyListener = null;  // removed in setFloating
    }

    if ( toolBarContListener != null )
    {
      toolBar.removeContainerListener( toolBarContListener );
      toolBarContListener = null;
    }

    if ( toolBarFocusListener != null )
    {
      // Remove focus listener from all components in toolbar
      Component[] components = toolBar.getComponents();

      for ( int i = 0; i < components.length; ++i )
      {
        components[ i ].removeFocusListener( toolBarFocusListener );
      }

      toolBarFocusListener = null;
    }
  }

  protected void installKeyboardActions( )
  {
    InputMap km = getInputMap2(JComponent.
                              WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    SwingUtilities.replaceUIInputMap(toolBar, JComponent.
                                              WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                                     km);
    ActionMap am = getActionMap();

    if (am != null) {
      SwingUtilities.replaceUIActionMap(toolBar, am);
    }
  }

  InputMap getInputMap2(final int condition) {
    if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
      return (InputMap)UIManager.get("ToolBar.ancestorInputMap");
    }
    return null;
  }

  ActionMap getActionMap() {
    ActionMap map = (ActionMap)UIManager.get("ToolBar.actionMap");

    if (map == null) {
      map = createActionMap();
      if (map != null) {
        UIManager.put("ToolBar.actionMap", map);
      }
    }
    return map;
  }

  ActionMap createActionMap() {
    ActionMap map = new ActionMapUIResource();
    map.put("navigateRight", new RightAction());
    map.put("navigateLeft", new LeftAction());
    map.put("navigateUp", new UpAction());
    map.put("navigateDown", new DownAction());
    return map;
  }

  protected void uninstallKeyboardActions( )
  {
    SwingUtilities.replaceUIActionMap(toolBar, null);
    SwingUtilities.replaceUIInputMap(toolBar, JComponent.
                                              WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                                     null);
  }

  @SuppressWarnings("deprecation")
	protected void navigateFocusedComp( int direction )
  {
    int nComp = toolBar.getComponentCount();
    int j;

    switch ( direction )
    {
      case EAST:
      case SOUTH:

        if ( focusedCompIndex < 0 || focusedCompIndex >= nComp ) break;

        j = focusedCompIndex + 1;

        while ( j != focusedCompIndex )
        {
          if ( j >= nComp ) j = 0;
          Component comp = toolBar.getComponentAtIndex( j++ );

          if ( comp != null && comp.isFocusTraversable() )
          {
            comp.requestFocus();
            break;
          }
        }

        break;

      case WEST:
      case NORTH:

        if ( focusedCompIndex < 0 || focusedCompIndex >= nComp ) break;

        j = focusedCompIndex - 1;

        while ( j != focusedCompIndex )
        {
          if ( j < 0 ) j = nComp - 1;
          Component comp = toolBar.getComponentAtIndex( j-- );

          if ( comp != null && comp.isFocusTraversable() )
          {
            comp.requestFocus();
            break;
          }
        }

        break;

      default:
        break;
    }
  }

  protected JDialog createMyFloatingFrame(JToolBar toolbar) {
    Frame f = (java.awt.Frame)SwingUtilities.getAncestorOfClass(java.awt.Frame.class, toolbar);

    JDialog frame = new JDialog(f, false);

    frame.setTitle(toolbar.getName() + " (" + getToolbarOwner()  + ")");
    frame.setResizable(true);
    WindowListener wl = createFrameListener();
    frame.addWindowListener(wl);

    // also listen to the internal frame closing - if we have one as a parent
    JInternalFrame internalFrame = (javax.swing.JInternalFrame)SwingUtilities.getAncestorOfClass(javax.swing.JInternalFrame.class, toolbar);
    if(internalFrame != null)
    {
      internalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter(){
        public void internalFrameClosed(InternalFrameEvent e)
        {
          setFloating(false, null);
        }
      });
    }

    // also listen to the properies panel - if we have one as a parent
    Component comp = toolbar.getComponentAtIndex(0);
    if(comp instanceof SwingPropertiesPanel.CloseableJPanel)
    {
      final SwingPropertiesPanel.CloseableJPanel panel = (SwingPropertiesPanel.CloseableJPanel) comp;
      if(panel != null)
      {
        ////////////////////////////////////////
        // listen out for it closing
        panel.addClosingListener(new SwingPropertiesPanel.ClosingEventListener()
              { public void isClosing()
                {
                  setFloating(false, null);
                }});
        ////////////////////////////////////////
        // tell it when we close
        frame.addWindowListener(new WindowAdapter()
                                {
          public void windowClosing(WindowEvent e)
          {
            panel.triggerClose();
          }
        });
      }
    }


    return frame;
  }


  /** get the name of the session, if we know it
   *
   */
  protected String getToolbarOwner()
  {
    return " ";
  }

  protected DragWindow createMyDragWindow(JToolBar toolbar) {
    Window frame = null;
    if(toolBar != null) {
      Container p;
      for(p = toolBar.getParent() ; p != null && !(p instanceof Frame) ;
          p = p.getParent());
      if(p != null && p instanceof Frame)
        frame = (Frame) p;
    }
    if(floatingFrame == null) {
      floatingFrame = createMyFloatingFrame(toolBar);
    }
    frame = floatingFrame;

    DragWindow dragWindow1 = new DragWindow(frame);
    return dragWindow1;
  }

  public Dimension getMinimumSize(JComponent c) {
    return getPreferredSize(c);
  }

  public Dimension getPreferredSize(JComponent c) {
    return null;
  }

  public Dimension getMaximumSize(JComponent c) {
    return getPreferredSize(c);
  }

  public void setFloatingLocation(int x, int y) {
    floatingX = x;
    floatingY = y;
  }

  public boolean isFloating() {
    return floating;
  }

  @SuppressWarnings("deprecation")
	public void setFloating(boolean b, Point p) {
    if (toolBar.isFloatable() == true) {
      if (dragWindow != null)
        dragWindow.setVisible(false);
      this.floating = b;
      if (b == true)
      {
        if (dockingSource == null)
        {
          dockingSource = toolBar.getParent();
          dockingSource.remove(toolBar);
        }
        if ( propertyListener != null )
          UIManager.addPropertyChangeListener( propertyListener );
        if (floatingFrame == null)
          floatingFrame = createMyFloatingFrame(toolBar);
        floatingFrame.getContentPane().add(toolBar,BorderLayout.CENTER);
        setOrientation( JToolBar.HORIZONTAL );
        floatingFrame.pack();
        floatingFrame.setLocation(floatingX, floatingY);
        floatingFrame.show();
      } else {
        if (floatingFrame == null)
          floatingFrame = createMyFloatingFrame(toolBar);
        floatingFrame.setVisible(false);
        floatingFrame.getContentPane().remove(toolBar);
        String constraint = getDockingConstraint(dockingSource,
                                                 p);
        int orientation = mapConstraintToOrientation(constraint);
        setOrientation(orientation);
        if (dockingSource== null)
          dockingSource = toolBar.getParent();
        if ( propertyListener != null )
          UIManager.removePropertyChangeListener( propertyListener );
        dockingSource.add(constraint, toolBar);
      }
      dockingSource.invalidate();
      Container dockingSourceParent = dockingSource.getParent();
      if (dockingSourceParent != null)
        dockingSourceParent.validate();
      dockingSource.repaint();
    }
  }

  private int mapConstraintToOrientation(String constraint)
  {
    int orientation = toolBar.getOrientation();

    if ( constraint != null )
    {
      if ( constraint.equals(BorderLayout.EAST) || constraint.equals(BorderLayout.WEST) )
        orientation = JToolBar.VERTICAL;
      else if ( constraint.equals(BorderLayout.NORTH) || constraint.equals(BorderLayout.SOUTH) )
        orientation = JToolBar.HORIZONTAL;
    }

    return orientation;
  }

  public void setOrientation(int orientation)
  {
    toolBar.setOrientation( orientation );

    if (dragWindow !=null)
      dragWindow.setOrientation(orientation);
  }

  /**
   * Gets the color displayed when over a docking area
   */
  public Color getDockingColor() {
    return dockingColor;
  }

  /**
   * Sets the color displayed when over a docking area
   */
  public void setDockingColor(Color c) {
    this.dockingColor = c;
  }

  /**
   * Gets the color displayed when over a floating area
   */
  public Color getFloatingColor() {
    return floatingColor;
  }

  /**
   * Sets the color displayed when over a floating area
   */
  public void setFloatingColor(Color c) {
    this.floatingColor = c;
  }

  public boolean canDock(Component c, Point p) {
    // System.out.println("Can Dock: " + p);
    boolean b = false;
    if (c.contains(p)) {
      if (dockingSensitivity == 0)
        dockingSensitivity = toolBar.getSize().height;
      // North
      if (p.y < dockingSensitivity)
        b = true;
      // South
      if (p.y > c.getSize().height-dockingSensitivity)
        b = true;
      // West  (Base distance on height for now!)
      if (p.x < dockingSensitivity)
        b = true;
      // East  (Base distance on height for now!)
      if (p.x > c.getSize().width-dockingSensitivity)
        b = true;
    }
    return b;
  }

  private String getDockingConstraint(Component c, Point p) {

//    String s = BorderLayout.NORTH;
//    if ((p != null) && (c.contains(p))) {
//      if (dockingSensitivity == 0)
//        dockingSensitivity = toolBar.getSize().height;
//      if (p.y > c.getSize().height-dockingSensitivity)
//        s = BorderLayout.SOUTH;
//      // West  (Base distance on height for now!)
//      if (p.x < dockingSensitivity)
//        s = BorderLayout.WEST;
//      // East  (Base distance on height for now!)
//      if (p.x > c.getSize().width-dockingSensitivity)
//        s = BorderLayout.EAST;
//      // North  (Base distance on height for now!)
//      if (p.y < dockingSensitivity)
//        s = BorderLayout.NORTH;
//    }
//    return s;

    // Changed by IanM jul 03 so that component is always placed back in centre

    return BorderLayout.CENTER;
  }

  @SuppressWarnings("deprecation")
	protected void dragTo(Point position, Point origin)
  {
    if (toolBar.isFloatable() == true)
    {
      try
      {
        if (dragWindow == null)
          dragWindow = createMyDragWindow(toolBar);
        Point offset = dragWindow.getOffset();
        if (offset == null) {
          Dimension size = toolBar.getPreferredSize();
          offset = new Point(size.width/2, size.height/2);
          dragWindow.setOffset(offset);
        }
        Point global = new Point(origin.x+ position.x,
                                 origin.y+position.y);
        Point dragPoint = new Point(global.x- offset.x,
                                    global.y- offset.y);
        if (dockingSource == null)
          dockingSource = toolBar.getParent();

        Point dockingPosition = dockingSource.getLocationOnScreen();
        Point comparisonPoint = new Point(global.x-dockingPosition.x,
                                          global.y-dockingPosition.y);
        if (canDock(dockingSource, comparisonPoint)) {
          dragWindow.setBackground(getDockingColor());
          String constraint = getDockingConstraint(dockingSource,
                                                   comparisonPoint);
          int orientation = mapConstraintToOrientation(constraint);
          dragWindow.setOrientation(orientation);
          dragWindow.setBorderColor(dockingBorderColor);
        } else {
          dragWindow.setBackground(getFloatingColor());
          dragWindow.setOrientation( JToolBar.HORIZONTAL );
          dragWindow.setBorderColor(floatingBorderColor);
        }

        dragWindow.setLocation(dragPoint.x, dragPoint.y);
        if (dragWindow.isVisible() == false) {
          Dimension size = toolBar.getPreferredSize();
          dragWindow.setSize(size.width, size.height);
          dragWindow.show();
        }
      }
      catch ( IllegalComponentStateException e )
      {
      }
    }
  }

  protected void floatAt(Point position, Point origin)
  {
    if(toolBar.isFloatable() == true)
    {
      try
      {
        Point offset = dragWindow.getOffset();
        if (offset == null) {
          offset = position;
          dragWindow.setOffset(offset);
        }
        Point global = new Point(origin.x+ position.x,
                                 origin.y+position.y);
        setFloatingLocation(global.x-offset.x,
                            global.y-offset.y);
        if (dockingSource != null) {
          Point dockingPosition = dockingSource.getLocationOnScreen();
          Point comparisonPoint = new Point(global.x-dockingPosition.x,
                                            global.y-dockingPosition.y);
          if (canDock(dockingSource, comparisonPoint)) {
            setFloating(false, comparisonPoint);
          } else {
            setFloating(true, null);
          }
        } else {
          setFloating(true, null);
        }
        dragWindow.setOffset(null);
      }
      catch ( IllegalComponentStateException e )
      {
      }
    }
  }

  protected ContainerListener createToolBarContListener( )
  {
    return new ToolBarContListener( );
  }

  protected FocusListener createToolBarFocusListener( )
  {
    return new ToolBarFocusListener( );
  }

  protected PropertyChangeListener createPropertyListener()
  {
    return new PropertyListener();
  }

  protected MouseInputListener createDockingListener( ) {
    return new DockingListener(toolBar);
  }

  protected WindowListener createFrameListener() {
    return new FrameListener();
  }

  // The private inner classes below should be changed to protected the
  // next time API changes are allowed.

  private static abstract class KeyAction extends AbstractAction {
    public boolean isEnabled() {
      return true;
    }
  };

  @SuppressWarnings("synthetic-access")
	static class RightAction extends KeyAction {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
      JToolBar toolBar = (JToolBar)e.getSource();
      MyBasicToolBarUI ui = (MyBasicToolBarUI)toolBar.getUI();
      ui.navigateFocusedComp(EAST);
    }
  };

  @SuppressWarnings("synthetic-access")
	static class LeftAction extends KeyAction {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
      JToolBar toolBar = (JToolBar)e.getSource();
      MyBasicToolBarUI ui = (MyBasicToolBarUI)toolBar.getUI();
      ui.navigateFocusedComp(WEST);
    }
  };

  @SuppressWarnings("synthetic-access")
	static class UpAction extends KeyAction {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
      JToolBar toolBar = (JToolBar)e.getSource();
      MyBasicToolBarUI ui = (MyBasicToolBarUI)toolBar.getUI();
      ui.navigateFocusedComp(NORTH);
    }
  };

  @SuppressWarnings("synthetic-access")
	static class DownAction extends KeyAction {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
      JToolBar toolBar = (JToolBar)e.getSource();
      MyBasicToolBarUI ui = (MyBasicToolBarUI)toolBar.getUI();
      ui.navigateFocusedComp(SOUTH);
    }
  };

  protected class FrameListener extends WindowAdapter {
    public void windowClosing(WindowEvent w) {
      setFloating(false, null);
    }

  }

  protected class ToolBarContListener implements ContainerListener
  {
    public void componentAdded( ContainerEvent e )
    {
      Component c = e.getChild();

      if ( toolBarFocusListener != null )
      {
        c.addFocusListener( toolBarFocusListener );
      }
    }

    public void componentRemoved( ContainerEvent e )
    {
      Component c = e.getChild();

      if ( toolBarFocusListener != null )
      {
        c.removeFocusListener( toolBarFocusListener );
      }
    }

  } // end class ToolBarContListener

  protected class ToolBarFocusListener implements FocusListener
  {
    public void focusGained( FocusEvent e )
    {
      Component c = e.getComponent();

      focusedCompIndex = toolBar.getComponentIndex( c );
    }

    public void focusLost( FocusEvent e )
    {
    }

  } // end class ToolBarFocusListener

  protected class PropertyListener implements PropertyChangeListener
  {
    public void propertyChange( PropertyChangeEvent e )
    {
      if ( e.getPropertyName().equals("lookAndFeel") )
      {
        toolBar.updateUI();
      }
    }
  }

  /**
   * This inner class is marked &quot;public&quot; due to a compiler bug.
   * This class should be treated as a &quot;protected&quot; inner class.
   * Instantiate it only within subclasses of MyBasicToolBarUI.
   */
  public class DockingListener implements MouseInputListener {
    protected JToolBar toolBar1;
    protected boolean isDragging = false;
    protected Point origin = null;

    public DockingListener(JToolBar t) {
      this.toolBar1 = t;
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
      if (!toolBar1.isEnabled()) {
        return;
      }
      isDragging = false;
    }
    public void mouseReleased(MouseEvent e) {
      if (!toolBar1.isEnabled()) {
        return;
      }
      if (isDragging == true) {
        Point position = e.getPoint();
        if (origin == null)
          origin = e.getComponent().getLocationOnScreen();
        floatAt(position, origin);
      }
      origin = null;
      isDragging = false;
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    public void mouseDragged(MouseEvent e) {
      if (!toolBar1.isEnabled()) {
        return;
      }
      isDragging = true;
      Point position = e.getPoint();
      if (origin == null)
        origin = e.getComponent().getLocationOnScreen();
      dragTo(position, origin);
    }
    public void mouseMoved(MouseEvent e) {
    }
  }

  protected class DragWindow extends Window
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Color borderColor = Color.gray;
    int orientation = toolBar.getOrientation();
    Point offset; // offset of the mouse cursor inside the DragWindow

    DragWindow(Window f) {
      super(f);
    }

    public void setOrientation(int o) {
      if(isShowing()) {
        if (o == this.orientation)
          return;
        this.orientation = o;
        Dimension size = getSize();
        setSize(new Dimension(size.height, size.width));
        if (offset!=null) {
          if( toolBar.getComponentOrientation().isLeftToRight()) {
            setOffset(new Point(offset.y, offset.x));
          } else if( o == JToolBar.HORIZONTAL ) {
            setOffset(new Point( size.height-offset.y, offset.x));
          } else {
            setOffset(new Point(offset.y, size.width-offset.x));
          }
        }
        repaint();
      }
    }

    public Point getOffset() {
      return offset;
    }

    public void setOffset(Point p) {
      this.offset = p;
    }

    public void setBorderColor(Color c) {
      if (this.borderColor == c)
        return;
      this.borderColor = c;
      repaint();
    }

    public Color getBorderColor() {
      return this.borderColor;
    }

    public void paint(Graphics g) {
      Color temp = g.getColor();
      g.setColor(getBackground());
      Dimension size = getSize();
      g.fillRect(0,0,size.width, size.height);
      g.setColor(getBorderColor());
      g.drawRect(0,0,size.width-1, size.height-1);
      g.setColor(temp);
      super.paint(g);
    }
    public Insets getInsets() {
      return new Insets(1,1,1,1);
    }
  }
}







