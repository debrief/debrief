package com.borlander.ianmayo.nviewer.app;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.TimeZone;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.ui_support.PartMonitor;

import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.IRollingNarrativeProvider.INarrativeListener;

import com.borlander.ianmayo.nviewer.Column;
import com.borlander.ianmayo.nviewer.ColumnFilter;
import com.borlander.ianmayo.nviewer.NarrativeViewer;
import com.borlander.ianmayo.nviewer.model.TimeFormatter;

public class NViewerView extends ViewPart
{
    public static final String VIEW_ID = "com.borlander.ianmayo.nviewer.app.view";

    private NarrativeViewer myViewer;

    private Composite myButtonPanel;

    private IRollingNarrativeProvider myCurrentInput = null;

    /**
     * helper application to help track creation/activation of new plots
     */
    private PartMonitor _myPartMonitor;
    private IRollingNarrativeProvider _myRollingNarrative;

    protected INarrativeListener _myRollingNarrListener;

    private Action _clipText;

    public void createPartControl(Composite parent)
    {

        _myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
                .getPartService());

        parent.setLayout(new GridLayout(1, false));
        Composite rootPanel = new Composite(parent, SWT.BORDER);
        rootPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        StackLayout rootPanelLayout = new StackLayout();
        rootPanel.setLayout(rootPanelLayout);

        myViewer = new NarrativeViewer(rootPanel, Activator.getInstance()
                .getPreferenceStore());
        rootPanelLayout.topControl = myViewer;
        
        // sort out the initial time format
        final String startFormat = DateFormatPropertyEditor.getTagList()[3];
        myViewer.setTimeFormatter(new TimeFormatter(){
                public String format(HiResDate time)
                {
                    String res = toStringHiRes(time,
                            startFormat);
                    return res;
                }
            });            
        

        /**
         * sort out the view menu & toolbar
         * 
         */
        populateMenu();

        /** and start listening out for new panels to open
         * 
         */
        setupPartListeners();
    }

    private void populateMenu()
    {
        // clear the list
        final IMenuManager menuManager = getViewSite().getActionBars()
                .getMenuManager();
        final IToolBarManager toolManager = getViewSite().getActionBars()
                .getToolBarManager();

        // the line below contributes the predefined viewer actions onto the
        // view action bar
        myViewer.getViewerActions().fillActionBars(
                getViewSite().getActionBars());
        
        // and another separator
        menuManager.add(new Separator());

        // add some more actions
        _clipText = new Action("Wrap entry text", Action.AS_CHECK_BOX)
        {
            public void run()
            {
                super.run();
                myViewer.setWrappingEntries(_clipText.isChecked());
            }
        };
        _clipText.setImageDescriptor(org.mwc.cmap.core.CorePlugin.getImageDescriptor("icons/wrap.gif"));
        _clipText
                .setToolTipText("Indicate whether to clip to visible space");
        _clipText.setChecked(true);

        menuManager.add(_clipText);
        toolManager.add(_clipText);

        // and the DTG formatter
        addDateFormats(menuManager);

    }

    /**
     * @param menuManager
     */
    private void addDateFormats(final IMenuManager menuManager)
    {
        // ok, second menu for the DTG formats
        MenuManager formatMenu = new MenuManager("DTG Format");

        // and store it
        menuManager.add(formatMenu);

        // and now the date formats
        String[] formats = DateFormatPropertyEditor.getTagList();
        for (int i = 0; i < formats.length; i++)
        {
            final String thisFormat = formats[i];

            // the properties manager is expecting the integer index of the new
            // format, not the string value.
            // so store it as an integer index
            final Integer thisIndex = new Integer(i);

            // and create a new action to represent the change
            Action newFormat = new Action(thisFormat, Action.AS_RADIO_BUTTON)
            {
                public void run()
                {
                    super.run();
                    final String theFormat = DateFormatPropertyEditor
                            .getTagList()[thisIndex];

                    myViewer.setTimeFormatter(new TimeFormatter()
                    {
                        public String format(HiResDate time)
                        {
                            String res = toStringHiRes(time,
                                    theFormat);
                            return res;
                        }
                    });
                }

            };
            formatMenu.add(newFormat);
        }
    }

    private abstract class TestButton
    {
        private final Button myButton;

        public TestButton(String caption)
        {
            myButton = new Button(myButtonPanel, SWT.NONE);
            myButton.setText(caption);
            myButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,
                    false));
            myButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    onClick();
                }
            });
        }

        abstract protected void onClick();
    }

    public void setFocus()
    {
        myViewer.setFocus();
    }

    protected void setInput(IRollingNarrativeProvider newNarr,
            IWorkbenchPart parentPart)
    {
        _myRollingNarrative = newNarr;
        myViewer.setInput(_myRollingNarrative);

        // check if we have our rolling narrative listener
        if (_myRollingNarrListener == null)
        {
            _myRollingNarrListener = new INarrativeListener()
            {
                public void newEntry(final NarrativeEntry entry)
                {
                    Display.getDefault().asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            // ok, sort it.
                            // viewer.add(entry);
                            System.err
                                    .println("should be updating with new narrative entry!!!");
                        }
                    });
                }
            };
        }
        // and start listening to it..
        _myRollingNarrative.addNarrativeListener(
                IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
    }

    /**
     * 
     */
    private void setupPartListeners()
    {
        _myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
                PartMonitor.ACTIVATED, new PartMonitor.ICallback()
                {
                    public void eventTriggered(String type, Object part,
                            IWorkbenchPart parentPart)
                    {
                        IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
                        setInput(newNarr, parentPart);
                    }
                });

        // unusually, we are also going to track the open event for narrative
        // data
        // so that we can start off with some data
        _myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
                PartMonitor.OPENED, new PartMonitor.ICallback()
                {
                    public void eventTriggered(String type, Object part,
                            IWorkbenchPart parentPart)
                    {
                        IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
                        setInput(newNarr, parentPart);
                    }

                });

        _myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
                PartMonitor.CLOSED, new PartMonitor.ICallback()
                {
                    public void eventTriggered(String type, Object part,
                            IWorkbenchPart parentPart)
                    {
                        IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
                        if (newNarr == _myRollingNarrative)
                        {
                            // stop listening to old narrative
                            _myRollingNarrative.removeNarrativeListener(
                                    IRollingNarrativeProvider.ALL_CATS,
                                    _myRollingNarrListener);
                            myViewer.setInput(null);
                            _myRollingNarrative = null;
                        }
                    }
                });

        // _myPartMonitor.addPartListener(TimeProvider.class,
        // PartMonitor.ACTIVATED,
        // new PartMonitor.ICallback()
        // {
        // public void eventTriggered(String type, Object part, IWorkbenchPart
        // parentPart)
        // {
        // // just check we're not already looking at it
        // if (part != _myTemporalDataset)
        // {
        // // ok, better stop listening to the old one
        // if (_myTemporalDataset != null)
        // {
        // // yup, better ignore it
        // _myTemporalDataset.removeListener(_temporalListener,
        // TimeProvider.TIME_CHANGED_PROPERTY_NAME);
        //
        // _myTemporalDataset = null;
        // }
        //
        // // implementation here.
        // _myTemporalDataset = (TimeProvider) part;
        // if (_temporalListener == null)
        // {
        // _temporalListener = new PropertyChangeListener()
        // {IEntryWrapper
        // public void propertyChange(PropertyChangeEvent event)
        // {
        // // ok, use the new time
        // HiResDate newDTG = (HiResDate) event.getNewValue();
        // timeUpdated(newDTG);
        // }
        // };
        // }
        // _myTemporalDataset.addListener(_temporalListener,
        // TimeProvider.TIME_CHANGED_PROPERTY_NAME);
        // }
        // }
        // });
        // _myPartMonitor.addPartListener(TimeProvider.class,
        // PartMonitor.CLOSED,
        // new PartMonitor.ICallback()
        // {
        // public void eventTriggered(String type, Object part, IWorkbenchPart
        // parentPart)
        // {
        // if (part == _myTemporalDataset)
        // {
        // _myTemporalDataset.removeListener(_temporalListener,
        // TimeProvider.TIME_CHANGED_PROPERTY_NAME);
        // }
        // }
        // });
        // _myPartMonitor.addPartListener(ControllableTime.class,
        // PartMonitor.ACTIVATED,
        // new PartMonitor.ICallback()
        // {
        // public void eventTriggered(String type, Object part, IWorkbenchPart
        // parentPart)
        // {
        // // implementation here.
        // ControllableTime ct = (ControllableTime) part;
        // _controllableTime = ct;
        // }
        // });
        // _myPartMonitor.addPartListener(ControllableTime.class,
        // PartMonitor.DEACTIVATED,
        // new PartMonitor.ICallback()
        // {
        // public void eventTriggered(String type, Object part, IWorkbenchPart
        // parentPart)
        // {
        // // no, don't bother clearing the controllable time when the plot is
        // // de-activated,
        // // - since with the highlight on the narrative, we want to be able
        // // to control the time still.
        // // _controllableTime = null;
        // }
        // });

        // ok we're all ready now. just try and see if the current part is valid
        _myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
                .getActivePage());
    }

    @Override
    public void dispose()
    {

        // and stop listening for part activity
        _myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());

        // let the parent do it's bit
        super.dispose();

    }

    
    private static SimpleDateFormat _myFormat;
    private static String _myFormatString;
    
    public static String toStringHiRes(HiResDate time, String pattern)
            throws IllegalArgumentException
    {
        // so, have a look at the data
        long micros = time.getMicros();
        // long wholeSeconds = micros / 1000000;

        StringBuffer res = new StringBuffer();

        java.util.Date theTime = new java.util.Date(micros / 1000);

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
            _myFormat = new SimpleDateFormat(pattern);
            _myFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        res.append(_myFormat.format(theTime));

        DecimalFormat microsFormat = new DecimalFormat("000000");
        DecimalFormat millisFormat = new DecimalFormat("000");

        // do we have micros?
        if (micros % 1000 > 0)
        {
            // yes
            res.append(".");
            res.append(microsFormat.format(micros % 1000000));
        }
        else
        {
            // do we have millis?
            if (micros % 1000000 > 0)
            {
                // yes, convert the value to millis

                long millis = micros = (micros % 1000000) / 1000;

                res.append(".");
                res.append(millisFormat.format(millis));
            }
            else
            {
                // just use the normal output
            }
        }

        return res.toString();
    }

}
