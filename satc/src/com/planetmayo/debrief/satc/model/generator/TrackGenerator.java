package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class TrackGenerator
{
	private ArrayList<BaseContribution> _contribs = new ArrayList<BaseContribution>();
	private PropertyChangeListener contribChange;
	private ProblemSpace _space;
	
	public TrackGenerator()
	{
		contribChange = new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent arg0)
			{
				propChange();
			}
		};
		_space = new ProblemSpace();
	}
	
	/** something has changed - rerun the scenario constraint management
	 * 
	 */
	protected void propChange()
	{
		// ok, re-run our constraint generation
		Iterator<BaseContribution> iter = _contribs.iterator();
		while (iter.hasNext())
		{
			BaseContribution bC = (BaseContribution) iter.next();
			bC.actUpon(_space);
		}
	}

	public void addContribution(BaseContribution contribution)
	{
		// remember it
		_contribs.add(contribution);
		
		// start listening to it
		contribution.addPropertyChangeListener(BaseContribution.ACTIVE, contribChange);
		contribution.addPropertyChangeListener(BaseContribution.HARD_CONSTRAINTS, contribChange);
		contribution.addPropertyChangeListener(BaseContribution.START_DATE, contribChange);
		contribution.addPropertyChangeListener(BaseContribution.FINISH_DATE, contribChange);
	}
	
	public void removeContribution(BaseContribution contribution)
	{
		// remember it
		_contribs.remove(contribution);
		
		// start listening to it
		contribution.removePropertyChangeListener(BaseContribution.ACTIVE, contribChange);
		contribution.removePropertyChangeListener(BaseContribution.HARD_CONSTRAINTS, contribChange);
		contribution.removePropertyChangeListener(BaseContribution.START_DATE, contribChange);
		contribution.removePropertyChangeListener(BaseContribution.FINISH_DATE, contribChange);
	}

}
