package com.planetmayo.debrief.satc.model.manager;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.generator.ContributionsChangedListener;

public interface MaintainContributionsView extends ContributionsChangedListener
{

	/**
	 * show the list of available contributions
	 * 
	 * @param items the types of contribution that the user may add
	 */
	public void populateContributionList(ArrayList<String> items);

	/**
	 * populate the list of vehicle types
	 * 
	 * @param vehicles
	 */
	public void populateVehicleTypesList(List<VehicleType> vehicles);

	/**
	 * populate the list of precisions
	 * 
	 * @param vehicles
	 */
	public void populatePrecisionsList(Precision[] precisions);

	/**
	 * allow us to listen to user asking to remove a contribution
	 * 
	 * @param listener
	 */
	public void setRemoveContributionListener(ChangeListener listener);

	/**
	 * allow us to listen to user asking to remove a contribution
	 * 
	 * @param listener
	 */
	public void setAddContributionListener(ChangeListener listener);

	/**
	 * allow us to listen to user asking to remove a contribution
	 * 
	 * @param listener
	 */
	public void setVehicleChangeListener(ChangeListener listener);

	/**
	 * allow us to listen to user asking to remove a contribution
	 * 
	 * @param listener
	 */
	public void setPrecisionChangeListener(ChangeListener listener);

}