package info.limpet.stackedcharts.ui.view;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.ui.editor.Activator;
import info.limpet.stackedcharts.ui.editor.StackedchartsEditControl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedTimeListener;
import info.limpet.stackedcharts.ui.view.adapter.IStackedTimeProvider;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionListener;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.experimental.chart.swt.ChartComposite;

public class StackedChartsView extends ViewPart implements
    ITabbedPropertySheetPageContributor, ISelectionProvider, DisposeListener,
    IStackedTimeListener
{

  /**
   * interface for external objects that are able to supply a date and resond to a new date
   * 
   * @author ian
   * 
   */
  public static interface ControllableDate
  {
    /**
     * retrieve the date
     * 
     * @return current date
     */
    Date getDate();

    /**
     * control the date
     * 
     * @param time
     */
    void setDate(Date date);
  }

  private static final int MARKER_STEP_SIZE = 1;

  public static final String STACKED_CHARTS_CONFIG = "StackedCharts";
  public static final int CHART_VIEW = 1;

  public static final int EDIT_VIEW = 2;

  public static final String ID = "info.limpet.StackedChartsView";

  private static final String TIME_PROVIDER_ID = "stacked_time_provider";

  private StackedPane stackedPane;
  // effects
  protected TransitionManager transitionManager = null;
  private Composite chartHolder;
  private Composite editorHolder;
  private ChartSet charts;

  private final AtomicBoolean initEditor = new AtomicBoolean(true);

  private StackedchartsEditControl chartEditor;
  private final List<ISelectionChangedListener> selectionListeners =
      new ArrayList<ISelectionChangedListener>();
  private Date _currentTime;
  private ChartComposite _chartComposite;
  private ArrayList<Runnable> _closeCallbacks;
  private ControllableDate _controllableDate = null;

  private JFreeChart jFreeChart;

  /**
   * flag for if we're currently in update
   * 
   */
  private static boolean _amUpdating = false;

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
   * let classes pass callbacks to be run when we are closing
   * 
   * @param runnable
   */
  public void addRunOnCloseCallback(final Runnable runnable)
  {
    if (_closeCallbacks == null)
    {
      _closeCallbacks = new ArrayList<Runnable>();
    }
    _closeCallbacks.add(runnable);
  }

  @Override
  public void addSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    selectionListeners.add(listener);

  }

  /**
   * convenience method to make the value marker labels larger or smaller
   * 
   * @param up
   */
  private void changeFontSize(final Boolean up)
  {
    // are we making a change?
    final float change;
    if (up != null)
    {
      // sort out which direction we're changing
      if (up)
      {
        change = MARKER_STEP_SIZE;
      }
      else
      {
        change = -MARKER_STEP_SIZE;
      }

    }
    else
    {
      change = 0;
    }

    // do we have a default?
    final float curSize =
        Activator.getDefault().getPreferenceStore().getFloat(
            TimeBarPlot.CHART_FONT_SIZE_NODE);

    // trim to reasonable size
    final float sizeToUse = Math.max(9, curSize);

    // produce new size
    final float newVal = sizeToUse + change;

    // store the new size
    Activator.getDefault().getPreferenceStore().setValue(
        TimeBarPlot.CHART_FONT_SIZE_NODE, newVal);

    final float base = newVal;

    final StandardChartTheme theme =
        (StandardChartTheme) StandardChartTheme.createJFreeTheme();

    theme.setRegularFont(theme.getRegularFont().deriveFont(base * 1.0f));
    theme.setExtraLargeFont(theme.getExtraLargeFont().deriveFont(base * 1.4f));
    theme.setLargeFont(theme.getLargeFont().deriveFont(base * 1.2f));
    theme.setSmallFont(theme.getSmallFont().deriveFont(base * 0.8f));
    theme.setChartBackgroundPaint(Color.white);
    theme.setPlotBackgroundPaint(Color.white);
    theme.setGridBandPaint(Color.lightGray);
    theme.setDomainGridlinePaint(Color.lightGray);
    theme.setRangeGridlinePaint(Color.lightGray);

    theme.apply(jFreeChart);
  }

  protected void connectFileDropSupport(final Control compoent)
  {
    final DropTarget target =
        new DropTarget(compoent, DND.DROP_MOVE | DND.DROP_COPY
            | DND.DROP_DEFAULT);
    final FileTransfer fileTransfer = FileTransfer.getInstance();
    target.setTransfer(new Transfer[]
    {fileTransfer});
    target.addDropListener(new DropTargetListener()
    {
      @Override
      public void dragEnter(final DropTargetEvent event)
      {
        if (event.detail == DND.DROP_DEFAULT)
        {
          if ((event.operations & DND.DROP_COPY) != 0)
          {
            event.detail = DND.DROP_COPY;
          }
          else
          {
            event.detail = DND.DROP_NONE;
          }
        }
        for (int i = 0; i < event.dataTypes.length; i++)
        {
          if (fileTransfer.isSupportedType(event.dataTypes[i]))
          {
            event.currentDataType = event.dataTypes[i];
            // files should only be copied
            if (event.detail != DND.DROP_COPY)
            {
              event.detail = DND.DROP_NONE;
            }
            break;
          }
        }
      }

      @Override
      public void dragLeave(final DropTargetEvent event)
      {
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent event)
      {
        if (event.detail == DND.DROP_DEFAULT)
        {
          if ((event.operations & DND.DROP_COPY) != 0)
          {
            event.detail = DND.DROP_COPY;
          }
          else
          {
            event.detail = DND.DROP_NONE;
          }
        }
        if (fileTransfer.isSupportedType(event.currentDataType))
        {
          if (event.detail != DND.DROP_COPY)
          {
            event.detail = DND.DROP_NONE;
          }
        }
      }

      @Override
      public void dragOver(final DropTargetEvent event)
      {
      }

      @Override
      public void drop(final DropTargetEvent event)
      {
        if (fileTransfer.isSupportedType(event.currentDataType))
        {
          final String[] files = (String[]) event.data;

          // *.stackedcharts
          if (files.length == 1 && files[0].endsWith("stackedcharts"))
          {
            final File file = new File(files[0]);
            final Resource resource =
                new ResourceSetImpl().createResource(URI.createURI(file.toURI()
                    .toString()));
            try
            {
              resource.load(new HashMap<>());
              final ChartSet chartsSet =
                  (ChartSet) resource.getContents().get(0);
              setModel(chartsSet);
            }
            catch (final IOException e)
            {
              e.printStackTrace();
              MessageDialog.openError(Display.getCurrent().getActiveShell(),
                  "Error", e.getMessage());
            }
          }
        }
      }

      @Override
      public void dropAccept(final DropTargetEvent event)
      {
      }
    });
  }

  protected void contributeToActionBars()
  {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected Control createChartView()
  {
    // defer creation of the actual chart until we receive
    // some model data. So, just have an empty panel
    // to start with
    chartHolder = new Composite(stackedPane, SWT.NONE);
    chartHolder.setLayout(new FillLayout());
    return chartHolder;
  }

  protected Control createEditView()
  {
    editorHolder = new Composite(stackedPane, SWT.NONE);
    editorHolder.setLayout(new FillLayout());
    // create gef base editor
    chartEditor = new StackedchartsEditControl(editorHolder);
    // proxy editor selection to view site
    chartEditor.getViewer().addSelectionChangedListener(
        new ISelectionChangedListener()
        {
          @Override
          public void selectionChanged(final SelectionChangedEvent event)
          {
            fireSelectionChnaged();

          }
        });
    return editorHolder;
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    getViewSite().setSelectionProvider(this);// setup proxy selection provider
    stackedPane = new StackedPane(parent);

    // note: The "Show in ..." action specifies a unique secondary id,
    // which it uses to force a new instance. Hence, if a secondary
    // id isn't provided we presume a blank chart is being requested
    String secondaryId = ((IViewSite) getSite()).getSecondaryId();
    if (secondaryId != null)
    {
      stackedPane.add(CHART_VIEW, createChartView());
      stackedPane.add(EDIT_VIEW, createEditView());

      selectView(CHART_VIEW);
    }
    else
    {
      // blank view
      // order is different
      stackedPane.add(EDIT_VIEW, createEditView());
      stackedPane.add(CHART_VIEW, createChartView());

      ChartSet blankModel = createBlankModel();
      setModel(blankModel, EDIT_VIEW);
    }
    contributeToActionBars();

    chartEditor.init(this);

    // Drop Support for *.stackedcharts
    connectFileDropSupport(stackedPane);
    final boolean IS_LINUX_OS =
        System.getProperty("os.name").toLowerCase().indexOf("nux") >= 0;
    final Image[] compImage = new Image[2]; // stackedPane comp count
    parent.addDisposeListener(new DisposeListener()
    {

      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        for (Image img : compImage)
        {
          if (img != null)
          {
            img.dispose();
          }
        }

      }
    });
    final Transitionable transitionable;
    transitionManager =
        new TransitionManager(transitionable = new Transitionable()
        {

          @Override
          public void addSelectionListener(final SelectionListener listener)
          {
            stackedPane.addSelectionListener(listener);
          }

          @Override
          public Composite getComposite()
          {
            return stackedPane;
          }

          @Override
          public Control getControl(final int index)
          {
            return stackedPane.getControl(index);
          }

          @Override
          public double getDirection(final int toIndex, final int fromIndex)
          {
            return toIndex == CHART_VIEW ? Transition.DIR_RIGHT
                : Transition.DIR_LEFT;
          }

          @Override
          public int getSelection()
          {
            return stackedPane.getActiveControlKey();
          }

          @Override
          public void setSelection(final int index)
          {
            stackedPane.showPane(index, false);
          }

        })
        {

          @Override
          public void startTransition(int fromIndex, int toIndex,
              double direction)
          {
            if (IS_LINUX_OS)
            {
              Control from = transitionable.getControl(fromIndex);
              Rectangle fromSize = from.getBounds();
              Image imgFrom =
                  new Image(from.getDisplay(), fromSize.width, fromSize.height);
              GC gcfrom = new GC(from);
              from.update();
              gcfrom.copyArea(imgFrom, 0, 0);
              if (compImage[fromIndex - 1] != null)
              {
                compImage[fromIndex - 1].dispose();
              }
              compImage[fromIndex - 1] = imgFrom;
              gcfrom.dispose();
            }

            super.startTransition(fromIndex, toIndex, direction);
          }

        };
    transitionManager.addTransitionListener(new TransitionListener()
    {

      @Override
      public void transitionFinished(final TransitionManager arg0)
      {
        stackedPane.completeSelection();

      }
    });
    transitionManager.setControlImages(compImage);
    // new SlideTransition(_tm)
    transitionManager.setTransition(new CubicRotationTransition(
        transitionManager));

    // listen out for closing
    parent.addDisposeListener(this);

    // and remember to detach ourselves
    final DisposeListener meL = this;
    final Runnable dropMe = new Runnable()
    {
      @Override
      public void run()
      {
        parent.removeDisposeListener(meL);
      }
    };
    addRunOnCloseCallback(dropMe);

    // ok, see if we have a time controller
    findTimeController();
  }

  private void findTimeController()
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    if (registry != null)
    {

      final IExtensionPoint point =
          Platform.getExtensionRegistry().getExtensionPoint(
              Activator.PLUGIN_ID, TIME_PROVIDER_ID);

      final IExtension[] extensions = point.getExtensions();
      for (int i = 0; i < extensions.length; i++)
      {
        final IExtension iExtension = extensions[i];
        final IConfigurationElement[] confE =
            iExtension.getConfigurationElements();
        for (IConfigurationElement extension : confE)
        {
          try
          {
            final IStackedTimeProvider provider =
                (IStackedTimeProvider) extension
                    .createExecutableExtension("class");
            final IStackedTimeListener listener = this;

            // ok, can it provide time control?
            if (provider.canProvideControl())
            {
              provider.controlThis(listener);

              // ok, remember that we need to close this
              addRunOnCloseCallback(new Runnable()
              {
                @Override
                public void run()
                {
                  provider.releaseThis(listener);
                }
              });
            }
          }
          catch (final CoreException ex)
          {
            ex.printStackTrace();
          }
        }
      }
    }

    //
    //
    // IConfigurationElement[] config =
    // Platform.getExtensionRegistry().getConfigurationElementsFor(
    // TIME_PROVIDER_ID);
    // for (IConfigurationElement e : config)
    // {
    // Object o;
    // try
    // {
    // o = e.createExecutableExtension("class");
    // if (o instanceof IStackedTimeProvider)
    // {
    // final IStackedTimeProvider sa = (IStackedTimeProvider) o;
    // final IStackedTimeListener listener = this;
    //
    // // ok, can it provide time control?
    // if(sa.canProvideControl())
    // {
    // sa.controlThis(listener);
    //
    // // ok, remember that we need to close this
    // addRunOnCloseCallback(new Runnable()
    // {
    // @Override
    // public void run()
    // {
    // sa.releaseThis(listener);
    // }
    // });
    // }
    // }
    // }
    // catch (CoreException e1)
    // {
    // e1.printStackTrace();
    // }
    // }
  }

  /**
   * Creates an Chart Set with a single chart so that user would be able to drop datasets in it.
   * 
   * @return
   */
  private ChartSet createBlankModel()
  {
    ChartSet chartSet = StackedchartsFactory.eINSTANCE.createChartSet();
    chartSet.getCharts().add(StackedchartsFactory.eINSTANCE.createChart());
    IndependentAxis independentAxis =
        StackedchartsFactory.eINSTANCE.createIndependentAxis();
    independentAxis
        .setAxisType(StackedchartsFactory.eINSTANCE.createDateAxis());
    chartSet.setSharedAxis(independentAxis);
    return chartSet;
  }

  protected void fillLocalPullDown(final IMenuManager manager)
  {
  }

  protected void fillLocalToolBar(final IToolBarManager manager)
  {
    String actionText =
        stackedPane.getActiveControlKey() == CHART_VIEW ? "Edit" : "View";
    Action toggleViewModeAction = new Action(actionText, SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        if (stackedPane.getActiveControlKey() == CHART_VIEW)
        {
          selectView(EDIT_VIEW);
          setText("View");
          manager.update(true);
        }
        else
        {
          // recreate the model
          // TODO: let's not re-create the model each time we revert
          // to the view mode. let's create listeners, so the
          // chart has discrete updates in response to
          // model changes

          // double check we have a charts model
          if (charts != null)
          {
            setModel(charts);
          }

          selectView(CHART_VIEW);
          setText("Edit");
          manager.update(true);
        }
      }
    };
    manager.add(toggleViewModeAction);

    final Action showTime = new Action("Show time marker", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        // ok, trigger graph redraw
        final JFreeChart combined = _chartComposite.getChart();
        final TimeBarPlot plot = (TimeBarPlot) combined.getPlot();
        plot._showLine = isChecked();

        // ok, trigger ui update
        refreshPlot();
      }
    };
    showTime.setChecked(true);
    showTime.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/clock.png"));
    manager.add(showTime);

    final Action showMarker = new Action("Show marker value", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        // ok, trigger graph redraw
        final JFreeChart combined = _chartComposite.getChart();
        final TimeBarPlot plot = (TimeBarPlot) combined.getPlot();
        plot._showLabels = isChecked();

        // ok, trigger ui update
        refreshPlot();
      }
    };
    showMarker.setChecked(true);
    showMarker.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/labels.png"));
    manager.add(showMarker);

    final Action export = new Action("Export image to clipboard", SWT.PUSH)
    {
      @Override
      public void run()
      {
        toWMF();
      }

      private void toWMF()
      {
        try
        {
          final Clipboard clpbrd =
              Toolkit.getDefaultToolkit().getSystemClipboard();
          clpbrd.setContents(new DrawableWMFTransfer(
              _chartComposite.getChart(), _chartComposite.getBounds()), null);
          MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
              "Image Export", "Exported to Clipboard in WMF && PDF format");

        }
        catch (final Exception e)
        {
          e.printStackTrace();
        }
      }

    };
    export.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/export_wmf.png"));
    manager.add(export);

    final Action sizeDown = new Action("-", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        changeFontSize(false);
      }
    };
    sizeDown.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/decrease.png"));
    sizeDown.setDescription("Decrease font size");
    manager.add(sizeDown);

    final Action sizeUp = new Action("+", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        changeFontSize(true);
      }
    };
    sizeUp.setDescription("Increase font size");
    sizeUp.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/increase.png"));
    manager.add(sizeUp);

  }

  /**
   * View Selection provider where it proxy between selected view
   */
  protected void fireSelectionChnaged()
  {
    final ISelection selection = getSelection();
    for (final ISelectionChangedListener listener : new ArrayList<>(
        selectionListeners))
    {
      listener.selectionChanged(new SelectionChangedEvent(this, selection));
    }
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Class type)
  {
    if (type == CommandStack.class)
    {
      return chartEditor.getViewer().getEditDomain().getCommandStack();
    }
    if (type == IPropertySheetPage.class)
    {
      return chartEditor.getPropertySheetPage();
    }
    return super.getAdapter(type);
  }

  /**
   * accessor, to be used in exporting the image
   * 
   * @return
   */
  public ChartComposite getChartComposite()
  {
    return _chartComposite;
  }

  @Override
  public String getContributorId()
  {
    return getViewSite().getId();
  }

  @Override
  public ISelection getSelection()
  {
    if (!initEditor.get() && stackedPane.getActiveControlKey() == EDIT_VIEW)
    {
      return chartEditor.getSelection();
    }
    // if chart view need to provide selection info via properties view, change empty selection to
    // represent object of chart view selection.
    return new StructuredSelection();// empty selection
  }

  protected void handleDoubleClick(final int x, final int y)
  {
    // retrieve the data location
    final Rectangle dataArea = _chartComposite.getScreenDataArea();
    final Rectangle2D d2 =
        new Rectangle2D.Double(dataArea.x, dataArea.y, dataArea.width,
            dataArea.height);
    final TimeBarPlot plot = (TimeBarPlot) _chartComposite.getChart().getPlot();
    final double chartX =
        plot.getDomainAxis().java2DToValue(x, d2, plot.getDomainAxisEdge());

    // do we have a date to control?
    if (_controllableDate != null)
    {
      // ok, update it
      _controllableDate.setDate(new Date((long) chartX));
    }
  }

  private void initEditorViewModel()
  {
    if (initEditor.getAndSet(false))
    {
      chartEditor.setModel(charts);
    }
    editorHolder.pack(true);
    editorHolder.getParent().layout();
  }

  private void refreshPlot()
  {
    final Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        if (_chartComposite != null && !_chartComposite.isDisposed())
        {
          final JFreeChart c = _chartComposite.getChart();
          if (c != null)
          {
            c.setNotify(true);
          }
        }
      }
    };
    if (Display.getCurrent() != null)
    {
      runnable.run();
    }
    else
    {
      Display.getDefault().syncExec(runnable);
    }
  }

  @Override
  public void removeSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    selectionListeners.remove(listener);
  }

  public void selectView(final int view)
  {
    if (stackedPane != null && !stackedPane.isDisposed())
    {
      // if switch to edit mode make sure to init editor model
      if (view == EDIT_VIEW)
      {
        initEditorViewModel();
      }
      stackedPane.showPane(view);
      // fire selection change to refresh properties view
      fireSelectionChnaged();
    }
  }

  public void setDateSupport(final ControllableDate controllableDate)
  {
    _controllableDate = controllableDate;

    if (_controllableDate != null)
    {
      final Date theDate = _controllableDate.getDate();
      if (theDate != null)
      {
        updateTime(theDate);
      }
    }
  }

  @Override
  public void setFocus()
  {
    if (stackedPane != null && !stackedPane.isDisposed())
    {
      stackedPane.forceFocus();
    }
  }

  public void setModel(final ChartSet charts)
  {
    setModel(charts, CHART_VIEW);
  }

  public void setModel(final ChartSet charts, int mode)
  {
    this.charts = charts;
    // mark editor to recreate
    initEditor.set(true);

    // remove any existing base items on view holder
    if (chartHolder != null)
    {
      for (final Control control : chartHolder.getChildren())
      {
        control.dispose();
      }
    }

    // and now repopulate
    jFreeChart = ChartBuilder.build(charts, _controllableDate);

    // initialise the theme
    changeFontSize(null);

    _chartComposite =
        new ChartComposite(chartHolder, SWT.NONE, jFreeChart, 400, 600, 300,
            200, 1800, 1800, true, false, true, true, true, true)
        {

          @Override
          public void mouseUp(final MouseEvent event)
          {
            super.mouseUp(event);
            final JFreeChart c = getChart();
            if (c != null)
            {
              c.setNotify(true); // force redraw
            }

            if (event.count == 2)
            {
              handleDoubleClick(event.x, event.y);
            }
          }
        };
    jFreeChart.setAntiAlias(false);

    // try the double-click handler
    _chartComposite.addMouseListener(new MouseListener()
    {

      @Override
      public void mouseDoubleClick(final MouseEvent e)
      {
        System.out.println("double-click at:" + e);
      }

      @Override
      public void mouseDown(final MouseEvent e)
      {
        System.out.println("down at:" + e);
      }

      @Override
      public void mouseUp(final MouseEvent e)
      {
        System.out.println("up at:" + e);
      }
    });

    chartHolder.pack(true);
    chartHolder.getParent().layout();
    selectView(mode);
  }

  @Override
  public void setSelection(final ISelection selection)
  {
    if (!initEditor.get())
    {
      chartEditor.getViewer().setSelection(selection);
    }
  }

  /**
   * update (or clear) the displayed time marker
   * 
   * @param newTime
   */
  @Override
  public void updateTime(final Date newTime)
  {
    final Date oldTime = _currentTime;
    _currentTime = newTime;

    if (newTime != null && !newTime.equals(oldTime) || newTime != oldTime)
    {

      if (!_amUpdating)
      {
        // ok, remember that we're updating
        _amUpdating = true;

        // remember the new one
        _pendingTime.set(newTime.getTime());

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
                final Date theDTG = new Date(safeTime);

                // try to get the time aware plot, if we have one.
                // it may be null if we're blank.
                if (_chartComposite != null)
                {
                  final JFreeChart combined = _chartComposite.getChart();
                  final TimeBarPlot plot = (TimeBarPlot) combined.getPlot();
                  plot.setTime(theDTG);

                  // ok, trigger ui update
                  refreshPlot();
                }
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

  @Override
  public void widgetDisposed(final DisposeEvent e)
  {
    if (_closeCallbacks != null)
    {
      for (final Runnable callback : _closeCallbacks)
      {
        callback.run();
      }
    }

    // and remove ourselves from our parent

  }
}
