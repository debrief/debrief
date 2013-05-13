package org.mwc.debrief.satc_interface.data;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

import MWC.GUI.BaseLayer;

public class SATC_Solution extends BaseLayer
{

	private ArrayList<BaseContribution> _myContributions = new ArrayList<BaseContribution>();
	
	public SATC_Solution(String solName)
	{
		super.setName(solName);
	}

	public void addContribution(BaseContribution cont)
	{
		_myContributions.add(cont);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
