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

	private HashMap _myEvents = null;
	
	private final IPartService _myPartService;

	public PartMonitor(IPartService partService)
	{
		_myPartService = partService;
		partService.addPartListener(this);
	}

	public Action createSyncedAction(final String title, final String tooltip, final IWorkbenchPartSite site)
	{
		Action res = new Action(title, Action.AS_CHECK_BOX)
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
	
	
	
	public void dispose(IPartService partService)
	{
		// right stop listening
		partService.removePartListener(this);
		
		// hey, ditch our lists aswell
		Iterator iter = _myEvents.values().iterator();
		while(iter.hasNext())
		{
			HashMap thisEventList = (HashMap) iter.next();
			Iterator iter2 = thisEventList.values().iterator();
			while(iter2.hasNext())
			{
				Vector callbacks = (Vector) iter2.next();
				
				// right. ditch the callbacks
				callbacks.clear();
			}
			
			// ok, now ditch this list of events
			thisEventList.clear();
		}
		
		// and ditch the full list of events
		_myEvents.clear();
	}

	/** convenience method to fire a part-activated message representing
	 * the currently active editor.  If there is a part already active when
	 * the view opens we will use that part to populate the new view, 
	 * if applicable
	 * @param currentPage
	 */
	public void fireActivePart(IWorkbenchPage currentPage)
	{
		// just check we have an editor
		if(currentPage != null)
			partActivated(currentPage.getActiveEditor());
	}
	
	public void ditch()
	{
		// stop listening for part changes
		_myPartService.removePartListener(this);
		
		// and clear what we're listening to
		_myEvents.clear();
	}
	
	public void addPartListener(Class Subject, String event, PartMonitor.ICallback callback)
	{

		if (_myEvents == null)
			_myEvents = new HashMap();

		// ok, see if we are watching for this event type
		HashMap thisEventList = (HashMap) _myEvents.get(event);

		// are we already looking for this event?
		if (thisEventList == null)
		{
			// nope, better create it
			thisEventList = new HashMap();
			_myEvents.put(event, thisEventList);
		}

		Vector thisSubjectList = (Vector) thisEventList.get(Subject);

		// are we already looking for this subject
		if (thisSubjectList == null)
		{
			thisSubjectList = new Vector();
			thisEventList.put(Subject, thisSubjectList);
		}

		// ok, add this callback for this subject
		thisSubjectList.add(callback);
	}

	private void processEvent(IWorkbenchPart part, String event)
	{
		if (_myEvents == null)
			return;

		// just check that we've actually received something
		if(part == null)
			 return;
		
		// ok. see if we are looking for any subjects related to this event
		HashMap thisEventList = (HashMap) _myEvents.get(event);
		if (thisEventList != null)
		{
			// double-check
			if (thisEventList.size() > 0)
			{
				// yup. work though and check the objects
				Set theSet = thisEventList.keySet();

				// have a go at sorting the events, so we can process them in a predictable fashion
				Comparator myComparator = new Comparator(){
					public int compare(Object arg0, Object arg1)
					{
						// the following comparison test is relied upon in order to fire
						// the new time provider partMonitor callback before the new
						// time control preferences callback event (all in TimeController view)
						return arg1.toString().compareTo(arg0.toString());
					}};
				SortedSet ss = new java.util.TreeSet(myComparator );
				
				// add all the current entries
				ss.addAll(theSet);
				
				// and work through them.
				Iterator iter = ss.iterator();
				while (iter.hasNext())
				{
					Class thisType = (Class) iter.next();
					Object adaptedItem = part.getAdapter(thisType);
					if (adaptedItem != null)
					{
						// yup, here we are. fire away.
						Vector callbacksForThisSubject = (Vector) thisEventList.get(thisType);
						Iterator iter2 = callbacksForThisSubject.iterator();
						while(iter2.hasNext())
						{
							PartMonitor.ICallback callback = (PartMonitor.ICallback) iter2.next();
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
	public void partActivated(IWorkbenchPart part)
	{
		processEvent(part, ACTIVATED);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partBroughtToTop(IWorkbenchPart part)
	{
		processEvent(part, BROUGHT_TO_TOP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partClosed(IWorkbenchPart part)
	{
		processEvent(part, CLOSED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partDeactivated(IWorkbenchPart part)
	{
		processEvent(part, DEACTIVATED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partOpened(IWorkbenchPart part)
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

		private static IPartListener _ipsRegistered = null;

		private static boolean _openedCalled = false;

		private static boolean _closedCalled = false;

		private static Vector _eventNames = new Vector(0, 1);

		private static Vector _eventTypes = new Vector(0, 1);

		public void testPartMonitor()
		{
			IPartService ips = new IPartService()
			{
				public void addPartListener(IPartListener listener)
				{
					_ipsRegistered = listener;
				}

				public void addPartListener(IPartListener2 listener)
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

				public void removePartListener(IPartListener listener)
				{
					_ipsRegistered = null;
				}

				public void removePartListener(IPartListener2 listener)
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
			PartMonitor pm = new PartMonitor(ips);

			assertEquals("PartMonitor registered", pm, _ipsRegistered);
			assertNull("PartMonitor empty", pm._myEvents);

			// ok, try some calls (without any listeners)
			pm.partOpened(new TestPart()
			{
				public Object getAdapter(Class adapter)
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
						public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
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
						public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
						{
							_closedCalled = true;
							_eventNames.add(type);
							_eventTypes.add(String.class);
						}
					});

			// fire one of the correct type
			pm.partOpened(new TestPart()
			{
				public Object getAdapter(Class adapter)
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
						public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
						{
							_openedCalled = true;
							_eventNames.add(type);
							_eventTypes.add(Integer.class);
						}
					});
			pm.addPartListener(Integer.class, PartMonitor.CLOSED,
					new PartMonitor.ICallback()
					{
						public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
						{
							_closedCalled = true;
							_eventNames.add(type);
							_eventTypes.add(Integer.class);
						}
					});

			// fire the event, just for strings.
			pm.partOpened(new TestPart()
					{
						public Object getAdapter(Class adapter)
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
				public Object getAdapter(Class adapter)
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
				public Object getAdapter(Class adapter)
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
						public Object getAdapter(Class adapter)
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
		private abstract static class TestPart implements IWorkbenchPart
		{
			public void addPropertyListener(IPropertyListener listener)
			{
			}

			public void createPartControl(Composite parent)
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

			public void removePropertyListener(IPropertyListener listener)
			{
			}

			public void setFocus()
			{
			}

			public abstract Object getAdapter(Class adapter);

		}
	}
	
}