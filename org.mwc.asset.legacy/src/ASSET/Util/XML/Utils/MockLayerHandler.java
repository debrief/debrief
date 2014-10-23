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
package ASSET.Util.XML.Utils;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import Debrief.ReaderWriter.XML.DebriefLayerHandler;
import MWC.GUI.*;
import MWC.Utilities.ReaderWriter.XML.*;

abstract public class MockLayerHandler extends MWCXMLReader
{
	BaseLayer _res = null;

	public MockLayerHandler(String elementName)
	{
		// inform our parent what type of class we are
		super(elementName);

		// and add the layer handler...
		addHandler(new DebriefLayerHandler(null)
		{
			public void elementClosed()
			{
				// pass on the layer
				setLayer(_myLayer);
				// and empty it
				_myLayer = null;
			}

		});
	}

	abstract public void setLayer(BaseLayer theLayer);

	public static void exportLocation(MWC.GenericData.WorldLocation loc, String title,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		org.w3c.dom.Element eLoc = doc.createElement(title);
		// for now, stick with exporting locations in short form
		ASSETShortLocationHandler.exportLocation(loc, eLoc, doc);
		parent.appendChild(eLoc);
	}

}