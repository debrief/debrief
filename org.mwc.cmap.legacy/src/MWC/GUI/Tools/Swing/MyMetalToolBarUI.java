/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package MWC.GUI.Tools.Swing;


import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.plaf.metal.MetalButtonUI;

/**
 * A Metal Look and Feel implementation of ToolBarUI.  This implementation
 * is a "combined" view/controller.
 * <p>
 *
 * @version 1.19 02/02/00
 * @author Jeff Shapiro
 */
public class MyMetalToolBarUI extends MyBasicToolBarUI
{
  private static Border rolloverBorder = new CompoundBorder(
          new MetalBorders.RolloverButtonBorder(), new BasicBorders.MarginBorder() );

  private static Border nonRolloverBorder = new CompoundBorder(
          new MetalBorders.ButtonBorder(), new BasicBorders.MarginBorder() );

  protected ContainerListener contListener;
  protected PropertyChangeListener rolloverListener;

  private final Hashtable<JButton, Border> borderTable = new Hashtable<JButton, Border>();
  private final Hashtable<JButton, Insets> marginTable = new Hashtable<JButton, Insets>();

  boolean rolloverBorders = false;

  static String IS_ROLLOVER = "JToolBar.isRollover";

  private final static Insets insets0 = new Insets( 0, 0, 0, 0 );

  /** the name of this session for this toolbar
   *
   */
  protected ToolbarOwner _owner = null;

  /** constructor for this class
   * @param owner the name of the session, displayed when the toolbar is floated
   */
  public MyMetalToolBarUI(final ToolbarOwner owner)
  {
    _owner = owner;
  }

  /** get the name of the session, if we know it
   *
   */
  protected String getToolbarOwner()
  {
    if(_owner != null)
      return _owner.getName();
    else
      return "n/a";
  }


  public static ComponentUI createUI( final JComponent c )
  {
    return new MyMetalToolBarUI(null);
  }

  public void installUI( final JComponent c )
  {
    super.installUI( c );

    final Object rolloverProp = c.getClientProperty( IS_ROLLOVER );

    if ( rolloverProp != null )
    {
      rolloverBorders = ((Boolean)rolloverProp).booleanValue();
    }
    else
    {
      rolloverBorders = false;
    }

    SwingUtilities.invokeLater( new Runnable()
    {
      public void run()
      {
        setRolloverBorders( isRolloverBorders() );
      }
    });
  }

  public void uninstallUI( final JComponent c )
  {
    super.uninstallUI( c );

    installNormalBorders( c );
  }

  protected void installListeners( )
  {
    super.installListeners( );

    contListener = createContainerListener( );
    if ( contListener != null ) toolBar.addContainerListener( contListener );

    rolloverListener = createRolloverListener( );
    if ( rolloverListener != null ) toolBar.addPropertyChangeListener( rolloverListener );
  }

  protected void uninstallListeners( )
  {
    super.uninstallListeners( );

    if ( contListener != null ) toolBar.removeContainerListener( contListener );
    contListener = null;

    if ( rolloverListener != null ) toolBar.removePropertyChangeListener( rolloverListener );
    rolloverListener = null;
  }

  protected ContainerListener createContainerListener( )
  {
    return new MetalContainerListener( );
  }

  protected PropertyChangeListener createRolloverListener( )
  {
    return new MetalRolloverListener( );
  }

  protected MouseInputListener createDockingListener( )
  {
    return new MetalDockingListener( toolBar );
  }

  protected void setDragOffset( final Point p )
  {
    if (dragWindow == null)
      dragWindow = createMyDragWindow(toolBar);

    dragWindow.setOffset( p );
  }

  public boolean isRolloverBorders()
  {
    return rolloverBorders;
  }

  public void setRolloverBorders( final boolean rollover )
  {
    rolloverBorders = rollover;

    if ( rolloverBorders )
    {
      installRolloverBorders( toolBar );
    }
    else
    {
      installNonRolloverBorders( toolBar );
    }
  }

  protected void installRolloverBorders ( final JComponent c )
  {
    // Put rollover borders on buttons
    final Component[] components = c.getComponents();

    for ( int i = 0; i < components.length; ++i )
    {
      if ( components[ i ] instanceof JComponent )
      {
        ( (JComponent)components[ i ] ).updateUI();

        setBorderToRollover( components[ i ] );
      }
    }
  }

  protected void installNonRolloverBorders ( final JComponent c )
  {
    // Put nonrollover borders on buttons
    final Component[] components = c.getComponents();

    for ( int i = 0; i < components.length; ++i )
    {
      if ( components[ i ] instanceof JComponent )
      {
        ( (JComponent)components[ i ] ).updateUI();

        setBorderToNonRollover( components[ i ] );
      }
    }
  }

  protected void installNormalBorders ( final JComponent c )
  {
    // Put back the normal borders on buttons
    final Component[] components = c.getComponents();

    for ( int i = 0; i < components.length; ++i )
    {
      setBorderToNormal( components[ i ] );
    }
  }

  protected void setBorderToRollover( final Component c )
  {
    if ( c instanceof JButton )
    {
      final JButton b = (JButton)c;

      if ( b.getUI() instanceof MetalButtonUI )
      {
        if ( b.getBorder() instanceof UIResource )
        {
          borderTable.put( b, b.getBorder() );
        }

        if ( b.getBorder() instanceof UIResource || b.getBorder() == nonRolloverBorder )
        {
          b.setBorder( rolloverBorder );
        }

        if ( b.getMargin() == null || b.getMargin() instanceof UIResource )
        {
          marginTable.put( b, b.getMargin() );
          b.setMargin( insets0 );
        }

        b.setRolloverEnabled( true );
      }
    }
  }

  protected void setBorderToNonRollover( final Component c )
  {
    if ( c instanceof JButton )
    {
      final JButton b = (JButton)c;

      if ( b.getUI() instanceof MetalButtonUI )
      {
        if ( b.getBorder() instanceof UIResource )
        {
          borderTable.put( b, b.getBorder() );
        }

        if ( b.getBorder() instanceof UIResource || b.getBorder() == rolloverBorder )
        {
          b.setBorder( nonRolloverBorder );
        }

        if ( b.getMargin() == null || b.getMargin() instanceof UIResource )
        {
          marginTable.put( b, b.getMargin() );
          b.setMargin( insets0 );
        }

        b.setRolloverEnabled( false );
      }
    }
  }

  protected void setBorderToNormal( final Component c )
  {
    if ( c instanceof JButton )
    {
      final JButton b = (JButton)c;

      if ( b.getUI() instanceof MetalButtonUI )
      {
        if ( b.getBorder() == rolloverBorder || b.getBorder() == nonRolloverBorder )
        {
          b.setBorder( (Border)borderTable.remove( b ) );
        }

        if ( b.getMargin() == insets0 )
        {
          b.setMargin( (Insets)marginTable.remove( b ) );
        }

        b.setRolloverEnabled( false );
      }
    }
  }


  protected class MetalContainerListener implements ContainerListener
  {
    public void componentAdded( final ContainerEvent e )
    {
      final Component c = e.getChild();

      if ( rolloverBorders )
      {
        setBorderToRollover( c );
      }
      else
      {
        setBorderToNonRollover( c );
      }
    }

    public void componentRemoved( final ContainerEvent e )
    {
      final Component c = e.getChild();
      setBorderToNormal( c );
    }

  } // end class MetalContainerListener


  protected class MetalRolloverListener implements PropertyChangeListener
  {
    public void propertyChange( final PropertyChangeEvent e )
    {
      final String name = e.getPropertyName();

      if ( name.equals( IS_ROLLOVER ) )
      {
        if ( e.getNewValue() != null )
        {
          setRolloverBorders( ((Boolean)e.getNewValue()).booleanValue() );
        }
        else
        {
          setRolloverBorders( false );
        }
      }
    }
  } // end class MetalRolloverListener


  protected class MetalDockingListener extends DockingListener
  {
    private boolean pressedInBumps = false;

    public MetalDockingListener( final JToolBar t )
    {
      super( t );
    }

    public void mousePressed( final MouseEvent e )
    {
      super.mousePressed( e );
      if (!toolBar.isEnabled()) {
        return;
      }
      pressedInBumps = false;

      final Rectangle bumpRect = new Rectangle();

      if ( toolBar.getSize().height <= toolBar.getSize().width )  // horizontal
      {
        if(toolBar.getComponentOrientation().isLeftToRight() ) {
          bumpRect.setBounds( 0, 0, 14, toolBar.getSize().height );
        } else {
          bumpRect.setBounds( toolBar.getSize().width-14, 0,
                              14, toolBar.getSize().height );
        }
      }
      else  // vertical
      {
        bumpRect.setBounds( 0, 0, toolBar.getSize().width, 14 );
      }

      if ( bumpRect.contains( e.getPoint() ) )
      {
        pressedInBumps = true;

        final Point dragOffset = e.getPoint();
        if( !toolBar.getComponentOrientation().isLeftToRight() ) {
          dragOffset.x -= toolBar.getSize().width
                  - toolBar.getPreferredSize().width;
        }
        setDragOffset( dragOffset );

      }
    }

    public void mouseDragged( final MouseEvent e )
    {
      if ( pressedInBumps )
      {
        super.mouseDragged( e );
      }
    }

  } // end class MetalDockingListener

  /** embedded interface for objects which are capable of owning a toolbar
   * this is needed for MDI interfaces when it is not clear which document
   * owns the various toolbars
   */
  public static interface ToolbarOwner
  {
    public String getName();
  }

}


