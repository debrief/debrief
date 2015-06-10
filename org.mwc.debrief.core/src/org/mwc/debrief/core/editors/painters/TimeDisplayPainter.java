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
package org.mwc.debrief.core.editors.painters;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.debrief.core.editors.PlotEditor;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.NeedsToBeInformedOfRemove;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DiagonalLocationPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * Class to plot a time display onto a plot
 */
public class TimeDisplayPainter implements Plottable, Serializable, NeedsToBeInformedOfRemove
{

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	/**
	 * version number for this painter
	 */
	static final long serialVersionUID = -1;

	/**
	 * colour of this time display
	 */
	Color _myColor = Color.darkGray;
	/**
	 * whether we are visible or not
	 */
	boolean _isOn = true;

	/**
	 * default location for the time display
	 */
	protected int _location = DiagonalLocationPropertyEditor.BOTTOM_RIGHT;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	private HiResDate _DTG;
	
	private String _name = "Time Display";

	/**
	 * the font we use for the D DifarSymbols
	 */
	private static java.awt.Font _myFont = new java.awt.Font("Arial",
			java.awt.Font.PLAIN, 12);

	protected PropertyChangeListener _timeListener = new PropertyChangeListener()
	{
		public void propertyChange(final PropertyChangeEvent event)
		{
			// right, retrieve the time
			_DTG = (HiResDate) event.getNewValue();
			if (_thisEditor != null)
			{
				_thisEditor.update();
			}
		}
	};

	IPartListener partListener = new IPartListener()
	{
		
		@Override
		public void partOpened(IWorkbenchPart part)
		{
		}
		
		@Override
		public void partDeactivated(IWorkbenchPart part)
		{
		}
		
		@Override
		public void partClosed(IWorkbenchPart part)
		{
			if (part == _thisEditor) 
			{
				beingRemoved();
			}
		}
		
		@Override
		public void partBroughtToTop(IWorkbenchPart part)
		{
		}
		
		@Override
		public void partActivated(IWorkbenchPart part)
		{
		}
	};
	
	private TimeManager _timeManager;

	private PlotEditor _thisEditor;
	
	/**
	 * constructor
	 */
	public TimeDisplayPainter()
	{
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart activeEditor = activePage.getActiveEditor();
		if (activeEditor instanceof PlotEditor)
		{
			_timeManager = (TimeManager) activeEditor
					.getAdapter(TimeProvider.class);
			if (_timeManager != null)
			{
				_DTG = _timeManager.getTime();
				_timeManager.addListener(_timeListener,
						TimeProvider.TIME_CHANGED_PROPERTY_NAME);
				_thisEditor = (PlotEditor) activeEditor;
				activePage.addPartListener(partListener);
			}
		}
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * whether the time display is visible or not
	 *
	 * @param val
	 *          yes/no visibility
	 */
	public void setVisible(final boolean val)
	{
		_isOn = val;
	}

	/**
	 * whether the time display is visible or not
	 *
	 * @return yes/no
	 */
	public boolean getVisible()
	{
		return _isOn;
	}

	/**
	 * current colour of the time display
	 *
	 * @param val
	 *          the colour
	 */
	public void setColor(final Color val)
	{
		_myColor = val;
	}

	/**
	 * current colour of the time display
	 *
	 * @return colour
	 */
	public Color getColor()
	{
		return _myColor;
	}

	public static java.awt.Font getFont()
	{
		return _myFont;
	}

	public static void setFont(java.awt.Font _myFont)
	{
		TimeDisplayPainter._myFont = _myFont;
	}
	/**
	 * which corner to position the time display
	 *
	 * @param loc
	 *          one of the enumerated types listed earlier
	 */
	public void setLocation(final Integer loc)
	{
		_location = loc.intValue();
	}

	/**
	 * retrieve the current location of the time display
	 *
	 * @return the current location, from the enumerated types defined for this
	 *         class
	 */
	public Integer getLocation()
	{
		return new Integer(_location);
	}

	/**
	 * redraw the time display
	 *
	 * @param g
	 *          the destination
	 */
	public void paint(final CanvasType g)
	{

		// check we are visible
		if (!_isOn)
			return;

		if (_DTG == null)
		{
			return;
		}

		// what is the screen width in logical coordinate?
		final MWC.Algorithms.PlainProjection proj = g.getProjection();

		// find the screen width
		final java.awt.Dimension screen_size = proj.getScreenArea().getSize();

		final int txtHt = g.getStringHeight(_myFont);

		if (_myEditor != null)
		{
			_myEditor.fireChanged(this, "Calc", null, this);
		}

		String str = DebriefFormatDateTime.toStringHiRes(_DTG);
		final int wid = g.getStringWidth(_myFont, str);

		int width = wid + 20;

		java.awt.Point TL = null, BR = null;
		switch (_location)
		{
		case (DiagonalLocationPropertyEditor.TOP_LEFT):
			TL = new Point((int) (screen_size.width * 0.05),
					(int) (txtHt + screen_size.height * 0.032));
			BR = new Point((TL.x + width), (int) (txtHt + screen_size.height * 0.035));
			break;
		case (DiagonalLocationPropertyEditor.TOP_RIGHT):
			BR = new Point((int) (screen_size.width * 0.95),
					(int) (txtHt + screen_size.height * 0.035));
			TL = new Point((BR.x - width), (int) (txtHt + screen_size.height * 0.032));
			break;
		case (DiagonalLocationPropertyEditor.BOTTOM_LEFT):
			TL = new Point((int) (screen_size.width * 0.05),
					(int) (screen_size.height * 0.987));
			BR = new Point((TL.x + width), (int) (screen_size.height * 0.99));
			break;
		default:
			BR = new Point((int) (screen_size.width * 0.95),
					(int) (screen_size.height * 0.99));
			TL = new Point((BR.x - width), (int) (screen_size.height * 0.987));
			break;
		}

		// setup the drawing object
		g.setColor(this.getColor());

		int this_dist = TL.x;

		// draw in the time display value
		g.drawText(_myFont, str, this_dist - (wid / 2),
				(int) (TL.y - (0.7 * txtHt)));

	}

	/**
	 * the area covered by the time display. It's null in this case, since the
	 * time display resizes to suit the data area.
	 *
	 * @return always null - meaning the time display doesn't mind what size the
	 *         visible plot is
	 */
	public MWC.GenericData.WorldArea getBounds()
	{
		// doesn't return a sensible size
		return null;
	}

	/**
	 * the range of the time display from a point (ignored)
	 *
	 * @param other
	 *          the other point
	 * @return INVALID_RANGE since this is value can't be calculated
	 */
	public double rangeFrom(final MWC.GenericData.WorldLocation other)
	{
		// doesn't return a sensible distance;
		return INVALID_RANGE;
	}

	/**
	 * return this item as a string
	 *
	 * @return the name of the time display
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * get the name of the time display
	 *
	 * @return the name of the time display
	 */
	public String getName()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		_name = name;
	}

	/**
	 * whether the time display has an editor
	 *
	 * @return yes
	 */
	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new TimeDisplayPainterInfo(this);

		return _myEditor;
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class TimeDisplayPainterInfo extends Editable.EditorType implements
			Serializable
	{

		// give it some old version id
		static final long serialVersionUID = 1L;

		public TimeDisplayPainterInfo(final TimeDisplayPainter data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Color", "the Color to draw the time display", FORMAT),
						prop("Name", "the Name for the time display", FORMAT),
						prop("Font", "the Font for the time display", FORMAT),
						prop("Visible", "whether this time display is visible", VISIBILITY),
						longProp("Location", "the time display location",
								MWC.GUI.Properties.DiagonalLocationPropertyEditor.class, FORMAT) 
				};

				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class TimeDisplayPainterTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public TimeDisplayPainterTest(final String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			MWC.GUI.Editable ed = new TimeDisplayPainter();
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	public int compareTo(final Plottable arg0)
	{
		final Plottable other = (Plottable) arg0;
		return this.getName().compareTo(other.getName());
	}

	@Override
	public void beingRemoved()
	{
		if (_thisEditor != null)
		{
			Runnable runnable = new Runnable()
			{
				
				@Override
				public void run()
				{
					if (_timeManager != null)
					{
						_timeManager.removeListener(_timeListener,
								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						_timeManager = null;
					}
					IWorkbenchPage activePage = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					activePage.removePartListener(partListener);
				}
			};
			if (Display.getCurrent() != null)
			{
				runnable.run();
			}
			else 
			{
				Display.getDefault().asyncExec(runnable);
			}
			_thisEditor = null;
			_DTG = null;
		}
	}

}
