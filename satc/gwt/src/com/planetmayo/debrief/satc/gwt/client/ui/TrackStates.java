package com.planetmayo.debrief.satc.gwt.client.ui;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundedStatesListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class TrackStates extends Composite implements IBoundedStatesListener
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

	@UiField
	InlineLabel debug;

	@UiHandler("debug")
	void clearDebug(ClickEvent e)
	{
		if (debug.getStyleName().contains("clicked"))
		{
			_inDebug = false;
			debug.removeStyleName("clicked");
		}
		else
		{
			_inDebug = true;
			debug.addStyleName("clicked");
		}
	}

	// TODO: Akash - this will actually be a GWT uiField, a toggle button
	Boolean _inDebug = false;

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
		String message2 = "Adding:" + e.getNewRange() + " to "
				+ e.getExistingRange();
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
					locStr = formatThis(state.getLocation());
				if (state.getSpeed() != null)
					speedStr = formatThis(state.getSpeed());
				if (state.getCourse() != null)
					courseStr = formatThis(state.getCourse());

				addStates(dateStr, locStr, speedStr, courseStr);
			}
		}

	}

	public static String formatThis(CourseRange course)
	{
		return "" + (int) Math.toDegrees(course.getMin()) + "\u00b0 - "
				+ (int) Math.toDegrees(course.getMax()) + "\u00b0";
	}

	public static String formatThis(SpeedRange speed)
	{
		// get the range, in knots
		int minSpdKts = (int) GeoSupport.MSec2kts(speed.getMin());
		int maxSpdKts = (int) GeoSupport.MSec2kts(speed.getMax());

		return "" + minSpdKts + " - " + maxSpdKts + " kts";
	}

	public static String formatThis(LocationRange location)
	{

		String res = "N/A";
		if (location != null)
		{
			res = location.numPoints() + " pts";
		}
		return res;

	}
}
