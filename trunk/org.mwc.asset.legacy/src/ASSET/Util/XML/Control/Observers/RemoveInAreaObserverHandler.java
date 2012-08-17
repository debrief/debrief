package ASSET.Util.XML.Control.Observers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.RemoveInAreaObserver;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAreaHandler;

abstract class RemoveInAreaObserverHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private final static String type = "RemoveInAreaObserver";

	private final static String ACTIVE = "Active";

	private final static String WATCH_TYPE = "Watch";

	protected TargetType _watchType = null;
	protected boolean _isActive;
	protected String _name = "Remove in area Observer";
	protected WorldArea _myArea;

	protected BatchCollatorHandler _myBatcher = new BatchCollatorHandler();

	public RemoveInAreaObserverHandler()
	{
		this(type);
	}

	public RemoveInAreaObserverHandler(String type)
	{
		super(type);

		addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
		{
			public void setValue(String name, final boolean val)
			{
				_isActive = val;
			}
		});

		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, final String val)
			{
				_name = val;
			}
		});

		addHandler(new WorldAreaHandler()
		{

			@Override
			public void setArea(WorldArea area)
			{
				_myArea = area;
			}
		});

		addHandler(new TargetHandler(WATCH_TYPE)
		{
			public void setTargetType(final TargetType type1)
			{
				_watchType = type1;
			}
		});

		addHandler(_myBatcher);
	}

	public void elementClosed()
	{
		// create ourselves
		final BatchCollator timeO = new RemoveInAreaObserver(_watchType, _myArea,
				_name, _isActive);

		// (possibly) set batch collation results
		_myBatcher.setData(timeO);

		setObserver(timeO);

		// and reset
		_name = "Remove in area Observer";
		_watchType = null;
		_myArea = null;
	}

	abstract public void setObserver(BatchCollator obs);

	static public void exportThis(final Object toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		throw new UnsupportedOperationException();
//		// create ourselves
//		final org.w3c.dom.Element thisPart = doc.createElement(type);
//
//		// get data item
//		final ProximityObserver bb = (ProximityObserver) toExport;
//
//		// output it's attributes
//		thisPart.setAttribute("Name", bb.getName());
//		thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));
//
//		TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);
//
//		// output it's attributes
//		parent.appendChild(thisPart);

	}


}