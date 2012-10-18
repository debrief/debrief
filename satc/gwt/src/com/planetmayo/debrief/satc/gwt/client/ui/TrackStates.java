package com.planetmayo.debrief.satc.gwt.client.ui;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.BoundedStatesListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;

public class TrackStates extends Composite implements BoundedStatesListener
{

	interface TrackStatesUiBinder extends UiBinder<Widget, TrackStates>
	{
	}

	private static TrackStatesUiBinder uiBinder = GWT
			.create(TrackStatesUiBinder.class);

	@UiField
	Grid grid;

	@UiField
	HTML errorPanel;

	public TrackStates()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void addStates(String time, String location, String speed,
			String course)
	{
		int index = grid.insertRow(grid.getRowCount());
		grid.setWidget(index, 0, new Label(time));
		grid.setWidget(index, 1, new Label(location));
		grid.setWidget(index, 2, new Label(speed));
		grid.setWidget(index, 3, new Label(course));
	}

	/**
	 * remove all rows
	 * 
	 */
	private void clearGrid()
	{
		while (grid.getRowCount() > 1)
			grid.removeRow(1);


	}
	
	private void clearMessage()
	{
		errorPanel.setText(""); // This clears the error label
	}

	@Override
	public void debugStatesBounded(Collection<BoundedState> newStates)
	{
	}

	@Override
	public void incompatibleStatesIdentified(BaseContribution contribution,
			IncompatibleStateException e)
	{
		// get rid of any existing message
		clearMessage();
		
		// empty the table
		clearGrid();
		
		String message1 = "Incompatible States. Contribution: "
				+ contribution.toString();
		String message2 = "Adding:" + e.getNewRange().getConstraintSummary()
				+ " to " + e.getExistingRange().getConstraintSummary();
		System.err.println(message1);
		System.err.println(message2);

		errorPanel.setHTML(message1 + "<BR>" + message2);
	}

	@Override
	public void statesBounded(Collection<BoundedState> newStates)
	{
		clearGrid();

		// do we have states?
		if ((newStates != null) && newStates.size() > 0)
		{
			// have data, check the error message is clear
			clearMessage();

			Iterator<BoundedState> iter = newStates.iterator();
			while (iter.hasNext())
			{
				BoundedState state = iter.next();
				@SuppressWarnings("deprecation")
				String dateStr = state.getTime().toGMTString();
				String locStr = "n/a";
				String speedStr = "n/a";
				String courseStr = "n/a";

				if (state.getLocation() != null)
					locStr = state.getLocation().getConstraintSummary();
				if (state.getSpeed() != null)
					speedStr = state.getSpeed().getConstraintSummary();
				if (state.getCourse() != null)
					courseStr = state.getCourse().getConstraintSummary();

				addStates(dateStr, locStr, speedStr, courseStr);
			}
		}

	}

}
