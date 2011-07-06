package org.mwc.asset.netasset2.connect;

import java.util.Vector;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.mwc.asset.netasset2.common.Network.LightParticipant;

public interface IVConnect
{

	public ListViewer getScenarioList();

	public ListViewer getServerList();

	void addPingListener(SelectionAdapter selectionAdapter);

	void addServerListener(IDoubleClickListener selectionAdapter);

	void addScenarioListener(IDoubleClickListener iDoubleClickListener);

	void disableServers();

	void enableServers();

	void disableScenarios();

	void enableScenarios();

	public void addParticipantListener(IDoubleClickListener listener);

	public void setPartLabelProvider(IBaseLabelProvider labelProvider);

	public void setPartContentProvider(IContentProvider provider);

	public void setParticipants(Vector<LightParticipant> listOfParticipants);

}
