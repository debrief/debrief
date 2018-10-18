/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class GenerateBenchmarkingRepForPPT
{
  public static void main(String[] args) throws IOException
  {
    DateFormat dateFormat = new SimpleDateFormat("yyMMdd HHmmss.SSS");

    final int amountOfSteps = 5000;
    final String vesselName = "Benchmarking";
    final String vesselShape = "@C";
    WorldLocation worldLocation = new WorldLocation(12.3, 12.4, 12.5);
    
    Calendar currentDate = Calendar.getInstance();

    File tempFile = File.createTempFile("benchmarking-", ".rep");
    PrintWriter printWriter = new PrintWriter(tempFile);
    for (int i = 0; i < amountOfSteps; i++)
    {
      StringBuilder builder = new StringBuilder();
      
      builder.append(dateFormat.format(currentDate.getTime()));
      currentDate.add(Calendar.MINUTE, 1);
      
      builder.append(" ");
      builder.append(vesselName);
      builder.append(" ");
      builder.append(vesselShape);
      
      String worldLocationString = worldLocation.toString().replaceAll("[^a-zA-Z0-9-_\\\\. ]", " ");
      builder.append(worldLocationString);
      
      builder.append("0 5 0");

      final WorldVector movementDelta = new WorldVector(Math.random() * 100, Math.random() * 100, Math.random() * 100);
      worldLocation = worldLocation.add(movementDelta);
      
      printWriter.println(builder);
    }
    printWriter.close();
    System.out.println(tempFile.getAbsolutePath());
  }
}
