package org.mwc.cmap.naturalearth.readerwriter;

import java.util.Enumeration;

import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;
import org.w3c.dom.Element;

import MWC.GUI.Editable;

public abstract class NEResolutionGroupHandler extends NEFeatureGroupHandler
{
	public static final String TYPE = "ResolutionGroup";

	public static final String MIN_RES = "MinRes";
	public static final String MAX_RES = "MaxRes";

	Double _minVal;
	Double _maxVal;
	
	public NEResolutionGroupHandler()
	{
		super(TYPE);
		
		// add our min/max value handlers
		addAttributeHandler(new HandleDoubleAttribute(MIN_RES)
		{
			public void setValue(String name, double value)
			{
				_minVal = value;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(MAX_RES)
		{
			public void setValue(String name, double value)
			{
				_maxVal = value;
			}
		});
	}

	private NEResolution getR()
	{
		return (NEResolution) super._list;
	}
	
	protected NEFeatureGroup createGroup()
	{
		return new NEResolution("pending");
	}
	

	@Override
	public void elementClosed()
	{
		super.elementClosed();
		
		if(_minVal != null)
			getR().setMin(_minVal);
		if(_maxVal != null)
			getR().setMin(_minVal);
		
		_minVal = null;
		_maxVal = null;		
		
		addGroup(getR());
	}
	
	public static void exportGroup(NEResolution res,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		final Element eRes = doc.createElement(TYPE);
		
		if(res.getMin() != null)
			eRes.setAttribute(MIN_RES,writeThis(res.getMin()));
		if(res.getMax() != null)
			eRes.setAttribute(MAX_RES,writeThis(res.getMax()));
		
		Enumeration<Editable> iter = res.elements();
		while (iter.hasMoreElements())
		{
			Editable next = (Editable) iter.nextElement();
			if(next instanceof NEFeatureStyle)
			{
				NEFeatureStyle style = (NEFeatureStyle) next;
				NEFeatureStyleHandler.exportStyle(style, eRes, doc);
			}
			else if(next instanceof NEFeatureGroup)
			{
				NEFeatureGroup group = (NEFeatureGroup) next;
				NEFeatureGroupHandler.exportGroup(group, eRes, doc);
			}
		}
		
		parent.appendChild(eRes);
	}
	
	
	
	
	
}
