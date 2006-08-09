package ASSET.GUI.Workbench.Plotters;

import ASSET.Models.DecisionType;

public class BehavioursPlottable extends BasePlottable
{
	
	final private DecisionType _myModel;
	
	public BehavioursPlottable(DecisionType decisionModel)
	{
		super(decisionModel.getName());
		_myModel = decisionModel;
	}

	public EditorType getInfo()
	{
		return _myModel.getInfo();
	}

	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return _myModel.hasEditor();
	}
	
	
}
