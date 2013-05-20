package org.mwc.debrief.satc_interface.data;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;

import org.mwc.debrief.satc_interface.utilities.conversions;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;

public class SATC_Solution extends BaseLayer implements NeedsToKnowAboutLayers
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ISolver _mySolver;

	private Layers _myLayers;

	/**
	 * the last set of bounded states that we know about
	 * 
	 */
	protected Collection<BoundedState> _lastStates;

	public SATC_Solution(String solName)
	{
		super.setName(solName);

		_mySolver = createSolver();

		listenToSolver(_mySolver);

	}

	private void listenToSolver(ISolver solver)
	{
		solver.getContributions().addContributionsChangedListener(
				new IContributionsChangedListener()
				{

					@Override
					public void removed(BaseContribution contribution)
					{
						fireRepaint();
					}

					@Override
					public void added(BaseContribution contribution)
					{
						fireRepaint();
					}
				});

		solver.getBoundsManager().addConstrainSpaceListener(
				new IConstrainSpaceListener()
				{
					@Override
					public void stepped(IBoundsManager boundsManager, int thisStep,
							int totalSteps)
					{
					}

					@Override
					public void statesBounded(IBoundsManager boundsManager)
					{
						// ok, better to plot them then!
						_lastStates = _mySolver.getProblemSpace().states();

						fireRepaint();
					}

					@Override
					public void restarted(IBoundsManager boundsManager)
					{
						_lastStates = null;
					}

					@Override
					public void error(IBoundsManager boundsManager,
							IncompatibleStateException ex)
					{
						_lastStates = null;
					}
				});
	}

	@Override
	public void paint(CanvasType dest)
	{
		dest.setColor(Color.green);
		if (getVisible())
		{
			if (_lastStates != null)
			{
				paintThese(dest, _lastStates);
			}

		}
	}

	private void paintThese(CanvasType dest, Collection<BoundedState> states)
	{
		for (Iterator<BoundedState> iterator = states.iterator(); iterator
				.hasNext();)
		{
			BoundedState thisS = (BoundedState) iterator.next();
			if (thisS.getLocation() != null)
			{
				LocationRange theLoc = thisS.getLocation();
				Coordinate[] pts = theLoc.getGeometry().getCoordinates();
				Point lastPt = null;
				for (int i = 0; i < pts.length; i++)
				{
					Coordinate thisC = pts[i];
					WorldLocation thisLocation = conversions.toLocation(thisC);
					Point pt = dest.toScreen(thisLocation);

					if (lastPt != null)
					{
						dest.drawLine(lastPt.x, lastPt.y, pt.x, pt.y);
					}
					lastPt = new Point(pt);
				}
			}
		}
	}

	protected void fireRepaint()
	{
		_myLayers.fireModified(this);
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

	@Override
	public void setLayers(Layers parent)
	{
		// ok, remember it
		_myLayers = parent;
	}

}
