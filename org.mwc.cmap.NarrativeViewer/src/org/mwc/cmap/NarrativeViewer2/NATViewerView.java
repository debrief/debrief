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
package org.mwc.cmap.NarrativeViewer2;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.NarrativeViewer.preferences.NarrativeViewerPrefsPage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.GenerateNewNarrativeEntry;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.core.wizards.NewNarrativeEntryWizard;
import org.mwc.cmap.gridharness.data.FormatDateTime;

import Debrief.ReaderWriter.Word.ImportRiderNarrativeDocument;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.IRollingNarrativeProvider.INarrativeListener;
import MWC.TacticalData.temporal.ControllableTime;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class NATViewerView extends ViewPart implements PropertyChangeListener,
    ISelectionProvider
{
  NatNarrativeViewer myViewer;

  /**
   * helper application to help track creation/activation of new plots
   */
  private PartMonitor _myPartMonitor;

  /**
   * help out with listening for selection changes
   * 
   */
  ISelectionChangedListener _selectionChangeListener;

  IRollingNarrativeProvider _myRollingNarrative;

  final protected INarrativeListener _myRollingNarrListener;

  /**
   * whether to clip text to the visible size
   * 
   */
  Action _clipText;

  private static DateFormat _myFormat;
  private static String _myFormatString;

  /**
   * whether to follow the controllable time
   * 
   */
  private Action _followTime;

  /**
   * value we use for null-time
   * 
   */
  private final long INVALID_TIME = -1L;

  /**
   * we don't want to process all new-time events, only the most recent one. So, take a note of the
   * most recent one
   */
  AtomicLong _pendingTime = new AtomicLong(INVALID_TIME);

  /**
   * whether to control the controllable time
   * 
   */
  private Action _controlTime;

  protected TimeProvider _myTemporalDataset;

  protected PropertyChangeListener _temporalListener;

  protected ControllableTime _controllableTime;

  /**
   * the current editor (we store this so we can create bookmarks
   * 
   */
  private IEditorPart _currentEditor;

  /**
   * the people listening to us
   */
  Vector<ISelectionChangedListener> _selectionListeners;

  private Action _setAsBookmarkAction;

  protected Layers _myLayers;

  /**
   * we need to listen out for layer modifications
   * 
   */
  protected final DataListener _layerListener;

  /**
   * flag for if we're currently in update
   * 
   */
  private static boolean _amUpdating = false;

  public synchronized static String toStringHiRes(final HiResDate time,
      final String pattern) throws IllegalArgumentException
  {
    // so, have a look at the data
    final long micros = time.getMicros();
    // long wholeSeconds = micros / 1000000;

    final StringBuffer res = new StringBuffer();

    final java.util.Date theTime = new java.util.Date(micros / 1000);

    // do we already know about a date format?
    if (_myFormatString != null)
    {
      // right, see if it's what we're after
      if (_myFormatString != pattern)
      {
        // nope, it's not what we're after. ditch gash
        _myFormatString = null;
        _myFormat = null;
      }
    }

    // so, we either don't have a format yet, or we did have, and now we
    // want to
    // forget it...
    if (_myFormat == null)
    {
      _myFormatString = pattern;
      _myFormat = new GMTDateFormat(pattern);
    }

    res.append(_myFormat.format(theTime));

    return res.toString();
  }

  public NATViewerView()
  {
    _layerListener = new DataListener()
    {
      @Override
      public void dataExtended(final Layers theData)
      {
        // nope, see if there is one
        final Layer match = theData.findLayer(LayerHandler.NARRATIVE_LAYER);

        // ok, do we already have a narrative?
        if (_myRollingNarrative == null)
        {
          if (match instanceof IRollingNarrativeProvider)
          {
            Display.getDefault().syncExec(new Runnable()
            {
              
              @Override
              public void run()
              {
                setInput((IRollingNarrativeProvider) match);    
              }
            });
          }
        }
        else
        {
          // hmm, has our narrative been deleted?
          if (match == null)
          {
            Display.getDefault().syncExec(new Runnable()
            {
              @Override
              public void run()
              {
                setInput(null);    
              }
            });
          }
        }
        // oh, oooh, see if we've learned some more colors
        refreshColors();
      }

      @Override
      public void dataModified(final Layers theData, final Layer changedLayer)
      {
        if (changedLayer == _myRollingNarrative)
        {
          uiUpdate();
        }
      }

      @Override
      public void dataReformatted(final Layers theData,
          final Layer changedLayer)
      {
        if (changedLayer == _myRollingNarrative)
        {
          uiUpdate();
        }

        // do we have a rolling narrative?
        if (_myRollingNarrative != null)
        {
          // ok, try to refresh the colors based on the new data
          refreshColor(changedLayer);
        }
      }

      private void uiUpdate()
      {
        Display.getDefault().syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            setInput(_myRollingNarrative);
          }
        });
      }

    };

    // check if we have our rolling narrative listener
    _myRollingNarrListener = new INarrativeListener()
    {

      private final AtomicBoolean updatePending = new AtomicBoolean(false);

      @Override
      public void entryRemoved(final NarrativeEntry entry)
      {
        updated();
      }

      @Override
      public void filtered()
      {
        updated();
      }

      @Override
      public void newEntry(final NarrativeEntry entry)
      {
        updated();
      }

      /**
       * force UI update
       * 
       */
      private void updated()
      {
        // is there already a pending update?
        if (updatePending.get())
        {
          // hey, we don't need to fire another!
        }
        else
        {
          // ok, indicate that there is an update pending
          updatePending.set(true);

          // queue up a screen refresh
          Display.getDefault().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              // is there a pending UI update (and clear the flag)
              final boolean isPending = updatePending.getAndSet(false);

              if (isPending)
              {
                if (_myRollingNarrative != null && _myRollingNarrative
                    .size() > 0)
                {
                  myViewer.setInput(_myRollingNarrative);
                }
                else
                {
                  myViewer.setInput(null);
                }
              }
            }
          });
        }
      }
    };

  }

  /**
   * @param menuManager
   */
  private void addDateFormats(final IMenuManager menuManager)
  {
    // ok, second menu for the DTG formats
    final MenuManager formatMenu = new MenuManager("DTG Format");

    // and store it
    menuManager.add(formatMenu);

    // and now the date formats
    final String[] formats = DateFormatPropertyEditor.getTagList();
    for (int i = 0; i < formats.length; i++)
    {
      final String thisFormat = formats[i];

      // the properties manager is expecting the integer index of the new
      // format, not the string value.
      // so store it as an integer index
      final Integer thisIndex = new Integer(i);

      // and create a new action to represent the change
      final Action newFormat = new Action(thisFormat, IAction.AS_RADIO_BUTTON)
      {
        @Override
        public void run()
        {
          super.run();
          final String theFormat = DateFormatPropertyEditor
              .getTagList()[thisIndex];

          myViewer.setTimeFormatter(new TimeFormatter()
          {
            @Override
            public String format(final HiResDate time)
            {
              final String res = toStringHiRes(time, theFormat);
              return res;
            }
          });
        }

      };
      formatMenu.add(newFormat);
    }
  }

  protected void addMarker()
  {
    try
    {
      // right, do we have an editor with a file?
      final IEditorInput input = _currentEditor.getEditorInput();
      if (input instanceof IFileEditorInput)
      {
        // aaah, and is there a file present?
        final IFileEditorInput ife = (IFileEditorInput) input;
        final IResource file = ife.getFile();

        final StructuredSelection selection = myViewer.getSelection();
        if (selection.getFirstElement() instanceof NarrativeEntry)
        {

          final NarrativeEntry entry = (NarrativeEntry) selection
              .getFirstElement();
          final long tNow = entry.getDTG().getMicros();
          final String currentText = FormatDateTime.toString(tNow / 1000);
          if (file != null)
          {
            // yup, get the description
            final InputDialog inputD = new InputDialog(getViewSite().getShell(),
                "Add bookmark at this DTG",
                "Enter description of this bookmark", currentText, null);
            inputD.open();

            final String content = inputD.getValue();
            if (content != null)
            {
              final IMarker marker = file.createMarker(IMarker.BOOKMARK);
              final Map<String, Object> attributes =
                  new HashMap<String, Object>(4);
              attributes.put(IMarker.MESSAGE, content);
              attributes.put(IMarker.LOCATION, currentText);
              attributes.put(IMarker.LINE_NUMBER, "" + tNow);
              attributes.put(IMarker.USER_EDITABLE, Boolean.FALSE);
              marker.setAttributes(attributes);
            }
          }
        }

      }
    }
    catch (final CoreException e)
    {
      e.printStackTrace();
    }

  }

  @Override
  public void addSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    if (_selectionListeners == null)
    {
      _selectionListeners = new Vector<ISelectionChangedListener>(0, 1);
    }

    // see if we don't already contain it..
    if (!_selectionListeners.contains(listener))
    {
      _selectionListeners.add(listener);
    }
  }

  @Override
  public void createPartControl(final Composite parent)
  {

    _myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
        .getPartService());

    parent.setLayout(new GridLayout(1, false));
    final Composite rootPanel = new Composite(parent, SWT.BORDER);
    rootPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    rootPanel.setLayout(new GridLayout());

    myViewer = new NatNarrativeViewer(rootPanel, CorePlugin.getDefault()
        .getPreferenceStore());

    getSite().setSelectionProvider(this);

    // sort out the initial time format
    final String startFormat = DateFormatPropertyEditor.getTagList()[3];
    myViewer.setTimeFormatter(new TimeFormatter()
    {
      @Override
      public String format(final HiResDate time)
      {
        final String res = toStringHiRes(time, startFormat);
        return res;
      }
    });

    /**
     * sort out the view menu & toolbar
     * 
     */
    populateMenu();

    /**
     * and start listening out for new panels to open
     * 
     */
    setupPartListeners();
    myViewer.addDoubleClickListener(new NatDoubleClickListener()
    {

      @Override
      public void doubleClick(final ISelection iSelection)
      {
        final StructuredSelection selection = (StructuredSelection) iSelection;
        if (selection.getFirstElement() instanceof NarrativeEntry)
        {
          fireNewSeletion((NarrativeEntry) selection.getFirstElement());
        }

      }
    });

    _selectionChangeListener = new ISelectionChangedListener()
    {

      @Override
      public void selectionChanged(final SelectionChangedEvent event)
      {
        // right, see what it is
        final ISelection sel = event.getSelection();
        if (sel instanceof StructuredSelection)
        {
          final StructuredSelection ss = (StructuredSelection) sel;
          final Object datum = ss.getFirstElement();
          if (datum instanceof EditableWrapper)
          {
            final EditableWrapper pw = (EditableWrapper) datum;

            // now see if it's a narrative entry
            if (pw.getEditable() instanceof NarrativeEntry)
            {
              final NarrativeEntry entry = (NarrativeEntry) pw.getEditable();
              timeUpdated(entry.getDTG());
            }
          }
        }
      }
    };

    // listen out for the view resizing
    parent.addControlListener(new ControlAdapter()
    {
      @Override
      public void controlResized(final ControlEvent e)
      {
        myViewer.refresh();
      }
    });

  }

  @Override
  public void dispose()
  {

    // and stop listening for part activity
    _myPartMonitor.ditch();

    if (_controllableTime != null)
    {
      _controllableTime = null;
    }

    if (_myTemporalDataset != null)
    {
      _myTemporalDataset.removeListener(_temporalListener,
          TimeProvider.TIME_CHANGED_PROPERTY_NAME);
      _myTemporalDataset = null;
    }

    ditchOldLayers();
    // let the parent do it's bit
    super.dispose();

  }

  protected void ditchOldLayers()
  {
    if (_myLayers != null)
    {
      _myLayers.removeDataModifiedListener(_layerListener);
      _myLayers.removeDataReformattedListener(_layerListener);
      _myLayers.removeDataExtendedListener(_layerListener);
      _myLayers = null;

      setInput(null);
    }
  }

  protected void entryUpdated(final NarrativeEntry entry)
  {
    if (_amUpdating)
    {
      // don't worry, we'll be finished soon
      System.err.println("already doing update");
    }
    else
    {
      // ok, remember that we're updating
      _amUpdating = true;

      // get on with the update
      try
      {
        Display.getDefault().asyncExec(new Runnable()
        {

          @Override
          public void run()
          {
            // ok, tell the model to move to the relevant item
            myViewer.setEntry(entry);
          }
        });
      }
      finally
      {
        // clear the updating lock
        _amUpdating = false;
      }

    }
  }

  /**
   * send this new time to the time controller
   * 
   * @param newEntry
   */
  protected void fireNewSeletion(final NarrativeEntry newEntry)
  {
    // first update the time
    if (_controlTime.isChecked())
    {
      if (_controllableTime != null)
      {
        _controllableTime.setTime(this, newEntry.getDTG(), true);
      }
    }

    // now update the selection
    final EditableWrapper wrappedEntry = new EditableWrapper(newEntry);
    final StructuredSelection structuredItem = new StructuredSelection(
        wrappedEntry);
    setSelection(structuredItem);
  }

  @Override
  public ISelection getSelection()
  {
    return null;
  }

  private static boolean internalEquals(final Color color1, final Color color2)
  {
    if (color1 == null && color2 == null)
    {
      return true;
    }
    if (color1 != null)
    {
      return color1.equals(color2);
    }
    return color2.equals(color1);
  }

  private void populateMenu()
  {
    // clear the list
    final IMenuManager menuManager = getViewSite().getActionBars()
        .getMenuManager();
    final IToolBarManager toolManager = getViewSite().getActionBars()
        .getToolBarManager();

    final Action _newEntry = new Action("New Entry", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        createNewEntry();
      }
    };
    _newEntry.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/add.png"));
    _newEntry.setToolTipText("Create new narrative entry");
    toolManager.add(_newEntry);
    menuManager.add(_newEntry);

    final Action _search = new Action("Search", IAction.AS_CHECK_BOX)
    {

      @Override
      public void run()
      {
        myViewer.setSearchMode(isChecked());
      }
    };
    _search.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/search.png"));
    _search.setToolTipText("Toggle search mode");
    _search.setChecked(true);
    toolManager.add(_search);

    // the line below contributes the predefined viewer actions onto the
    // view action bar
    myViewer.fillActionBars(getViewSite().getActionBars());

    menuManager.add(new Separator());
    final Action editPhrases = new Action("Edit Highlight Phrases")
    {
      @Override
      public void run()
      {
        final PreferenceDialog dialog = PreferencesUtil
            .createPreferenceDialogOn(getSite().getShell(),
                "org.mwc.cmap.narratives.preferences.NarrativeViewerPrefsPage",
                null, null);
        if (dialog.open() == IDialogConstants.OK_ID)
        {
          myViewer.refresh();
        }
      }
    };
    editPhrases.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/properties.png"));
    menuManager.add(editPhrases);
    toolManager.add(editPhrases);

    final Action fontSize = new Action("Font Size")
    {
      @Override
      public void run()
      {
        final PreferenceDialog dialog = PreferencesUtil
            .createPreferenceDialogOn(getSite().getShell(),
                "org.mwc.cmap.narratives.preferences.NarrativeViewerPrefsPage",
                null, null);
        if (dialog.open() == IDialogConstants.OK_ID)
        {
          myViewer.refresh();
        }
      }
    };
    fontSize.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/font.png"));
    menuManager.add(fontSize);
    // and another separator
    menuManager.add(new Separator());

    // add some more actions
    _clipText = new Action("Wrap entry text", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        super.run();
        myViewer.setWrappingEntries(_clipText.isChecked());
      }
    };
    _clipText.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/wrap.png"));
    _clipText.setToolTipText("Whether to clip to visible space");
    _clipText.setChecked(true);

    menuManager.add(_clipText);
    toolManager.add(_clipText);

    _followTime = new Action("Follow current time", IAction.AS_CHECK_BOX)
    {
    };
    _followTime.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/follow_time.png"));
    _followTime.setToolTipText("Whether to listen to the time controller");
    _followTime.setChecked(true);

    menuManager.add(_followTime);

    _controlTime = new Action("Control current time", IAction.AS_CHECK_BOX)
    {
    };
    _controlTime.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/control_time.png"));
    _controlTime.setToolTipText("Whether to control the current time");
    _controlTime.setChecked(true);
    menuManager.add(_controlTime);

    // now the add-bookmark item
    _setAsBookmarkAction = new Action("Add DTG as bookmark",
        IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void runWithEvent(final Event event)
      {
        addMarker();
      }
    };
    _setAsBookmarkAction.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/add_bookmark.png"));
    _setAsBookmarkAction.setToolTipText(
        "Add this DTG to the list of bookmarks");
    menuManager.add(_setAsBookmarkAction);

    // and the DTG formatter
    addDateFormats(menuManager);

    menuManager.add(new Separator());
    menuManager.add(CorePlugin.createOpenHelpAction(
        "org.mwc.debrief.help.Narrative", null, this));

    Action fontPlus = new Action("+", IAction.AS_PUSH_BUTTON)
    {

      @Override
      public void run()
      {
        IPreferenceStore preferenceStore = CorePlugin.getDefault()
            .getPreferenceStore();

        final String fontStr = preferenceStore.getString(
            NarrativeViewerPrefsPage.PreferenceConstants.FONT);
        if (fontStr != null)
        {
          final FontData[] readFontData = PreferenceConverter.readFontData(
              fontStr);
          if (readFontData != null && readFontData.length > 0)
          {
            readFontData[0].setHeight((int) readFontData[0].height + 1);
            FontData[] bestFont = JFaceResources.getFontRegistry().filterData(
                readFontData, Display.getCurrent());
            if (bestFont != null)
              preferenceStore.setValue(
                  NarrativeViewerPrefsPage.PreferenceConstants.FONT,
                  org.eclipse.jface.resource.StringConverter.asString(
                      bestFont));

          }
        }
      }
    };
    fontPlus.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/increase.png"));
    fontPlus.setToolTipText("+");

    Action fontMin = new Action("-", IAction.AS_PUSH_BUTTON)
    {

      @Override
      public void run()
      {
        IPreferenceStore preferenceStore = CorePlugin.getDefault()
            .getPreferenceStore();

        final String fontStr = preferenceStore.getString(
            NarrativeViewerPrefsPage.PreferenceConstants.FONT);
        if (fontStr != null)
        {
          final FontData[] readFontData = PreferenceConverter.readFontData(
              fontStr);
          if (readFontData != null && readFontData.length > 0)
          {
            readFontData[0].setHeight((int) readFontData[0].height - 1);
            FontData[] bestFont = JFaceResources.getFontRegistry().filterData(
                readFontData, Display.getCurrent());
            if (bestFont != null)
              preferenceStore.setValue(
                  NarrativeViewerPrefsPage.PreferenceConstants.FONT,
                  org.eclipse.jface.resource.StringConverter.asString(
                      bestFont));

          }
        }
      }
    };
    fontMin.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/decrease.png"));
    fontMin.setToolTipText("-");

    toolManager.add(fontPlus);
    toolManager.add(fontMin);

  }

  protected void createNewEntry()
  {
    // check we've got a "real" narrative
    final NarrativeWrapper theNarrative;
    if (_myRollingNarrative instanceof NarrativeWrapper)
    {
      theNarrative = (NarrativeWrapper) _myRollingNarrative;
    }
    else
    {
      theNarrative = null;
    }

    // try to get the current plot date
    // ok, populate the data
    final IEditorPart curEditor = PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    HiResDate date;
    if (curEditor instanceof IAdaptable)
    {
      TimeProvider prov = (TimeProvider) curEditor.getAdapter(
          TimeProvider.class);
      if (prov != null)
      {
        date = prov.getTime();

        final NewNarrativeEntryWizard wizard = new NewNarrativeEntryWizard(
            date);

        final WizardDialog dialog = new WizardDialog(Display.getCurrent()
            .getActiveShell(), wizard);
        TrayDialog.setDialogHelpAvailable(true);
        dialog.setHelpAvailable(true);
        dialog.create();
        dialog.open();

        // did it work?
        if (dialog.getReturnCode() == WizardDialog.OK)
        {
          final NarrativeEntry ne = wizard.getEntry();
          // ok, go for it.
          // sort it out as an operation
          final IUndoableOperation addTheCut =
              new GenerateNewNarrativeEntry.AddNarrativeEntry(_myLayers,
                  theNarrative, ne);

          // ok, stick it on the buffer
          CorePlugin.run(addTheCut);
        }
      }
    }
  }

  /**
   * the user has selected a new time
   * 
   */
  @Override
  public void propertyChange(final PropertyChangeEvent evt)
  {
    // are we syncing with time?
    if (_followTime.isChecked())
    {

    }
  }

  private void refreshColor(final Layer layer)
  {
    if (layer instanceof TrackWrapper)
    {
      final TrackWrapper track = (TrackWrapper) layer;
      final String name = track.getName();
      final Color color = track.getColor();
      boolean refresh = false;
      final NarrativeEntry[] entries = _myRollingNarrative.getNarrativeHistory(
          new String[]
          {});
      for (final NarrativeEntry entry : entries)
      {
        if (entry.getTrackName() != null && entry.getTrackName().equals(name))
        {
          // special handling for rider narratives
          if (entry.getType() != null && entry.getType().equals(
              ImportRiderNarrativeDocument.RIDER_SOURCE))
          {
            // don't over-write the color. We leave rider narratives unchanged
          }
          else if (!internalEquals(color, entry.getColor()))
          {
            if (color == null)
            {
              entry.setColor(Color.DARK_GRAY);
            }
            else
            {
              entry.setColor(color);
            }
            refresh = true;
          }
        }
      }
      if (refresh)
      {
        myViewer.refresh();
      }
    }
  }

  private void refreshColors()
  {
    if (_myLayers == null)
    {
      return;
    }
    if (_myRollingNarrative == null)
    {
      return;
    }
    final Enumeration<Editable> elements = _myLayers.elements();

    while (elements.hasMoreElements())
    {
      final Editable element = elements.nextElement();
      if (element instanceof Layer)
      {
        refreshColor((Layer) element);
      }
    }

  }

  @Override
  public void removeSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    _selectionListeners.remove(listener);
  }

  @Override
  public void setFocus()
  {
    myViewer.getControl().setFocus();
  }

  protected void setInput(final IRollingNarrativeProvider newNarr)
  {

    if (newNarr != _myRollingNarrative)
    {
      if (_myRollingNarrative != null)
      {
        // clear what's displayed
        myViewer.setInput(null);

        // stop listening to old narrative
        _myRollingNarrative.removeNarrativeListener(
            IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
      }

      // ok remember the new provider (even if it's null)
      _myRollingNarrative = newNarr;

      // is the new one a real object?
      if (newNarr != null)
      {
        // ok, register as a listener
        _myRollingNarrative.addNarrativeListener(
            IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);

        // also sort out the colors
        refreshColors();

        // ok - show the narrative. We can't rely on
        // listening to the rolling narrative, since we
        // may be switching back to a previous plot.
        myViewer.setInput(_myRollingNarrative);
      }
    }
  }

  @Override
  public void setSelection(final ISelection selection)
  {
    // tell everybody about us
    for (final Iterator<ISelectionChangedListener> iterator =
        _selectionListeners.iterator(); iterator.hasNext();)
    {
      final ISelectionChangedListener type = iterator.next();
      final SelectionChangedEvent event = new SelectionChangedEvent(this,
          selection);
      type.selectionChanged(event);
    }
  }

  /**
   * 
   */
  private void setupPartListeners()
  {

    final NATViewerView me = this;

    // //////////////////////////////////////////
    // and the layers - to hear about refresh
    // //////////////////////////////////////////

    _myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final Layers layer = (Layers) part;
            if (layer != _myLayers)
            {
              // ditch to old layers
              ditchOldLayers();

              _myLayers = layer;

              // and sort out the listeners
              _myLayers.addDataModifiedListener(_layerListener);
              _myLayers.addDataReformattedListener(_layerListener);
              _myLayers.addDataExtendedListener(_layerListener);

              // fire the extended listener once, just in case
              // we need to empty the narrative
              _layerListener.dataExtended(_myLayers);
            }
          }

        });

    _myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final Layers layer = (Layers) part;
            if (layer == _myLayers)
            {
              // ditch to old layers
              ditchOldLayers();
            }
          }
        });

    // ///////////////////////////////////////////////
    // now for time provider support
    // ///////////////////////////////////////////////
    _myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {

          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // just check we're not already looking at it
            if (part != _myTemporalDataset)
            {
              // ok, better stop listening to the old one
              if (_myTemporalDataset != null)
              {
                // yup, better ignore it
                _myTemporalDataset.removeListener(_temporalListener,
                    TimeProvider.TIME_CHANGED_PROPERTY_NAME);

                _myTemporalDataset = null;
              }

              // implementation here.
              _myTemporalDataset = (TimeProvider) part;
              if (_temporalListener == null)
              {
                _temporalListener = new PropertyChangeListener()
                {
                  @Override
                  public void propertyChange(final PropertyChangeEvent event)
                  {
                    // ok, use the new time
                    final HiResDate newDTG = (HiResDate) event.getNewValue();
                    timeUpdated(newDTG);
                  }
                };
              }
              _myTemporalDataset.addListener(_temporalListener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);

              // and is it an editor we want to remember?
              // hmm, do we want to store this part?
              if (parentPart instanceof IEditorPart)
              {
                _currentEditor = (IEditorPart) parentPart;
              }
            }
          }
        });
    _myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            if (part == _myTemporalDataset)
            {
              _myTemporalDataset.removeListener(_temporalListener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);

              // and clear the pointer
              _myTemporalDataset = null;
            }
          }
        });
    _myPartMonitor.addPartListener(ControllableTime.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // implementation here.
            _controllableTime = (ControllableTime) part;
          }
        });
    _myPartMonitor.addPartListener(ControllableTime.class,
        PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // no, don't bother clearing the controllable time when
            // the plot is
            // de-activated,
            // - since with the highlight on the narrative, we want
            // to be able
            // to control the time still.
            // _controllableTime = null;
          }
        });

    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // aah, just check it's not is
            if (part != me)
            {
              final ISelectionProvider iS = (ISelectionProvider) part;
              iS.addSelectionChangedListener(_selectionChangeListener);
            }
          }
        });
    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // aah, just check it's not is
            if (part != me)
            {
              final ISelectionProvider iS = (ISelectionProvider) part;
              iS.removeSelectionChangedListener(_selectionChangeListener);
            }
          }
        });

    // ok we're all ready now. just try and see if the current part is valid
    _myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
        .getActivePage());
  }

  protected void timeUpdated(final HiResDate dtg)
  {
    if (_followTime.isChecked())
    {
      if (!_amUpdating)
      {
        // ok, remember that we're updating
        _amUpdating = true;

        // remember the new one
        _pendingTime.set(dtg.getMicros());

        // get on with the update
        try
        {
          Display.getDefault().asyncExec(new Runnable()
          {

            @Override
            public void run()
            {
              // quick, capture the time
              final long safeTime = _pendingTime.get();

              // do we have a pending time value
              if (safeTime != INVALID_TIME)
              {
                _pendingTime.set(INVALID_TIME);

                // now create the time object
                final HiResDate theDTG = new HiResDate(0, safeTime);

                // ok, tell the model to move to the relevant item
                myViewer.setDTG(theDTG);
              }
              else
              {
                // ok, there isn't a pending date, we can just skip the update
              }

              // Note: we don't need to clear the lock, we do it in the finally block
            }
          });
        }
        finally
        {
          // clear the updating lock
          _amUpdating = false;
        }
      }
    }
  }

}