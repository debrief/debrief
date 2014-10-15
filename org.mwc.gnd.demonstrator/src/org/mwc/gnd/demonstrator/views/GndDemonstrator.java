/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
		public void generate(final IMenuManager parent, final Layers theLayers,
				final Layer[] parentLayers, final Editable[] subjects)
		{
			// we're only going to work with one item
			if (subjects.length > 0)
			{
				for (int i=0;i<subjects.length;i++)
				{
					// is it a track?
					final Editable thisE = subjects[i];
					if (thisE instanceof TrackWrapper)
					{
						final TrackWrapper thisTrack = (TrackWrapper) thisE;

						// right,stick in a separator
						parent.add(new Separator());

						// and the new drop-down list of interpolation frequencies
						// yes, create the action
						final Action convertToTrack = new Action("Copy to database")
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
	public void createPartControl(final Composite parent)
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
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager)
	{
		manager.add(action1);
		manager.add(sendToDB);
		manager.add(action3);
		manager.add(action4);
	}

	private void fillLocalToolBar(final IToolBarManager manager)
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
			final java.sql.Connection conn = getConnection();
			final Statement s = conn.createStatement();
			int ctr = 0;
			final String theQuery = "delete from roads_geom;";
			ctr = s.executeUpdate(theQuery);
			System.out.println("changed " + ctr + " records");
			s.close();
			conn.close();
		}
		catch (final Exception e)
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
		final String url = "jdbc:postgresql://localhost:5432/postgis";
		conn = DriverManager.getConnection(url, "postgres", "4pfonmr");

		return conn;
	}

	private void countData()
	{
		try
		{
			final java.sql.Connection conn = getConnection();
			final Statement s = conn.createStatement();
			final ResultSet r = s.executeQuery("select id, name from roads_geom");
			int ctr = 0;
			while (r.next())
			{

				ctr++;
			}
			System.out.println("found " + ctr + " records");
			s.close();
			conn.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	int rowCounter = 100;
	protected TrackWrapper _myTrack = null;

	private void copyThisTrackToDatabase(final TrackWrapper theTrack)
	{
		try
		{
			if (theTrack == null)
				return;

			final java.sql.Connection conn = getConnection();
			int ctr = 0;
			final String theQuery = "insert into roads_geom (id, latVal, longVal, name) VALUES (?,?,?,'aa' );";
			final PreparedStatement st = conn.prepareStatement(theQuery);

			final Enumeration<Editable> enumer = theTrack.getPositions();
			while (enumer.hasMoreElements())
			{
				final FixWrapper thisF = (FixWrapper) enumer.nextElement();
				final WorldLocation wl = thisF.getLocation();
				st.setInt(1, ++rowCounter);
				st.setFloat(2, (float) wl.getLong());
				st.setFloat(3, (float) wl.getLat());
				ctr += st.executeUpdate();
			}

			System.out.println("changed " + ctr + " records");
			st.close();
			conn.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	private void doConnect()
	{

		try
		{
			final java.sql.Connection conn = getConnection();

			System.out.println("Connection successful!");
			conn.close();
		}
		catch (final Exception e)
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
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final TrackDataProvider trk = (TrackDataProvider) part;
						_myTrack = (TrackWrapper) trk.getPrimaryTrack();
						sendToDB.setEnabled(true);
					}
				});
	}

	@Override
	public void dispose()
	{
		if (_myPartMonitor != null)
		{
			_myPartMonitor.ditch();
		}
		super.dispose();
	}
}