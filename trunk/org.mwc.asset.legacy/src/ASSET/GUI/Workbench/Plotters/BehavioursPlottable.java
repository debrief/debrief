package ASSET.GUI.Workbench.Plotters;

import java.util.*;

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import MWC.GUI.Editable;

public class BehavioursPlottable extends BasePlottable 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BehavioursPlottable(DecisionType decisionModel, ScenarioLayer parent)
	{
		super(decisionModel, parent);
	}
	
	public boolean hasOrderedChildren()
	{
		return true;
	}

	public DecisionType getDecisionModel()
	{
		return (DecisionType) getModel();
	}
	
	public Enumeration<Editable> elements()
	{
		Vector<Editable> list = new Vector<Editable>(0,1);

		// hmm, do we have child behaviours?
		if(getModel() instanceof BehaviourList)
		{
			BehaviourList bl = (BehaviourList) getModel();
			Vector<DecisionType> theModels = bl.getModels();
			Iterator<DecisionType> iter = theModels.iterator();
			while(iter.hasNext())
			{
				list.add(iter.next());
			}
		}
		
		return list.elements();
	}
	
	
}
