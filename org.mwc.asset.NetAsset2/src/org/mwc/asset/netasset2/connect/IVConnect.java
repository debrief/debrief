/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.netasset2.connect;

import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.mwc.asset.netCore.common.Network.LightParticipant;
import org.mwc.asset.netCore.common.Network.LightScenario;

public interface IVConnect
{

	public static interface BooleanHandler
	{
		public void change(boolean val);
	};

	public static interface ClickHandler
	{
		public void clicked();
	}

	public static interface ParticipantSelected
	{
		public void selected(LightParticipant participant);
	}

	public static interface ScenarioSelected
	{
		public void selected(LightScenario scenario);
	}

	public static interface ServerSelected
	{
		public void selected(InetAddress address);
	}

	public static interface StringProvider
	{
		/**
		 * get a string from the user
		 * 
		 * @param title
		 * @param message
		 * @return
		 */
		public String getString(String title, String message);
	}

	void addDisconnectListener(ClickHandler handler);

	void addManualListener(ClickHandler handler);

	public void addParticipantListener(ParticipantSelected listener);

	void addPingListener(ClickHandler selectionAdapter);

	void addScenarioListener(ScenarioSelected iDoubleClickListener);

	public void addSelfHostListener(final BooleanHandler yesNo);

	void addServerListener(ServerSelected selectionAdapter);

	void disableDisconnect();

	void disableParticipants();

	void disableScenarios();

	void disableServers();

	void enableDisconnect();

	void enableParticipants();

	void enableScenarios();

	void enableServers();

	String getString(String title, String message);

	public void setPartContentProvider(IContentProvider provider);

	public void setParticipants(Vector<LightParticipant> listOfParticipants);

	public void setPartLabelProvider(IBaseLabelProvider labelProvider);

	public void setScenarios(Vector<LightScenario> results);

	void setServers(List<InetAddress> adds);

	public void clearServers();

}
