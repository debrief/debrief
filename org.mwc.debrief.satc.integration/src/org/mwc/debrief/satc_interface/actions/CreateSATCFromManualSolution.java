/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.satc_interface.actions;

import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.satc_interface.data.SATC_Solution;

import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class CreateSATCFromManualSolution implements
    RightClickContextItemGenerator
{

  @Override
  public void generate(IMenuManager parent, final Layers theLayers,
      Layer[] parentLayers, Editable[] subjects)
  {
    ArrayList<RelativeTMASegment> legs = new ArrayList<RelativeTMASegment>();

    // whole solution?
    if (subjects.length == 1 && subjects[0] instanceof TrackWrapper)
    {
      TrackWrapper track = (TrackWrapper) subjects[0];
      if (track.isTMATrack())
      {
        // ok, add the legs
        SegmentList segs = track.getSegments();
        Enumeration<Editable> sIter = segs.elements();
        while (sIter.hasMoreElements())
        {
          TrackSegment seg = (TrackSegment) sIter.nextElement();
          if (seg instanceof RelativeTMASegment)
          {
            legs.add((RelativeTMASegment) seg);
          }
        }
      }
    }
    else
    {
      // loop through legs

      for (int i = 0; i < subjects.length; i++)
      {
        Editable thisItem = subjects[i];
        if (thisItem instanceof RelativeTMASegment)
        {
          RelativeTMASegment scw = (RelativeTMASegment) thisItem;
          legs.add(scw);
        }
      }
    }

    // ok, is it worth going for?
    if (legs.size() > 0)
    {

      parent.add(new Separator());
      IMenuManager thisMenu = new MenuManager("SemiAuto TMA");
      parent.add(thisMenu);

      // right,stick in a separator
      thisMenu.add(new Separator());

      // see if there's an existing solution in there.
      ArrayList<SATC_Solution> existingSolutions = findExistingSolutionsIn(theLayers);

      if ((existingSolutions != null) && (existingSolutions.size() > 0))
      {
        for(SATC_Solution layer: existingSolutions)
        {
          // create a top level menu item
          MenuManager thisD = new MenuManager("Add to " + layer.getName());
          thisMenu.add(thisD);

          // add the child items
          addItemsTo(layer, thisD, legs);
        }
      }

    }
  }

  protected void addItemsTo(final SATC_Solution solution,
      final MenuManager parent, final ArrayList<RelativeTMASegment> legs)
  {

    final String actionTitle =
        "Generate Straight Leg forecasts for these manual legs";

    parent.add(new Action(actionTitle)
    {

      @Override
      public void run()
      {
        CorePlugin.run(new StraightLegForecastsFromLegs(solution, actionTitle, legs));
      }
    });
  }

  private ArrayList<SATC_Solution> findExistingSolutionsIn(Layers theLayers)
  {
    ArrayList<SATC_Solution> res = null;

    Enumeration<Editable> iter = theLayers.elements();
    while (iter.hasMoreElements())
    {
      Editable thisL = iter.nextElement();
      if (thisL instanceof SATC_Solution)
      {
        if (res == null)
          res = new ArrayList<SATC_Solution>();

        res.add((SATC_Solution) thisL);
      }
    }

    return res;
  }

  private class StraightLegForecastsFromLegs extends CMAPOperation
  {
    private final SATC_Solution _theSolution;
    private final ArrayList<RelativeTMASegment> _theLegs;
    private ArrayList<StraightLegForecastContribution> _newLegs;

    public StraightLegForecastsFromLegs(SATC_Solution solution, String title,
        ArrayList<RelativeTMASegment> legs)
    {
      super(title);
      _theSolution = solution;
      _theLegs = legs;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      _newLegs = new ArrayList<StraightLegForecastContribution>();
      
      for(RelativeTMASegment t: _theLegs)
      {
        final StraightLegForecastContribution leg = new StraightLegForecastContribution();
        leg.setStartDate(t.getDTG_Start().getDate());
        leg.setFinishDate(t.getDTG_End().getDate());
        leg.setName(t.getName());
        _theSolution.addContribution(leg);
        _newLegs.add(leg);
      }
      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      for(StraightLegForecastContribution e: _newLegs)
      {
        _theSolution.removeContribution(e);
      }
      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      for(StraightLegForecastContribution e: _newLegs)
      {
        _theSolution.addContribution(e);
      }
      return Status.OK_STATUS;
    }
  }

}
