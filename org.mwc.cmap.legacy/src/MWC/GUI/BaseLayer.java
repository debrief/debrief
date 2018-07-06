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
// $RCSfile: BaseLayer.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.10 $
// $Log: BaseLayer.java,v $
// Revision 1.10  2006/11/22 13:21:40  Ian.Mayo
// By default all layers should be double-buffered
//
// Revision 1.9  2006/09/25 14:51:13  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.8  2006/01/13 15:24:35  Ian.Mayo
// Switch on buffering by default, minor refactoring
//
// Revision 1.7  2005/09/08 08:57:09  Ian.Mayo
// Refactor name of chart features layer
//
// Revision 1.6  2005/05/19 14:46:50  Ian.Mayo
// Add more categories to editable bits
//
// Revision 1.5  2004/10/07 14:23:19  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.4  2004/09/03 15:13:31  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.3  2004/08/31 09:38:21  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 15:45:20  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:02  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-07-04 11:00:57+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.5  2003-03-27 16:57:39+00  ian_mayo
// Add property for visibility change
//
// Revision 1.4  2002-10-30 16:27:03+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-10-28 09:23:29+00  ian_mayo
// support line widths
//
// Revision 1.2  2002-05-28 09:25:37+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:23+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-01-29 07:56:28+00  administrator
// Introduce a flag to indicate if this layer should be double-buffered
//
// Revision 1.2  2002-01-10 12:04:52+00  administrator
// switch to protected access for editor
//
// Revision 1.1  2001-08-01 14:30:10+01  administrator
// playing around with javadoc comments
//
// Revision 1.0  2001-07-17 08:46:33+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-22 12:29:29+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:43:05+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:42  ianmayo
// initial version
//
// Revision 1.1  2000-11-02 16:43:51+00  ian_mayo
// Initial revision
//
// Revision 1.8  2000-09-21 09:06:46+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.7  2000-08-18 13:36:05+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.6  2000-08-11 08:42:01+01  ian_mayo
// tidy beaninfo
//
// Revision 1.5  2000-02-22 13:51:21+00  ian_mayo
// add version id, and make exportable
//
// Revision 1.4  2000-01-20 10:16:16+00  ian_mayo
// removed d-lines
//
// Revision 1.3  1999-12-02 09:48:11+00  ian_mayo
// make into Editable
//
// Revision 1.2  1999-11-26 15:45:08+00  ian_mayo
// adding toString method
//
// Revision 1.1  1999-10-12 15:37:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:50+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-12 08:09:20+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:08+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:00+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-04 08:02:30+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-01-31 13:33:14+00  sm11td
// Initial revision
//

package MWC.GUI;

import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;

/**
 * this class is a collection of objects which may be plotted to a Chart
 * 
 * @version $Revision: 1.10 $
 * @see Plottables
 * @see Plottable
 */
public class BaseLayer extends Plottables implements Layer, SupportsPropertyListeners
{

	/** marker interface for layers that implement their own getRange(), and where
	 * our processing should not inspect the children
	 * @author ian
	 *
	 */
	public static interface ProvidesRange
	{
		
	}
	
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * store the serial version of the file
	 */
	static final long serialVersionUID = -4744521439513494065L;

	/**
	 * our editor
	 */
	transient protected Editable.EditorType _myEditor;

	/**
	 * whether this layer is a candidate for being buffered when plotted i.e. is
	 * it likely to contain very complex data (such as VPF plots)
	 */
	private boolean _bufferMe = false;

	/**
	 * the width to draw this line
	 */
	private int _lineWidth;

	/**
	 * property change support for the base layer
	 */
	private transient java.beans.PropertyChangeSupport _propSupport;

	/**
	 * whether my children have their own order
	 * 
	 */
	private final boolean _orderedChildren;

	/**
	 * the name of the visibility change event
	 */
	final public static String VISIBILITY_CHANGE = "VISIBILITY_CHANGE";

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * create a base layer
	 */
	public BaseLayer()
	{
		// this layer isn't ordered by default
		this(false);
	}

	/**
	 * indicate whether our children have a native order
	 * 
	 * @param orderedChildren
	 */
	public BaseLayer(final boolean orderedChildren)
	{
		_orderedChildren = orderedChildren;

		// initialise the property change support
		_propSupport = getProperties();
	}
	
	/** double-check we have properties, they're a
	 * transient object that may not be present for new layers
	 * 
	 * @return
	 */
	final PropertyChangeSupport getProperties()
	{
	  if(_propSupport == null)
	  {
	    _propSupport = new PropertyChangeSupport(this);
	  }
	  return _propSupport;
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	@Override
	public void add(final Editable thePlottable)
	{
		if (thePlottable instanceof NotInBaseLayer)
			throw new RuntimeException("Can't hold such item: " + thePlottable.getClass() + "!");
		else
			super.add(thePlottable);
	}

	/** whether this type of BaseLayer is able to have shapes added to it
	 * 
	 * @return
	 */
	public boolean canTakeShapes()
	{
		return true;
	}
	
	public void append(final Layer other)
	{
		if (other instanceof BaseLayer)
		{
			final BaseLayer bl = (BaseLayer) other;
			super.append(bl);
		}
	}

	@Override
	@FireReformatted
	public void setName(final String theName)
	{
		super.setName(theName);
		
		// special handling.  If this the chart features layer, we will double-buffer it, so VPF redraws
		// more quickly
	  // Time Display requires painting
		//
		// NOTE - SPECIAL HANDLING: on linux the transparent buffer image isn't actually
		// transparent. To be fixed.
		final boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
		
		if(!isLinux && theName.equals(Layers.CHART_FEATURES))
			setBuffered(true);
	}

	/**
	 * find out whether this layer is a candidate for being buffered (could it
	 * contain very complex data which is worth keeping in a buffer, instead of
	 * painting it fresh each time)
	 */
	public boolean isBuffered()
	{
		// we were getting wierd fuzzy outlines for double-buffered plot items.
		// Experiment with not double-buffering
		return _bufferMe;
	}

	/**
	 * specify whether this layer is a candidate for being buffered by the
	 * graphics engine
	 */
	public void setBuffered(final boolean bufferMe)
	{
		this._bufferMe = bufferMe;
	}

	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new LayerInfo(this);

		return _myEditor;
	}

	/**
	 * paint this list to the canvas
	 */
	public void paint(final CanvasType dest)
	{
		// ok, sort out the thickness
		final float oldThick = dest.getLineWidth();
		dest.setLineWidth(_lineWidth);

		// get the plottables to do the painting
		super.paint(dest);

		// and restort
		dest.setLineWidth((int) oldThick);
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 * 
	 * @return
	 */
	public int getLineThickness()
	{
		return _lineWidth;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 */
	public void setLineThickness(final int val)
	{
		_lineWidth = val;
	}

	public Enumeration<Editable> elements()
	{
		return super.elements();
	}

	// ////////////////////////////////////////////////
	// override the set vis method, so we can fire our event
	// ////////////////////////////////////////////////

	/**
	 * set the visible flag for this layer
	 */
	public void setVisible(final boolean visible)
	{
		super.setVisible(visible);

		// and now fire the event
		getProperties().firePropertyChange(VISIBILITY_CHANGE, !visible, visible);
	}

	@FireReformatted
	public void hideChildren()
	{
		doHideChildren(true);
	}

	@FireReformatted
	public void revealChildren()
	{
		doHideChildren(false);
	}

	private void doHideChildren(final boolean b)
	{
		final Enumeration<Editable> iter = this.elements();
		while (iter.hasMoreElements())
		{
			final Plottable thisE = (Plottable) iter.nextElement();
			thisE.setVisible(!b);
		}
	}

	public void exportShape()
	{
		MWC.Utilities.ReaderWriter.ImportManager
				.exportThis(";;Layer: " + getName());

		// go through the layer, exporting each plottable, if it will.
		final Enumeration<Editable> enumer = this.elements();
		while (enumer.hasMoreElements())
		{
			final Editable pl = (Editable) enumer.nextElement();
			if (pl instanceof Exportable)
			{
				final Exportable e = (Exportable) pl;
				e.exportThis();
			}
		}
	}
	
  public void firePropertyChange(final String propertyChanged, final Object oldValue,
			final Object newValue)
	{
    getProperties().firePropertyChange(propertyChanged, oldValue, newValue);
	}


	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class LayerInfo extends Editable.EditorType
	{

		public LayerInfo(final BaseLayer data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Visible", "the Layer visibility", VISIBILITY),
						prop("Name", "the name of the Layer", FORMAT),
						displayProp("LineThickness", "Line thickness", "the thickness of lines in this layer",
								FORMAT),
						prop("Buffered", "whether to double-buffer Layer. ('Yes' for better performance)", FORMAT), };

				res[2]
						.setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		@SuppressWarnings("rawtypes")
		public MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class c = BaseLayer.class;
			final MethodDescriptor mds[] =
			{ method(c, "exportShape", null, "Export Shape"),
					method(c, "hideChildren", null, "Hide all children"),
					method(c, "revealChildren", null, "Reveal all children") };
			return mds;
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class BaseLayerTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public BaseLayerTest(final String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			MWC.GUI.Editable ed = new BaseLayer();
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	/**
	 * determine whether my children have their own order that should be used
	 * rather than just their name.
	 * 
	 * @return yes/no.
	 */
	public boolean hasOrderedChildren()
	{
		return _orderedChildren;
	}
	

	// ////////////////////////////////////////////////
	// property change support
	// ////////////////////////////////////////////////


	@Override
	public void addPropertyChangeListener(final String property,
			final PropertyChangeListener listener)
	{
	  getProperties().addPropertyChangeListener(property, listener);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener)
	{
	  getProperties().addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener)
	{
	  getProperties().removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final String property,
			final PropertyChangeListener listener)
	{
	  getProperties().removePropertyChangeListener(property, listener);
	}
	

  /** find the named object in this layer
   * 
   * @param theTrackName
   * @return
   */
  public Editable find(final String subject)
  {
    final Enumeration<Editable> ele = elements();
    while(ele.hasMoreElements())
    {
      final Editable next = ele.nextElement();
      if(next.getName().equals(subject))
      {
        return next;
      }
    }
    
    return null;
  }
}


