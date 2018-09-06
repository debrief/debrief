/**
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcWizard;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Tools.Palette.PlainCreate.CreateLabelAction;

/**
 * Generates a new sensor arc action on right clicking a
 * layer on the plot editor's outline page
 * 
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class GenerateNewInsertSensorArcAction implements
                  RightClickContextItemGenerator
{

  private static final String WIZARD_TITLE = "Insert new Sensor Arc";
  
  
  public static class InsertSensorArcOperation extends CMAPOperation{

    private TrackWrapper _track;
    private DynamicTrackShapeSetWrapper _shapeWrapper;
    private Layers _layers;
    public InsertSensorArcOperation(Layers theLayers,TrackWrapper theTrack,DynamicTrackShapeSetWrapper shape)
    {
      super(WIZARD_TITLE);
      this._layers = theLayers;
      this._track = theTrack;
      this._shapeWrapper=shape;
    }
    
    
    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {

      final CreateLabelAction res = new CreateLabelAction(null,_track,_layers,_shapeWrapper);

      // did we get an action?
      if (res != null)
      {
        // do we know the layer?
        Layer layer = res.getLayer();

        // is it null? in which case we're adding a new layer
        if (layer == null)
        {
          // try to get the new plottable
          final Plottable pl = res.getNewFeature();
          if (pl instanceof Layer)
            layer = (Layer) pl;
          else
          {
            CorePlugin
                .logError(
                    Status.ERROR,
                    "WE WERE EXPECTING THE NEW FEATURE TO BE A LAYER - in CoreInsertChartFeature",
                    null);
          }
        }
        final Layers data = res.getLayers();

        // ok, now wrap the action
        final DebriefActionWrapper daw = new DebriefActionWrapper(res, data, layer);

        // and add it to our buffer (which will execute it anyway)
        CorePlugin.run(daw);
      }
      return Status.OK_STATUS;
    }
    
    

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      _track.removeElement(_shapeWrapper);
      return Status.OK_STATUS;
    }
    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // TODO Auto-generated method stub
      return super.redo(monitor, info);
    }
    
  }
  
  private void runOperation(final Layers theLayers,
      final TrackWrapper theTrack, final NewSensorArcWizard wizard)
  {
    final WizardDialog dialog = new WizardDialog(Display.getCurrent()
        .getActiveShell(), wizard);
    TrayDialog.setDialogHelpAvailable(true);
    dialog.setHelpAvailable(true);
    dialog.create();
    dialog.open();

    // did it work?
    if (dialog.getReturnCode() == WizardDialog.OK)
    {
      final DynamicTrackShapeSetWrapper dynamicShapeWrapper = wizard.getDynamicShapeWrapper();
      // ok, go for it.
      // sort it out as an operation
      final IUndoableOperation addTheCut = new InsertSensorArcOperation(theLayers,
          theTrack, dynamicShapeWrapper);

      // ok, stick it on the buffer
      runIt(addTheCut);
    }
  }
  
  
  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    boolean goForIt = false;
    TrackWrapper track = null;

    // we're only going to work with two or more items
    if (subjects.length == 0)
    {
      goForIt = true;
    }

    if (subjects.length == 1 && subjects[0] instanceof TrackWrapper)
    {
      goForIt = true;
      track = (TrackWrapper) subjects[0];
    }

    if (goForIt)
    {
      // try to get the current plot date
      // ok, populate the data
      final IEditorPart curEditor = PlatformUI.getWorkbench()
          .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
      final Date startTime;
      final Date endTime;
      if (curEditor instanceof IAdaptable)
      {
        TimeProvider prov = (TimeProvider) curEditor.getAdapter(TimeProvider.class);
        if(prov != null)
        {
          startTime = prov.getPeriod().getStartDTG().getDate();
          endTime = prov.getPeriod().getEndDTG().getDate();
        }
        else
        {
          startTime = null;
          endTime = null;
        }
      }
      else
      {
        startTime = null;
        endTime = null;
      }
      
      // right,stick in a separator
      parent.add(new Separator());

      final TrackWrapper theTrack = track;
      final Map<String,Editable> tracksMap = new HashMap<>();
      tracksMap.put(track.getName(), track);

      // create this operation
      Action insertSensorArcAction = 
      
      new Action(WIZARD_TITLE)
      {
        public void run()
        {
          // get the supporting data
          String[] tracks = new String[1];
          tracks[0]=theTrack.getName();
          NewSensorArcWizard wizard = new NewSensorArcWizard(tracksMap,theTrack.getName(), startTime, endTime) ;
          runOperation(theLayers, theTrack, wizard);
        }
      };
      
      // ok - set the image descriptor
      insertSensorArcAction.setImageDescriptor(DebriefPlugin
          .getImageDescriptor("icons/16/sensor.png"));

      
      parent.add(insertSensorArcAction);
    }
  }
  /**
   * put the operation firer onto the undo history. We've refactored this into a
   * separate method so testing classes don't have to simulate the CorePlugin
   * 
   * @param operation
   */
  protected void runIt(final IUndoableOperation operation)
  {
    CorePlugin.run(operation);
  }
}
