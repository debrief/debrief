/**
 * 
 */
package org.mwc.cmap.core.ui_support;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.mwc.cmap.core.CorePlugin;

public class PartMonitor implements IPartListener
{

	public static interface ICallback
	{
		public void eventTriggered(String type, Object instance, IWorkbenchPart parentPart);
	}

	public final static String ACTIVATED = "ACTIVATED";

	public final static String BROUGHT_TO_TOP = "BROUGHT_TO_TOP";

	public final static String CLOSED = "CLOSED";

	public final static String DEACTIVATED = "DEACTIVATED";

	public final static String OPENED = "OPENED";

	HashMap<String, HashMap<Class<?>, Vector<ICallback>>> _myEvents = null;
	
	private final IPartService _myPartService;

	public PartMonitor(final IPartService partService)
	{
		_myPartService = partService;
		partService.addPartListener(this);
	}

	public Action createSyncedAction(final String title, final String tooltip, final IWorkbenchPartSite site)
	{
		final Action res = new Action(title, Action.AS_CHECK_BOX)
		{
			public void run()
			{
				if(isChecked())
				{
					// hey, user wants to start listening to narratives, fire an update so
					// we're looking at the right one.
					fireActivePart(site.getWorkbenchWindow().getActivePage());
				}
			}
		};
		res.setText(title);
		res.setChecked(true);
		res.setToolTipText(tooltip);
		res.setImageDescriptor(CorePlugin.getImageDescriptor("icons/synced.gif"));
		return res;
	}
	
	
	
	/** 
	 * @deprecated - we should just use the ditch method, it's neater
	 * @param partService
	 */
	public void dispose(final IPartService partService)
	{
		if(_myPartService != partService)
			CorePlugin.logError(Status.ERROR, "FOR MAINTAINER: PartMonitor is looking at wrong server", null);
		ditch();
	}

	/** convenience method to fire a part-activated message representing
	 * the currently active editor.  If there is a part already active when
	 * the view opens we will use that part to populate the new view, 
	 * if applicable
	 * @param currentPage
	 */
	public void fireActivePart(final IWorkbenchPage currentPage)
	{
		// just check we have an editor
		if(currentPage != null)
			partActivated(currentPage.getActiveEditor());
	}
	
	public void ditch()
	{

		// right stop listening
		_myPartService.removePartListener(this);
		
		// hey, ditch our lists aswell
		final Iterator<HashMap<Class<?>, Vector<ICallback>>> iter = _myEvents.values().iterator();
		while(iter.hasNext())
		{
			final HashMap<Class<?>, Vector<ICallback>> thisEventList = iter.next();
			final Iterator<Vector<ICallback>> iter2 = thisEventList.values().iterator();
			while(iter2.hasNext())
			{
				final Vector<ICallback> callbacks =  iter2.next();
				
				// right. ditch the callbacks
				callbacks.clear();
			}
			
			// ok, now ditch this list of events
			thisEventList.clear();
		}
		
		// and ditch the full list of events
		_myEvents.clear();
	}
	
	public void addPartListener(final Class<?> Subject, final String event, final PartMonitor.ICallback callback)
	{

		if (_myEvents == null)
			_myEvents = new HashMap<String, HashMap<Class<?>, Vector<ICallback>>>();

		// ok, see if we are watching for this event type
		HashMap<Class<?>, Vector<ICallback>> thisEventList =  _myEvents.get(event);

		// are we already looking for this event?
		if (thisEventList == null)
		{
			// nope, better create it
			thisEventList = new HashMap<Class<?>, Vector<ICallback>>();
			_myEvents.put(event, thisEventList);
		}

		Vector<ICallback> thisSubjectList = thisEventList.get(Subject);

		// are we already looking for this subject
		if (thisSubjectList == null)
		{
			thisSubjectList = new Vector<ICallback>();
			thisEventList.put(Subject, thisSubjectList);
		}

		// ok, add this callback for this subject
		thisSubjectList.add(callback);
	}

	private void processEvent(final IWorkbenchPart part, final String event)
	{
		if (_myEvents == null)
			return;

		// just check that we've actually received something
		if(part == null)
			 return;
		
		// ok. see if we are looking for any subjects related to this event
		 final HashMap<Class<?>, Vector<ICallback>> thisEventList = _myEvents.get(event);
		if (thisEventList != null)
		{
			// double-check
			if (thisEventList.size() > 0)
			{
				// yup. work though and check the objects
				final Set<Class<?>> theSet = thisEventList.keySet();

				// have a go at sorting the events, so we can process them in a predictable fashion
				final Comparator<Object> myComparator = new Comparator<Object>(){
					public int compare(final Object arg0, final Object arg1)
					{
						// the following comparison test is relied upon in order to fire
						// the new time provider partMonitor callback before the new
						// time control preferences callback event (all in TimeController view)
						return arg1.toString().compareTo(arg0.toString());
					}};
				final SortedSet<Class<?>> ss = new java.util.TreeSet<Class<?>>(myComparator );
				
				// add all the current entries
				ss.addAll(theSet);
				
				// and work through them.
				final Iterator<Class<?>> iter = ss.iterator();
				while (iter.hasNext())
				{
					final Class<?> thisType = iter.next();
					final Object adaptedItem = part.getAdapter(thisType);
					if (adaptedItem != null)
					{
						// yup, here we are. fire away.
						final Vector<PartMonitor.ICallback> callbacksForThisSubject =  thisEventList.get(thisType);
						final Iterator<ICallback> iter2 = callbacksForThisSubject.iterator();
						while(iter2.hasNext())
						{
							final PartMonitor.ICallback callback = (PartMonitor.ICallback) iter2.next();
							callback.eventTriggered(event, adaptedItem, part);
						}
					}
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partActivated(final IWorkbenchPart part)
	{
		processEvent(part, ACTIVATED);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partBroughtToTop(final IWorkbenchPart part)
	{
		processEvent(part, BROUGHT_TO_TOP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partClosed(final IWorkbenchPart part)
	{
		processEvent(part, CLOSED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partDeactivated(final IWorkbenchPart part)
	{
		processEvent(part, DEACTIVATED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partOpened(final IWorkbenchPart part)
	{
		processEvent(part, OPENED);
	}

/////////////////////////////////////////////////
	// and the testing code
	// ///////////////////////////////////////////////
	public static class TestNarrativeViewer extends TestCase
	{
		public TestNarrativeViewer()
		{
			super("test narrative viewer");
		}

		static IPartListener _ipsRegistered = null;

		static boolean _openedCalled = false;

		static boolean _closedCalled = false;

		static Vector<String> _eventNames = new Vector<String>(0, 1);

		static Vector<Class<?>> _eventTypes = new Vector<Class<?>>(0, 1);

		public void testPartMonitor()
		{
			final IPartService ips = new IPartService()
			{
				public void addPartListener(final IPartListener listener)
				{
					_ipsRegistered = listener;
				}

				public void addPartListener(final IPartListener2 listener)
				{
				}

				public IWorkbenchPart getActivePart()
				{
					return null;
				}

				public IWorkbenchPartReference getActivePartReference()
				{
					return null;
				}

				public void removePartListener(final IPartListener listener)
				{
					_ipsRegistered = null;
				}

				public void removePartListener(final IPartListener2 listener)
				{
				}
			};

			// right, check that the ips bits are ready
			assertNull("part monitor not registered yet", _ipsRegistered);
			assertEquals("part monitor not registered yet", 0, _eventNames.size());
			assertEquals("part monitor not registered yet", 0, _eventTypes.size());
			assertFalse("part monitoring not ready", _openedCalled);
			assertFalse("part monitoring not ready", _closedCalled);

			// and on with the testing
			final PartMonitor pm = new PartMonitor(ips);

			assertEquals("PartMonitor registered", pm, _ipsRegistered);
			assertNull("PartMonitor empty", pm._myEvents);

			// ok, try some calls (without any listeners)
			pm.partOpened(new TestPart()
			{
				@SuppressWarnings("rawtypes")
				public Object getAdapter(final Class adapter)
				{
					return new String("string");
				}
			});

			// right, check that the ips bits are still empty
			assertEquals("part monitor not registered yet", 0, _eventNames.size());
			assertEquals("part monitor not registered yet", 0, _eventTypes.size());
			assertFalse("part monitoring not ready", _openedCalled);
			assertFalse("part monitoring not ready", _closedCalled);

			// add listeners
			pm.addPartListener(String.class, PartMonitor.OPENED,
					new PartMonitor.ICallback()
					{
						public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
						{
							_openedCalled = true;
							_eventNames.add(type);
							_eventTypes.add(String.class);
						}
					});

			// and add another listener
			pm.addPartListener(String.class, PartMonitor.CLOSED,
					new PartMonitor.ICallback()
					{
						public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
						{
							_closedCalled = true;
							_eventNames.add(type);
							_eventTypes.add(String.class);
						}
					});

			// fire one of the correct type
			pm.partOpened(new TestPart()
			{
				@SuppressWarnings("rawtypes")
				public Object getAdapter(final Class adapter)
				{
					return new String("string");
				}
			});

			assertEquals("part monitor not registered yet", 1, _eventNames.size());
			assertEquals("part monitor not registered yet", 1, _eventTypes.size());
			assertTrue("part monitoring not ready", _openedCalled);
			assertFalse("part monitoring not ready", _closedCalled);

			// clear out the lists
			_openedCalled = false;
			_closedCalled = false;
			_eventNames.clear();
			_eventTypes.clear();

			// add more listeners
			pm.addPartListener(Integer.class, PartMonitor.OPENED,
					new PartMonitor.ICallback()
					{
						public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
						{
							_openedCalled = true;
							_eventNames.add(type);
							_eventTypes.add(Integer.class);
						}
					});
			pm.addPartListener(Integer.class, PartMonitor.CLOSED,
					new PartMonitor.ICallback()
					{
						public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
						{
							_closedCalled = true;
							_eventNames.add(type);
							_eventTypes.add(Integer.class);
						}
					});

			// fire the event, just for strings.
			pm.partOpened(new TestPart()
					{
						@SuppressWarnings("rawtypes")
						public Object getAdapter(final Class adapter)
						{
							Object res = null;
							if(adapter == String.class)
								res = new String("string");
							return res;
						}
					});
			
			// check events got created (we're still only returning a String object,
			// so the integer one shouldn't get called
			assertEquals("part monitor not registered yet", 1, _eventNames.size());
			assertEquals("part monitor not registered yet", 1, _eventTypes.size());
			assertTrue("part monitoring not ready", _openedCalled);
			assertFalse("part monitoring not ready", _closedCalled);

			_eventNames.clear();
			_eventTypes.clear();
			_openedCalled = false;
			_closedCalled = false;
			
			// fire one of both classes
			pm.partOpened(new TestPart()
			{
				@SuppressWarnings("rawtypes")
				public Object getAdapter(final Class adapter)
				{
					Object res = null;
					if (adapter == String.class)
					{
						res = new String("stst");
					}
					else if (adapter == Integer.class)
					{
						res = new Integer(1);
					}
					return res;
				}
			});

			// check events got created (we're now returning both types - but still only 
			// for open event)
			assertEquals("part monitor not registered yet", 2, _eventNames.size());
			assertEquals("part monitor not registered yet", 2, _eventTypes.size());
			assertTrue("part monitoring not ready", _openedCalled);
			assertFalse("part monitoring not ready", _closedCalled);

			// and clear.
			_eventNames.clear();
			_eventTypes.clear();
			_openedCalled = false;
			_closedCalled = false;			
			
			// fire both events for both classes
			pm.partOpened(new TestPart()
			{
				@SuppressWarnings("rawtypes")
				public Object getAdapter(final Class adapter)
				{
					Object res = null;
					if (adapter == String.class)
					{
						res = new String("stst");
					}
					else if (adapter == Integer.class)
					{
						res = new Integer(1);
					}
					return res;
				}
			});
			pm.partClosed(new TestPart()
					{
						@SuppressWarnings("rawtypes")
						public Object getAdapter(final Class adapter)
						{
							Object res = null;
							if (adapter == String.class)
							{
								res = new String("stst");
							}
							else if (adapter == Integer.class)
							{
								res = new Integer(1);
							}
							return res;
						}
					});
			
			// check events got created (we're now returning both types for both events
			assertEquals("part monitor not registered yet", 4, _eventNames.size());
			assertEquals("part monitor not registered yet", 4, _eventTypes.size());
			assertTrue("part monitoring not ready", _openedCalled);
			assertTrue("part monitoring not ready", _closedCalled);
			
			// right, lastly test the ditching part
			pm.dispose(ips);
			assertNull("part monitor not de-registered", _ipsRegistered);			
			
		}

		/** wrapper stub used to for testing of PartMonitor
		 * 
		 * @author ian.mayo
		 *
		 */
		protected abstract static class TestPart implements IWorkbenchPart
		{
			public void addPropertyListener(final IPropertyListener listener)
			{
			}

			public void createPartControl(final Composite parent)
			{
			}

			public void dispose()
			{
			}

			public IWorkbenchPartSite getSite()
			{
				return null;
			}

			public String getTitle()
			{
				return null;
			}

			public Image getTitleImage()
			{
				return null;
			}

			public String getTitleToolTip()
			{
				return null;
			}

			public void removePropertyListener(final IPropertyListener listener)
			{
			}

			public void setFocus()
			{
			}

			@SuppressWarnings("rawtypes")
			public abstract Object getAdapter(Class adapter);

		}
	}
	
}