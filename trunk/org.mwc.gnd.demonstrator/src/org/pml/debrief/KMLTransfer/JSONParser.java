package org.pml.debrief.KMLTransfer;

import java.util.ArrayList;
import java.util.Date;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Track;

public class JSONParser
{
	public JSONParser(String dirName)
	{
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		// get the directory name
		String dirName = "";
		
		// get the coords
		WorldLocation tl = new WorldLocation(-5, 50, 0);
		WorldLocation br = new WorldLocation(-4, 52, 0);
		WorldArea wa = new WorldArea(tl, br);

		// get the time period
		HiResDate start = new HiResDate(new Date(2012,2,20,10,30,0));
		HiResDate end = new HiResDate(new Date(2012,2,20,14,00,0));
		TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(start, end);
		
		// go for it
		
		JSONParser parser = new JSONParser(dirName);

		ArrayList<Track> tracks = parser.extractDataFor(timePeriod, wa);
		
		// ok, now output them.
		dumpToREP(tracks);
	}

	private static void dumpToREP(ArrayList<Track> tracks)
	{
		// TODO Auto-generated method stub
		
	}

	private ArrayList<Track> extractDataFor(TimePeriod timePeriod, WorldArea wa)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
