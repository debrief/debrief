// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainCreate.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: PlainCreate.java,v $
// Revision 1.6  2006/06/12 09:18:47  Ian.Mayo
// Better layer extended messaging
//
// Revision 1.5  2006/05/16 14:14:16  Ian.Mayo
// When we're adding a raster painter (like ETOPO) only add it once
//
// Revision 1.4  2005/09/30 09:48:24  Ian.Mayo
// Allow creation of layers, not just plottables
//
// Revision 1.3  2005/07/08 14:18:50  Ian.Mayo
// Make utility methods more accessible
//
// Revision 1.2  2004/05/25 15:44:28  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:46  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-07-02 09:13:46+01  ian_mayo
// Check we are provided with a layer
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:04+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:36+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-01-24 14:22:30+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.2  2001-10-29 12:58:07+00  administrator
// Check that shape creation was successful
//
// Revision 1.1  2001-08-23 13:27:56+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.0  2001-07-17 08:42:52+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-16 15:38:07+01  novatech
// add comments
//
// Revision 1.1  2001-01-03 13:41:41+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:58  ianmayo
// initial version
//
// Revision 1.3  1999-11-26 15:51:40+00  ian_mayo
// tidying up
//
// Revision 1.2  1999-11-11 18:23:03+00  ian_mayo
// new classes, to allow creation of shapes from palette
//

package MWC.GUI.Tools.Palette;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

abstract public class PlainCreate extends PlainTool
{

	/**
	 * the layer we are dumping this item into
	 */
	Layer _theLayer;

	/**
	 * the panel used to edit this item
	 */
	PropertiesPanel _thePanel;

	/**
	 * the chart we are dropping onto
	 */
	PlainChart _theChart;

	/**
	 * the Layers object, which we need in order to fire data extended event
	 */
	Layers _theData;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	/**
	 * constructor for label
	 * 
	 * @param theParent
	 *          parent where we can change cursor
	 * @param thePanel
	 *          panel
	 * @param theData
	 *          the layer we are adding the item to
	 */
	public PlainCreate(ToolParent theParent, PropertiesPanel thePanel, Layer theLayer,
			Layers theData, MWC.GUI.PlainChart theChart, String theName, String theImage)
	{
		super(theParent, theName, theImage);

		_thePanel = thePanel;
		_theLayer = theLayer;
		_theChart = theChart;
		_theData = theData;
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	protected MWC.GUI.PlainChart getChart()
	{
		return _theChart;
	}

	/**
	 * accessor to retrieve the layered data
	 */
	public Layers getLayers()
	{
		return _theData;
	}

	protected abstract Plottable createItem(MWC.GUI.PlainChart theChart);

	public Action getData()
	{
		Action res = null;

		// ask the child class to create itself
		Plottable pl = createItem(_theChart);

		// did it work?
		if (pl != null)
		{
			// wrap it up in an action
			res = new CreateLabelAction(_thePanel, _theLayer, _theData, pl);
		}

		return res;
	}

	// /////////////////////////////////////////////////////
	// store action information
	// /////////////////////////////////////////////////////
	public static class CreateLabelAction implements Action
	{
		/**
		 * the panel we are going to show the initial editor in
		 */
		final protected PropertiesPanel _thePanel;

		final protected Layer _theLayer;

		protected Plottable _theShape;

		final protected Layers _myData;

		public CreateLabelAction(PropertiesPanel thePanel, Layer theLayer, Layers theData,
				Plottable theShape)
		{
			_thePanel = thePanel;
			_theLayer = theLayer;
			_theShape = theShape;
			_myData = theData;
		}

		/**
		 * specify is this is an operation which can be undone
		 */
		public boolean isUndoable()
		{
			return true;
		}

		/**
		 * specify is this is an operation which can be redone
		 */
		public boolean isRedoable()
		{
			return true;
		}

		/**
		 * return string describing this operation
		 * 
		 * @return String describing this operation
		 */
		public String toString()
		{
			return "New grid:" + _theShape.getName();
		}

		/**
		 * take the shape away from the layer
		 */
		public void undo()
		{
			if (_theLayer != null)
			{
				_theLayer.removeElement(_theShape);
			}
			else
			{
				if (_theShape instanceof Layer)
				{
					_myData.removeThisLayer((Layer) _theShape);
				}
				else
					MWC.Utilities.Errors.Trace.trace("Missing layer data in undo operation");
			}

			_myData.fireExtended();
		}

		/**
		 * make it so!
		 */
		public void execute()
		{
			// check that the creation worked
			if (_theShape != null)
			{
				if (_theLayer != null)
				{

					// add the Shape to the layer, and put it
					// in the property editor
					_theLayer.add(_theShape);
					if (_thePanel != null)
						_thePanel.addEditor(_theShape.getInfo(), _theLayer);
					_myData.fireExtended(_theShape, _theLayer);
				}
				else
				{
					// no layer provided, stick into the top level
					if (_theShape instanceof Layer)
					{
						// ahh, just check we don't have one already
						Layer newLayer = (Layer) _theShape;
						Layer sameLayer = _myData.findLayer(newLayer.getName());
						if (sameLayer == null)
						{
							// no, we don't already store it.  add it.
							_myData.addThisLayer((Layer) _theShape);
							if (_thePanel != null)
								_thePanel.addEditor(_theShape.getInfo(), newLayer);
							_myData.fireExtended(_theShape, newLayer);
						}
						else
						{
							// ok - just display the same layer
							if (_thePanel != null)
								_thePanel.addEditor(_theShape.getInfo(), sameLayer);
							
							// and store the existing layer as the new item
							_theShape = sameLayer;
						}
					}
					else
						MWC.Utilities.Errors.Trace.trace("Failed to add new layer");
				}
			}
		}
	}

	// /////////////////////////////////////////////////////
	// store action information
	// /////////////////////////////////////////////////////
	public static class CreateLayerAction implements Action
	{
		/**
		 * the panel we are going to show the initial editor in
		 */
		final protected PropertiesPanel _thePanel;

		final protected Layer _theLayer;

		final protected Layers _myData;

		public CreateLayerAction(PropertiesPanel thePanel, Layer theLayer, Layers theData)
		{
			_thePanel = thePanel;
			_theLayer = theLayer;
			_myData = theData;
		}

		/**
		 * specify is this is an operation which can be undone
		 */
		public boolean isUndoable()
		{
			return true;
		}

		/**
		 * specify is this is an operation which can be redone
		 */
		public boolean isRedoable()
		{
			return true;
		}

		/**
		 * return string describing this operation
		 * 
		 * @return String describing this operation
		 */
		public String toString()
		{
			return "New layer:" + _theLayer.getName();
		}

		/**
		 * take the shape away from the layer
		 */
		public void undo()
		{
			if (_theLayer != null)
			{
				_myData.removeThisLayer(_theLayer);
			}

			_myData.fireExtended();
		}

		/**
		 * make it so!
		 */
		public void execute()
		{
			// add our new layer, and put it
			// in the property editor
			_myData.addThisLayer(_theLayer);

			if (_thePanel != null)
				_thePanel.addEditor(_theLayer.getInfo(), _theLayer);
			_myData.fireExtended();
		}
	}
}
