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
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.ContextOperations.GenerateInfillSegment.GenerateInfillOperation;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * @author ian.mayo
 * 
 */
public class CopyBearingsToClipboard implements RightClickContextItemGenerator
{

  private static class BearingList extends HashMap<HiResDate, WorldVector>
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final String _name;

    private final Color _color;

    public BearingList(final String name, final Color color)
    {
      _name = name;
      _color = color;
    }

    public Color getColor()
    {
      return _color;
    }

    public String getName()
    {
      return _name;
    }

  }

  private static class CopyBearingData extends CMAPOperation
  {

    private static BearingList calculateOffsets(final TrackWrapper subject,
        final TrackWrapper refTrack)
    {
      final BearingList res =
          new BearingList(subject.getName(), subject.getColor());

      final Enumeration<Editable> posits = subject.getPositionIterator();

      final boolean refWasInterpolated = refTrack.getInterpolatePoints();
      refTrack.setInterpolatePoints(true);

      while (posits.hasMoreElements())
      {
        final FixWrapper nextF = (FixWrapper) posits.nextElement();
        final HiResDate tNow = nextF.getDateTimeGroup();
        final Watchable[] nearest = refTrack.getNearestTo(tNow, false);
        if (nearest != null && nearest.length > 0)
        {
          final FixWrapper near = (FixWrapper) nearest[0];
          final WorldVector offset =
              nextF.getLocation().subtract(near.getLocation());
          res.put(tNow, offset);
        }
      }

      refTrack.setInterpolatePoints(refWasInterpolated);

      return res;
    }

    private Clipboard _clip;

    private final TrackWrapper _subject;

    private final TrackWrapper _referenceTrack;

    private BearingList _offsets;

    public CopyBearingData(final String title, final TrackWrapper subject,
        final TrackWrapper referenceTrack)
    {
      super(title);
      _subject = subject;
      _referenceTrack = referenceTrack;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      // ok, get ready
      _offsets = calculateOffsets(_subject, _referenceTrack);

      // did we find any?
      if (_offsets.size() > 0)
      {
        writeOffsets(_offsets);
      }

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // ok, just clear the clipboard
      _clip.setContents(new Transferable()
      {
        @Override
        public Object getTransferData(final DataFlavor flavor)
            throws UnsupportedFlavorException
        {
          throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
          return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor)
        {
          return false;
        }
      }, CorePlugin.getDefault());

      return Status.OK_STATUS;
    }

    private void writeOffsets(final BearingList offsets)
    {
      // create the clipboard buffer
      _clip = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();

      // store our data
      final TransferableBearingList ourData =
          new TransferableBearingList(offsets);

      // and put it on the clipboard
      _clip.setContents(ourData, CorePlugin.getDefault());
    }

  }

  private static class PasteBearingData extends CMAPOperation
  {

    private static void createPointsFor(final TrackWrapper newTrack,
        final BearingList offsets, final TrackWrapper refTrack)
    {
      for (final HiResDate dtg : offsets.keySet())
      {
        final WorldVector vector = offsets.get(dtg);

        // find nearest in the host
        final Watchable[] nearest = refTrack.getNearestTo(dtg);

        if (nearest != null && nearest.length > 0)
        {
          // ok, we've got data
          final FixWrapper nearF = (FixWrapper) nearest[0];

          final WorldLocation nearLoc = nearF.getLocation();

          // add the location
          final WorldLocation newLoc = nearLoc.add(vector);

          // generate the fix
          final Fix newFix = new Fix(dtg, newLoc, 0d, 0d);
          final FixWrapper newFW = new FixWrapper(newFix);

          // store the fix
          newTrack.add(newFW);
        }
      }

      // lastly, regenerate course and speed for the fixes in the new track
      newTrack.calcCourseSpeed();
    }

    private final Layers _layers;
    private final TrackWrapper _referenceTrack;
    private final BearingList _offsets;

    /**
     * the new track we're creating
     * 
     */
    private TrackWrapper _newTrack;

    public PasteBearingData(final String title, final Layers layers,
        final BearingList offsets, final TrackWrapper referenceTrack)
    {
      super(title);
      _layers = layers;
      _referenceTrack = referenceTrack;
      _offsets = offsets;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      // ok, create the track
      _newTrack = new TrackWrapper();
      _newTrack.setName(_offsets.getName());
      _newTrack.setColor(_offsets.getColor());

      // now write the points
      createPointsFor(_newTrack, _offsets, _referenceTrack);

      // and store the track
      _layers.addThisLayer(_newTrack);

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // ok, just delete the track
      if (_newTrack != null)
      {
        _layers.removeThisLayer(_newTrack);
        _newTrack = null;
      }

      return Status.OK_STATUS;
    }

  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(final String val)
    {
      super(val);
    }

    @SuppressWarnings("deprecation")
    private void doTestCopyBearings(int startIndex, int endIndex) throws ExecutionException
    {
      final Layers layers = new Layers();
      final TrackWrapper host = new TrackWrapper();
      host.setName("Host");
      final SensorWrapper sensor = new SensorWrapper("Sensor");
      host.add(sensor);
      layers.addThisLayer(host);

      for (int i = startIndex; i < endIndex; i++)
      {
        final Date newDate = new Date(2018, 01, 01, 02, i * 2, 0);
        final WorldLocation loc = new WorldLocation(12, 12 + i / 60, 0d);
        final Fix newF = new Fix(new HiResDate(newDate.getTime()), loc, 0, 12);
        final FixWrapper fix = new FixWrapper(newF);
        host.add(fix);

        final Date newDate2 = new Date(2018, 01, 01, 02, i * 2, 10);

        // public SensorContactWrapper(final String trackName, final HiResDate dtg,
        // final WorldDistance range, final Double bearingDegs,
        // final WorldLocation origin, final java.awt.Color color,
        // final String label, final int style, final String sensorName)
        if (i % 9 != 0)
        {
          final SensorContactWrapper contact =
              new SensorContactWrapper(host.getName(), new HiResDate(newDate2
                  .getTime()), null, 12d, null, Color.red, "Some label", 1,
                  "Sensor");
          sensor.add(contact);
        }
      }

      final TrackWrapper subject = new TrackWrapper();
      subject.setName("subject");
      layers.addThisLayer(subject);

      final SensorContactWrapper[] obs =
          new SensorContactWrapper[sensor.size()];
      Enumeration<Editable> ele = sensor.elements();
      int ctr = 0;
      while (ele.hasMoreElements())
      {
        final SensorContactWrapper cut =
            (SensorContactWrapper) ele.nextElement();
        obs[ctr++] = cut;
      }

      final WorldVector offset = new WorldVector(12, 0.002, 0.0d);
      final RelativeTMASegment seg =
          new RelativeTMASegment(obs, offset,
              new WorldSpeed(12, WorldSpeed.Kts), 12d, layers, Color.green);
      subject.add(seg);

      assertEquals("have valid segment", 53, seg.size());

      final FixWrapper split1 = (FixWrapper) seg.getData().toArray()[12];
      final FixWrapper split2 = (FixWrapper) seg.getData().toArray()[21];

      subject.splitTrack(split1, true);

      assertEquals("have legs", 2, subject.getSegments().size());

      subject.splitTrack(split2, true);

      assertEquals("have legs", 3, subject.getSegments().size());

      final Editable[] subjects = new Editable[3];
      int ctr2 = 0;
      ele = subject.getSegments().elements();
      while (ele.hasMoreElements())
      {
        subjects[ctr2++] = ele.nextElement();
      }

      final GenerateInfillOperation operation =
          new GenerateInfillOperation("title", subjects, layers, subject,
              getMyLogger(), true);

      assertEquals("before infills", 3, subject.getSegments().size());

      operation.execute(null, null);

      assertEquals("has infills", 5, subject.getSegments().size());

      // ok, now we can run the get bearings
      final CopyBearingData oper = new CopyBearingData("title", subject, host);

      // check bearings are on clipboard.
      oper.execute(null, null);
    }

    @SuppressWarnings("deprecation")
    private void doTestPasteBearings(final int startIndex, final int endIndex, final int numPoints, Object menuItems)
    {
      final Layers layers = new Layers();
      final TrackWrapper host = new TrackWrapper();
      host.setName("Host");
      final SensorWrapper sensor = new SensorWrapper("Sensor");
      host.add(sensor);
      layers.addThisLayer(host);

      for (int i = startIndex; i < endIndex; i++)
      {
        final Date newDate = new Date(2018, 01, 01, 02, i * 2, 0);
        final WorldLocation loc = new WorldLocation(12, 12 + i / 60, 0d);
        final Fix newF = new Fix(new HiResDate(newDate.getTime()), loc, 0, 12);
        final FixWrapper fix = new FixWrapper(newF);
        host.add(fix);
      }

      final IMenuManager menu = new MenuManager();
      new CopyBearingsToClipboard().generatePasteAction(menu, layers, host);

      assertEquals("have items", menuItems, menu.getItems().length);
      
      if(menu.getItems().length == 0)
      {
        // ok, we didn't create menu items, just leave
        return;
      }
      
      final ActionContributionItem first =
          (ActionContributionItem) menu.getItems()[1];
      final IAction action = first.getAction();

      assertEquals("just one layer", 1, layers.size());

      action.run();

      assertEquals("now two layers", 2, layers.size());
      
      TrackWrapper dropped = (TrackWrapper) layers.findLayer("subject");
      assertNotNull("found new layer", dropped);
      
      int ctr = 0;
      Enumeration<Editable> iter = dropped.getPositionIterator();
      while(iter.hasMoreElements())
      {
        ctr++;
        iter.nextElement();
      }
      assertEquals("correct size", numPoints, ctr);

    }

    private ErrorLogger getMyLogger()
    {
      return new ErrorLogger()
      {

        @Override
        public void logError(final int status, final String text,
            final Exception e)
        {
          CorePlugin.logError(status, text, e);
        }

        @Override
        public void logError(final int status, final String text,
            final Exception e, final boolean revealLog)
        {
          logError(status, text, e);
        }

        @Override
        public void logStack(final int status, final String text)
        {
          logError(status, text, null);
        }
      };
    }

    public void testCopyPaste() throws ExecutionException
    {
      doTestCopyBearings(0, 60);
      doTestPasteBearings(0, 60, 56, 2);
    }

    public void testCopyTrackNoOverlap() throws ExecutionException
    {
      doTestCopyBearings(0, 60);
      doTestPasteBearings(20, 70, 0, 0);
    }

    public void testCopyTrackLate() throws ExecutionException
    {
      doTestCopyBearings(0, 60);
      doTestPasteBearings(5, 70, 52, 2);
    }
    
    public void testCopyTrackEarly() throws ExecutionException
    {
      doTestCopyBearings(10, 69);
      doTestPasteBearings(5, 55, 44, 2);
    }

  }

  private static class TransferableBearingList implements Transferable
  {
    public static DataFlavor FLAVOR = new DataFlavor(BearingList.class,
        "BearingList");

    private static DataFlavor[] FLAVORS = new DataFlavor[]
    {FLAVOR};

    private final BearingList list; // This is the PolyLine we wrap.

    public TransferableBearingList(final BearingList list)
    {
      this.list = list;
    }

    /** Return the wrapped PolyLine, if the flavor is right */
    @Override
    public Object getTransferData(final DataFlavor f)
        throws UnsupportedFlavorException
    {
      if (!f.equals(FLAVOR))
      {
        throw new UnsupportedFlavorException(f);
      }
      return list;
    }

    /** Return the supported flavor */
    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
      return FLAVORS;
    }

    /** Check for the one flavor we support */
    @Override
    public boolean isDataFlavorSupported(final DataFlavor f)
    {
      return f.equals(FLAVOR);
    }

  }

  /**
   * @param parent
   * @param theLayers
   * @param parentLayers
   * @param subjects
   */
  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    // ok, we only allow a single selection
    if (subjects.length == 1)
    {
      // ok, good start

      final Editable first = subjects[0];
      if (first instanceof TrackWrapper)
      {
        final TrackWrapper track = (TrackWrapper) first;

        // ok, first see if it's a relative track - so we can
        // offer to copy it to the clipboard
        generateCopyAction(parent, track);

        // and now the paste action
        generatePasteAction(parent, theLayers, track);
      }
    }
  }

  private void generateCopyAction(final IMenuManager parent, final TrackWrapper track)
  {
    // ok, it's a track. Is it made from relative TMA segments?
    final SegmentList segments = track.getSegments();
    final Enumeration<Editable> ele = segments.elements();

    TrackWrapper host = null;

    while (ele.hasMoreElements())
    {
      final TrackSegment seg = (TrackSegment) ele.nextElement();

      if (seg instanceof RelativeTMASegment)
      {
        final RelativeTMASegment rel = (RelativeTMASegment) seg;
        final WatchableList refTrack = rel.getReferenceTrack();
        if (refTrack == null)
        {
          // ok, show error
          CorePlugin.logError(IStatus.ERROR,
              "Host track for TMA leg can't be determined", null);
          break;
        }
        else if (refTrack instanceof TrackWrapper)
        {
          host = (TrackWrapper) refTrack;
          break;
        }
        else
        {
          CorePlugin.logError(IStatus.ERROR,
              "Host track for TMA leg isn't a TrackWrapper", null);
          break;
        }
      }
    }

    if (host == null)
    {
      // ok, it's not a relative track.
      // see if we have a primary track assigned
      final IWorkbench wb = PlatformUI.getWorkbench();
      final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
      final IWorkbenchPage page = win.getActivePage();
      final IEditorPart editor = page.getActiveEditor();

      if (editor instanceof PlotEditor)
      {
        final TrackManager trackManager =
            (TrackManager) editor.getAdapter(TrackManager.class);
        if (trackManager != null)
        {
          host = (TrackWrapper) trackManager.getPrimaryTrack();
        }
      }
    }

    if (host != null && !host.equals(track))
    {
      // yes, create the action
      final String title =
          "Copy to clipboard as offsets from " + host.getName();
      final TrackWrapper theHost = host;

      final Action convertToTrack = new Action(title)
      {
        @Override
        public void run()
        {
          // ok, go for it.
          // sort it out as an operation
          final IUndoableOperation copyBearings =
              new CopyBearingData(title, track, theHost);

          // ok, stick it on the buffer
          runIt(copyBearings);
        }
      };

      // right,stick in a separator
      parent.add(new Separator());

      // ok - flash up the menu item
      parent.add(convertToTrack);
    }
  }

  private void generatePasteAction(final IMenuManager parent,
      final Layers theLayers, final TrackWrapper track)
  {
    // ok, see if we have some bearing data on the clipboard
    final Clipboard clip =
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();

    // and put it on the clipboard
    if (clip.isDataFlavorAvailable(TransferableBearingList.FLAVOR))
    {
      try
      {
        final Object contents = clip.getData(TransferableBearingList.FLAVOR);
        if (contents != null)
        {
          // ok, process it
          final BearingList bearingList = (BearingList) contents;

          if (bearingList != null)
          {
            // ok, now check this track matches the bearings
            final Object[] dates = bearingList.keySet().toArray();
            final TimePeriod listP =
                new TimePeriod.BaseTimePeriod((HiResDate) dates[0],
                    (HiResDate) dates[dates.length - 1]);
            final TimePeriod trackP =
                new TimePeriod.BaseTimePeriod(track.getStartDTG(), track
                    .getEndDTG());

            if (listP.overlaps(trackP))
            {
              // ok, create the action
              final String title =
                  "Create new track by adding clipboard bearings to "
                      + track.getName();

              final Action convertToTrack = new Action(title)
              {
                @Override
                public void run()
                {
                  // ok, go for it.
                  // sort it out as an operation
                  final IUndoableOperation copyBearings =
                      new PasteBearingData(title, theLayers, bearingList, track);

                  // ok, stick it on the buffer
                  runIt(copyBearings);
                }
              };
              // right,stick in a separator
              parent.add(new Separator());

              // ok - flash up the menu item
              parent.add(convertToTrack);
            }
          }
        }
      }
      catch (UnsupportedFlavorException | IOException e)
      {
        CorePlugin.logError(IStatus.ERROR,
            "Problem creating new track from clipboard", e);
      }
    }
  }

  /**
   * put the operation firer onto the undo history. We've refactored this into a separate method so
   * testing classes don't have to simulate the CorePlugin
   * 
   * @param operation
   */
  protected void runIt(final IUndoableOperation operation)
  {
    CorePlugin.run(operation);
  }
}
