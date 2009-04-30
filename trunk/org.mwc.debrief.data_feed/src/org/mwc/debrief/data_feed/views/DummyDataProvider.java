/**
 * 
 */
package org.mwc.debrief.data_feed.views;

import java.awt.event.ActionEvent;

import MWC.Utilities.Timer.TimerListener;

/**
 * @author ian.mayo
 */
public class DummyDataProvider implements RealTimeProvider, TimerListener
{

	/**
	 * the automatic timer we are using
	 */
	private MWC.Utilities.Timer.Timer _theTimer;

	/**
	 * the feed we're listening to
	 */
	private LiveFeedViewer _myHost;

//	private HiResDate _dtg;
//
//	private WorldLocation _lastLoc;
//
//	private PlainImporterBase _iff;
//
//	private TrackWrapper _trk;

	public DummyDataProvider()
	{

		/**
		 * the timer-related settings
		 */
		_theTimer = new MWC.Utilities.Timer.Timer();
		_theTimer.stop();
		_theTimer.setDelay(500);
		_theTimer.addTimerListener(this);
	}

	/**
	 * @param host
	 */
	public void connect(LiveFeedViewer host)
	{
		_myHost = host;

		_myHost.showMessage("Connecting to dummy");

		// ok, start the trigger
		startPlaying();

		// and update our state
		_myHost.showState("Connected");
		_myHost.showMessage("Connected to dummy");

	}

	/**
	 * @param host
	 */
	public void disconnect(LiveFeedViewer host)
	{
		_myHost.showMessage("Disconnecting");

		// ok, start the trigger
		stopPlaying();

		// and update our state
		_myHost.showState("Disconnected");
		_myHost.showMessage("Disconnected");

		// done.
		_myHost = null;

	}

	/**
	 * @return
	 */
	public String getName()
	{
		return "Dummy provider";
	}

	/**
	 * ok, start auto-stepping forward through the serial
	 */
	private void startPlaying()
	{
		_theTimer.start();
	}

	protected void stopPlaying()
	{
		_theTimer.stop();
	}

	public void onTime(ActionEvent event)
	{
		
		// this section has been commented out, in respect of the changed
		// procedure for exporting to file (in which we no longer
		// receive a copy of the text to export.
		
//		// ok, fire the new data event
//		double myCourse = 0;
//
//		if (_dtg == null)
//		{
//			_dtg = new HiResDate(new Date());
//			_lastLoc = new WorldLocation(50.75, -1.1, 0);
//			_iff = new ImportReplay();
//			_trk = new TrackWrapper();
//			Track trk = new Track();
//			trk.setName("DUMMY TRACK");
//			_trk.setTrack(trk);
//		}
//		else
//		{
//			// ok, move forward
//			_dtg = new HiResDate((_dtg.getMicros() / 1000) + 60000);
//			myCourse = 1 + Math.random() * 0.6;
//			WorldVector newVec = new WorldVector(myCourse, new WorldDistance(12,
//					WorldDistance.METRES), new WorldDistance(300, WorldDistance.METRES));
//			_lastLoc = new WorldLocation(_lastLoc.add(newVec));
//		}
//
//		Fix fix = new Fix(_dtg, _lastLoc, myCourse, 12);
//		FixWrapper fw = new FixWrapper(fix);
//		fw.setTrackWrapper(_trk);
//
//		// ok, now export it
//		String theStr = _iff.exportThis(fw);
//
//		_myHost.insertData(theStr);
////		_myHost.showMessage("DATA RX");
//
//		// just see if we are doing a fix aswell?
//		if (Math.random() > 0.9)
//		{
//			// ok, inject a position aswell
//			WorldVector newOffset = new WorldVector(Math.random() * 3, new WorldDistance(1,
//					WorldDistance.NM), null);
//			WorldLocation tmpLoc = _lastLoc.add(newOffset);
//			LabelWrapper lw = new LabelWrapper("DATUM", tmpLoc, Color.YELLOW, _dtg, _dtg);
//			String lblStr = _iff.exportThis(lw);
//			_myHost.insertData(lblStr);
//			_myHost.showMessage("LBL RX");
//		}

	}

}
