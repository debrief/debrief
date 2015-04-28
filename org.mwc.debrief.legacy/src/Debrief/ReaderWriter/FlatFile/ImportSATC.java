package Debrief.ReaderWriter.FlatFile;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class ImportSATC
{

	/**
	 * where we write our data
	 * 
	 */
	private final Layers _layers;


	public ImportSATC(final Layers target)
	{
		super();
		_layers = target;
	}

	public void importThis(final String fName, final InputStream is)
			throws Exception
	{
		// ok, loop through the lines
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		
		TrackWrapper myTrack = null;

		String satc_sentence;
		
		// ok, ditch the first line.
		String firstLine = br.readLine();
		
		WorldLocation thisLocation = new WorldLocation(60, -12, 0);
		SimpleDateFormat sds = new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
		                                             
		sds.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		boolean positionsComplete = false;
		
		Date lastDate = null;

		// loop through the remaining lines
		while ((satc_sentence = br.readLine()) != null)
		{

			// ok,  are we now on the bearings?
			if(satc_sentence.startsWith("//"))
			{
				positionsComplete = true;
				
				// reset the date
				lastDate = null;
				// carry on with the next line
				continue;
			}
			
			// ok, are we working with a position, or a sensor cut?
			if(!positionsComplete)
			{
				//  ok, parse the position
				String[] elements = satc_sentence.split(",");
				double x = Double.valueOf(elements[0]);
				double y = Double.valueOf(elements[1]);
				String date = elements[2];
				double courseDegs = Double.valueOf(elements[3]);
				double speedKts = Double.valueOf(elements[4]);
				
				double courseRads = Math.toRadians(courseDegs);
				double speedYps = new WorldSpeed(speedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3;
				
				Date theTime = sds.parse(date);
				
				WorldVector vector = null;
				
				// ok, have we passed the first time?
				if(lastDate != null)
				{
					// ok, sort out the vector
					long elapsedMillis = theTime.getTime() - lastDate.getTime();
					double elapsedSecs = elapsedMillis / 1000d;
					double speedMs = new WorldSpeed(speedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
					WorldDistance dist = new WorldDistance(speedMs / elapsedSecs, WorldSpeed.M_sec);
					vector = new WorldVector(Math.toRadians(courseDegs), dist, null);
					thisLocation = thisLocation.add(vector);
				}
				else
				{
					// first location = do some start up
					myTrack = new TrackWrapper();
					myTrack.setName(fName);
					myTrack.setColor(Color.red);
					this._layers.addThisLayer(myTrack);
				}

				// ok, create the entry
				Fix newFix = new Fix(new HiResDate(theTime.getTime()), thisLocation, courseRads, speedYps);
				
				System.out.println("new time is:" + newFix.getTime().getDate());
				
				
				FixWrapper fw = new FixWrapper(newFix);
				myTrack.add(fw);

				// and move the time along
				lastDate = theTime;
			}
			else
			{
				// ok, parse the sensor data
			}
				
		}
	}
}
