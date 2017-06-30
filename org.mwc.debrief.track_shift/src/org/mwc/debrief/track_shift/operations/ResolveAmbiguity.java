package org.mwc.debrief.track_shift.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.ColorPropertyEditor;
import MWC.GUI.Properties.ColorPropertyEditor.NamedColor;
import MWC.GenericData.Watchable;

public class ResolveAmbiguity implements RightClickContextItemGenerator
{

  private static interface IOperator
  {
    public void operate(SensorContactWrapper contact);
  }

  private static class ResolveAmbiguityOperation extends CMAPOperation
  {
    final private List<RelativeTMASegment> _segments;
    final private IOperator _operator;
    final private Layers _layers;

    public ResolveAmbiguityOperation(final Layers layers, final String title,
        final List<RelativeTMASegment> segments, final IOperator operator)
    {
      super(title);
      _segments = segments;
      _operator = operator;
      _layers = layers;
    }

    @Override
    public boolean canExecute()
    {
      return true;
    }

    @Override
    public boolean canRedo()
    {
      return false;
    }

    @Override
    public boolean canUndo()
    {
      return false;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      final Set<TrackWrapper> tracks = new HashSet<TrackWrapper>();
      for (final RelativeTMASegment t : _segments)
      {
        final SensorWrapper sensor = t.getReferenceSensor();

        if (sensor != null)
        {
          // get the contacts
          final Collection<Editable> items =
              sensor.getItemsBetween(t.getDTG_Start(), t.getDTG_End());
          for (final Editable contact : items)
          {
            _operator.operate((SensorContactWrapper) contact);
          }
        }

        tracks.add(sensor.getHost());
      }

      // and fire updates (probably just the one)
      for (final TrackWrapper t : tracks)
      {
        _layers.fireReformatted(t);
      }

      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // TODO
      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // TODO:
      return Status.OK_STATUS;
    }

  }

  public static final String USE_CUT_COLOR = "USE_CUT_COLOR";

  private void assignColors(final Layers theLayers,
      final MenuManager changeColor, final List<RelativeTMASegment> segments)
  {
    final Vector<NamedColor> colors = ColorPropertyEditor.createColors();
    for (final NamedColor t : colors)
    {
      changeColor.add(new Action(t.name)
      {
        @Override
        public void run()
        {
          CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
              "Change color to " + t.name, segments, new IOperator()
              {
                @Override
                public void operate(final SensorContactWrapper contact)
                {
                  contact.setColor(t.getColor());
                }
              }));
        }
      });
    }
  }

  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    final List<RelativeTMASegment> allSegments =
        new ArrayList<RelativeTMASegment>();

    for (final Editable item : subjects)
    {
      if (item instanceof RelativeTMASegment)
      {
        allSegments.add((RelativeTMASegment) item);
      }
      else
      {
        // not all TMA segments, drop out
        return;
      }
    }

    // we will only here if they're all relative TMA segmemts
    final List<RelativeTMASegment> ambigSegments =
        new ArrayList<RelativeTMASegment>();
    final List<RelativeTMASegment> nonAmbigSegments =
        new ArrayList<RelativeTMASegment>();

    // ok, did we find any?
    if (allSegments.isEmpty())
    {
      return;
    }
    else
    {
      // ok, sort out which ones have ambiguous data
      for (final RelativeTMASegment seg : allSegments)
      {
        // try to get the sensor
        final SensorWrapper sensor = seg.getReferenceSensor();

        // try to get the cut nearest the start of this segment
        final Watchable[] nearest = sensor.getNearestTo(seg.getDTG_Start());
        if (nearest != null && nearest.length > 0)
        {
          final SensorContactWrapper scw = (SensorContactWrapper) nearest[0];

          // is it ambiguous
          if (Double.isNaN(scw.getAmbiguousBearing()))
          {
            nonAmbigSegments.add(seg);
          }
          else
          {
            ambigSegments.add(seg);
          }
        }
      }
    }

    // we will only get here if the segments all have ambiguous data

    // insert a separator
    parent.add(new Separator());

    if (!ambigSegments.isEmpty())
    {
      // generate the operations
      parent.add(new Action("Keep Port bearings", DebriefPlugin
          .getImageDescriptor("icons/16/sensor_contact.png"))
      {
        @Override
        public void run()
        {
          CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
              "Keep Port bearings", ambigSegments, new IOperator()
              {

                @Override
                public void operate(final SensorContactWrapper contact)
                {
                  contact.keepPortBearing();
                }
              }));
        }
      });
      parent.add(new Action("Keep Starboard bearings", DebriefPlugin
          .getImageDescriptor("icons/16/sensor_contact_inv.png"))
      {
        @Override
        public void run()
        {
          CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
              "Keep Starboard bearings", ambigSegments, new IOperator()
              {

                @Override
                public void operate(final SensorContactWrapper contact)
                {
                  contact.keepStarboardBearing();
                }
              }));

        }
      });
      parent.add(new Action("Restore ambiguous bearing bearings", DebriefPlugin
          .getImageDescriptor("icons/16/ascend.png"))
      {
        @Override
        public void run()
        {
          CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
              "Restore ambiguous bearings", ambigSegments, new IOperator()
              {

                @Override
                public void operate(final SensorContactWrapper contact)
                {
                  contact.setHasAmbiguousBearing(true);
                }
              }));

        }
      });

      final MenuManager changeColor =
          new MenuManager("Change sensor cut color to", DebriefPlugin
              .getImageDescriptor("icons/16/repaint.png"), null);
      parent.add(changeColor);
      assignColors(theLayers, changeColor, ambigSegments);
    }

    if (!nonAmbigSegments.isEmpty())
    {
      parent.add(generateAmbigBearings(theLayers, nonAmbigSegments));
    }
  }

  private Action generateAmbigBearings(final Layers theLayers,
      final List<RelativeTMASegment> segments)
  {
    return new Action("Calculate ambiguous bearing", DebriefPlugin
        .getImageDescriptor("icons/16/calculator.png"))
    {
      @Override
      public void run()
      {
        CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
            "Generate ambiguous bearings (for testing)", segments,
            new IOperator()
            {

              @Override
              public void operate(final SensorContactWrapper contact)
              {
                // ok, we need to find the ownship course
                final Watchable[] nearest =
                    contact.getSensor().getHost()
                        .getNearestTo(contact.getDTG());
                if (nearest != null && nearest.length > 0)
                {
                  final FixWrapper fix = (FixWrapper) nearest[0];
                  final double course = fix.getCourseDegs();
                  final double brg = contact.getBearing();
                  final double relBrg = brg - course;
                  double ambigBrg = course - relBrg;

                  // trim the bearing
                  while (ambigBrg > 360)
                  {
                    ambigBrg -= 360;
                  }
                  while (ambigBrg < 0)
                  {
                    ambigBrg += 360;
                  }

                  contact.setAmbiguousBearing(ambigBrg);
                  contact.setHasAmbiguousBearing(true);
                }
              }
            }));

      }
    };
  }
}
