package org.mwc.asset.netasset2.view;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.events.SelectionAdapter;

public interface IVClient
{


	public  ListViewer getScenarioList();

	public  ListViewer getServerList();

	void addPingListener(SelectionAdapter selectionAdapter);
	void addServerListener(IDoubleClickListener selectionAdapter);
	void addScenarioListener(IDoubleClickListener iDoubleClickListener);

	void disableServers();
	void enableServers();
	void disableScenarios();
	void enableScenarios();


}
