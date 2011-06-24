package MWC.Utilities.ReaderWriter.XML.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class WorldAreaHandler extends MWCXMLReader
{

	static final private String _myType = "WorldArea";
	static final private String TOP_LEFT = "TopLeft";
	static final private String BOTTOM_RIGHT = "BottomRight";

	WorldLocation _topLeft;
	WorldLocation _bottomRight;

	public WorldAreaHandler(String name)
	{
		super(name);
		addHandler(new LocationHandler(TOP_LEFT)
		{
			public void setLocation(WorldLocation res)
			{
				_topLeft = res;
			}
		});
		addHandler(new LocationHandler(BOTTOM_RIGHT)
		{
			public void setLocation(WorldLocation res)
			{
				_bottomRight = res;
			}
		});
	}

	public WorldAreaHandler()
	{
		this(_myType);
	}

	public void elementClosed()
	{
		setArea(new WorldArea(_topLeft, _bottomRight));
		_topLeft = null;
		_bottomRight = null;
	}

	abstract public void setArea(WorldArea area);

	public static void exportThis(WorldArea area, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc, String name)
	{
		org.w3c.dom.Element eLoc = doc.createElement(name);

		// step through the list
		LocationHandler.exportLocation(area.getTopLeft(), TOP_LEFT, eLoc, doc);
		LocationHandler.exportLocation(area.getBottomRight(), BOTTOM_RIGHT, eLoc,
				doc);

		parent.appendChild(eLoc);
	}
	public static void exportThis(WorldArea area, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{
		exportThis(area, parent, doc, _myType);
	}

}