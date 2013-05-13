package org.mwc.debrief.satc_interface.readerwriter;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.mwc.debrief.core.loaders.xml_handlers.LayerHandlerExtension;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class SATCHandler extends MWCXMLReader implements LayerHandlerExtension
{
	private static final String MY_TYPE = "satc_solution";

	private static final String CONTENTS = "CONTENTS";
	private static final String NAME = "NAME";

	protected String _myContents;

	private String _name;

	private Layers _theLayers;

	public SATCHandler()
	{
		this(MY_TYPE);
	}

	public SATCHandler(String theType)
	{
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(String name, String val)
			{
				_name = val;
			}
		});
		addAttributeHandler(new HandleAttribute(CONTENTS)
		{
			public void setValue(String name, String val)
			{
				_myContents = val;
			}
		});

	}

	public void elementClosed()
	{
		SATC_Solution solution = new SATC_Solution(_name);

		// ok, repopulate the solver from the contents

		// put it into the solution.

		// and save it.
		_theLayers.addThisLayer(solution);
	}

	/**
	 * export this grid
	 * 
	 * @param plottable
	 *          the grid we're going to export
	 * @param parent
	 * @param doc
	 */
	public void exportThisPlottable(MWC.GUI.Plottable plottable,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{

		// MWC.GUI.Chart.Painters.GridPainter theGrid =
		// (MWC.GUI.Chart.Painters.GridPainter) plottable;
		// Element gridElement = doc.createElement(MY_TYPE);
		//
		// exportGridAttributes(gridElement, theGrid, doc);
		//
		// parent.appendChild(gridElement);
	}

	/**
	 * utility class which appends the other grid attributes
	 * 
	 * @param gridElement
	 *          the element to put the grid into
	 * @param theGrid
	 *          the grid to export
	 * @param doc
	 *          the document it's all going into
	 */
	protected static void exportGridAttributes(Element gridElement,
			MWC.GUI.Chart.Painters.GridPainter theGrid, Document doc)
	{
		// // do the visibility
		// gridElement.setAttribute(VISIBLE, writeThis(theGrid.getVisible()));
		// gridElement.setAttribute(PLOT_LABELS,
		// writeThis(theGrid.getPlotLabels()));
		//
		// // does it have a none-standard name?
		// if (theGrid.getName() != GridPainter.GRID_TYPE_NAME)
		// {
		// gridElement.setAttribute(NAME, theGrid.getName());
		// }
		//
		// FontHandler.exportFont(theGrid.getFont(), gridElement, doc);
		//
		// // and the delta (retaining the units
		// WorldDistanceHandler.exportDistance(DELTA, theGrid.getDelta(),
		// gridElement,
		// doc);
		//
		// // do the colour
		// ColourHandler.exportColour(theGrid.getColor(), gridElement, doc);
	}

	@Override
	public void setLayers(Layers theLayers)
	{
		_theLayers = theLayers;
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return (subject instanceof SATC_Solution);
	}

	@Override
	public void exportThis(Layer theLayer, Element parent, Document doc)
	{
		// TODO Auto-generated method stub

		SATC_Solution solution = (SATC_Solution) theLayer;
		
		// ok, marshall it into a String
		
		// now store the components

		
	}

}