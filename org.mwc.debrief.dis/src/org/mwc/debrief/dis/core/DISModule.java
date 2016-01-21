package org.mwc.debrief.dis.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;
import edu.nps.moves.disutil.CoordinateConversions;

public class DISModule implements IDISModule, IDISGeneralPDUListener
{
	private IDISPreferences _disPrefs = null;
	private List<IDISFixListener> _fixListeners = new ArrayList<IDISFixListener>();
	private List<IDISGeneralPDUListener> _generalListeners = new ArrayList<IDISGeneralPDUListener>();
	private List<IDISScenarioListener> _scenarioListeners = new ArrayList<IDISScenarioListener>();
	private boolean _newStart = false;

	public DISModule()
	{

	}

	@Override
	public void addFixListener(IDISFixListener handler)
	{
		_fixListeners.add(handler);
	}

	@Override
	public void addScenarioListener(IDISScenarioListener handler)
	{
		_scenarioListeners.add(handler);
	}

	@Override
	public void setProvider(IPDUProvider provider)
	{
		// remember we're restarting
		_newStart = true;

		// register as a listener, to hear about new data
		provider.addListener(this);
	}

	private void handleFix(EntityStatePdu pdu)
	{
		// unpack the data
		final long hisId = pdu.getEntityID().getEntity();
		final long time = pdu.getTimestamp();
		Vector3Double loc = pdu.getEntityLocation();
		double[] worldCoords = CoordinateConversions.getXYZfromLatLonDegrees(
				loc.getX(), loc.getY(), loc.getZ());
		Orientation orientation = pdu.getEntityOrientation();
		Vector3Float velocity = pdu.getEntityLinearVelocity();

		// entity state
		Iterator<IDISFixListener> fIter = _fixListeners.iterator();
		while (fIter.hasNext())
		{
			IDISFixListener thisF = (IDISFixListener) fIter.next();
			thisF.add(time, hisId, worldCoords[0], worldCoords[1], worldCoords[2],
					orientation.getPhi(), velocity.getX());
		}
	}

	@Override
	public void addGeneralPDUListener(IDISGeneralPDUListener listener)
	{
		_generalListeners.add(listener);
	}

	@Override
	public IDISPreferences getPrefs()
	{
		return _disPrefs;
	}

	@Override
	public void setPrefs(IDISPreferences preferences)
	{
		_disPrefs = preferences;
	}

	@Override
	public void logPDU(Pdu data)
	{
		// is this new?
		if (_newStart)
		{
			// share the good news
			Iterator<IDISScenarioListener> sIter = _scenarioListeners.iterator();
			while (sIter.hasNext())
			{
				IDISScenarioListener sl = (IDISScenarioListener) sIter.next();
				sl.restart();
			}
			_newStart = false;
		}

		// give it to any general listenrs
		Iterator<IDISGeneralPDUListener> gIter = _generalListeners.iterator();
		while (gIter.hasNext())
		{
			IDISGeneralPDUListener gPdu = (IDISGeneralPDUListener) gIter.next();
			gPdu.logPDU(data);
		}

		// check the type
		final short type = data.getPduType();
		switch (type)
		{
		case 1:
		{
			handleFix((EntityStatePdu) data);
			break;
		}
		default:
			throw new RuntimeException("PDU type not handled:" + type);
		}
	}

}
