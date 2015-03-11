package org.mwc.cmap.ais3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.freeais.ais.AISBaseStation;
import org.freeais.ais.AISParseException;
import org.freeais.ais.AISParser;
import org.freeais.ais.AISPositionA;
import org.freeais.ais.AISPositionB;
import org.freeais.ais.AISVessel;
import org.freeais.ais.IAISMessage;

public class TestImport
{
	public static void main(String[] args) throws FileNotFoundException,
			IOException, AISParseException
	{
		System.out.println("running");

		// ok, get the file
		File inFile = new File("data/150304_0854.txt");
		if (inFile.exists())
		{
			System.out.println("doing import");
			boolean debug = true;

			AISParser parser = new AISParser();

			// ok, loop through the lines
			try (BufferedReader br = new BufferedReader(new FileReader(inFile)))
			{

				String nmea_sentence;
				while ((nmea_sentence = br.readLine()) != null)
				{
					if(nmea_sentence.endsWith("3D"))
						System.out.println("here");
					
					IAISMessage res = parser.parse(nmea_sentence);
					
					

					if(res instanceof AISPositionA)
					{
						AISPositionA ar = (AISPositionA) res;
						System.out.println("A Lat:" + ar.getLatitude() + " Lon:" + ar.getLongitude() + 
								" Secs:" + ar.getMsgTimestamp().getSeconds());
					}
					else if(res instanceof AISPositionB)
					{
						AISPositionB ar = (AISPositionB) res;
						System.out.println("B Lat:" + ar.getLatitude() + " Lon:" + ar.getLongitude() + 
								" Secs:" + ar.getMsgTimestamp().getSeconds());
						
					}
					else if(res instanceof AISBaseStation)
					{
						AISBaseStation base = (AISBaseStation) res;
						System.out.println("Base time:" + base.getTimestamp());
					}
					else if(res instanceof AISVessel)
					{
						AISVessel vess = (AISVessel) res;
					}
					else
					{
						System.out.println(res);						
					}
					
				}
			}

		}

	}

}
