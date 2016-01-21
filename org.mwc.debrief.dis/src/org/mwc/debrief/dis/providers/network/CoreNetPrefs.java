package org.mwc.debrief.dis.providers.network;

public class CoreNetPrefs implements IDISNetworkPrefs
{

	private final String _address;
	private final int _port;

	public CoreNetPrefs(String address, int port)
	{
		_address = address;
		_port = port;
	}

	@Override
	public String getIPAddress()
	{
		return _address;
	}

	@Override
	public int getPort()
	{
		return _port;
	}

}
