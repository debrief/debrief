package com.planetmayo.debrief.satc.model.contributions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class StraightLegForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String COLOR = "COLOR";
	
	private Color _myCol = null;
	
	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// track the color to use for this straight leg
		Color newCol = null;
		
		for (BoundedState state : space.getBoundedStatesBetween(startDate,
				finishDate))
		{
			// just double-check that this doesn't already have a leg - we can't
			// let them overlap
			String existing = state.getMemberOf();
			if (existing != null)
				throw new IncompatibleStateException(
						"We don't support overlapping legs. Old leg:" + existing
								+ " New leg:" + this.getName() + " state at:" + state.getTime(), null, null);

			// ok, now just store the leg id
			state.setMemberOf(this.getName());
			
			// do we have a color?
			if(newCol == null && state.getColor() != null)
			{
				// nope, store it.
				newCol = new Color(state.getColor().getRGB());
			}
		}

		// share the good news about the color change
		Color oldCol = _myCol;
		_myCol = newCol;
		firePropertyChange(COLOR, oldCol, _myCol);
		
		// check that we have at least one state between two straight legs
		List<BoundedState> previousState = new ArrayList<BoundedState>(
				space.getBoundedStatesBetween(space.getStartDate(), new Date(startDate.getTime() - 1))
		);
		if (previousState.isEmpty()) 
		{
			return;
		}
		ListIterator<BoundedState> backIterator = previousState.listIterator(previousState.size());
		BoundedState endPreviousLeg = backIterator.previous();
		String prevStraightLeg = endPreviousLeg.getMemberOf();
		if (prevStraightLeg == null) 
		{
			return;
		}
		BoundedState state = endPreviousLeg;
		while (backIterator.hasPrevious()) 
		{
			state = backIterator.previous();
			if (! prevStraightLeg.equals(state.getMemberOf())) 
			{
				break;
			}
		}
		addStatesForAltering(space, new Date(state.getTime().getTime() + 1), endPreviousLeg.getTime(), startDate, finishDate);
	}
	
	private void addStatesForAltering(ProblemSpace space, Date startPrevious, Date endPrevious, Date startNext, Date endNext) 
	{
		BoundedState previous = null;
		long sum = 0;
		int count = 0;
		for (BoundedState state : space.getBoundedStatesBetween(startPrevious, endPrevious))
		{
			if (previous != null) 
			{
				sum += state.getTime().getTime() - previous.getTime().getTime();
				count++;
			}
			previous = state;
		}
		previous = null;
		for (BoundedState state : space.getBoundedStatesBetween(startNext, endNext))
		{
			if (previous != null) 
			{
				sum += state.getTime().getTime() - previous.getTime().getTime();
				count++;
			}
			previous = state;
		}		
		long delta = sum / count;
		boolean stateCreated = false; 
		for (long a = endPrevious.getTime() + delta; a < startNext.getTime(); a += delta)
		{
			try 
			{
				space.add(new BoundedState(new Date(a)));
				stateCreated = true;
			} 
			catch (IncompatibleStateException ex) 
			{				
			}
		}
		if (! stateCreated) 
		{
			try 
			{
				space.add(new BoundedState(new Date((endPrevious.getTime() + startNext.getTime()) / 2)));
			} 
			catch (IncompatibleStateException ex) 
			{				
			}			
		}
	}
	
	@Override
	protected int compareEqualClass(BaseContribution o)
	{
		int res =getStartDate().compareTo(o.getStartDate());
		
		if(res == 0)
		{
			// ok, have to compare names
			res = getName().compareTo(o.getName());
		}
		
		return res;
	}
	
	public Color getColor()
	{
		return _myCol;
	}
	
	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	@Override
	protected int getSortOrder()
	{
		return MEASUREMENT_DEFAULT_SCORE + 1;
	}
}
