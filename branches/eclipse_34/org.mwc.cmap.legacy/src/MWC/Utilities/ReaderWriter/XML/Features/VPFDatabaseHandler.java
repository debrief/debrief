package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

import MWC.GUI.VPF.*;
import MWC.Utilities.ReaderWriter.XML.*;

import com.bbn.openmap.layer.vpf.LibrarySelectionTable;

abstract public class VPFDatabaseHandler extends MWCXMLReader implements
		LayerHandler.exporter
{

	private static final String _myType = "vpf_database";

	private boolean _isVisible;

	private VPFDatabase _myDatabase;

	public VPFDatabaseHandler()
	{
		// inform our parent what type of class we are
		super(_myType);

		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			public void setValue(String name, boolean value)
			{
				_isVisible = value;
			}
		});
		addHandler(new VPFLibraryHandler()
		{
			public void addLibrary(String name, boolean visible,
					java.util.Vector coverages)
			{
				addThisLibrary(name, visible, coverages);
			}

			public com.bbn.openmap.layer.vpf.LibrarySelectionTable getLST(String name)
			{
				LibrarySelectionTable res = null;

				checkDatabase();

				if (_myDatabase != null)
					res = _myDatabase.getLST(name);

				return res;
			}

			public DebriefFeatureWarehouse getWarehouse()
			{
				checkDatabase();
				return _myDatabase.getWarehouse();
			}
		});

	}

	private void checkDatabase()
	{
		if (_myDatabase == null)
		{
			// create the database, but don't populate it
			_myDatabase = MWC.GUI.Tools.Palette.CreateVPFLayers
					.createMyLibrary(false);
		}
	}

	public void addThisLibrary(String name, boolean visible,
			java.util.Vector coverages)
	{
		checkDatabase();

		// did we manage to load the library?
		if (_myDatabase != null)
		{

			// do we know about this library?
			LibraryLayer lib = _myDatabase.getLibrary(name);

			if (lib == null)
			{
				MWC.Utilities.Errors.Trace.trace("Unable to find VPF library for:"
						+ name);
				return;
			}

			// set the visibility
			lib.setVisible(visible);

			// check that there are coverages in this layer
			if (coverages != null)
			{

				// now add the coverages to the library
				java.util.Enumeration enumer = coverages.elements();
				while (enumer.hasMoreElements())
				{
					CoverageLayer cl = (CoverageLayer) enumer.nextElement();
					lib.add(cl);
				}
			}
		}
		else
		{
			MWC.Utilities.Errors.Trace.trace("VPF Library paths not set");
		}
	}

	public void elementClosed()
	{
		// have we created any data?
		checkDatabase();

		if (_myDatabase == null)
			return;

		// update the object
		_myDatabase.setVisible(_isVisible);

		// add ourselves to the parent layer
		addPlottable(_myDatabase);

		// reset the data
		_myDatabase = null;

	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	public void exportThisPlottable(MWC.GUI.Plottable plottable,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{

		MWC.GUI.VPF.VPFDatabase ll = (MWC.GUI.VPF.VPFDatabase) plottable;
		Element coast = doc.createElement(_myType);

		// do the visibility
		coast.setAttribute("Visible", writeThis(ll.getVisible()));

		// now pass throuth the coverages, outputting each one
		java.util.Enumeration enumer = ll.elements();
		while (enumer.hasMoreElements())
		{
			LibraryLayer cl = (LibraryLayer) enumer.nextElement();
			VPFLibraryHandler.exportThisPlottable(cl, coast, doc);
		}

		parent.appendChild(coast);
	}

}