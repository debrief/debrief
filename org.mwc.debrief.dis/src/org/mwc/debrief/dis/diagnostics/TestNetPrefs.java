package org.mwc.debrief.dis.diagnostics;

import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;

public class TestNetPrefs implements IDISNetworkPrefs
{
	final static String IP = "127.0.0.1";
	static final int PORT = 2000;

	@Override
	public String getIPAddress()
	{
		return IP;
	}

	@Override
	public int getPort()
	{
		return PORT;
	}

}