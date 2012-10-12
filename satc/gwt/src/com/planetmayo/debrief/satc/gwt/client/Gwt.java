package com.planetmayo.debrief.satc.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.planetmayo.debrief.satc.gwt.client.services.GWTConverterService;
import com.planetmayo.debrief.satc.gwt.client.services.GWTIOService;
import com.planetmayo.debrief.satc.gwt.client.services.GWTLogService;
import com.planetmayo.debrief.satc.gwt.client.ui.RootLayout;
import com.planetmayo.debrief.satc.support.SupportServices;

public class Gwt implements EntryPoint
{

	public void onModuleLoad()
	{
		RootPanel.get().add(new RootLayout());

		SupportServices.INSTANCE.initialize(new GWTLogService(),
				new GWTConverterService(), new GWTIOService());
	}
}
