package org.mwc.debrief.dis.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.dis.listener.DISListenerTest.IDISFixListener;
import org.mwc.debrief.dis.listener.DISListenerTest.IDISModule;
import org.mwc.debrief.dis.listener.DISListenerTest.IDISNetworkPrefs;
import org.mwc.debrief.dis.listener.DISListenerTest.IDISScenarioListener;
import org.mwc.debrief.dis.listener.DISListenerTest.IPDUProvider;

import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;
import edu.nps.moves.disutil.CoordinateConversions;

public class DISModule implements IDISModule
{
	private List<IDISFixListener> _fixListeners = new ArrayList<IDISFixListener>();
	private IDISNetworkPrefs _prefs;

	public DISModule()
	{

	}

	@Override
	public void addFixListener(IDISFixListener handler)
	{
		_fixListeners .add(handler);
	}

	@Override
	public void addScenarioListener(IDISScenarioListener handler)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrefs(IDISNetworkPrefs prefs)
	{
		_prefs = prefs;
	}

	@Override
	public Object getPrefs()
	{
		return _prefs;
	}

	@Override
	public void setProvider(IPDUProvider provider)
	{
		while (provider.hasMoreElements())
		{
			Pdu data = provider.next();

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

	private void handleFix(EntityStatePdu pdu)
	{
		// unpack the data
		final long hisId = pdu.getEntityID().getEntity();
		final long time = pdu.getTimestamp();
		Vector3Double loc = pdu.getEntityLocation();
		double[] worldCoords = CoordinateConversions.getXYZfromLatLonDegrees(loc.getX(), loc.getY(), loc.getZ());
		Orientation orientation = pdu.getEntityOrientation();
		Vector3Float velocity = pdu.getEntityLinearVelocity();
		
		// entity state
		Iterator<IDISFixListener> fIter = _fixListeners.iterator();
		while (fIter.hasNext())
		{
			DISListenerTest.IDISFixListener thisF = (DISListenerTest.IDISFixListener) fIter
					.next();
			thisF.add(time, hisId, worldCoords[0], worldCoords[1], worldCoords[2], orientation.getPhi(), velocity.getX());
		}
	}

}
