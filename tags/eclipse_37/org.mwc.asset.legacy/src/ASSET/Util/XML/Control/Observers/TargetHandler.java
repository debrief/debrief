package ASSET.Util.XML.Control.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

abstract public class TargetHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	TargetType _type = null;

	public TargetHandler(final String myType)
	{
		super(myType);
		addHandler(new TargetTypeHandler()
		{
			public void setTargetType(final TargetType type)
			{
				_type = type;
			}
		});
	}

	public void elementClosed()
	{
		setTargetType(_type);
		_type = null;
	}

	abstract public void setTargetType(TargetType type);

	static public void exportThis(final Object toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc,
			final String myType)
	{
		// create ourselves
		final org.w3c.dom.Element thisElement = doc.createElement(myType);

		// get data item
		final TargetType bb = (TargetType) toExport;

		// output it's attributes

		// output the target type
		TargetTypeHandler.exportThis(bb, thisElement, doc);

		// output it's attributes
		parent.appendChild(thisElement);
	}
}