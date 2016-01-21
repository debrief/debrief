package org.mwc.debrief.dis.providers.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;

public class DummyDataProvider implements IPDUProvider
{

	private int ctr = 0;
	final private int _numTracks;
	final private int _numPoints;
	final private long _timeStep;
	private long _timeNow;

	HashMap<Integer, EntityStatePdu> dummyStates = new HashMap<Integer, EntityStatePdu>();
	private List<IDISGeneralPDUListener> _listeners = new ArrayList<IDISGeneralPDUListener>();

	/**
	 * 
	 * @param num
	 *          how many data points to generate
	 * @param i
	 */
	public DummyDataProvider(int numTracks, int numPoints, long timeNow,
			long timeStep)
	{
		_numTracks = numTracks;
		_numPoints = numPoints;
		_timeNow = timeNow;
		_timeStep = timeStep;
	}

	@Override
	public void addListener(IDISGeneralPDUListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public void connect()
	{
		while (ctr < (_numPoints * _numTracks))
		{

			// create
			int hisId = (int) ctr % _numTracks;

			if (hisId == 0 && ctr != 0)
			{
				_timeNow += _timeStep;
			}

			// ok, where is he?
			EntityStatePdu lastLoc = dummyStates.get(hisId);
			if (lastLoc == null)
			{
				lastLoc = new EntityStatePdu();
				dummyStates.put(hisId, lastLoc);

				// and pre-populate it
				EntityID eId = new EntityID();
				eId.setEntity(hisId);
				lastLoc.setEntityID(eId);
				lastLoc
						.setTimestamp(1000 + ((long) (Math.random() * 1000) / 100 * 100));
				Vector3Double eLoc = new Vector3Double();
				lastLoc.setEntityLocation(eLoc);
				eLoc.setX((1 + hisId) * 1000);
				eLoc.setY((1 + hisId) * 100);
				eLoc.setZ(-1 + hisId);

				Orientation theO = new Orientation();
				theO.setPhi(30 * (1 + hisId));
				lastLoc.setEntityOrientation(theO);

				Vector3Float linearVel = new Vector3Float();
				linearVel.setX((float) Math.cos(theO.getPhi()));
				linearVel.setY((float) Math.sin(theO.getPhi()));
				lastLoc.setEntityLinearVelocity(linearVel);

			}

			// create our PDU
			EntityStatePdu res = new EntityStatePdu();

			res.setEntityID(lastLoc.getEntityID());

			// plot on fromt the last location
			Vector3Double newLoc = new Vector3Double();
			Vector3Double oldLoc = lastLoc.getEntityLocation();
			Vector3Float oldVel = lastLoc.getEntityLinearVelocity();
			newLoc.setX(oldLoc.getX() + oldVel.getX() * (_timeStep / 1000d));
			newLoc.setY(oldLoc.getY() + oldVel.getY() * (_timeStep / 1000d));
			newLoc.setZ(oldLoc.getZ() + oldVel.getZ() * (_timeStep / 1000d));

			res.setEntityLocation(newLoc);
			res.setEntityLinearVelocity(oldVel);
			res.setEntityOrientation(lastLoc.getEntityOrientation());

			res.setTimestamp(_timeNow);

			// increment counter
			ctr++;

			// share the good news
			Iterator<IDISGeneralPDUListener> lIter = _listeners.iterator();
			while (lIter.hasNext())
			{
				IDISGeneralPDUListener thisL = (IDISGeneralPDUListener) lIter.next();
				thisL.logPDU(res);
			}

		}
	}

	@Override
	public void disconnect()
	{
		// TODO Auto-generated method stub

	}

}