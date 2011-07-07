package org.mwc.asset.netasset2.connect;

import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.mwc.asset.netasset2.common.Network.LightParticipant;
import org.mwc.asset.netasset2.common.Network.LightScenario;

public interface IVConnect
{

	public static interface ClickHandler
	{
		public void clicked();
	};

	public static interface ServerSelected
	{
		public void selected(InetAddress address);
	}

	public static interface ScenarioSelected
	{
		public void selected(LightScenario scenario);
	}

	public static interface ParticipantSelected
	{
		public void selected(LightParticipant participant);
	}

	void addPingListener(ClickHandler selectionAdapter);

	void addServerListener(ServerSelected selectionAdapter);

	void addScenarioListener(ScenarioSelected iDoubleClickListener);

	void addDisconnectListener(ClickHandler handler);

	void disableServers();

	void enableServers();

	void disableScenarios();

	void enableScenarios();

	void disableParticipants();
	void enableParticipants();

	public void addParticipantListener(ParticipantSelected listener);

	public void setPartLabelProvider(IBaseLabelProvider labelProvider);

	public void setPartContentProvider(IContentProvider provider);

	public void setParticipants(Vector<LightParticipant> listOfParticipants);

	public void setScenarios(Vector<LightScenario> results);

	void setServers(List<InetAddress> adds);


}
