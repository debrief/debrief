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
package MWC.GUI.Shapes;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ShapeWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.13 $
// $Log: ShapeWrapper.java,v $
// Revision 1.13  2007/03/12 11:40:25  ian.mayo
// Change default font size to 9px
//
// Revision 1.12  2007/01/05 15:13:16  ian.mayo
// Provide support for line-styles for shapes
//
// Revision 1.11  2006/10/03 08:23:45  Ian.Mayo
// Switch to Java 5. Use better compareTo methods
//
// Revision 1.10  2006/05/02 14:07:19  Ian.Mayo
// Draggable components aswell as features
//
// Revision 1.9  2006/04/21 08:18:31  Ian.Mayo
// Implement drag support
//
// Revision 1.8  2006/03/22 10:56:47  Ian.Mayo
// Reflect property name changes
//
// Revision 1.7  2005/12/13 09:05:00  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.6  2005/05/25 08:39:03  Ian.Mayo
// Minor tidying from Eclipse
//
// Revision 1.5  2005/05/19 14:48:13  Ian.Mayo
// Add more categorised properties
//
// Revision 1.4  2004/11/25 11:04:39  Ian.Mayo
// More test fixing after hi-res switch, largely related to me removing some unused accessors which were property getters
//
// Revision 1.3  2004/11/25 10:24:49  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2003/10/27 12:58:58  Ian.Mayo
// Update the color of the label as well as the shape
//
// Revision 1.1.1.2  2003/07/21 14:49:26  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.21  2003-07-04 10:59:23+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.20  2003-03-27 11:22:54+00  ian_mayo
// reflect new strategy where we return all data when asked to filter by invalid time
//
// Revision 1.19  2003-03-25 15:55:44+00  ian_mayo
// working on Chuck's problems with lots of annotations on 3-d plot.
//
// Revision 1.18  2003-03-20 15:16:32+00  ian_mayo
// fix problem managing time periods
//
// Revision 1.17  2003-03-19 15:36:49+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.16  2003-03-10 14:12:23+00  ian_mayo
// Tidy unused imports
//
// Revision 1.15  2003-03-03 14:04:25+00  ian_mayo
// When label colour updated, don't pass the colour back up to the parent
//
// Revision 1.14  2003-02-21 11:13:45+00  ian_mayo
// Set the colour in the parent
//
// Revision 1.13  2003-02-05 15:56:28+00  ian_mayo
// Correctly return the depth of the object
//
// Revision 1.12  2003-02-03 14:11:17+00  ian_mayo
// Improve support when DTG missing
//
// Revision 1.11  2003-01-30 16:07:44+00  ian_mayo
// Minor tidying, unused property
//
// Revision 1.10  2003-01-21 16:30:08+00  ian_mayo
// Some refactoring, move setColor management back into this component from the shape itself (so we can fire the property change)
//
// Revision 1.9  2003-01-17 15:08:38+00  ian_mayo
// Correct unit test to reflect new functionality
//
// Revision 1.8  2003-01-15 15:48:24+00  ian_mayo
// With getNearestTo, return annotation when no DTG supplied
//
// Revision 1.7  2002-10-01 15:41:38+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.6  2002-07-23 08:48:39+01  ian_mayo
// Return the correct type of object for getWatchable
//
// Revision 1.5  2002-07-10 14:59:00+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.4  2002-07-09 15:28:29+01  ian_mayo
// Return zero-length list instead of null
//
// Revision 1.3  2002-07-08 09:41:17+01  ian_mayo
// Don't return null, return zero length array
//
// Revision 1.2  2002-05-28 09:25:14+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:27+01  ian_mayo
// Initial revision
//
// Revision 1.7  2002-03-19 11:08:48+00  administrator
// Name the editor according to the shape, not the rectangle.  Specify that additional items should be shown alongside basic items
//
// Revision 1.6  2002-03-13 19:40:47+00  administrator
// Add visible and show label flags
//
// Revision 1.5  2002-03-13 08:58:42+00  administrator
// Correctly implement start and finish times
//
// Revision 1.4  2002-02-25 13:21:41+00  administrator
// Don't fix anchor point, keep it flexible
//
// Revision 1.3  2001-10-01 12:49:52+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.2  2001-08-29 19:18:02+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.1  2001-08-06 16:58:16+01  administrator
// Fire property change event to indicate that object has been filtered
//
// Revision 1.0  2001-07-17 08:41:09+01  administrator
// Initial revision
//
// Revision 1.6  2001-06-01 13:49:38+01  novatech
// handled instances where Data isn't available (return -1)
//
// Revision 1.5  2001-01-22 12:30:02+00  novatech
// added JUnit testing code
//
// Revision 1.4  2001-01-17 09:47:31+00  novatech
// implement time support properly
//
// Revision 1.3  2001-01-15 11:19:28+00  novatech
// store the symbol for this shape
//
// Revision 1.2  2001-01-09 10:29:45+00  novatech
// add extra parameters to allow WatchableLists to be used instead of TrackWrappers
//
// Revision 1.1  2001-01-03 13:40:23+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:26  ianmayo
// initial import of files
//
// Revision 1.23  2000-11-17 09:32:58+00  ian_mayo
// tidied up processing of moving Label when shape moves
//
// Revision 1.22  2000-10-16 11:51:26+01  ian_mayo
// tidied up retrieval of time threshold (now allowing retrieval from properties)
//
// Revision 1.21  2000-09-22 11:45:31+01  ian_mayo
// handle time-related plotting correctly
//
// Revision 1.20  2000-09-21 09:05:24+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.19  2000-08-18 13:34:02+01  ian_mayo
// Editable.EditorType
//
// Revision 1.18  2000-08-14 15:50:18+01  ian_mayo
// GUI name changes
//
// Revision 1.17  2000-08-11 08:41:04+01  ian_mayo
// tidy beaninfo
//
// Revision 1.16  2000-08-09 16:04:00+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.15  2000-05-23 13:40:24+01  ian_mayo
// switch to Arial
//
// Revision 1.14  2000-04-03 10:47:19+01  ian_mayo
// add filtering functionality
//
// Revision 1.13  2000-02-22 13:48:31+00  ian_mayo
// exportShape name changed to exportThis
//
// Revision 1.12  2000-02-14 16:48:49+00  ian_mayo
// insert code to set name of shape component into constructor
//
// Revision 1.11  2000-01-20 10:09:57+00  ian_mayo
// experimenting with copy functionality
//
// Revision 1.10  1999-11-26 15:50:16+00  ian_mayo
// adding toString methods
//
// Revision 1.9  1999-11-15 15:42:38+00  ian_mayo
// checking whether shape & label is in the current data area
//
// Revision 1.8  1999-11-12 14:35:39+00  ian_mayo
// part way through getting them to export themselves
//
// Revision 1.7  1999-11-11 18:23:22+00  ian_mayo
// added DTG
//
// Revision 1.6  1999-11-11 10:34:08+00  ian_mayo
// changed signature of ShapeWrapper constructor
//
// Revision 1.5  1999-10-15 12:36:51+01  ian_mayo
// improved relative label locating
//
// Revision 1.4  1999-10-14 16:32:26+01  ian_mayo
// update label location on creation
//
// Revision 1.3  1999-10-14 12:03:12+01  ian_mayo
// made 'Label' reflect changes in shape location
//
// Revision 1.2  1999-10-12 16:27:18+01  ian_mayo
// tidied up implementation of 'get additional bean info'
//
// Revision 1.1  1999-10-12 15:34:03+01  ian_mayo
// Initial revision
//

import java.awt.Font;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.File;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class ChartBoundsWrapper extends MWC.GUI.PlainWrapper implements
		NeedsToKnowAboutLayers
{

	public static final String WORLDIMAGE_TYPE = "GeoTiff";
	public static final String SHAPEFILE_TYPE = "Shapefile";
	public static final String NELAYER_TYPE = "NELayer";
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * filename of the chart we manager
	 * 
	 */
	private final String _fileName;

	/**
	 * the layers target we will drop new charts into
	 * 
	 */
	private transient Layers _myLayers;

	/**
	 * property name to indicate that the symbol visibility has been changed
	 */
	public static final String LABEL_VIS_CHANGED = "LABEL_VIS_CHANGE";

	/**
	 * keep track of versions
	 */
	static final long serialVersionUID = 1;

	/**
	 * the label
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	/**
	 * the symbol for this label
	 */
	final MWC.GUI.Shapes.RectangleShape _theShape;

	/**
	 * our editor
	 */
	transient protected Editable.EditorType _myEditor = null;

	/**
	 * the style of line to use for this feature
	 * 
	 */
	private int _lineStyle;

	/**
	 * the width to draw this line
	 */
	private int _lineWidth;

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class ChartInfo extends Editable.EditorType
	{

		public ChartInfo(final ChartBoundsWrapper data, final String theName)
		{
			super(data, theName, data._theShape.getType() + ":");
		}

		/**
		 * whether the normal editable properties should be combined with the
		 * additional editable properties into a single list. This is typically used
		 * for a composite object which has two lists of editable properties but
		 * which is seen by the user as a single object To be overwritten to change
		 * it
		 */
		public final boolean combinePropertyLists()
		{
			return true;
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<?> c = ChartBoundsWrapper.class;
			final MethodDescriptor[] mds =
			{ method(c, "loadThisChart", null, "Load this chart") };

			return mds;
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] myRes =
				{ prop("LabelLocation", "the relative location of the label", FORMAT),
						prop("Visible", "whether this shape is visible", VISIBILITY),
						prop("LabelVisible", "whether the label is visible", VISIBILITY),
						prop("Color", "the color of the shape itself", FORMAT), };
				myRes[0]
						.setPropertyEditorClass(MWC.GUI.Properties.LocationPropertyEditor.class);

				return myRes;

			}
			catch (final IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}

	}

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public ChartBoundsWrapper(final String label, final WorldLocation tl,
			final WorldLocation br, final java.awt.Color theColor, final String fileName)
	{
		_theShape = new RectangleShape(tl, br);
		_fileName = fileName;

		_theLabel = new MWC.GUI.Shapes.TextLabel(_theShape, label);
		_theLabel.setVisible(false);
		_theLabel.setRelativeLocation(LocationPropertyEditor.TOP);

		// store the name of the shape
		_theShape.setName(getName());

		_theLabel.setFont(new Font("Sans Serif", Font.PLAIN, 9));
		_theLabel.setColor(theColor);

		// override the shape, just to be sure...
		_theShape.setColor(theColor);

		// tell the parent object what colour we are
		super.setColor(theColor);

		// also update the location of the text anchor
		updateLabelLocation();
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public final void paint(final CanvasType dest)
	{
		// check if we are visible
		if (getVisible())
		{
			// sort out the line style
			dest.setLineStyle(_lineStyle);

			// store the current line width
			final float lineWid = dest.getLineWidth();

			// and the line width
			dest.setLineWidth(_lineWidth);

			// first paint the symbol
			_theShape.paint(dest);

			// and restore the line style
			dest.setLineStyle(CanvasType.SOLID);

			// and restore the line width
			dest.setLineWidth(lineWid);

			// now paint the text
			_theLabel.paint(dest);
		}
	}

	/**
	 * get the shape we are containing
	 */
	public final MWC.GUI.Shapes.RectangleShape getShape()
	{
		return _theShape;
	}

	public final WorldArea getBounds()
	{
		// don't return a bounds object - let the user zoom out to cover the whole
		// region
		return null;
	}

	public final String toString()
	{
		return _theShape.getName() + ":" + _theLabel.getString();
	}

	public final String getName()
	{
		return _theLabel.getString();
	}

	public String getFileName()
	{
		return _fileName;
	}

	/**
	 * get the first part of the file for this filename
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getCoverageName(final String fileName)
	{
		// sort out the name of the map
		String coverageName = fileName;
		final int dotIndex = coverageName.lastIndexOf(".");
		coverageName = (dotIndex == -1) ? coverageName : coverageName.substring(0,
				dotIndex);
		final int pathIndex = coverageName.lastIndexOf(File.separator);
		if (pathIndex > 0)
			coverageName = coverageName.substring(pathIndex + 1,
					coverageName.length());
		return coverageName;
	}

	@Override
	public void setLayers(final Layers parent)
	{
		_myLayers = parent;
	}

	public void loadThisChart()
	{
		System.err.println("loading:" + _fileName + " into:" + _myLayers);

		// represent it as a normal shapefile
		final Layer res = new ExternallyManagedDataLayer(WORLDIMAGE_TYPE,
				getCoverageName(_fileName), _fileName);
		_myLayers.addThisLayer(res);

	}

	/**
	 * does this item have an editor?
	 */
	public final boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new ChartInfo(this, this.getName());

		return _myEditor;
	}

	/**
	 * whether to show the label for this shape
	 */
	public final boolean getLabelVisible()
	{
		return _theLabel.getVisible();
	}

	/**
	 * whether to show the label for this shape
	 */
	public final void setLabelVisible(final boolean val)
	{
		_theLabel.setVisible(val);

		// ok, inform any listeners
		getSupport().firePropertyChange(ChartBoundsWrapper.LABEL_VIS_CHANGED, null,
				new Boolean(val));

	}

	public final void setLabelLocation(final Integer val)
	{
		_theLabel.setRelativeLocation(val);

		// and update the label relative to the shape if necessary
		updateLabelLocation();
	}

	public final Integer getLabelLocation()
	{
		return _theLabel.getRelativeLocation();
	}

	/**
	 * find the range from this shape to some point
	 */
	public final double rangeFrom(final WorldLocation other)
	{
		return Math.min(_theLabel.rangeFrom(other), _theShape.rangeFrom(other));
	}

	// ////////////////////////////////////////////////////
	// property change support
	// ///////////////////////////////////////////////////
	private void updateLabelLocation()
	{
		final WorldLocation newLoc = _theShape.getAnchor(_theLabel
				.getRelativeLocation().intValue());
		_theLabel.setLocation(newLoc);
	}

	// ////////////////////////////////////////////////////
	// watchableList support
	// ///////////////////////////////////////////////////

	/**
	 * method to fulfil requirements of WatchableList
	 */
	public final java.awt.Color getColor()
	{
		return super.getColor();
	}

	@FireReformatted
	public final void setColor(final java.awt.Color theCol)
	{
		super.setColor(theCol);

		// and set the colour of the shape
		_theShape.setColor(theCol);

		// don't forget the color of the label
		_theLabel.setColor(theCol);
	}
}
