package ASSET.Util.XML.Utils;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

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
		addHandler(new LayerHandler(null)
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