package ASSET.GUI.Workbench.Plotters;

import java.util.*;

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;

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
	
	public Enumeration elements()
	{
		Enumeration res = null;

		// hmm, do we have child behaviours?
		if(getModel() instanceof BehaviourList)
		{
			BehaviourList bl = (BehaviourList) getModel();
			Vector theModels = bl.getModels();
			res = theModels.elements();
		}
		
		return res;
	}
	
	
}
