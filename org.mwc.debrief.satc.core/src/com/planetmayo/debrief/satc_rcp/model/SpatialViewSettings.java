package com.planetmayo.debrief.satc_rcp.model;

import java.util.HashSet;
import java.util.Set;

public class SpatialViewSettings
{
	private final Set<SpatialSettingsListener> _listeners = new HashSet<SpatialSettingsListener>();
	
	/**
	 * level of diagnostics for user
	 */
	private boolean _showLegEndBounds;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showAllBounds;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showPoints;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showAchievablePoints;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showRoutes;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showRoutesWithScores;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showRecommendedSolutions;

	/**
	 * level of diagnostics for user
	 */	
	private boolean _showIntermediateGASolutions;
	
	private boolean _showRoutePointLabels;
	
	private boolean _showRoutePoints;
	
	private boolean _showTargetSolution;	
	
	public void addListener(SpatialSettingsListener listener) 
	{
		_listeners.add(listener);
	}
	
	public void removeListener(SpatialSettingsListener listener) 
	{
		_listeners.remove(listener);
	}	
	
	private void fireSettingsChanged()
	{
		for (SpatialSettingsListener listener : _listeners)
		{
			listener.onSettingsChanged();
		}
	}
	
	public boolean isShowLegEndBounds()
	{
		return _showLegEndBounds;
	}

	public boolean isShowAllBounds()
	{
		return _showAllBounds;
	}

	public boolean isShowPoints()
	{
		return _showPoints;
	}

	public boolean isShowAchievablePoints()
	{
		return _showAchievablePoints;
	}

	public boolean isShowRoutes()
	{
		return _showRoutes;
	}

	public boolean isShowRoutesWithScores()
	{
		return _showRoutesWithScores;
	}

	public boolean isShowRecommendedSolutions()
	{
		return _showRecommendedSolutions;
	}

	public boolean isShowIntermediateGASolutions()
	{
		return _showIntermediateGASolutions;
	}

	public boolean isShowRoutePointLabels()
	{
		return _showRoutePointLabels;
	}

	public boolean isShowRoutePoints()
	{
		return _showRoutePoints;
	}

	public boolean isShowTargetSolution()
	{
		return _showTargetSolution;
	}

	public void setShowAllBounds(boolean onOff)
	{
		_showAllBounds = onOff;
		fireSettingsChanged();
	}

	public void setShowLegEndBounds(boolean onOff)
	{
		_showLegEndBounds = onOff;
		fireSettingsChanged();
	}
	
	public void setShowTargetSolution(boolean onOff)
	{
		_showTargetSolution = onOff;		
		fireSettingsChanged();
	}
	
	public void setShowIntermediateGASolutions(boolean onOff)
	{
		_showIntermediateGASolutions = onOff;		
		fireSettingsChanged();
	}

	public void setShowRecommendedSolutions(boolean onOff)
	{
		_showRecommendedSolutions = onOff;
		fireSettingsChanged();
	}	
	
	public void setShowPoints(boolean onOff)
	{
		_showPoints = onOff;
		fireSettingsChanged();
	}

	public void setShowAchievablePoints(boolean onOff)
	{
		_showAchievablePoints = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutes(boolean onOff)
	{
		_showRoutes = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutePointLabels(boolean onOff)
	{
		_showRoutePointLabels = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutePoints(boolean onOff)
	{
		_showRoutePoints = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutesWithScores(boolean onOff)
	{
		_showRoutesWithScores = onOff;
		fireSettingsChanged();
	}
	
	public static interface SpatialSettingsListener 
	{
		
		void onSettingsChanged();		
	}
}
