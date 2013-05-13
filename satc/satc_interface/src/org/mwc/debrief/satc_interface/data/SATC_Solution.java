package org.mwc.debrief.satc_interface.data;

import MWC.GUI.BaseLayer;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class SATC_Solution extends BaseLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ISolver _mySolver;

	public SATC_Solution(String solName)
	{
		super.setName(solName);

		_mySolver = createSolver();

	}

	private ISolver createSolver()
	{
		return SATC_Activator.getDefault().getService(ISolver.class, true);
	}

	public void addContribution(BaseContribution cont)
	{
		_mySolver.getContributions().addContribution(cont);
	}

	public ISolver getSolver()
	{
		return _mySolver;
	}

}
