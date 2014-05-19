package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.PatternSearch_Core;
import ASSET.Models.Decision.Tactical.PatternSearch_OutwardSpiral;

abstract public class PatternOutwardSpiral_SearchHandler extends
		PatternSearchCore_Handler
{

	final static String type = "OutwardSpiralSearch";

	public PatternOutwardSpiral_SearchHandler()
	{
		super(type);
	}

	@Override
	protected PatternSearch_Core getModel()
	{
		return new PatternSearch_OutwardSpiral(_origin, _spacing, _searchHeight,
				_searchSpeed, null, _height, _height);
	}

	public static void exportThis(final Object toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		exportCore(toExport, parent, type, doc);
	}

}