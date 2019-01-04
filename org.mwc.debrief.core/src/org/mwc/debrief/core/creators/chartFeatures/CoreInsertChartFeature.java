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
package org.mwc.debrief.core.creators.chartFeatures;

import java.util.Enumeration;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;
import org.mwc.debrief.core.preferences.PrefsPage;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedEditable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Palette.PlainCreate;
import MWC.GUI.Tools.Palette.PlainCreate.CreateLabelAction;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 */
abstract public class CoreInsertChartFeature extends CoreEditorAction
{

  public static ToolParent _theParent = null;

  private boolean multiple = false;
  protected String _selectedLayer;
  private Plottable _shape = null;

  /**
   * whether this item is a top-level layer
   */
  private final boolean _isTopLevelLayer;

  public CoreInsertChartFeature()
  {
    this(false);
  }

  public CoreInsertChartFeature(final boolean isLayer)
  {
    _isTopLevelLayer = isLayer;
  }

  /**
   * ok, store who the parent is for the operation
   * 
   * @param theParent
   */
  public static void init(final ToolParent theParent)
  {
    _theParent = theParent;
  }

  /**
   * convenience method to return the centre of hte visible area, at the surface
   * 
   * @param theChart
   * @return
   */
  protected static WorldLocation getCentre(final PlainChart theChart)
  {
    // right, what's the area we're looking at
    final WorldArea wa = theChart.getCanvas().getProjection()
        .getVisibleDataArea();

    // get centre of area (at zero depth)
    final WorldLocation centre = wa.getCentreAtSurface();

    return centre;
  }

  public Plottable createAndStore()
  {
    execute();
    return _shape;
  }

  /**
   * and execute..
   */
  public void execute()
  {
    final PlainChart theChart = getChart();

    final CreateLabelAction res = createAction(theChart);

    // We store the object created in the _shape variable to user it in the createAndStore method
    _shape = res.getNewFeature();

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
          CorePlugin.logError(Status.ERROR,
              "WE WERE EXPECTING THE NEW FEATURE TO BE A LAYER - in CoreInsertChartFeature",
              null);
        }
      }
      // and the data?
      final Layers data = res.getLayers();

      // ok, now wrap the action
      final DebriefActionWrapper daw = new DebriefActionWrapper(res, data,
          layer);

      // and add it to our buffer (which will execute it anyway)
      CorePlugin.run(daw);
    }
  }

  protected final CreateLabelAction createAction(final PlainChart theChart)
  {
    CreateLabelAction res = null;
    final WorldArea wa = theChart.getDataArea();

    // see if we have an area defined
    if (wa != null)
    {
      // ok, get our layer name
      final String myLayer = getLayerName();

      _selectedLayer = myLayer;
      // drop out if we don't have a target layer (the user may have cancelled)
      if (myLayer == null)
        return null;

      // ok - get the object we're going to insert
      final Plottable thePlottable = getPlottable(theChart);

      if (thePlottable != null)
      {

        // lastly, get the data
        final Layers theData = theChart.getLayers();

        // aah, and the misc layer, in which we will store the shape
        Layer theLayer = null;

        // hmm, do we want to insert ourselves as a layer?
        if (!_isTopLevelLayer)
        {
          theLayer = theData.findLayer(myLayer);

          // did we find it?
          if (theLayer == null)
          {
            // nope, better create it.
            theLayer = getLayer();
            theLayer.setName(myLayer);
            theData.addThisLayer(theLayer);
          }
          String name = thePlottable.getName();
          if (isMultiple() && name != null
              && thePlottable instanceof ExtendedEditable)
          {
            Editable existing = findPlottable(theLayer, name);
            int i = 1;
            while (existing != null)
            {
              name = name + " " + i;
              existing = findPlottable(theLayer, name);
            }
            ((ExtendedEditable) thePlottable).setName(name);
          }

        }

        // and put it into an action (so we can undo it)
        res = new PlainCreate.CreateLabelAction(null, theLayer, theChart
            .getLayers(), thePlottable)
        {

          public void execute()
          {
            // generate the object
            super.execute();

            Display.getDefault().syncExec(new Runnable()
            {
              @Override
              public void run()
              {
                // right, does the user want me to auto-select the newly created
                // item?
                final String autoSelectStr = CorePlugin.getToolParent()
                    .getProperty(PrefsPage.PreferenceConstants.AUTO_SELECT);
                final boolean autoSelect = Boolean.parseBoolean(autoSelectStr);
                if (autoSelect)
                {
                  // ok, now open the properties window
                  try
                  {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().showView(IPageLayout.ID_PROP_SHEET);
                  }
                  catch (final PartInitException e)
                  {
                    CorePlugin.logError(Status.WARNING,
                        "Failed to open properties view", e);
                  }
                }

                // find the editor
                final IChartBasedEditor editor = getEditor();

                // highlight the editor
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().activate((IWorkbenchPart) editor);

                // select the shape
                editor.selectPlottable(_theShape, _theLayer);
              }
            });
          }
        };
      }

    }
    else
    {
      // we haven't got an area, inform the user
      CorePlugin.showMessage("Create Feature",
          "Sorry, we can't create a shape until the area is defined.  Try adding a coastline first");
    }

    return res;
  }

  private Editable findPlottable(Layer theLayer, String name)
  {
    if (name == null)
    {
      return null;
    }
    Enumeration<Editable> elements = theLayer.elements();
    while (elements.hasMoreElements())
    {
      Editable element = elements.nextElement();
      if (name.equals(element.getName()))
      {
        return element;
      }
    }
    return null;
  }

  public Layer getLayer()
  {
    return new BaseLayer();
  }

  /**
   * ok, create whatever we're after
   * 
   * @param theChart
   * @return
   */
  abstract protected Plottable getPlottable(PlainChart theChart);

  /**
   * @return
   */
  protected String getLayerName()
  {
    return Layers.CHART_FEATURES;
  }

  public boolean isMultiple()
  {
    return multiple;
  }

  public void setMultiple(boolean multiple)
  {
    this.multiple = multiple;
  }
}