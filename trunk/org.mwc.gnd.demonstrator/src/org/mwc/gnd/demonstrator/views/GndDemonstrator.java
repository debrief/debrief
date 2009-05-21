package org.mwc.gnd.demonstrator.views;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.postgresql.Driver;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;

public class GndDemonstrator extends ViewPart
{
	private Action action1;
	private Action sendToDB;
	private Action action3;
	private Action action4;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	/**
	 * The constructor.
	 */
	public GndDemonstrator()
	{
	}

	public class GenerateTrack implements RightClickContextItemGenerator
	{
		public void generate(IMenuManager parent, Layers theLayers,
				Layer[] parentLayers, Editable[] subjects)
		{
			// we're only going to work with one item
			if (subjects.length > 0)
			{
				for (int i=0;i<subjects.length;i++)
				{
					// is it a track?
					Editable thisE = subjects[i];
					if (thisE instanceof TrackWrapper)
					{
						final TrackWrapper thisTrack = (TrackWrapper) thisE;

						// right,stick in a separator
						parent.add(new Separator());

						// and the new drop-down list of interpolation frequencies
						// yes, create the action
						Action convertToTrack = new Action("Copy to database")
						{
							public void run()
							{
								copyThisTrackToDatabase(thisTrack);
							}
						};

						parent.add(convertToTrack);
					}
				}
			}
		}
	}	
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		makeActions();
		contributeToActionBars();

		// declare the part monitor, we use it when we generate actions
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		// and start listening
		listenToMyParts();
		
		// also tell the layer manager what we're actually capable of
		RightClickSupport.addRightClickGenerator(new GenerateTrack());
		
		
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(action1);
		manager.add(sendToDB);
		manager.add(action3);
		manager.add(action4);
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(action1);
		manager.add(sendToDB);
		manager.add(action3);
		manager.add(action4);
	}

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
				doConnect();
			}
		};
		action1.setText("Connect");

		sendToDB = new Action("To database", Action.AS_CHECK_BOX)
		{
			public void run()
			{
				copyThisTrackToDatabase(_myTrack);
			}
		};
		sendToDB.setEnabled(false);

		action3 = new Action("Count rows", Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				countData();
			}
		};
		action4 = new Action("Clear db", Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				clearDB();
			}
		};
	}

	protected void clearDB()
	{
		try
		{
			java.sql.Connection conn = getConnection();
			Statement s = conn.createStatement();
			int ctr = 0;
			String theQuery = "delete from roads_geom;";
			ctr = s.executeUpdate(theQuery);
			System.out.println("changed " + ctr + " records");
			s.close();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	private java.sql.Connection getConnection() throws SQLException
	{
		java.sql.Connection conn;
		String url = "jdbc:postgresql://localhost:5432/postgis";
		conn = DriverManager.getConnection(url, "postgres", "4pfonmr");

		return conn;
	}

	private void countData()
	{
		try
		{
			java.sql.Connection conn = getConnection();
			Statement s = conn.createStatement();
			ResultSet r = s.executeQuery("select id, name from roads_geom");
			int ctr = 0;
			while (r.next())
			{

				ctr++;
			}
			System.out.println("found " + ctr + " records");
			s.close();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	int rowCounter = 100;
	protected TrackWrapper _myTrack = null;

	private void copyThisTrackToDatabase(TrackWrapper theTrack)
	{
		try
		{
			if (theTrack == null)
				return;

			java.sql.Connection conn = getConnection();
			int ctr = 0;
			String theQuery = "insert into roads_geom (id, latVal, longVal, name) VALUES (?,?,?,'aa' );";
			PreparedStatement st = conn.prepareStatement(theQuery);

			Enumeration<Editable> enumer = theTrack.getPositions();
			while (enumer.hasMoreElements())
			{
				FixWrapper thisF = (FixWrapper) enumer.nextElement();
				WorldLocation wl = thisF.getLocation();
				st.setInt(1, ++rowCounter);
				st.setFloat(2, (float) wl.getLong());
				st.setFloat(3, (float) wl.getLat());
				ctr += st.executeUpdate();
			}

			System.out.println("changed " + ctr + " records");
			st.close();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void doConnect()
	{

		try
		{
			java.sql.Connection conn = getConnection();

			System.out.println("Connection successful!");
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private void listenToMyParts()
	{
		_myPartMonitor.addPartListener(TrackDataProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						TrackDataProvider trk = (TrackDataProvider) part;
						_myTrack = (TrackWrapper) trk.getPrimaryTrack();
						sendToDB.setEnabled(true);
					}
				});
	}
}