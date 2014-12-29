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

	public NEFeatureGroup(String name)
	{
		_name = name;
		_created = System.currentTimeMillis();
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
		if(!(thePlottable instanceof NEFeatureGroup) && !(thePlottable instanceof NEFeatureStyle))
		{
			Activator.logError(Status.WARNING, "Should not be adding this to a NE Feature:" + thePlottable, null);
		}
		super.add(thePlottable);
	}

	@Override
	public long getCreated()
	{
		return _created;
	}

}