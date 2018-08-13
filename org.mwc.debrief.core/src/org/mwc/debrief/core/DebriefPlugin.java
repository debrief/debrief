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
package org.mwc.debrief.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.GenerateNewNarrativeEntry;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.debrief.core.ContextOperations.ConvertAbsoluteTmaToRelative;
import org.mwc.debrief.core.ContextOperations.ConvertTrackToLightweightTrack;
import org.mwc.debrief.core.ContextOperations.ConvertLightweightTrackToTrack;
import org.mwc.debrief.core.ContextOperations.CopyBearingsToClipboard;
import org.mwc.debrief.core.ContextOperations.GenerateInfillSegment;
import org.mwc.debrief.core.ContextOperations.GenerateNewSensor;
import org.mwc.debrief.core.ContextOperations.GenerateNewSensorContact;
import org.mwc.debrief.core.ContextOperations.GenerateSensorRangePlot;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromCuts;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromInfillSegment;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromOwnshipPositions;
import org.mwc.debrief.core.ContextOperations.GenerateTUASolution;
import org.mwc.debrief.core.ContextOperations.GenerateTrack;
import org.mwc.debrief.core.ContextOperations.GenerateTrackFromActiveCuts;
import org.mwc.debrief.core.ContextOperations.GroupLightweightTracks;
import org.mwc.debrief.core.ContextOperations.GroupTracks;
import org.mwc.debrief.core.ContextOperations.ImportAsTrack;
import org.mwc.debrief.core.ContextOperations.InterpolateTrack;
import org.mwc.debrief.core.ContextOperations.MergeContacts;
import org.mwc.debrief.core.ContextOperations.MergeTracks;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts;
import org.mwc.debrief.core.ContextOperations.SelectCutsForThisTMASegment;
import org.mwc.debrief.core.ContextOperations.ShowCutsForThisTMASegment;
import org.mwc.debrief.core.ContextOperations.TrimTrack;
import org.mwc.debrief.core.creators.chartFeatures.InsertTrackSegment;
import org.mwc.debrief.core.preferences.PrefsPage;
import org.mwc.debrief.core.ui.DebriefImageHelper;
import org.mwc.debrief.core.ui.ImportNarrativeHelper;
import org.mwc.debrief.core.ui.SWTEclipseHelper;
import org.osgi.framework.BundleContext;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.Word.ImportNarrativeDocument;
import Debrief.ReaderWriter.XML.extensions.AdditionalDataHandler;
import Debrief.ReaderWriter.XML.extensions.AdditionalDataHandler.ExportProvider;
import Debrief.ReaderWriter.ais.AISDecoder;
import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.CompositeTrackWrapper.GiveMeALeg;
import MWC.GUI.Layer;
import MWC.GUI.MessageProvider;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;
import MWC.Utilities.ReaderWriter.ImportManager;
import MWC.Utilities.ReaderWriter.XML.IDOMExporter;
import MWC.Utilities.ReaderWriter.XML.ISAXImporter;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebriefPlugin extends AbstractUIPlugin implements MessageProvider
{
  public static final String PLUGIN_NAME = "org.mwc.debrief.core";

  public static final String DEBRIEF_EDITOR = "org.mwc.debrief.PlotEditor";
  public static final String SENSOR_FUSION = "org.mwc.debrief.SensorFusion";
  public static final String MULTI_PATH = "org.mwc.debrief.MultiPath2";
  public static final String MULTI_PATH_TEST = "org.mwc.debrief.MultiPath2Test";
  public static final String TIME_BAR = "org.mwc.debrief.TimeBar";
  public static final String SATC_MAINTAIN_CONTRIBUTIONS =
      "com.planetmayo.debrief.satc_rcp.views.MaintainContributionsView";

  public static final String RESET_PERSPECTIVE = "resetPerspective";
  public static final long RESET_PERSPECTIVE_DEFAULT_VALUE = 0;

  public static final String INTROVIEW = "org.eclipse.ui.internal.introview";

  private static final String BUILD_MODE = "buildMode";

  private static final String REP_READER_EXTENSION_POINT_ID = "RepReader";
  public static final String CONTENT_PROVIDER_EXTENSION_POINT_ID =
      "OutlineContentProvider";
  public static final String EXPORT_HELPER_EXTENSION_POINT_ID =
      "DPFReaderWriter";

  // The shared instance.
  private static DebriefPlugin plugin;

  /**
   * Returns the shared instance.
   */
  public static DebriefPlugin getDefault()
  {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in relative path.
   * 
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(final String path)
  {
    ImageDescriptor res;

    // hey is this one we can't find?
    if (_cantFind.contains(path))
    {
      res = null;
    }
    else
    {

      // have we found it already?
      res = _haveFound.get(path);

      if (res == null)
      {
        res = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_NAME, path);

        // did we fail to find it?
        if (res == null)
        {
          _cantFind.add(path);
        }
        else
        {
          _haveFound.put(path, res);
        }
      }
    }

    return res;
  }

  /**
   * Returns the string from the plugin's resource bundle, or 'key' if not found.
   */
  public static String getResourceString(final String key)
  {
    final ResourceBundle bundle =
        DebriefPlugin.getDefault().getResourceBundle();
    try
    {
      return (bundle != null) ? bundle.getString(key) : key;
    }
    catch (final MissingResourceException e)
    {
      return key;
    }
  }

  /**
   * error logging utility
   * 
   * @param severity
   *          the severity; one of <code>OK</code>, <code>ERROR</code>, <code>INFO</code>,
   *          <code>WARNING</code>, or <code>CANCEL</code>
   * @param message
   *          a human-readable message, localized to the current locale
   * @param exception
   *          a low-level exception, or <code>null</code> if not applicable
   */
  public static void logError(final int severity, final String message,
      final Throwable exception)
  {
    final Status stat =
        new Status(severity, PLUGIN_NAME, IStatus.OK, message, exception);
    getDefault().getLog().log(stat);

    // also throw it to the console
    if (exception != null)
      exception.printStackTrace();
  }

  // Resource bundle.
  private ResourceBundle resourceBundle;
  private DebriefImageHelper _myImageHelper;

  private ArrayList<ExtensibleLineImporter> _repFileExtensionLoaders;

  private List<IDOMExporter> _exportHelpers;

  private ArrayList<ISAXImporter> _importHelpers;

  /**
   * keep track of images we can't find. It's no use carrying on trying to find them
   * 
   */
  private static List<String> _cantFind = new ArrayList<String>();

  /**
   * keep track of images we've found - to save going through the bundles
   * 
   */
  private static Map<String, ImageDescriptor> _haveFound =
      new HashMap<String, ImageDescriptor>();

  /**
   * The constructor.
   */
  public DebriefPlugin()
  {
    super();
    plugin = this;
  }

  /**
   * Returns the plugin's resource bundle,
   */
  public ResourceBundle getResourceBundle()
  {
    try
    {
      if (resourceBundle == null)
        resourceBundle =
            ResourceBundle
                .getBundle("org.mwc.debrief.core.CorePluginResources");
    }
    catch (final MissingResourceException x)
    {
      resourceBundle = null;
    }
    return resourceBundle;
  }

  public void show(final String title, final String message, final int status)
  {
    Display display = Display.getCurrent();
    final Display fDisplay;

    // if we have a current display, use it. else get the default display
    if (display != null)
      fDisplay = display;
    else
      fDisplay = Display.getDefault();

    fDisplay.asyncExec(new Runnable()
    {
      public void run()
      {
        // sort out the status
        if (status == MessageProvider.INFO || status == MessageProvider.OK)
          MessageDialog.openInformation(fDisplay.getActiveShell(), title,
              message);
        else if (status == MessageProvider.WARNING)
          MessageDialog.openWarning(fDisplay.getActiveShell(), title, message);
        else if (status == MessageProvider.ERROR)
          MessageDialog.openError(fDisplay.getActiveShell(), title, message);
      }
    });
  }

  /**
   * This method is called upon plug-in activation
   */
  @Override
  public void start(final BundleContext context) throws Exception
  {
    super.start(context);

    // also provide someps extra functionality to the right-click editor
    RightClickSupport.addRightClickGenerator(new ConvertTrackToLightweightTrack());
    RightClickSupport.addRightClickGenerator(new ConvertLightweightTrackToTrack());
    RightClickSupport.addRightClickGenerator(new GenerateTrack());
    RightClickSupport.addRightClickGenerator(new GroupTracks());
    RightClickSupport.addRightClickGenerator(new GroupLightweightTracks());
    RightClickSupport.addRightClickGenerator(new GenerateInfillSegment());
    RightClickSupport.addRightClickGenerator(new MergeTracks());
    RightClickSupport.addRightClickGenerator(new MergeContacts());
    RightClickSupport
        .addRightClickGenerator(new ConvertAbsoluteTmaToRelative());
    RightClickSupport.addRightClickGenerator(new ShowCutsForThisTMASegment());
    RightClickSupport.addRightClickGenerator(new SelectCutsForThisTMASegment());
    RightClickSupport.addRightClickGenerator(new GenerateTMASegmentFromCuts());
    RightClickSupport
        .addRightClickGenerator(new GenerateTMASegmentFromOwnshipPositions());
    RightClickSupport
        .addRightClickGenerator(new GenerateTMASegmentFromInfillSegment());
    RightClickSupport.addRightClickGenerator(new GenerateTUASolution());
    RightClickSupport.addRightClickGenerator(new GenerateTrackFromActiveCuts());
    RightClickSupport.addRightClickGenerator(new GenerateSensorRangePlot());
    RightClickSupport.addRightClickGenerator(new GenerateNewSensor());
    RightClickSupport.addRightClickGenerator(new GenerateNewSensorContact());
    RightClickSupport.addRightClickGenerator(new GenerateNewNarrativeEntry());
    RightClickSupport.addRightClickGenerator(new ImportAsTrack());
    RightClickSupport.addRightClickGenerator(new TrimTrack());
    RightClickSupport.addRightClickGenerator(new RainbowShadeSonarCuts());
    RightClickSupport.addRightClickGenerator(new InterpolateTrack());
    RightClickSupport.addRightClickGenerator(new CopyBearingsToClipboard());
    

    // and the Replay importer/exporter (used to export items from the
    // layer-manager)
    ImportManager.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

    // tell ImportReplay that we can provide more importers
    List<ExtensibleLineImporter> importers = getRepImporterExtensions();
    ImportReplay.addExtraImporters(importers);

    // make Debrief the default editor for XML files
    Display.getDefault().asyncExec(new Runnable()
    {
      
      @Override
      public void run()
      {
        final IEditorRegistry editorRegistry =
            PlatformUI.getWorkbench().getEditorRegistry();
        editorRegistry.setDefaultEditor("*.xml", "org.mwc.debrief.PlotEditor");
      }
    });
    

    // tell the message provider where it can fire messages to
    MessageProvider.Base.setProvider(this);

    // give the LayerManager our image creator.
    _myImageHelper = new DebriefImageHelper();
    CoreViewLabelProvider.addImageHelper(_myImageHelper);

    // see if there are any extensions to handle images
    loadContentProviderExtensions();

    // provide helper for triggering 'new-leg' operation
    final GiveMeALeg triggerNewLeg = new GiveMeALeg()
    {

      @Override
      public void createLegFor(final Layer parent)
      {
        final InsertTrackSegment ts = new InsertTrackSegment(parent);
        ts.run(null);
      }
    };

    CompositeTrackWrapper.setNewLegHelper(triggerNewLeg);
    CompositeTrackWrapper.initialise(CorePlugin.getToolParent());
    AISDecoder.initialise(CorePlugin.getToolParent());

    ImportNarrativeDocument.setQuestionHelper(new SWTEclipseHelper());
    ImportNarrativeDocument.setNarrativeHelper(new ImportNarrativeHelper());

    // tell the additional data that we can help
    AdditionalDataHandler.setExportHelper(new ExportProvider()
    {
      @Override
      public List<IDOMExporter> getExporters()
      {
        initImportExportHelpers();
        return _exportHelpers;
      }

      @Override
      public List<ISAXImporter> getImporters()
      {
        initImportExportHelpers();
        return _importHelpers;
      }
    });

  }

  private void initImportExportHelpers()
  {
    if (_exportHelpers == null)
    {
      _exportHelpers = new ArrayList<IDOMExporter>();
      _importHelpers = new ArrayList<ISAXImporter>();

      IExtensionRegistry registry = Platform.getExtensionRegistry();
      if (registry != null)
      {

        final IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint(
                DebriefPlugin.PLUGIN_NAME, EXPORT_HELPER_EXTENSION_POINT_ID);

        final IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++)
        {
          final IExtension iExtension = extensions[i];
          final IConfigurationElement[] confE =
              iExtension.getConfigurationElements();
          for (int j = 0; j < confE.length; j++)
          {
            final IConfigurationElement iConfigurationElement = confE[j];
            try
            {
              final IDOMExporter newInstance =
                  (IDOMExporter) iConfigurationElement
                      .createExecutableExtension("writer");
              _exportHelpers.add(newInstance);
              final ISAXImporter newInstance2 =
                  (ISAXImporter) iConfigurationElement
                      .createExecutableExtension("reader");
              _importHelpers.add(newInstance2);
            }
            catch (final CoreException e)
            {
              CorePlugin.logError(Status.ERROR,
                  "Trouble whilst loading image helper", e);
            }
          }
        }
      }
    }
  }

  private void loadContentProviderExtensions()
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    if (registry != null)
    {

      final IExtensionPoint point =
          Platform.getExtensionRegistry().getExtensionPoint(
              DebriefPlugin.PLUGIN_NAME, CONTENT_PROVIDER_EXTENSION_POINT_ID);

      final IExtension[] extensions = point.getExtensions();
      for (int i = 0; i < extensions.length; i++)
      {
        final IExtension iExtension = extensions[i];
        final IConfigurationElement[] confE =
            iExtension.getConfigurationElements();
        for (int j = 0; j < confE.length; j++)
        {
          final IConfigurationElement iConfigurationElement = confE[j];
          ViewLabelImageHelper newInstance;
          try
          {
            newInstance =
                (ViewLabelImageHelper) iConfigurationElement
                    .createExecutableExtension("imageProvider");
            CoreViewLabelProvider.addImageHelper(newInstance);
          }
          catch (final CoreException e)
          {
            CorePlugin.logError(Status.ERROR,
                "Trouble whilst loading image helper", e);
          }
        }
      }
    }
  }

  private List<ExtensibleLineImporter> getRepImporterExtensions()
  {
    if (_repFileExtensionLoaders == null)
    {
      _repFileExtensionLoaders = new ArrayList<ExtensibleLineImporter>();

      IExtensionRegistry registry = Platform.getExtensionRegistry();

      if (registry != null)
      {

        final IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_NAME,
                REP_READER_EXTENSION_POINT_ID);

        final IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++)
        {
          final IExtension iExtension = extensions[i];
          final IConfigurationElement[] confE =
              iExtension.getConfigurationElements();
          for (int j = 0; j < confE.length; j++)
          {
            final IConfigurationElement iConfigurationElement = confE[j];
            ExtensibleLineImporter newInstance;
            try
            {
              newInstance =
                  (ExtensibleLineImporter) iConfigurationElement
                      .createExecutableExtension("class");
              _repFileExtensionLoaders.add(newInstance);
            }
            catch (final CoreException e)
            {
              CorePlugin.logError(Status.ERROR,
                  "Trouble whilst loading REP import extensions", e);
            }
          }
        }
      }
    }
    return _repFileExtensionLoaders;
  }

  /**
   * This method is called when the plug-in is stopped
   */
  @Override
  public void stop(final BundleContext context) throws Exception
  {
    super.stop(context);
    plugin = null;
    resourceBundle = null;
  }

  public long getResetPerspectivePreference()
  {
    return getDefault().getPreferenceStore().getLong(RESET_PERSPECTIVE);
  }

  public boolean getCreateProject()
  {
    if (isRunningTests())
    {
      return false;
    }
    // check standard Debrief preference
    String createProject =
        CorePlugin.getDefault().getPreferenceStore().getString(
            PrefsPage.PreferenceConstants.ASK_ABOUT_PROJECT);
    if (createProject == null || createProject.isEmpty())
    {
      createProject = Boolean.TRUE.toString();
    }
    return (Boolean.TRUE.toString().equals(createProject));
  }

  public boolean isRunningTests()
  {
    // check settings system property on command line
    // for tycho/travis test we need add -DbuildMode=true
    String buildMode = System.getProperty(BUILD_MODE, "false");
    if ("true".equals(buildMode))
    {
      return true;
    }
    return false;
  }
}
