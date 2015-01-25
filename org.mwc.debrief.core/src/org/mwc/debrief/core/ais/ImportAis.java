package org.mwc.debrief.core.ais;

import java.awt.Color;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Status;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.PlainImporterBase;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage4;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.reader.AisReader;
import dk.dma.ais.reader.AisReaders;
import dk.dma.enav.util.function.Consumer;

public class ImportAis extends PlainImporterBase
{

	private Map<String, TrackWrapper> trackWrappers = new HashMap<String, TrackWrapper>();
	private HiResDate theDate;

	public ImportAis()
	{
		super();
		_myTypes = new String[]{".ais"};
	}

	@Override
	public boolean canImportThisFile(String theFile)
	{
		boolean res = true;
    String theSuffix=null;
    final int pos = theFile.lastIndexOf(".");
    theSuffix = theFile.substring(pos, theFile.length());

    for(int i=0; i<_myTypes.length; i++)
    {
      if(theSuffix.equalsIgnoreCase(_myTypes[i]))
      {
        res = true;
        break;
      }
    }

    return res;
	}

	@Override
	public void exportThis(Plottable item)
	{
	}

	@Override
	public void exportThis(String comment)
	{
	}

	@Override
	public void importThis(String fName, InputStream is)
	{
		AisReader reader = AisReaders.createReaderFromInputStream(is);
		theDate = new HiResDate();
		reader.registerHandler(new Consumer<AisMessage>()
		{
			public void accept(AisMessage aisMessage)
			{
				if (aisMessage.getMsgId() == 4)
				{
					AisMessage4 a4 = (AisMessage4) aisMessage;
					System.out.println("date:" + a4.getDate());
				}
				else if (aisMessage.getMsgId() == 1)
				{
					AisPositionMessage apm = (AisPositionMessage) aisMessage;
//				System.out.println("id: " + apm.getMsgId());
//				System.out.println("userId: " + apm.getUserId());
//				System.out.println("longitude: " + apm.getPos().getLongitudeDouble());
//				System.out.println("latitude: " + apm.getPos().getLatitudeDouble());
//				System.out.println("speed over ground: " + apm.getSog());
//				System.out.println("course over ground: " + apm.getCog());
//				System.out.println("utcsec: " + apm.getUtcSec());
//				System.out.println("utcsec: " + apm.getVdm().getOrgLinesJoined());
					
					double lat = apm.getPos().getLatitudeDouble();
					double lon = apm.getPos().getLongitudeDouble();
					if (!apm.isPositionValid() || Math.abs(lat) >= 90.0 || Math.abs(lon) >= 180.0 )
					{
						return;
					}
					String name = new Integer(apm.getUserId()).toString();
					TrackWrapper trackWrapper = trackWrappers.get(name);
					if (trackWrapper == null) {
						trackWrapper = new TrackWrapper();
						addLayer(trackWrapper);
						trackWrapper.setName(name);
						trackWrapper.setColor(Color.BLUE);
						trackWrapper.setSymbolColor(Color.BLUE);
						trackWrappers.put(name, trackWrapper);
					}
					theDate = new HiResDate(theDate.getDate().getTime() + apm.getUtcSec()*1000);
					WorldLocation loc = new WorldLocation(lat, lon, 0);
					int course = apm.getCog();
					int speed = apm.getSog();
					Fix fix = new Fix(theDate, loc, course, speed);
					FixWrapper fixWrapper = new FixWrapper(fix);
					fixWrapper.setTrackWrapper(trackWrapper);
					fixWrapper.setColor(Color.BLUE);
					fixWrapper.setLabelFormat("ddHHmm.ss");
					trackWrapper.addFix(fixWrapper);
				}
			}
		});
		reader.start();
		try
		{
			reader.join();
		}
		catch (InterruptedException e)
		{
			DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
					+ fName, e);
		}
	}

}
