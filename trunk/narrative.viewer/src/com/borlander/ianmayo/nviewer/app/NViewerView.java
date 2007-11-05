package com.borlander.ianmayo.nviewer.app;

import java.util.Collections;

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
import org.mwc.cmap.core.ui_support.PartMonitor;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.IRollingNarrativeProvider.INarrativeListener;

import com.borlander.ianmayo.nviewer.Column;
import com.borlander.ianmayo.nviewer.ColumnFilter;
import com.borlander.ianmayo.nviewer.NarrativeViewer;
import com.borlander.ianmayo.nviewer.model.TimeFormatter;

public class NViewerView extends ViewPart {
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

	public void createPartControl(Composite parent) {
	    
        _myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
	    
		parent.setLayout(new GridLayout(1, false));
		Composite rootPanel = new Composite(parent, SWT.BORDER);
		rootPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		StackLayout rootPanelLayout = new StackLayout();
		rootPanel.setLayout(rootPanelLayout);

		myViewer = new NarrativeViewer(rootPanel, Activator.getInstance().getPreferenceStore());
		rootPanelLayout.topControl = myViewer;
		
		//the line below contributes the predefined viewer actions onto the view action bar
		myViewer.getViewerActions().fillActionBars(getViewSite().getActionBars());
		
		//all below this line is mockup code
		myButtonPanel = new Composite(parent, SWT.NONE);
		myButtonPanel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		myButtonPanel.setLayout(new GridLayout(3, true));

		new TestButton("Set input") {
			@Override
			protected void onClick() {
//				MockEntryWrapper entryWrapper = new MockEntryWrapper();
//				for (int i = 0; i < 30; i++) {
//					String entry = "";
//					for (int j = 0; j <= i; j++) {
//						entry += "entry#" + (j + 1);
//						if (j < i) {
//							entry += " ";
//						}
//					}
//					entryWrapper.addEntry("source #" + (i % 3 + 1), "type #" + (i % 4 + 1), entry);
//				}
//				myCurrentInput = entryWrapper;
//				myViewer.setInput(myCurrentInput);
			}
		};
		new TestButton("Format time as HH:mm") {
			@Override
			protected void onClick() {
				myViewer.setTimeFormatter(new TimeFormatter() {
					public String format(HiResDate time) {
					    return time.toString();
//						Calendar calendar = Calendar.getInstance();
//						calendar.setTimeInMillis(time);
//						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
//						return simpleDateFormat.format(calendar.getTime());
					}
				});
			}
		};
		new TestButton("Format time as HH:mm:ss") {
			@Override
			protected void onClick() {
				myViewer.setTimeFormatter(new TimeFormatter() {
                    public String format(HiResDate time) {
                        return "[" + time.toString() + "]";
//						Calendar calendar = Calendar.getInstance();
//						calendar.setTimeInMillis(time);
//						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//						return simpleDateFormat.format(calendar.getTime());
					}
				});
			}
		};
		new TestButton("Filter source to 'source #2'") {
			@Override
			protected void onClick() {
				Column sourceColumn = myViewer.getModel().getColumnSource();
				ColumnFilter filter = sourceColumn.getFilter();
				filter.setAllowedValues(Collections.singleton("source #2"));
				myViewer.refresh();
			}
		};
		new TestButton("Remove source filter") {
			@Override
			protected void onClick() {
				Column sourceColumn = myViewer.getModel().getColumnSource();
				ColumnFilter filter = sourceColumn.getFilter();
				filter.clear();
				myViewer.refresh();
			}
		};
		new TestButton("Filter type to 'type #3'") {
			@Override
			protected void onClick() {
				Column typeColumn = myViewer.getModel().getColumnType();
				ColumnFilter filter = typeColumn.getFilter();
				filter.setAllowedValues(Collections.singleton("type #3"));
				myViewer.refresh();
			}
		};
		new TestButton("Remove type filter") {
			@Override
			protected void onClick() {
				Column typeColumn = myViewer.getModel().getColumnType();
				ColumnFilter filter = typeColumn.getFilter();
				filter.clear();
				myViewer.refresh();
			}
		};

		new TestButton("Switch Entry wrap mode") {
			private boolean myIsWrapping = myViewer.isWrappingEntries();

			@Override
			protected void onClick() {
				myIsWrapping = !myIsWrapping;
				myViewer.setWrappingEntries(myIsWrapping);
			}
		};

		new TestButton("Open filter dialog for source") {
			@Override
			protected void onClick() {
				Column sourceColumn = myViewer.getModel().getColumnSource();
				myViewer.showFilterDialog(sourceColumn);
			}
		};

		new TestButton("Open filter dialog for type") {
			@Override
			protected void onClick() {
				Column typeColumn = myViewer.getModel().getColumnType();
				myViewer.showFilterDialog(typeColumn);
			}
		};
		
		setupPartListeners();
	}

	private abstract class TestButton {
		private final Button myButton;

		public TestButton(String caption) {
			myButton = new Button(myButtonPanel, SWT.NONE);
			myButton.setText(caption);
			myButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
			myButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					onClick();
				}
			});
		}

		abstract protected void onClick();
	}

	public void setFocus() {
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
                            //viewer.add(entry);
                            System.err.println("should be updating with new narrative entry!!!");
                        }
                    });
                }
            };
        }
        // and start listening to it..
        _myRollingNarrative.addNarrativeListener(IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
    }
    	

    /**
     * 
     */
    private void setupPartListeners()
    {
        _myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
                PartMonitor.ACTIVATED, new PartMonitor.ICallback()
                {
                    public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
                    {
                        IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
                        setInput(newNarr, parentPart);
                    }
                });

        // unusually, we are also going to track the open event for narrative data
        // so that we can start off with some data
        _myPartMonitor.addPartListener(IRollingNarrativeProvider.class, PartMonitor.OPENED,
                new PartMonitor.ICallback()
                {
                    public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
                    {
                        IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
                        setInput(newNarr, parentPart);
                    }

                });

        _myPartMonitor.addPartListener(IRollingNarrativeProvider.class, PartMonitor.CLOSED,
                new PartMonitor.ICallback()
                {
                    public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
                    {
                        IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
                        if (newNarr == _myRollingNarrative)
                        {
                            // stop listening to old narrative
                            _myRollingNarrative.removeNarrativeListener(
                                    IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
                            myViewer.setInput(null);
                            _myRollingNarrative = null;
                        }
                    }
                });

//        _myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
//                new PartMonitor.ICallback()
//                {
//                    public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//                    {
//                        // just check we're not already looking at it
//                        if (part != _myTemporalDataset)
//                        {
//                            // ok, better stop listening to the old one
//                            if (_myTemporalDataset != null)
//                            {
//                                // yup, better ignore it
//                                _myTemporalDataset.removeListener(_temporalListener,
//                                        TimeProvider.TIME_CHANGED_PROPERTY_NAME);
//
//                                _myTemporalDataset = null;
//                            }
//
//                            // implementation here.
//                            _myTemporalDataset = (TimeProvider) part;
//                            if (_temporalListener == null)
//                            {
//                                _temporalListener = new PropertyChangeListener()
//                                {IEntryWrapper
//                                    public void propertyChange(PropertyChangeEvent event)
//                                    {
//                                        // ok, use the new time
//                                        HiResDate newDTG = (HiResDate) event.getNewValue();
//                                        timeUpdated(newDTG);
//                                    }
//                                };
//                            }
//                            _myTemporalDataset.addListener(_temporalListener,
//                                    TimeProvider.TIME_CHANGED_PROPERTY_NAME);
//                        }
//                    }
//                });
//        _myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
//                new PartMonitor.ICallback()
//                {
//                    public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//                    {
//                        if (part == _myTemporalDataset)
//                        {
//                            _myTemporalDataset.removeListener(_temporalListener,
//                                    TimeProvider.TIME_CHANGED_PROPERTY_NAME);
//                        }
//                    }
//                });
//        _myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.ACTIVATED,
//                new PartMonitor.ICallback()
//                {
//                    public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//                    {
//                        // implementation here.
//                        ControllableTime ct = (ControllableTime) part;
//                        _controllableTime = ct;
//                    }
//                });
//        _myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.DEACTIVATED,
//                new PartMonitor.ICallback()
//                {
//                    public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
//                    {
//                        // no, don't bother clearing the controllable time when the plot is
//                        // de-activated,
//                        // - since with the highlight on the narrative, we want to be able
//                        // to control the time still.
//                        // _controllableTime = null;
//                    }
//                });

        // ok we're all ready now. just try and see if the current part is valid
        _myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
    }


    @Override
    public void dispose()
    {

        // and stop listening for part activity
        _myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());

        // let the parent do it's bit
        super.dispose();
        
        
    }	
}
