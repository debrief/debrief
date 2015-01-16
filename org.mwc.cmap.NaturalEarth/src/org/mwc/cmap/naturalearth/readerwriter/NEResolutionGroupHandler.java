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
	public static final String VIS = "Visible";
	public static final String MIN_RES = "MinRes";
	public static final String MAX_RES = "MaxRes";

	Double _minVal;
	Double _maxVal;
	String _name;
	boolean _isVis;
	
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
		
		addAttributeHandler(new HandleBooleanAttribute(VIS)
		{
			public void setValue(final String name, final boolean val)
			{
				_isVis = val;
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
			getR().setMin(_minVal);
		if(_maxVal != null)
			getR().setMaxScale(_maxVal);
		if(_name != null)
			getR().setName(_name);
		getR().setVisible(_isVis);

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
		
		eRes.setAttribute(NELayerHandler.VIS, writeThis(res.getVisible()));
		if(res.getMinScale() != null)
			eRes.setAttribute(MIN_RES,writeThis(res.getMinScale()));
		if(res.getMaxScale() != null)
			eRes.setAttribute(MAX_RES,writeThis(res.getMaxScale()));
		if(res.getLocalName() != null)
			eRes.setAttribute(NAME,res.getLocalName());
		
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