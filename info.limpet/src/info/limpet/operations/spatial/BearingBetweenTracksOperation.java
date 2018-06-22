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
import info.limpet.impl.SampleData;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Angle;

public class BearingBetweenTracksOperation extends TwoTrackOperation
{

  public List<ICommand> actionsFor(
      List<IStoreItem> rawSelection, IStoreGroup destination, IContext context)
  {
    List<ICommand> res =
        new ArrayList<ICommand>();
    
    // get some tracks
    List<IStoreItem> collatedTracks = getLocationDatasets(rawSelection);
    
    if (appliesTo(collatedTracks))
    {
      // ok, are we doing a tempoarl opeartion?
      if (getATests().suitableForIndexedInterpolation(collatedTracks))
      {
        // hmm, find the time provider
        final IDocument<?> timeProvider =
            getATests().getLongestIndexedCollection(collatedTracks);

        ICommand newC =
            new TwoTrackCommand(collatedTracks, destination,
                "Bearing between tracks (interpolated)",
                "Calculate bearing between two tracks (interpolated)",
                timeProvider, context, SampleData.DEGREE_ANGLE.asType(Angle.class))
            {

              @Override
              protected String getOutputName()
              {
                return getContext().getInput("Generate bearing",
                    NEW_DATASET_MESSAGE,
                    "Bearing between " + super.getSubjectList());
              }

              @Override
              protected void calcAndStore(final IGeoCalculator calc, final Point2D locA,
                  final Point2D locB, final Double time)
              {
                // now find the range between them
                double thisDist = calc.getAngleBetween(locA, locB);
                
                // and correct it
                if(thisDist < 0)
                {
                  thisDist += 360d;
                }

                if (time != null)
                {
                  _builder.add(time, thisDist);
                }
                else
                {
                  _builder.add(thisDist);
                }
              }
            };

        res.add(newC);
      }
      else if (getATests().allEqualLengthOrSingleton(collatedTracks))
      {
        ICommand newC =
            new TwoTrackCommand(collatedTracks, destination,
                "Bearing between tracks (indexed)",
                "Calculate bearing between two tracks (indexed)", null, context,
                SampleData.DEGREE_ANGLE.asType(Angle.class))
            {

              @Override
              protected String getOutputName()
              {
                return getContext().getInput("Generate bearing",
                    NEW_DATASET_MESSAGE,
                    "Bearing between " + super.getSubjectList());
              }

              @Override
              protected void calcAndStore(final IGeoCalculator calc, final Point2D locA,
                  final Point2D locB, final Double time)
              {
                // now find the range between them
                final double thisDist = calc.getAngleBetween(locA, locB);

                if (time != null)
                {
                  _builder.add(time, thisDist);
                }
                else
                {
                  _builder.add(thisDist);
                }
              }
            };

        res.add(newC);
      }
    }

    return res;
  }

}
