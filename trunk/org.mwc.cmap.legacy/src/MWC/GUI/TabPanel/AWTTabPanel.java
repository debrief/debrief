package MWC.GUI.TabPanel;

/*
 Copyright (c) 1995, 1996 Connect! Corporation, Inc. All Rights Reserved.
 Source code usage restricted as defined in Connect! Widgets License Agreement
*/

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Vector;


//	01/29/97	RKM	Integrated Tim's changes to preserve the current index
//	05/30/97	LAB	Updated to support Java 1.1
//	06/01/97	RKM	Changed symantec.beans references to java.beans
//	07/22/97	LAB	Updated preferredSize and minimumSize to getPreferredSize and getMinimumSize.
//  07/25/97    CAR marked fields transient as needed
//                  innerclasses implement java.io.Serializable
//	08/21/97	RKM	Used setSuppressRepaints in adding of components
//  08/28/97    CAR add(Component, int) now uses the specified index position to place the new tab
//  08/29/97    CAR modified getPreferredSize and getMinimumSize

/**
 * TabPanel is a Panel extension which provides for a tabbed dialog effect.
 * Along the top (by default) of the panel is a series of file folder-like tabs,
 * each with a text label. Each tab is associated with a panel or component
 * that gets shown when the user clicks on the tab.
 * The TabPanel automatically manages swapping panels when a tab selected.
 * It can be used directly or extended.
 * When extending from TabPanel be sure to super()
 * during construction and to super.handleEvent(evt) from
 * handleEvent if you override it.
 *
 * @author Scott Fauerbach
 */
public class AWTTabPanel extends BaseTabbedPanel implements java.io.Serializable,
	MWC.GUI.CoreTabPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Constructs a TabPanel with tabs on top, rounded
     */
	public AWTTabPanel()
	{
		this(TOP, ROUNDED);
	}

    /**
     * Obsolete. Use TabPanel(int tabsPostion, int tabsStyle).
     */
     // @see symantec.itools.awt.TabPanel#TabPanel(int, int)
	public AWTTabPanel(boolean bTabsOnTop)
	{
		this(bTabsOnTop ? TOP : BOTTOM, bTabsOnTop ? ROUNDED : SQUARE);
	}

    /**
     * Constructs a TabPanel with the tabs in the given position and
     * having the specified look.
     * Note that if the tabs are on top, then they always are rounded.
     * @param tabsPosition a constant indicating TOP or BOTTOM
     * @param tabsStyle a constant indicating ROUNDED or SQUARE
     * @see symantec.itools.awt.BaseTabbedPanel#TOP
     * @see symantec.itools.awt.BaseTabbedPanel#BOTTOM
     * @see symantec.itools.awt.BaseTabbedPanel#ROUNDED
     * @see symantec.itools.awt.BaseTabbedPanel#SQUARE
     */
	public AWTTabPanel(int tabsPostion, int tabsStyle)
	{
		super(tabsPostion, tabsStyle);
		vPanels = new Vector<Component>();
		String sOS = System.getProperty("os.name");
		if (sOS.equals("Windows 95"))
			bOsHack = true;
		else
			bOsHack = false;

		super.addCurrentTabListener(myPropertyChangeHandler);
	}

    /**
     * Returns the zero-relative index of the currently selected panel.
     * @return the currently selected panel or -1 if none are shown
     * @see #setCurrentPanelNdx
     */
	public int getCurrentPanelNdx()
	{
	    return curIndex;
	}

    /**
     * Selects the specified tab and shows its associated panel.
     * @param index the zero-relative index of the tab to select
     * @see #getCurrentPanelNdx
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
	public void setCurrentPanelNdx(int index) throws PropertyVetoException
	{
		if(index != curIndex)
		{
			Integer oldindex = new Integer(curIndex);
			Integer newindex = new Integer(index);

			vetos.fireVetoableChange("CurrentPanelNdx", oldindex, newindex);
		    showTabPanel(index);

	        // If we aren't designing, set current index even if the panel hasn't been
	        // added yet (we'll switch to it in add())
	        if (! java.beans.Beans.isDesignTime())
	            curIndex = index;

	        changes.firePropertyChange("CurrentPanelNdx", oldindex, newindex);
		}
	}

    /**
     * Sets the tab labels associated with the panel positions.
     * Note that the panels do not need to have been added yet for
     * this method to work.
     * @param sLabels an array of tab labels for the panel positions
     * @see #getPanelLabels
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
	public void setPanelLabels(String[] sLabels) throws PropertyVetoException
	{
		String[] oldLabels = labels;

		vetos.fireVetoableChange("PanelLabels", oldLabels, sLabels);

	    labels=sLabels;
	    updatePanelLabels();

        changes.firePropertyChange("PanelLabels", oldLabels, sLabels);
	}

    /**
     * Gets the current tab labels associated with the panel positions.
     * @return an array of tab labels for the panel positions
     * @see #setPanelLabels
     */
	public String[] getPanelLabels()
	{
	    return labels;
	}

    /**
     * Sets the tab label  associated with the current panels at the given index.
     * @param String newLabel the label to use for the tab at the given index.
     * @param int labelIndex an index in the array of tab labels for the current panels.
     * @see getPanelLabel
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
	public void setPanelLabel(String newLabel, int labelIndex) throws PropertyVetoException
	{

		if(labelIndex >= 0 && labelIndex < labels.length && labels[labelIndex] != newLabel)
		{
			String oldLabel = labels[labelIndex];
			vetos.fireVetoableChange("PanelLabel", oldLabel, newLabel);

		    labels[labelIndex] = newLabel;
		    updatePanelLabels();

	        changes.firePropertyChange("PanelLabel", oldLabel, newLabel);
		}

	}

    /**
     * Gets the current tab label associated with the current panels at the given index.
     * @param int labelIndex an index in the array of tab labels for the current panels.
     * @return String the label of the panel at the given index. Null if the index is out of range.
     * @see setPanelLabel
     */
	public String getPanelLabel(int labelIndex)
	{
		if(labelIndex >= 0 && labelIndex < labels.length)
		{
	    	return labels[labelIndex];
	    }
	    else
	    	return null;
	}

    /**
     * Puts the tabs on the top or bottom of the dialog.
     * @param bTabsOnBottom if true the tabs are placed at the bottom of the
     * dialog, if false on top
     * @see #getTabsOnBottom
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
	public void setTabsOnBottom(boolean bTabsOnBottom) throws PropertyVetoException
	{
		Boolean oldValue = new Boolean(getTabsOnBottom());
		Boolean newValue = new Boolean(bTabsOnBottom);

		if(!oldValue.equals(newValue))
		{
			vetos.fireVetoableChange("TabsOnBottom", oldValue, newValue);

			setTabsInfo(bTabsOnBottom ? BOTTOM : TOP, bTabsOnBottom ? SQUARE : ROUNDED);
		    doLayout();

		    changes.firePropertyChange("TabsOnBottom", oldValue, newValue);
		}
	}

    /**
     * Gets whether the tabs are at the bottom of the dialog.
     * @return true if the tabs are at the bottom of the dialog, false if
     * they are at the top
     * @see #setTabsOnBottom
     */
	public boolean isTabsOnBottom()
	{
	     return getTabsPosition()==TOP ? false : true;
	}

    /**
     * @deprecated
     * @see #isTabsOnBottom()
     */
	public boolean getTabsOnBottom()
	{
	     return isTabsOnBottom();
	}

    /**
     * Replaces a tab and its associated panel at the index specified.
     * If it is desired to only change the label, use the base class
     * BaseTabbedPanel's method setTab(String sLabel, boolean bEnabled, int index).
	 * @param sLabel the new tab label
	 * @param bEnabled enable the tab or not
     * @param panel the new panel
     * @param index the zero-relative index of the tab to change
     * @see BaseTabbedPanel#setTab
	 * @see #getTabPanel
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
	public synchronized void setTabPanel(String sLabel, boolean bEnabled, Component panel, int index) throws PropertyVetoException
	{
		if ((index < 0) || (index >= vPanels.size()))
			return;

		if (index == getCurrentTab() && !bEnabled)
			return;

		try
		{
			Component oldPanel = (Component) vPanels.elementAt(index);
			vetos.fireVetoableChange("TabPanel", oldPanel, panel);

			vPanels.setElementAt(panel, index);
			setTab(sLabel, bEnabled, index);

			changes.firePropertyChange("TabPanel", oldPanel, panel);
		}
		catch (ArrayIndexOutOfBoundsException e) {}

	}

	/**
	 * Gets the panel for the tab at the given index.
	 * @param index zero-relative index of the tab
	 * @return returns the Panel associated with the tab
	 * @see #setTabPanel
	 */
	public synchronized Component getTabPanel(int index)
	{
		if ((index < 0) || (index >= vPanels.size()))
			return null;

		Component p = null;
		try
		{
			p =  (Component)vPanels.elementAt(index);
		}
		catch (ArrayIndexOutOfBoundsException e) {}

		return p;
	}

	/**
	 * Gets the index for a specific panel.
	 * @param panel the panel to get the index of
	 * @return the zero-relative index of the panel or -1 if it is not found
	 */
	public synchronized int getPanelTabIndex(Component panel)
	{
		return vPanels.indexOf(panel);
	}

	/**
	 * Selects the tab at the given index, showing it and its associated panel.
	 * The panel is activated, ready for user input.
	 * The tab position must be enabled.
	 * @param index zero-relative index of the tab to select
	 * @see #enableTabPanel
	 */
	public synchronized void showTabPanel(int index)
	{
		if ( isEnabled(index) )
		{
			try
			{
				Component p = (Component) vPanels.elementAt(index);
				setCurrentTab(index);
//				if (bOsHack && p != null)
//				{
//					p.hide();
//					setPanel(p);
//					p.show();
//				}
//				else
					showPanel(p);
			}
			catch (ArrayIndexOutOfBoundsException e) {}
			catch (PropertyVetoException e) {}
		}
	}

    /**
     * Appends a new tab and associated panel, which will be shown when the
     * tab is selected. The tab/panel is added after the last existing
     * panel.
	 * @param sLabel the tab label
	 * @param bEnabled enable the tab or not
     * @param panel the panel to associate with the tab
	 * @return the zero-relative index of the newly added tab panel
     */
	public int addTabPanel(String sLabel, boolean bEnabled, Component panel)
	{
		return addTabPanel(sLabel, bEnabled, panel, -1);
	}

    /**
     * Adds a new tab and associated panel. The tab/panel is added at the
     * specified index.
	 * @param sLabel the tab label
	 * @param bEnabled enable the tab or not
     * @param panel the panel to associate with the tab
     * @param pos the zero-relative index of the new tab panel
	 * @return the zero-relative index of the newly added tab panel
     */
	public int addTabPanel(String sLabel, boolean bEnabled, Component panel, int pos)
	{
		if (pos == -1)
		    vPanels.addElement(panel);
		else
		    vPanels.insertElementAt(panel, pos);

		return addTab(sLabel, bEnabled, pos);
	}

    /**
     * Adds a component to the end of this container.
     * This is a standard Java AWT method which gets called to add a
     * component to a container. The specified component is added to
     * the end of this container.
     * <p>
     * If the tab label for this position has not been set it is given
     * the name "tab - #", where # is the zero-relative index of its
     * position.
     *
     * @param comp the component to add
     * @return the added component
     * @see #remove
     */
	public Component add(Component comp) { return add(comp,-1); }


    /**
     * Adds a component to the end of this container.
     * This is a standard Java AWT method which gets called to add a
     * component to a container. Typically, the specified component is added to
     * this container at the given zero-relative position index. A
     * position index of -1 would append the component to the end.
     * <p>
     * It is overridden so that it only appends to the TabPanel.
     * <p>
     * If the tab label for this position has not been set it is given
     * the name "tab - #", where # is the zero-relative index of its
     * position.
     *
     * @param comp the component to add
     * @param pos the zero-relative index at which to add the component or -1
     * for end (IGNORED)
     * @return the added component
     * @see #remove
     */
	public synchronized Component add(Component comp, int pos)
	{
		boolean wasSuppressingRepaints = setSuppressRepaints(true);

		try
		{
			int newIndex = addTabPanel(createDefaultLabel(vPanels.size()),true,comp,pos);

			// If this is the panel that we've set to be the default, or we're designing,
			// go ahead and switch to the new panel.
			if (newIndex == curIndex || java.beans.Beans.isDesignTime())
				showTabPanel(newIndex);
			updatePanelLabels();
		}
		finally
		{
			setSuppressRepaints(wasSuppressingRepaints);
		}

		triggerRepaint();

		return comp;
	}

    /**
     * Takes no action.
     * This is a standard Java AWT method which gets called to add a
     * component to a container.
     * It is overridden here to do nothing, so the user cannot change the way
     * this container works.
     *
     * @param name the positioning directive for the layout manager (IGNORED)
     * @param comp the component to add (IGNORED)
     * @return the component parameter
     */
	public synchronized Component add(String name, Component comp) { return comp; }

    /**
	 * Removes the specified component from this container.
	 * This is a standard Java AWT method which gets called to remove a
	 * component from a container. When this happens the component's
	 * removeNotify() will also get called to indicate component removal.
	 *
	 * @param comp the component to remove
     * @see symantec.itools.awt.BaseTabbedPanel#removeAll
     * @see #add
	 */
	public synchronized void remove(Component comp)
	{
		int i=getPanelTabIndex(comp);

		if (countTabs()==1)
			removeAllTabPanels();
		else
		{
			if (i==0)
				showTabPanel(1);
			else
				showTabPanel(i-1);
			removeTabPanel(i);
		}
	}


    /**
     * This routine re-sets all the tab labels using the latest string array
     * provided in setPanelLabels().
     * It is not typically called directly.
     * @see #setPanelLabels
     */
	public void updatePanelLabels()
	{
	    try
	    {
	    	for (int i=0;i<vPanels.size();i++)
		    {
				String newlabel;
				if (labels!=null)
				{
					try
					{
			    		newlabel=labels[i];
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
			    		newlabel=createDefaultLabel(i);
					}
				}
				else
			    	newlabel=createDefaultLabel(i);
				setLabel(newlabel,i);
		    }
	    }
	    catch(Throwable thr) {}
	}


	/**
	 * Conditionally enables a tab and its associated panel at the given index.
	 * The currently active tab cannot be disabled.
	 * @param bEnable true to enable, false to disable
	 * @param index the zero-relative index of the tab
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
	 */
	public synchronized void enableTabPanel(boolean bEnable, int index) throws PropertyVetoException
	{
		// ??? LAB ??? 05/07/97 Shouldn't this become an ambient property?
		if ((index < 0) || (index >= vPanels.size()) || index == curIndex)
			return;

		setEnabled(bEnable, index);
	}

	/**
	 * Inserts a tab panel based on index.
	 * @param sLabel Label of the new tab to insert
	 * @param bEnabled If the new tab is enabled or not
	 * @param panel The panel to insert
	 * @param index zero-relative index at which the tab panel will be inserted.
	 */
	public synchronized void insertTabPanel(String sLabel, boolean bEnabled, Component panel, int index)
    {
        if ((index < 0) || (index >= vPanels.size()))
            return;

        if (index == getCurrentTab() && !bEnabled)
            return;

        try
        {
            vPanels.insertElementAt(panel, index);
            insertTab(sLabel, bEnabled, index);
        }
        catch (ArrayIndexOutOfBoundsException e) {}
    }

	/**
	 * Removes a tab and its associated panel at the given index.
	 * The currently active tab cannot be removed.
	 * @param index zero-relative index of the tab
	 */
	public synchronized void removeTabPanel(int index)
	{
    
    // select another, but leave the first one  HACK: IM
    if(index > 0)
      this.showTabPanel(index - 1);
    
		if ((index < 0) || (index >= vPanels.size()) || index == curIndex)
			return;

		try
		{
			Component p = (Component) vPanels.elementAt(index);
			super.remove(p);
			vPanels.removeElementAt(index);
		}
		catch (ArrayIndexOutOfBoundsException e) {}

		removeTab(index);
	}

	/**
	 * Removes all tabs and their associated panels, clearing the TabPanel entirely.
	 */
	public synchronized void removeAllTabPanels()
	{
		vPanels = new Vector<Component>();
		curIndex = -1;
		removeAllTabs();
	}

	/**
	 * Gets the number of tab panels in the TabPanel.
	 * @return the number of tab panels currently in the TabPanel
	 */
	public int countTabs()
	{
		return vPanels.size();
	}

   	/**
	 * Returns the recommended dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the recommended size of this component.
     * <p>
     * The returned size is large enough to display the biggest tab panel
     * at its preferred size.
     *
     * @see #getMinimumSize
	 */
    public Dimension getPreferredSize()
    {
    	Dimension p = getSize();
    	Dimension m = getMinimumSize();
    	return new Dimension(Math.max(p.width, m.width), Math.max(p.height, m.height));
    }

    /**
	 * Returns the minimum dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the minimum size of this component.
     * <p>
     *
     * @see #getPreferredSize
	 */
    public Dimension getMinimumSize()
    {
        return new Dimension(20, 40);
    }

    /**
     * Adds a listener for all event changes.
     * @param listener the listener to add.
     * @see #removePropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
    	super.addPropertyChangeListener(listener);
    	changes.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener for all event changes.
     * @param listener the listener to remove.
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
    	super.removePropertyChangeListener(listener);
    	changes.removePropertyChangeListener(listener);
    }

    /**
     * Adds a vetoable listener for all event changes.
     * @param listener the listener to add.
     * @see #removeVetoableChangeListener
     */
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
     	super.addVetoableChangeListener(listener);
		vetos.addVetoableChangeListener(listener);
    }

    /**
     * Removes a vetoable listener for all event changes.
     * @param listener the listener to remove.
     * @see #addVetoableChangeListener
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
    	super.removeVetoableChangeListener(listener);
    	vetos.removeVetoableChangeListener(listener);
    }

	/**
	 * This is the PropertyChange Event handling innerclass.
	 */
    class PropertyChange implements java.beans.PropertyChangeListener, java.io.Serializable
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

				public void propertyChange(PropertyChangeEvent e)
        {
	        showTabPanel(((Integer)e.getNewValue()).intValue());
        }
    }

	/**
	 * Creates a default name for a tab.
	 * @param int i Number to display as part of the tab's label.
	 * @return String The name to use for the new label.
	 * @see add(Component comp, int pos)
	 * @see updatePanelLabels()
	 */
	private String createDefaultLabel(int i)
	{
	    String name="tab - ";
	    name += String.valueOf(i);
	    return name;
	}

    private PropertyChange myPropertyChangeHandler = new PropertyChange();
    private VetoableChangeSupport vetos = new VetoableChangeSupport(this);
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	Vector<Component> vPanels;

	String[] labels = null;

	transient boolean bOsHack;
}
