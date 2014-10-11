/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.xyplot.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.xyplot.views.providers.CrossSectionDatasetProvider;
import org.mwc.cmap.xyplot.views.providers.ICrossSectionDatasetProvider;
import org.mwc.cmap.xyplot.views.snail.ISnailPeriodChangedListener;
import org.mwc.cmap.xyplot.views.snail.SnailPeriodTracker;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;


public class CrossSectionView extends ViewPart implements ISnailPeriodChangedListener
{
	
	CrossSectionViewer _viewer;

	/**
	 * listen to line annotations being selected
	 */
	private ISelectionChangedListener _selectionChangeListener;
	
	/**
	 * Provider listening to us
	 */
	private ISelectionProvider _selectionProvider;
	
	/**
	 * helper application to help track activation/closing of new plots
	 */
	private PartMonitor _partMonitor;
	
	/**
	 * listen out for new times
	 */
	private PropertyChangeListener _timeListener;
	
	private TimeProvider _timeProvider;
		
	private PropertyChangeListener _lineListener = new LineChangeListener();
	
	/**
	 *  current line annotation being selected 
	 */
	private LineShape _line = null;
	
	/**
	 * Debrief data
	 */
	private Layers _myLayers;
	
	private Layers.DataListener _myLayersListener;
	
	private ICrossSectionDatasetProvider _datasetProvider = 
			new CrossSectionDatasetProvider();	
	
	/**
	 * store the plot information when we're reloading a plot in a fresh session
	 */
	private IMemento _memento = null;
	

	/**
	 * Toolbar drop down for specifying time period
	 */
	private SnailPeriodTracker _snailMode = new SnailPeriodTracker();

	@Override
	public void createPartControl(final Composite parent) 
	{
		_viewer = new CrossSectionViewer(parent);	

		_partMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		
		 
		listenToMyParts();
		setupFiringTimeChangesToChart();
		contributeToActionBars();
		
		_selectionChangeListener = new ISelectionChangedListener() 
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event) 
			{
				final ISelection sel = event.getSelection();
				if (!(sel instanceof IStructuredSelection))
					return;
				final IStructuredSelection ss = (IStructuredSelection) sel;
				final Object o = ss.getFirstElement();
				if (o instanceof EditableWrapper) 
				{
					// check if Line annotation is selected
					final Editable eb = ((EditableWrapper) o).getEditable();
					if (eb instanceof ShapeWrapper)
					{
						final PlainShape shape = ((ShapeWrapper) eb).getShape();
						if (shape instanceof LineShape && !shape.equals(_line))
						{
							clearLineListener();
							_line = (LineShape) shape;
							_line.addPropertyListener(_lineListener);
							_viewer.fillPlot(_myLayers, _line, _datasetProvider);
						}
					}		
				}
			}
		};
		_viewer.addSelectionChangedListener(_selectionChangeListener);
		
		_snailMode.addSnailPeriodChangedListener(this);
		
		// have we got our data? 
		if (_memento != null)
		{
			// restore it
			_viewer.restoreState(_memento);
			_snailMode.setPeriod(_viewer.getPeriod());
		} 
	}
	
	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(final IToolBarManager manager)
	{
		manager.add(_snailMode);
	}
	
	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException
	{
		super.init(site, memento);
		if (memento != null)
		{
			_memento = memento;
		}
	}
	
	private void listenToMyParts()
	{
		// Listen to Layers
		_partMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_partMonitor.addPartListener(Layers.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_partMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// is this our set of layers?
						if (part == _myLayers)
						{
							clearLineListener();
							// stop listening to this layer
							clearLayerListener();
						}					
					}

				});		
			
		_partMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != _viewer)
						{
							final ISelectionProvider iS = (ISelectionProvider) part;
							if (!iS.equals(_selectionProvider))
							{
								if (part instanceof IEditorPart)
									setUpTimeProvider((IEditorPart) part);	
								
								_selectionProvider = iS;
								if (_selectionChangeListener != null)
								{
									_selectionProvider
										.addSelectionChangedListener(_selectionChangeListener);
								}
							}
						}
					}
				});
		_partMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != _viewer)
						{
							if (_selectionProvider != null && _selectionChangeListener != null)
							{
								_selectionProvider
									.removeSelectionChangedListener(_selectionChangeListener);
							}
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_partMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}
	
	private void setupFiringTimeChangesToChart()
	{

		// see if we've alreay been configured
		if (_timeListener != null)
			return;

		// get the document being edited
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		IEditorPart editor = null;

		// the page might not yet be open...
		if (page != null)
		{
			editor = page.getActiveEditor();
			// do we have an active editor?
			if (editor == null)
			{
				// see if there are any editors at all open
				final IEditorReference[] theEditors = page.getEditorReferences();
				for (int i = 0; i < theEditors.length; i++)
				{
					final IEditorReference thisE = theEditors[i];
					editor = thisE.getEditor(false);

					// right, see if it has a time manager
					final TimeProvider tp = (TimeProvider) editor
							.getAdapter(TimeProvider.class);
					if (tp != null)
					{
						final String hisId = tp.getId();
						if (hisId.equals(_viewer.getPlotId()))
							break;
					}
				}

				// nope, drop out.
				return;
			}
		}

		setUpTimeProvider(editor);
	}
	
	private void setUpTimeProvider(IEditorPart editor)
	{
		if (editor != null)
		{
			// get it's time-provider interface
			final TimeProvider prov = (TimeProvider) editor.getAdapter(TimeProvider.class); 
			if (_timeProvider != null && _timeProvider.equals(prov)) 
					return;
			if (_timeListener != null)
				_timeProvider.removeListener(_timeListener, 
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
			_timeProvider = prov;
		}
		else
			CorePlugin.logError(Status.WARNING, "Failed to identify time provider",
					null);

		if (_timeProvider != null)
		{
			// create our listener
			_timeListener = new NewTimeListener();
			// add our listener to the time object
			_timeProvider.addListener(_timeListener, 
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
			_viewer.setPlotId(_timeProvider.getId());
		}
	}


	@Override
	public void setFocus() 
	{
		//TODO:
	}	
	
	void processNewLayers(final Object part)
	{
		// just check we're not already looking at it
		if (!part.equals(_myLayers))
		{
			// de-register current layers before tracking the new one
			clearLayerListener();
			clearLineListener();
			_viewer.clearPlot();
		}
		else
		{
			return;
		}		
		
		_myLayers = (Layers) part;
		if (_myLayersListener == null)
		{
			_myLayersListener = new Layers.DataListener2()
			{

				public void dataModified(final Layers theData, final Layer changedLayer)
				{			
					processReformattedLayer(theData, changedLayer);
				}

				public void dataExtended(final Layers theData)
				{
					dataExtended(theData, null, null);
				}

				public void dataReformatted(final Layers theData, final Layer changedLayer)
				{
					processReformattedLayer(theData, changedLayer);
				}

				public void dataExtended(final Layers theData, final Plottable newItem,
								final Layer parentLayer)
				{
					processNewData(theData, newItem, parentLayer);
				}
			};
		}
		// right, listen for data being added
		_myLayers.addDataExtendedListener(_myLayersListener);

		// and listen for items being reformatted
		_myLayers.addDataReformattedListener(_myLayersListener);
		
		// and listen for items being reformatted
		_myLayers.addDataModifiedListener(_myLayersListener);

		// do an initial population.
		processNewData(_myLayers, null, null);		
	}	
	
	void processReformattedLayer(final Layers theData, final Layer changedLayer)
	{
		_viewer.fillPlot(theData, _line, _datasetProvider);
	}
	
	@Override
	public void snailPeriodChanged(final long timePeriod)
	{
		_viewer.fillPlot(_myLayers, _line, _datasetProvider, timePeriod);
	}
	
	void processNewData(final Layers theData, final Editable newItem,
			final Layer parentLayer)
	{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					// ok, fire the change in the UI thread
					_viewer.fillPlot(theData, _line, _datasetProvider);
				}
			});
	}	
	
	/**
	 * stop listening to the layer, if necessary
	 */
	void clearLayerListener()
	{
		if (_myLayers != null)
		{
			_myLayers.removeDataExtendedListener(_myLayersListener);
			_myLayers.removeDataModifiedListener(_myLayersListener);
			_myLayers.removeDataReformattedListener(_myLayersListener);
			_myLayersListener = null;
			_myLayers = null;
		}
	}
	
	void clearLineListener()
	{
		if (_line != null)
		{
			_line.removePropertyListener(_lineListener);
			_lineListener = null;
			_line = null;
		}
	}
	
	void clearTimeListener()
	{
		if (_timeProvider != null)
		{
			_timeProvider.removeListener(_timeListener, 
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
			_timeListener = null;
			_timeProvider = null;			
		}
	}
	
	public void dispose()
	{
		super.dispose();
		// make sure we close the listeners
		clearLayerListener();
		clearLineListener();		
		clearTimeListener();
		
		if(_viewer != null)
		{
			_viewer.removeSelectionChangedListener(_selectionChangeListener);
		}
		
		if (_selectionProvider != null)
		{
			_selectionProvider.removeSelectionChangedListener(_selectionChangeListener);
			_selectionProvider = null;
		}
		
		_selectionChangeListener = null;
		
		if (_partMonitor != null)
		{
			_partMonitor.ditch();
		}
		_viewer.clearPlot();
	}
	
	@Override
	public void saveState(final IMemento memento)
	{
		// let our parent go for it first
		super.saveState(memento);
		_viewer.saveState(memento);
	}
	
	protected final class LineChangeListener implements PropertyChangeListener
	{
		public void propertyChange(final PropertyChangeEvent event)
		{
			if (event.getPropertyName().equals(
					PlainWrapper.LOCATION_CHANGED))
			{
				final Runnable nextEvent = new Runnable()
				{
					public void run()
					{
						if (event.getSource().equals(_line))
						{
							_viewer.fillPlot(_myLayers, _line, _datasetProvider);
						}
					}
				};
				Display.getDefault().syncExec(nextEvent);				
			}
		}
	}
	
	protected final class NewTimeListener implements PropertyChangeListener
	{
		public void propertyChange(final PropertyChangeEvent event)
		{
			// see if it's the time or the period which
			// has changed
			if (event.getPropertyName().equals(
					TimeProvider.TIME_CHANGED_PROPERTY_NAME))
			{
				// ok, use the new time
				final HiResDate newDTG = (HiResDate) event.getNewValue();
				final Runnable nextEvent = new Runnable()
				{
					public void run()
					{
						_viewer.newTime(newDTG);
						_viewer.fillPlot(_myLayers, _line, _datasetProvider);
					}
				};
				Display.getDefault().syncExec(nextEvent);				
			}
		}
	}
	
	
}
