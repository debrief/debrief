package org.mwc.debrief.track_shift.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.RelativeTMASegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
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

        _layers.fireReformatted(sensor.getHost());
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

  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    final List<RelativeTMASegment> segments =
        new ArrayList<RelativeTMASegment>();

    for (final Editable item : subjects)
    {
      if (item instanceof RelativeTMASegment)
      {
        segments.add((RelativeTMASegment) item);
      }
      else
      {
        // not all TMA segments, drop out
        return;
      }
    }

    // we will only here if they're all relative TMA segmemts

    // ok, did we find any?
    if (!segments.isEmpty())
    {
      for (final RelativeTMASegment seg : segments)
      {
        // try to get the sensor
        final SensorWrapper sensor = seg.getReferenceSensor();

        // try to get the cut nearest the start of this segment
        final Watchable[] nearest = sensor.getNearestTo(seg.getDTG_Start());
        if (nearest != null && nearest.length > 0)
        {
          final SensorContactWrapper scw = (SensorContactWrapper) nearest[0];

          // is it ambiguous
          if (scw.getHasAmbiguousBearing()
              || scw.getAmbiguousBearing() != Doublet.INVALID_BASE_FREQUENCY)
          {
            // ok, it's suitable
          }
          else
          {
            // ok, the data isn't ambiguous. drop out
          }
        }
      }
    }

    // we will only get here if the segments all have ambiguous data

    // insert a separator
    parent.add(new Separator());

    final ImageDescriptor image =
        DebriefPlugin.getImageDescriptor("icons/16/sensor_contact.png");
    final MenuManager holder =
        new MenuManager("Resolve ambiguity", image, null);
    parent.add(holder);

    // generate the operations
    holder.add(new Action("Keep Port bearings")
    {
      @Override
      public void run()
      {
        CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
            "Keep Port bearings", segments, new IOperator()
            {

              @Override
              public void operate(final SensorContactWrapper contact)
              {
                contact.keepPortBearing();
              }
            }));
      }
    });
    holder.add(new Action("Keep Starboard bearings")
    {
      @Override
      public void run()
      {
        CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
            "Keep Starboard bearings", segments, new IOperator()
            {

              @Override
              public void operate(final SensorContactWrapper contact)
              {
                contact.keepStarboardBearing();
              }
            }));

      }
    });
    holder.add(new Action("Restore ambiguous bearing bearings")
    {
      @Override
      public void run()
      {
        CorePlugin.run(new ResolveAmbiguityOperation(theLayers,
            "Restore ambiguous bearings", segments, new IOperator()
            {

              @Override
              public void operate(final SensorContactWrapper contact)
              {
                contact.setHasAmbiguousBearing(true);
              }
            }));

      }
    });

  }

  public static class TestMe extends TestCase
  {
    public void testGenerate()
    {
      // TODO: test the class
      fail("not implemented");
    }
  }
  
}
