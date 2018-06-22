/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.spatial;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.NonSI;

public class ProplossBetweenTwoTracksOperation extends TwoTrackOperation
{

  /** make the actual calc more accessible, for testing
   * 
   * @param thisDistMetres range in metres
   * @return doppler loss over this distance
   */
  public static double calcDopplerFor(final double thisDistMetres)
  {
    return   20d * Math.log10(thisDistMetres);
  }
  
  private final class ProplossBetweenOperation extends TwoTrackCommand
  {
    ProplossBetweenOperation(final List<IStoreItem> selection,
        final IStoreGroup store, final String title, final String description,
        final IDocument<?> timeProvider, final IContext context)
    {
      super(selection, store, title, description, timeProvider, context,
          NonSI.DECIBEL);
    }

    private ProplossBetweenOperation(final List<IStoreItem> selection,
        final IStoreGroup store, final String title, final String description,
        final IContext context)
    {
      this(selection, store, title, description, null, context);
    }

    @Override
    protected void calcAndStore(final IGeoCalculator calc, final Point2D locA,
        final Point2D locB, final Double time)
    {
      // now find the range between them
      final double thisDistMetres = calc.getDistanceBetween(locA, locB);

      // ok, we've got to do 20 log R
      final double thisLoss = calcDopplerFor(thisDistMetres);
      
      if (time != null)
      {
        _builder.add(time, thisLoss);
      }
      else
      {
        _builder.add(thisLoss);
      }
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Generate propagation loss",
          NEW_DATASET_MESSAGE, "Proploss between " + super.getSubjectList());
    }
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {

    final List<IStoreItem> collatedTracks = getLocationDatasets(selection);

    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(collatedTracks))
    {
      // ok, are we doing a tempoarl opeartion?
      if (getATests().suitableForIndexedInterpolation(collatedTracks))
      {
        // hmm, find the time provider
        final IDocument<?> timeProvider =
            getATests().getLongestIndexedCollection(collatedTracks);

        // ok, provide an interpolated action
        final ICommand newC =
            new ProplossBetweenOperation(collatedTracks, destination,
                "Propagation loss between tracks (interpolated)",
                "Calculate prop loss between two tracks (interpolated)",
                timeProvider, context);
        res.add(newC);
      }
      else if (getATests().allEqualLengthOrSingleton(collatedTracks))
      {
        // ok, provide an indexed action
        final ICommand newC =
            new ProplossBetweenOperation(collatedTracks, destination,
                "Propagation loss between tracks (indexed)",
                "Calculate loss between two tracks (indexed)", context);
        res.add(newC);
      }
    }

    return res;
  }
}
