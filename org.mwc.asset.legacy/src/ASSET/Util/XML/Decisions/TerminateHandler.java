package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Terminate;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class TerminateHandler extends CoreDecisionHandler
{

	private final static String type = "Terminate";

	public TerminateHandler()
	{
		super(type);
	}

	public void elementClosed()
	{
		final CoreDecision ev = new Terminate();

		super.setAttributes(ev);

		// finally output it
		setModel(ev);
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

	static public void exportThis(final Object toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final Terminate bb = (Terminate) toExport;

		// first output the parent bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		parent.appendChild(thisPart);

	}

}