package com.planetmayo.debrief.satc.gwt.client.ui;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.generator.BoundedStatesListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;

public class TrackStates extends Composite implements BoundedStatesListener {

	private static TrackStatesUiBinder uiBinder = GWT
			.create(TrackStatesUiBinder.class);

	interface TrackStatesUiBinder extends UiBinder<Widget, TrackStates> {
	}

	public TrackStates() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Grid grid;

	public void addStates(String time, String location, String speed,
			String course) {
		int index = grid.insertRow(grid.getRowCount());
		grid.setWidget(index, 0, new Label(time));
		grid.setWidget(index, 1, new Label(location));
		grid.setWidget(index, 2, new Label(speed));
		grid.setWidget(index, 3, new Label(course));

	}

	/** remove all rows
	 * 
	 */
	private void clearGrid()
	{
		while(grid.getRowCount() > 1)
			grid.removeRow(1);
	}

	@Override
	public void debugStatesBounded(Collection<BoundedState> newStates)
	{
		statesBounded(newStates);
	}

	@Override
	public void statesBounded(Collection<BoundedState> newStates)
	{
		clearGrid();
		
		Iterator<BoundedState> iter = newStates.iterator();
		while (iter.hasNext())
		{
			BoundedState state = (BoundedState) iter.next();
			@SuppressWarnings("deprecation")
			String dateStr = state.getTime().toGMTString();
			String locStr = "n/a";
			String speedStr = "n/a";
			String courseStr = "n/a";
			
			if(state.getLocation() != null)
				locStr = state.getLocation().getConstraintSummary();
			if(state.getSpeed() != null)
				speedStr = state.getSpeed().getConstraintSummary();
			if(state.getCourse() != null)
				courseStr = state.getCourse().getConstraintSummary();
					
					
					
			addStates(dateStr, locStr, speedStr, courseStr);
		}
	}

	@Override
	public void incompatibleStatesIdentified(IncompatibleStateException e)
	{
		// TODO Auto-generated method stub
		
	}

}
