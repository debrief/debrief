package MWC.GUI.TabPanel;


import java.awt.*;
import java.applet.*;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.lang.IllegalArgumentException;

//	05/30/97	LAB	Updated to support Java 1.1
//	06/24/97	LAB	Made the button behave like a button. i.e. When you press the
//					button, then move off of it, it responds.  This also affects the
//					notification process.
//					Fixed problem which required setNotifyDelay() to be
//					called before setNotifyWhilePressed().
//	06/26/97	LAB	Changed the way the button is drawn.  Now it uses an offscreen
//					Image as its drawing buffer via updateButtonImage().  Added
//					capabilities to allow the button to have a user definable color,
//					and the highlights are derived from that.  Also added a user
//					definable border color.
//	06/27/97	LAB	Added UseOffset property to allow the button to draw differently
//					if the user wants objects in the button (like a direction arrow, or
//					image) to be offset vertically and horizontally by the bevel height
//					when the button is drawn is its down state.  Changed the default bevel
//					height to 2 pixels.
//	07/09/97	LAB	Added LinkURL property and supporting properties/methods/members
//					to allow buttons to have a URL associated with them by defualt.
//					Added ShowURLStatus property to allow control over the status
//					messaging for the linkURL.  Condensed all of the java.awt.whatever
//					imports to java.awt.* for readability. Added minimumSize() and
//					preferedSize() methods. Updated the enable() and disable() methods
//					to the new setEnabled() method.
//	07/13/97	RKM	Fixed misspelling of prefered
//	07/19/97	LAB	Added deprecated preferedSize and minimumSize to call the non-deprecated versions.
//  07/23/97    CAR marked fields transient as needed, inner classes implement java.io.Serializable
//	08/05/97	LAB	Added a call to clipRect in the updateButtonImage method to clip subsequent
//					drawings to the internal button area (sans border and bevel).
//	08/06/97	LAB	Added buttonImageGraphics as a protected data member for use in child
//					classes overridden updateButtonImage calls.  This allows the cliping
//					that is done in the ButtonBase to propagate correctly.
//					Removed all traces of infoTips.
//  08/23/97    CAR button can now be tabbed to and from
//                  when button has focus on Windows a broken line outline will indicate focus
//  08/25/97    CAR when property showFocus is set to true a broken line outline will indicate focus
//                  on Windows showFocus defaults to true, on all other platforms it defaults to false
//  08/28/97    CAR when showFocus is true and button has focus and not on the Macintosh platform, pressing
//                  the space bar will press the button
//  09/01/97    CAR setAppletContext now also called from addNotify in addition to being called from
//                  validate (validate not called when applet loaded in IE)

/**
 * An abstract class used to implement special Symantec buttons.
 * It supports 3-D beveled borders, and the posting of continuous
 * notification events while the button is pressed.
 * <p>
 * @version 1.1, June 26, 1997
 * @author Symantec
 */
public abstract class ButtonBase extends Canvas
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Constructs a default ButtonBase. The defaults are no notifyWhilePressed,
     * no offset, and a bevel height of 2.
     */
    protected ButtonBase()
    {
        pressed				= false;
        released			= true;
        notifyWhilePressed	= false;
        running				= false;
        notified			= false;
        useOffset			= false;
        showURLStatus		= true;
        isAdded				= false;
        notifyTimer			= null;
        notifyDelay			= 1000;
        bevel				= 2;
        pressedAdjustment	= 0;

        try
        {
        	setBorderColor(Color.black);
        	setButtonColor(Color.lightGray);
        }
        catch (PropertyVetoException exc) {}

        if(OS.isWindows())
        {
            try {
                setShowFocus(true);
            } catch (PropertyVetoException e) { }
        }
	}

    /**
     * Sets the "height" (cross-section) of a beveled edge, in pixels.
     * @param height the size of the bevel in pixels
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #getBevelHeight
     */
    public void setBevelHeight(int height) throws PropertyVetoException
    {
    	if(bevel != height)
    	{
			Integer oldValue = new Integer(bevel);
			Integer newValue = new Integer(height);

			vetos.fireVetoableChange("BevelHeight", oldValue, newValue);

		    bevel = height;

		    repaint();

		    changes.firePropertyChange("BevelHeight", oldValue, newValue);
		}
    }

    /**
     * Returns the current "height" (cross-section) of a beveled edge, in pixels.
     * @return the current bevel height in pixels.
     * @see #setBevelHeight
     */
    public int getBevelHeight()
    {
        return bevel;
    }

    /**
     * Sets whether the button will continually post notify events while pressed.
     * @param flag true to post notify events; false to not post events
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #isNotifyWhilePressed
     * @see #setNotifyDelay
     * @see #getNotifyDelay
     */
    public void setNotifyWhilePressed(boolean flag) throws PropertyVetoException
    {
    	if(notifyWhilePressed != flag)
    	{
			Boolean oldValue = new Boolean(notifyWhilePressed);
			Boolean newValue = new Boolean(flag);

			vetos.fireVetoableChange("NotifyWhilePressed", oldValue, newValue);

	        notifyWhilePressed = flag;

	        if (notifyWhilePressed)
	        {
	            notifyTimer = new Timer(notifyDelay, true);
	            notifyTimer.addActionListener(action);
	        }
	        else if (notifyTimer != null)
	        {
	            notifyTimer = null;
	        }

		    changes.firePropertyChange("NotifyWhilePressed", oldValue, newValue);
		}
    }

    /**
     * Gets whether the button will continuously post events while pressed.
     * @return true if it will continuously post events while pressed, false
     * otherwise
     * @see #setNotifyWhilePressed
     * @see #setNotifyDelay
     * @see #getNotifyDelay
     */
    public boolean isNotifyWhilePressed()
    {
        return notifyWhilePressed;
    }

    /**
     * @deprecated
     * @see #isNotifyWhilePressed
     */
    public boolean getNotifyWhilePressed()
    {
        return isNotifyWhilePressed();
    }

    /**
     * Sets the notification event delay in milliseconds.
     * @param delay the delay between notification events in milliseconds
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #setNotifyWhilePressed
     * @see #getNotifyDelay
     */
    public void setNotifyDelay(int delay) throws PropertyVetoException
    {
    	if(notifyDelay != delay)
    	{
			Integer oldValue = new Integer(notifyDelay);
			Integer newValue = new Integer(delay);

			vetos.fireVetoableChange("NotifyDelay", oldValue, newValue);
	        notifyDelay = delay;
	        if(notifyTimer != null)
	        	notifyTimer.setDelay(notifyDelay);
			changes.firePropertyChange("NotifyDelay", oldValue, newValue);
		}
    }

    /**
     * Returns the current delay in milliseconds between notification events.
     * @see #setNotifyWhilePressed
     * @see #setNotifyDelay
     */
    public int getNotifyDelay()
    {
        return notifyDelay;
    }

    /**
     * Sets whether objects in the button will be offset down and to the right
     * bevel height amount or not.
     * This also impacts the way the button is drawn when it is pressed.
     * @param flag true to have objects use the offset; false to have objects not move.
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #isUseOffset
     * @see #setBevelHeight
     * @see #getBevelHeight
     */
    public void setUseOffset(boolean flag) throws PropertyVetoException
    {
		if(useOffset != flag)
		{
			Boolean oldValue = new Boolean(useOffset);
			Boolean newValue = new Boolean(flag);
			vetos.fireVetoableChange("UseOffset", oldValue, newValue);

	        useOffset = flag;
	        repaint();

			changes.firePropertyChange("UseOffset", oldValue, newValue);
		}
    }

    /**
     * Returns whether this button will be shown as having the focus when the mouse enters.
     * @see #setShowFocus
     */
    public boolean isUseOffset()
    {
        return useOffset;
    }

    /**
     * Sets whether this button will be shown as having the focus when the mouse enters.
     * @param flag true to show focus at mouse enter; false to not show at mouse enter
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #isShowFocus
     */
    public void setShowFocus(boolean flag) throws PropertyVetoException
    {
    	if(showFocus != flag)
    	{
			Boolean oldValue = new Boolean(showFocus);
			Boolean newValue = new Boolean(flag);
			vetos.fireVetoableChange("ShowFocus", oldValue, newValue);

	        showFocus = flag;

			changes.firePropertyChange("ShowFocus", oldValue, newValue);
		}
    }

    /**
     * Returns whether this button will be shown as having the focus when the mouse enters.
     * @see #setShowFocus
     */
    public boolean isShowFocus()
    {
        return showFocus;
    }

    /**
     * @deprecated
     * @see #isShowFocus
     */
    public boolean getShowFocus()
    {
        return isShowFocus();
    }

    /**
     * Sets whether the linkURL will be displayed in the status area when the mouse
     * is over the button.
     * This flag also controls erasing of the status area after the URL has been displayed.
     * @param flag true if the linkURL will be displayed in the status area when the mouse
     * is over the button; false if not.
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #isShowURLStatus
     */
    public void setShowURLStatus(boolean flag) throws PropertyVetoException
    {
    	if(showURLStatus != flag)
    	{
			Boolean oldValue = new Boolean(showURLStatus);
			Boolean newValue = new Boolean(flag);
			vetos.fireVetoableChange("ShowURLStatus", oldValue, newValue);

	        showURLStatus = flag;

			changes.firePropertyChange("ShowURLStatus", oldValue, newValue);
		}
    }

    /**
     * If true show the linkURL in the status area when the mouse is over the button.
     * If the linkURL is null, nothing is displayed, regardless of this flag.
     * This flag also controls erasing of the status area after the URL has been displayed.
     * @return true if the linkURL will be displayed in the status area when the mouse
     * is over the button.
     * @see #setShowURLStatus
     */
    public boolean isShowURLStatus()
    {
        return showURLStatus;
    }

	/**
	 * Sets the current border color.
	 * @param color the new border color
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
	 * @see #getBorderColor
	 */
	public void setBorderColor(Color color) throws PropertyVetoException
	{
		if (!GeneralUtils.objectsEqual(borderColor, color))
		{
			Color oldValue = borderColor;

			vetos.fireVetoableChange("BorderColor", oldValue, color);

			borderColor = color;
			try
			{
				disabledBorderColor	= ColorUtils.lighten(borderColor,	0.466);
			}
			catch (IllegalArgumentException exc) {}
			repaint();

			changes.firePropertyChange("BorderColor", oldValue, color);
		}
	}

    /**
     * Gets the current border color.
     * @return the current border color
     * @see #setBorderColor
     */
	public Color getBorderColor()
	{
	    return borderColor;
	}

	/**
	 * Sets the current button color.
	 * @param color the new button color.  The highlights of the button are
	 * derived from this color.
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
	 * @see #getButtonColor
	 */
	public void setButtonColor(Color color) throws PropertyVetoException
	{
		if (!GeneralUtils.objectsEqual(buttonColor, color))
		{
			Color oldValue = buttonColor;

			vetos.fireVetoableChange("ButtonColor", oldValue, color);
			buttonColor = color;
			try
			{
				hilightColor			= ColorUtils.lighten(buttonColor,	0.600);
				pressedHilightColor		= ColorUtils.darken(buttonColor,	0.580);
				disabledHilightColor	= ColorUtils.lighten(buttonColor,	0.666);
				shadowColor				= ColorUtils.darken(buttonColor,	0.250);
				pressedShadowColor		= ColorUtils.darken(buttonColor,	0.100);
				disabledShadowColor		= ColorUtils.darken(buttonColor,	0.166);
				disabledButtonColor		= ColorUtils.lighten(buttonColor,	0.333);
				pressedButtonColor		= ColorUtils.darken(buttonColor,	0.250);
			}
			catch (IllegalArgumentException exc) {}
			repaint();

			changes.firePropertyChange("ButtonColor", oldValue, color);
		}
	}

    /**
     * Gets the current button color.
     * @return the current button color
     * @see #setButtonColor
     */
	public Color getButtonColor()
	{
	    return buttonColor;
	}

    /**
     * Sets the URL of the document to link to when the button is clicked.
     * @param url the URL
     * @exception java.beans.PropertyVetoException
     * if the specified property value is unacceptable
     * @see #getLinkURL
     */
    public void setLinkURL(URL url) throws PropertyVetoException
    {
    	if(!GeneralUtils.objectsEqual(linkURL, url))
    	{
    		URL oldValue = linkURL;

			vetos.fireVetoableChange("LinkURL", oldValue, url);

	        linkURL = url;
	        context = null;

			changes.firePropertyChange("LinkURL", oldValue, url);
		}
    }

    /**
     * Returns the URL of the document to link to when the button is clicked.
     * @see #setLinkURL
     */
    public URL getLinkURL()
	{
        return linkURL;
    }

	/**
	 * Sets the frame specifier for showing a URL document in a browser or applet
	 * viewer. It is interpreted as follows:
	 * <UL>
	 * <DT>"_self"  show document in the current frame</DT>
	 * <DT>"_parent"    show document in the parent frame</DT>
	 * <DT>"_top"   show document in the topmost frame</DT>
	 * <DT>"_blank" show document in a new unnamed toplevel window</DT>
	 * <DT>all others   show document in a new toplevel window with the given name</DT>
	 * </UL>
     * @param newFrame the frame specifier
     * @exception java.beans.PropertyVetoException
     * if the specified property value is unacceptable
     * @see #getFrame
     * @see symantec.itools.util.GeneralUtils#frameTarget_self
     * @see symantec.itools.util.GeneralUtils#frameTarget_parent
     * @see symantec.itools.util.GeneralUtils#frameTarget_top
     * @see symantec.itools.util.GeneralUtils#frameTarget_blank
     */
    public void setFrame(String newFrame) throws PropertyVetoException
    {
    	String oldValue = frame;

		vetos.fireVetoableChange("Frame", oldValue, newFrame);

        frame = newFrame;

		changes.firePropertyChange("Frame", oldValue, newFrame);
    }

	/**
	 * Gets the frame specifier for showing a URL document in a browser or applet
	 * viewer. It is interpreted as follows:
	 * <UL>
	 * <DT>"_self"  show document in the current frame</DT>
	 * <DT>"_parent"    show document in the parent frame</DT>
	 * <DT>"_top"   show document in the topmost frame</DT>
	 * <DT>"_blank" show document in a new unnamed toplevel window</DT>
	 * <DT>all others   show document in a new toplevel window with the given name</DT>
	 * </UL>
	 * @return the frame specifier
     * @see #setFrame
     * @see symantec.itools.util.GeneralUtils#frameTarget_self
     * @see symantec.itools.util.GeneralUtils#frameTarget_parent
     * @see symantec.itools.util.GeneralUtils#frameTarget_top
     * @see symantec.itools.util.GeneralUtils#frameTarget_blank
     */
    public String getFrame()
    {
        return frame;
    }

	/**
	 * Ensures that this component is laid out properly, as needed.
	 * This is a standard Java AWT method which gets called by the AWT to
	 * make sure this component and its subcomponents have a valid layout.
	 * If this component was made invalid with a call to invalidate(), then
	 * it is laid out again.
	 *
	 * It is overridden here to locate the applet containing this component.
	 *
	 * @see java.awt.Component#invalidate
	 */
    public void validate()
    {
        // On validation, try to find the containing applet.  If we can find
        // it, we don't bother doing the link...
        if (context == null)
        {
            Container c;

            c = getParent();

            while (c != null)
            {
                if (c instanceof Applet)
                {
                    setAppletContext(((Applet) c).getAppletContext());
                    break;
                }

                c = c.getParent();
            }
        }
    }

    /**
     * Enables this component so that it will respond to user input.
     * This is a standard Java AWT method which gets called to enable or disable
     * this component. Once enabled this component will respond to user input.
     *
     * @param flag true if the component is to be enabled; false if it is to be disabled.
     * @see java.awt.Component#isEnabled
     */
    public void setEnabled(boolean flag)
    {
    	if(isEnabled() != flag)
    	{
	    	if(flag)
	    	{
	    		// !!! LAB !!!	This MUST be a call to super.enable(), not super.enable(boolean)
	    		//				or super.setEnabled(boolean)  If it is not, then it will result
	    		//				in an endless loop!
    			super.setEnabled(true);
		        pressed = false;
		        pressedAdjustment = 0;
			}
			else
			{
	    		// !!! LAB !!!	This MUST be a call to super.disable(), not super.enable(boolean)
	    		//				or super.setEnabled(boolean)  If it is not, then it will result
	    		//				in an endless loop!
    			super.setEnabled(false);
		        if (notifyTimer != null)
		        {
		            notifyTimer.stop();
		        }

		        pressed = false;
		        pressedAdjustment = 0;
			}

	        repaint();
		}
    }

    /**
     * @deprecated
     * @see #setEnabled
     */
  /*  public void enable()
    {
    	setEnabled(true);
    }*/

    /**
     * @deprecated
     * @see #setEnabled
     */
    /*public void disable()
    {
    	setEnabled(false);
    }*/

	/**
	 * Tells this component that it has been added to a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is added to a container. Typically, it is used to
	 * create this component's peer.
	 *
	 * It has been overridden here to hook-up event listeners.
	 *
	 * @see #removeNotify
	 */
	public synchronized void addNotify()
	{
		super.addNotify();

		//Hook up listeners
		if (focus == null)
		{
			focus = new Focus();
			addFocusListener(focus);
		}
		if (key == null)
		{
			key = new Key();
			addKeyListener(key);
		}
		if (mouse == null)
		{
			mouse = new Mouse();
			addMouseListener(mouse);
		}
		if (bevelVeto == null)
		{
			bevelVeto = new BevelVeto();
			addBevelHeightListener(bevelVeto);
		}
		if (frameVeto == null)
		{
			frameVeto = new FrameVeto();
			addFrameListener(frameVeto);
		}
		isAdded = true;

        // On addNotify, try to find the containing applet.
        if (context == null)
        {
            Container c;

            c = getParent();

            while (c != null)
            {
                if (c instanceof Applet)
                {
                    setAppletContext(((Applet) c).getAppletContext());
                    break;
                }

                c = c.getParent();
            }
        }
	}

	/**
	 * Tells this component that it is being removed from a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is removed from a container. Typically, it is used to
	 * destroy the peers of this component and all its subcomponents.
	 *
	 * It has been overridden here to unhook event listeners.
	 *
	 * @see #addNotify
	 */
	public synchronized void removeNotify()
	{
		//Unhook listeners
		if (focus != null)
		{
			removeFocusListener(focus);
			focus = null;
		}
		if (key != null)
		{
			removeKeyListener(key);
			key = null;
		}
		if (mouse != null)
		{
			removeMouseListener(mouse);
			mouse = null;
		}
		if (bevelVeto != null)
		{
			removeBevelHeightListener(bevelVeto);
			bevelVeto = null;
		}
		if (frameVeto != null)
		{
			removeFrameListener(frameVeto);
			frameVeto = null;
		}

		super.removeNotify();
		isAdded = false;
	}

    /**
     * Handles redrawing of this component on the screen.
     * This is a standard Java AWT method which gets called by the Java
     * AWT (repaint()) to handle repainting this component on the screen.
     * The graphics context clipping region is set to the bounding rectangle
     * of this component and its [0,0] coordinate is this component's
     * top-left corner.
     * Typically this method paints the background color to clear the
     * component's drawing space, sets graphics context to be the foreground
     * color, and then calls paint() to draw the component.
     *
     * It is overridden here to prevent the flicker associated with the standard
     * update() method's repainting of the background before painting the component
     * itself.
     *
     * @param g the graphics context
     * @see java.awt.Component#repaint
     * @see #paint
     */
    public void update(Graphics g)
    {
        paint(g);
    }

	/**
	 * Paints this component using the given graphics context.
     * This is a standard Java AWT method which typically gets called
     * by the AWT to handle painting this component. It paints this component
     * using the given graphics context. The graphics context clipping region
     * is set to the bounding rectangle of this component and its [0,0]
     * coordinate is this component's top-left corner.
     *
     * @param g the graphics context used for painting
     * @see java.awt.Component#repaint
     * @see #update
	 */
    public void paint(Graphics g)
    {
		updateButtonImage();
		g.drawImage(buttonImage, 0, 0, this);
    }

	/**
	 * Returns the recommended dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the recommended size of this component.
	 */
    public Dimension getPreferredSize()
    {
		return new Dimension(bevel + bevel + 2, bevel + bevel + 2);
    }

    /**
	 * Returns the minimum dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the minimum size of this component.
     * It simply returns the results of a call to preferedSize().
	 */
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    public boolean isFocusable()
    {
        return true;
    }

    /**
     * Adds a listener for all event changes.
     * @param listener the listener to add.
     * @see #removePropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
    	changes.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener for all event changes.
     * @param listener the listener to remove.
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
    	changes.removePropertyChangeListener(listener);
    }

    /**
     * Adds a vetoable listener for all event changes.
     * @param listener the listener to add.
     * @see #removeVetoableChangeListener
     */
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
		vetos.addVetoableChangeListener(listener);
    }

    /**
     * Removes a vetoable listener for all event changes.
     * @param listener the listener to remove.
     * @see #addVetoableChangeListener
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
    	vetos.removeVetoableChangeListener(listener);
    }

    /**
     * Adds a listener for the BevelHeight property changes.
     * @param listener the listener to add.
     * @see #removeBevelHeightListener(java.beans.PropertyChangeListener)
     */
    public void addBevelHeightListener(PropertyChangeListener listener)
    {
    	changes.addPropertyChangeListener("BevelHeight", listener);
    }

    /**
     * Removes a listener for the BevelHeight property changes.
     * @param listener the listener to remove.
     * @see #addBevelHeightListener(java.beans.PropertyChangeListener)
     */
    public void removeBevelHeightListener(PropertyChangeListener listener)
    {
    	changes.removePropertyChangeListener("BevelHeight", listener);
    }

    /**
     * Adds a vetoable listener for the BevelHeight property changes.
     * @param listener the listener to add.
     * @see #removeBevelHeightListener(java.beans.VetoableChangeListener)
     */
    public void addBevelHeightListener(VetoableChangeListener listener)
    {
    	vetos.addVetoableChangeListener("BevelHeight", listener);
    }

    /**
     * Removes a vetoable listener for the BevelHeight property changes.
     * @param listener the listener to remove.
     * @see #addBevelHeightListener(java.beans.VetoableChangeListener)
     */
    public void removeBevelHeightListener(VetoableChangeListener listener)
    {
    	vetos.removeVetoableChangeListener("BevelHeight", listener);
    }

    /**
     * Adds a listener for the Frame property changes.
     * @param listener the listener to add.
     * @see #removeFrameListener(java.beans.PropertyChangeListener)
     */
    public void addFrameListener(PropertyChangeListener listener)
    {
    	changes.addPropertyChangeListener("Frame", listener);
    }

    /**
     * Removes a listener for the Frame property changes.
     * @param listener the listener to remove.
     * @see #addFrameListener(java.beans.PropertyChangeListener)
     */
    public void removeFrameListener(PropertyChangeListener listener)
    {
    	changes.removePropertyChangeListener("Frame", listener);
    }

    /**
     * Adds a vetoable listener for the Frame property changes.
     * @param listener the listener to add.
     * @see #removeFrameListener(java.beans.VetoableChangeListener)
     */
    public void addFrameListener(VetoableChangeListener listener)
    {
    	vetos.addVetoableChangeListener("Frame", listener);
    }

    /**
     * Removes a vetoable listener for the Frame property changes.
     * @param listener the listener to remove.
     * @see #addFrameListener(java.beans.VetoableChangeListener)
     */
    public void removeFrameListener(VetoableChangeListener listener)
    {
    	vetos.removeVetoableChangeListener("Frame", listener);
    }

    /**
     * Sets the command name of the action event fired by this button.
     * @param command The name of the action event command fired by this button
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
    public void setActionCommand(String command) throws PropertyVetoException
    {
    	String oldValue = actionCommand;

		vetos.fireVetoableChange("ActionCommand", oldValue, command);
        actionCommand = command;
		changes.firePropertyChange("ActionCommand", oldValue, command);
    }

    /**
     * Returns the command name of the action event fired by this button.
     * @return the action command name
     */
    public String getActionCommand()
    {
        return actionCommand;
    }

    /**
     * Adds the specified action listener to receive action events
     * from this button.
     * @param l the action listener
     */
	public void addActionListener(ActionListener l)
	{
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this button.
     * @param l the action listener
     */
	public void removeActionListener(ActionListener l)
	{
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	class Focus extends java.awt.event.FocusAdapter implements java.io.Serializable
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

			public void focusGained(FocusEvent e)
	    {
	        hasFocus = true;
	        repaint();
	    }

	    public void focusLost(FocusEvent e)
	    {
	        hasFocus = false;
	        repaint();
	    }
	}

	class Key extends java.awt.event.KeyAdapter implements java.io.Serializable
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
			public void keyPressed(KeyEvent evt)
	    {
	        boolean isSpaceBar = (evt.getKeyCode() & KeyEvent.VK_SPACE) == KeyEvent.VK_SPACE;

	        if(isSpaceBar && hasFocus && showFocus && !OS.isMacintosh()) {
	        inButton = true;
	        notified = false;
	        if (notifyTimer != null && notifyWhilePressed && !running)
	        {
	            running     = true;
	            notifyTimer.start();
	        }

	        pressed           = true;
	        released          = false;
	        if(useOffset)
		        pressedAdjustment = bevel;
	        repaint();
	        }

	    }
	    public void keyReleased(KeyEvent evt)
	    {
	        boolean isSpaceBar = (evt.getKeyCode() & KeyEvent.VK_SPACE) == KeyEvent.VK_SPACE;

	        if(isSpaceBar && hasFocus && showFocus && !OS.isMacintosh()) {
	        inButton = false;
	        if (notifyTimer != null && running)
	        {
	            running = false;
	            notifyTimer.stop();
	        }

	        if (pressed)
	        {
	            pressed = false;
	            pressedAdjustment = 0;

	            if (!notifyWhilePressed || !notified)
	            {
	            	//Handle going to the linkURL
	            	linkToURL();
	                sourceActionEvent();
	            }
	        }

	        released = true;
        	repaint();
	        }

	    }
	}

	/**
	 * This is the Mouse Event handling innerclass.
	 */
	class Mouse extends java.awt.event.MouseAdapter implements java.io.Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
	     * Handles the Mouse Pressed events
		 * If the notifyWhilePressed flag is true the notification Timer is started
		 * @param e the MouseEvent
		 * @see #setNotifyWhilePressed
		 * @see #setNotifyDelay
		 * @see #mouseReleased
		 */
		public void mousePressed(MouseEvent e)
		{
	        requestFocus();
	        notified = false;
	        if (notifyTimer != null && notifyWhilePressed && !running)
	        {
	            running     = true;
	            notifyTimer.start();
	        }

	        pressed           = true;
	        released          = false;
	        if(useOffset)
		        pressedAdjustment = bevel;
	        repaint();
		}

	    /**
	     * Handles the Mouse Released events
	     * If the notification timer is running it is stopped.
	     * If the mouse was pressed inside the button then fire an action event.
	     * @param e the MouseEvent
	     * @see #mousePressed
	     */
	    public void mouseReleased(MouseEvent e)
	    {
	        if (notifyTimer != null && running)
	        {
	            running = false;
	            notifyTimer.stop();
	        }

	        if (pressed)
	        {
	            pressed = false;
	            pressedAdjustment = 0;

	            if (!notifyWhilePressed || !notified)
	            {
	            	//Handle going to the linkURL
	            	linkToURL();
	                sourceActionEvent();
	            }
	        }

	        released = true;
	        if(inButton)
	        	repaint();
	    }

		/**
		 * Handles Mouse Entered events
		 * @param e the MouseEvent
		 */
		public void mouseEntered(MouseEvent e)
		{
	        inButton = true;

	        if (!released)
	        {
		        pressed = true;
		        if(useOffset)
		        	pressedAdjustment = bevel;

			    if(notifyTimer != null && notifyWhilePressed && !running)
				{
		            running = true;
		            notifyTimer.start();
				}

				//Display the linkURL
				if (showURLStatus && context != null && linkURL != null)
				{
					context.showStatus(linkURL.toString());
				}

		        repaint();
			}
		}

		/**
		 * Handles Mouse Exited events
		 * @param e the MouseEvent
		 */
		public void mouseExited(MouseEvent e)
		{
	        inButton = false;

	        if (notifyTimer != null && running)
	        {
	            running = false;
	            notifyTimer.stop();
	        }

	        if (pressed)
	        {
	            pressed = false;
	            pressedAdjustment = 0;
	            repaint();
	        }

			if (showURLStatus && context != null && linkURL != null)
			{
				context.showStatus("");
			}
		}
	}

	/**
	 * This is the Action Event handling innerclass.
	 */
	class Action implements java.awt.event.ActionListener, java.io.Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//Implement ActionListener to catch ActionEvents sent by either the notifyTimer.
		/**
		 * Handles Action events
		 * @param e the ActionEvent
		 */
		public void actionPerformed(ActionEvent e)
		{
	        if (e.getSource() == notifyTimer && notifyWhilePressed && !java.beans.Beans.isDesignTime())
	        {
	        	notified = true;
	            sourceActionEvent();
	            return;
	        }
		}
	}

	/**
	 * This is the PropertyChangeEvent handling inner class for the constrained BevelHeight property.
	 * Handles vetoing BevelHeights that are not valid.
	 */
	class BevelVeto implements java.beans.VetoableChangeListener, java.io.Serializable
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

			/**
	     * This method gets called when an attempt to change the constrained BevelHeight property is made.
	     * Ensures the given bevel size is valid for this button.
	     *
	     * @param     e a <code>PropertyChangeEvent</code> object describing the
	     *   	      event source and the property that has changed.
	     * @exception PropertyVetoException if the recipient wishes the property
	     *              change to be rolled back.
	     */
	    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
	    {
	    	int i = ((Integer)e.getNewValue()).intValue();
	        if (!isValidBevelSize(i))
	        {
	            throw new PropertyVetoException("Invalid bevel size: " + i, e);
	        }
	    }
	}

	/**
	 * This is the PropertyChangeEvent handling inner class for the constrained Frame property.
	 * Handles vetoing Frame strings that are not valid.
	 */
	class FrameVeto implements java.beans.VetoableChangeListener, java.io.Serializable
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

			/**
	     * This method gets called when an attempt to change the constrained Frame property is made.
	     * Ensures the given Frame string is valid for this button.
	     *
	     * @param     e a <code>PropertyChangeEvent</code> object describing the
	     *   	      event source and the property that has changed.
	     * @exception PropertyVetoException if the recipient wishes the property
	     *              change to be rolled back.
	     */
	    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
	    {
	    	String string = (String)e.getNewValue();
	        if (!isValidFrame(string))
	        {
	            throw new PropertyVetoException("Invalid Frame: " + string, e);
	        }
	    }
	}

    /**
     * Fire an action event to the listeners.
     */
	protected void sourceActionEvent()
	{
		if (actionListener != null)
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand));
	}

    /**
     * Is the given bevel size valid for this button.
     * @param i the given bevel size
     * @return true if the given bevel size is acceptable, false if not.
     */
    protected boolean isValidBevelSize(int i)
    {
        Dimension s = getSize();

        if (i < 0 || i >= (s.width / 2) || i >= (s.height / 2))
            return false;
        else
        	return true;
    }

    /**
     * Is the given frame string valid.
     * @param string the given frame
     * @return true if the given frame is acceptable, false if not.
     * To be valid it has to be null, or one of the four strings below:
     * @see symantec.itools.util.GeneralUtils#frameTarget_self
     * @see symantec.itools.util.GeneralUtils#frameTarget_parent
     * @see symantec.itools.util.GeneralUtils#frameTarget_top
     * @see symantec.itools.util.GeneralUtils#frameTarget_blank
     */
    protected boolean isValidFrame(String string)
    {
    	if(string == null || string.equals(""))
    		return true;

    	if(	string.equals(GeneralUtils.frameTarget_self)	||
    		string.equals(GeneralUtils.frameTarget_parent)	||
    		string.equals(GeneralUtils.frameTarget_top)		||
    		string.equals(GeneralUtils.frameTarget_blank)	)
            return true;
        else
        	return false;
    }

	/**
	 * Tell the browser to show the document referenced by the linkURL.
	 * If the frame specifier is not null or empty, then tell the browser
	 * to open the document with that frame.
	 */
	protected void linkToURL()
	{
		if (context != null && linkURL != null)
		{
		    if (frame == null || frame.length() == 0)
		        context.showDocument(linkURL);
		    else
		        context.showDocument(linkURL, frame);
		}
	}

    /**
     * Sets the applet context used to view documents.
     * @param c the new applet context
     */
    protected void setAppletContext(AppletContext c)
    {
        context = c;
    }

	/**
	 * Maintains the buttonImage size and draws the
	 * button in the buttonImage offscreen image.
	 * @see #paint
	 */
	protected void updateButtonImage()
	{
        Dimension s	= getSize();
        int width	= s.width;
        int height	= s.height;
        int x		= bevel  + 1;
        int y		= bevel  + 1;
        int w		= width  - 1;
        int h		= height - 1;
        int i;
        Color highlight1, highlight2, fillColor, tempBorderColor;
		boolean raised = !(pressed && inButton);

		if(isButtonImageInvalid())
		{
			buttonImage = createImage(width, height);
			try
			{
	            MediaTracker tracker = new MediaTracker(this);
	            tracker.addImage(buttonImage, 0);
	            tracker.waitForID(0);
            }
            catch(InterruptedException e){}
        }

        buttonImageGraphics = buttonImage.getGraphics();
        Color oldColor = buttonImageGraphics.getColor();

		if(isEnabled())	//Enabled
		{
			tempBorderColor = borderColor;

			if (raised)
			{
				fillColor	= buttonColor;
				highlight1	= hilightColor;
				highlight2	= shadowColor;

			}
			else	//Pressed
			{
			    fillColor	= pressedButtonColor;
				highlight1	= pressedHilightColor;
			    highlight2	= pressedShadowColor;

			}
		}
		else //Disabled
		{
			tempBorderColor	= disabledBorderColor;
			fillColor		= disabledButtonColor;
			highlight1		= disabledHilightColor;
			highlight2		= disabledShadowColor;
		}

		if(!raised && useOffset)
		{
			//Fill the button content
			buttonImageGraphics.setColor(fillColor);
			buttonImageGraphics.fillRect(x, y, w - x, h - y);

			//Draw the bevels
			buttonImageGraphics.setColor(highlight1);
			for(i = 1; i <= bevel; i++)
			{
			    buttonImageGraphics.drawLine(i, i, i, h);
			    buttonImageGraphics.drawLine(i, i, w, i);
			}
		}

		if(raised || !useOffset)
		{
			//Fill the button content
			buttonImageGraphics.setColor(fillColor);
			buttonImageGraphics.fillRect(x, y, w - x, h - y);

		    //Draw the bevels
			buttonImageGraphics.setColor(highlight1);
		    for(i = 1; i <= bevel; i++)
		    {
		        buttonImageGraphics.drawLine(i, i, i, h - i);
		        buttonImageGraphics.drawLine(i, i, w - i, i);
		    }
			buttonImageGraphics.setColor(highlight2);
		    for(i = 1; i <= bevel; ++i)
		    {
		        buttonImageGraphics.drawLine(i, h - i, w - i, h - i);
		        buttonImageGraphics.drawLine(w - i, i, w - i, h - i);
		    }
		}

		//Draw the border
		buttonImageGraphics.setColor(tempBorderColor);
        buttonImageGraphics.drawLine(1, 0, w - 1, 0);
        buttonImageGraphics.drawLine(0, 1, 0, h - 1);
        buttonImageGraphics.drawLine(1, h, w - 1, h);
        buttonImageGraphics.drawLine(w, h - 1, w, 1);

        if (hasFocus && showFocus) {
		    buttonImageGraphics.setColor(java.awt.Color.darkGray);
            for(x = 3; x <= w - 3; x += 3)
                buttonImageGraphics.drawLine(x, 3, x+1, 3);
            for(y = 3; y <= h - 3; y += 3)
                buttonImageGraphics.drawLine(3, y, 3, y+1);
            for(x = 3; x <= w - 3; x += 3)
                buttonImageGraphics.drawLine(x, h-3, x+1, h-3);
            for(y = 3; y <= h - 3; y += 3)
                buttonImageGraphics.drawLine(w-3, y, w-3, y+1);
        }


		//!!! LAB !!! This should be changed to setClip when it works.
		//Set the clipping area to be the inside of the button.
		buttonImageGraphics.clipRect(bevel + 1, bevel + 1, width - bevel - bevel - 2, height - bevel - bevel - 2);

        //Restore the original color
		buttonImageGraphics.setColor(oldColor);
	}

	/**
	 * Returns true if a button image has been set, but it is not the
	 * size of this component.
	 */
	protected boolean isButtonImageInvalid()
	{
		Dimension s = getSize();
		return (buttonImage == null || s.width	 != buttonImage.getWidth(this) || s.height != buttonImage.getHeight(this));
	}

    /**
     * True if the button is currently pressed.
     */
    transient protected boolean   pressed;
    /**
     * True if the button has been released.
     */
    transient protected boolean   released;
    /**
     * True if the mouse is over this button.
     */
    transient protected boolean   inButton;
    /**
     * If true the button will continuously post events while pressed.
     */
    protected boolean   notifyWhilePressed;
    /**
     * True if the notify timer is running.
     */
    transient protected boolean   running;
    /**
     * True if a notification has been posted in response to a mouse down.
     */
    transient protected boolean   notified;
    /**
     * If true show the focus when the mouse enters the button.
     */
    protected boolean   showFocus = false;
    /**
     * If true set pressedAdjustment accordingly, else, it is always 0.
     */
    protected boolean   useOffset;
    /**
     * If true show the linkURL in the status area when the mouse is over the button.
     * If the linkURL is null, nothing is displayed, regardless of this flag.
     * This flag also controls erasing of the status area after the URL has been displayed.
     */
    protected boolean   showURLStatus;
    /**
     * Keeps track of wheather or not the button is added to a container.
     * Check before attempting to getFontMetrics() to avoid getting a null pointer.
     */
    transient protected boolean   isAdded;
    /**
     * The "height" (cross-section) of a beveled edge, in pixels.
     */
    protected int       bevel;
    /**
     * The delay in milliseconds between notifications while the button is pressed.
     */
    protected int       notifyDelay;
    /**
     * A drawing location adjustment for the 3-D bevel while button is pressed.
     */
    protected int       pressedAdjustment;
	/**
	 * Frame specifier for showing a URL document in a browser or applet
	 * viewer. It is interpreted as follows:
	 * <UL>
	 * <DT>"_self"  show document in the current frame</DT>
	 * <DT>"_parent"    show document in the parent frame</DT>
	 * <DT>"_top"   show document in the topmost frame</DT>
	 * <DT>"_blank" show document in a new unnamed toplevel window</DT>
	 * <DT>all others   show document in a new toplevel window with the given name</DT>
	 * </UL>
	 */
    protected String	frame = null;
    /**
     * The color of the border around the button.
     */
    protected Color	borderColor;
    /**
     * The color of the content of the button.  The highlights are derived from this color.
     */
    protected Color	buttonColor;
    /**
     * Timer used to time notification events while button pressed.
     */
    protected Timer	notifyTimer;
    /**
     *	The offscreen buffer to draw the button in.
     */
    transient protected Image	buttonImage = null;
    /**
     *	The Graphics of the offscreen buffer to draw the button in.
     */
    transient protected Graphics	buttonImageGraphics = null;
    /**
     * The URL of the document to show when the button is clicked.
     */
    protected URL	linkURL = null;
    /**
     * Applet context that shows the document.
     */
    transient protected AppletContext context = null;

    String actionCommand;
	ActionListener actionListener = null;
	transient boolean hasFocus = false;

	private Color hilightColor			= null;
	private Color pressedHilightColor	= null;
	private Color disabledHilightColor	= null;
	private Color shadowColor			= null;
	private Color pressedShadowColor	= null;
	private Color disabledShadowColor	= null;
	private Color disabledBorderColor	= null;
	private Color disabledButtonColor	= null;
	private Color pressedButtonColor	= null;
    private Key key = null;
    private Focus focus = null;
    private Action	action		= new Action();
    private Mouse		mouse		= null;
    private BevelVeto	bevelVeto	= null;
    private FrameVeto	frameVeto	= null;
	private VetoableChangeSupport vetos = new VetoableChangeSupport(this);
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
}
