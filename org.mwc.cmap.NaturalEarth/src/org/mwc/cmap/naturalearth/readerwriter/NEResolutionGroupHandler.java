package org.mwc.cmap.naturalearth.readerwriter;

import java.util.Enumeration;

import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;
import org.w3c.dom.Element;

import MWC.GUI.Editable;

public abstract class NEResolutionGroupHandler extends NEFeatureGroupHandler
{
	public static final String TYPE = "NEResolution";

	public static final String NAME = "Name";
	public static final String MIN_RES = "MinRes";
	public static final String MAX_RES = "MaxRes";

	Double _minVal;
	Double _maxVal;
	String _name;
	
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
		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(String name, String value)
			{
				_name = value;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(MAX_RES)
		{
			public void setValue(String name, double value)
			{
				_maxVal = value;
			}
		});
		addHandler(new NEFeatureGroupHandler()
		{			
			@Override
			public void addGroup(NEFeatureGroup group)
			{
				addFeatureGroup(group);
			}
		});
	}

	protected void addFeatureGroup(NEFeatureGroup group)
	{
		getR().add(group);
	}

	private NEResolution getR()
	{
		return (NEResolution) super._list;
	}
	
	protected NEFeatureGroup createGroup()
	{
		return new NEResolution("pending resolution");
	}
	

	@Override
	public void elementClosed()
	{
		if(_minVal != null)
			getR().setMinScale(_minVal);
		if(_maxVal != null)
			getR().setMaxScale(_minVal);
		if(_name != null)
			getR().setName(_name);

		// store the list
		addGroup(getR());

		// clear out
		_name = null;
		_minVal = null;
		_maxVal = null;		
		_list = null;

	}
	
	public static void exportGroup(NEResolution res,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		final Element eRes = doc.createElement(TYPE);
		
		if(res.getMinScale() != null)
			eRes.setAttribute(MIN_RES,writeThis(res.getMinScale()));
		if(res.getMaxScale() != null)
			eRes.setAttribute(MAX_RES,writeThis(res.getMaxScale()));
		if(res.getName() != null)
			eRes.setAttribute(NAME,res.getName());
		
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