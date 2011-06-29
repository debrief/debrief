package org.mwc.asset.netassetclient.test;

import java.io.IOException;
import java.util.Vector;

import org.mwc.asset.comms.kryo.common.ACallback;
import org.mwc.asset.comms.kryo.common.ASpecs;
import org.mwc.asset.netassetclient.core.CPresenter;
import org.mwc.asset.netassetclient.core.CView;

import ASSET.NetworkScenario;

public class TClient implements ASpecs
{
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		CView list = new CView(){

			@Override
			public void showMessage(String string)
			{
				System.out.println(string);
			}};
		final CPresenter cp = new CPresenter(list );
		
		ACallback<ScenarioList> scL = new ACallback<ScenarioList>(){

			@Override
			public void onSuccess(ScenarioList result)
			{
				Vector<NetworkScenario> sList = result.scenarios;
				System.out.println("got:" + sList.size() + " scenarios");
				
				// now go for first scenario
				String name = sList.elementAt(0).getName();
				ACallback<NetworkScenario> tsL = new ACallback<NetworkScenario>(){

					@Override
					public void onSuccess(NetworkScenario result)
					{
						Integer[] list = result.getListOfParticipants();
						System.out.println("got:" + list.length + " parts");
					}};
				cp.getScenario(name, tsL);
			}};
		cp.getScenarioList(scL);
		
		
		System.out.println("pausing");
		System.in.read();
		
	}
}
