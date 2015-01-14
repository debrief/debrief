package org.mwc.cmap.naturalearth.view;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.wrapper.NELayer.HasCreatedDate;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;

/** a collection of style objects
 * 
 * @author ian
 *
 */
public class NEFeatureGroup extends BaseLayer implements HasCreatedDate
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// name for this style
	protected final String _name;

	private final long _created;

	private NEFeatureStore _parent;

	public NEFeatureGroup(String name)
	{
		this(null, name);
	}
	
	public NEFeatureGroup(NEFeatureStore featureSet, String name)
	{
		_name = name;
		_created = System.currentTimeMillis();
		_parent = featureSet;
	}

	public NEFeatureStore getParent() {
		return _parent;
	}
	
	public void setParent(NEFeatureStore parent)
	{
		this._parent = parent;
	}
	
	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}

	public String getName()
	{
		return _name;
	}

	@Override
	public void add(Editable thePlottable)
	{
		if (!(thePlottable instanceof NEFeatureGroup)
				&& !(thePlottable instanceof NEFeatureStyle))
		{
			Activator.logError(Status.WARNING,
					"Should not be adding this to a NE Feature:" + thePlottable, null);
		}
		else
		{
			super.add(thePlottable);
			if (thePlottable instanceof NEFeatureStyle)
			{
				((NEFeatureStyle) thePlottable).setParent(this);
			}
		}
	}

	@Override
	public long getCreated()
	{
		return _created;
	}

}