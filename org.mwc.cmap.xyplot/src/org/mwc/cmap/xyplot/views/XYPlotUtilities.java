/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.xyplot.views;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


public class XYPlotUtilities 
{
	
	final static SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy HH:mm");
	
	public static void copyToClipboard(final String plotTitle,
			final TimeSeriesCollection dataset)
	{		
		final String dataStr = textMatrix(plotTitle, dataset);
		new TextTransfer().setClipboardContents(dataStr.toString());		
	}
	
	private synchronized static String textMatrix(final String plotTitle,
			final TimeSeriesCollection dataset)
	{
		StringBuffer dataStr = new StringBuffer();
		
		for(int i = 0; i < dataset.getSeriesCount(); i++)
		{
			final TimeSeries series = dataset.getSeries(dataset.getSeriesKey(i));
			dataStr.append(plotTitle);
			dataStr.append(", ");
			dataStr.append(series.getKey().toString());
			dataStr.append("\n");
			
			for (int j = 0; j < series.getItemCount(); j++) 
			{	
				dataStr.append(df.format(series.getTimePeriod(j).getStart()));
				dataStr.append(", ");				
				dataStr.append(series.getDataItem(j).getValue());
				dataStr.append("\n");
			}			
		}
		return dataStr.toString();
	}
	
	static final class TextTransfer implements ClipboardOwner 
	{
		  private TextTransfer() {}
		  
		  @Override public void lostOwnership(Clipboard aClipboard, Transferable aContents)
		  {
		     //do nothing
		  }

		  /**
		  * Place a String on the clipboard, and make this class the
		  * owner of the Clipboard's contents.
		  */
		  public void setClipboardContents(String aString)
		  {
			  StringSelection stringSelection = new StringSelection(aString);
			  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			  clipboard.setContents(stringSelection, this);
		  }
		 
	} 
	
	static public final class XYPlotUtilitiesTest extends junit.framework.TestCase
	{
		public synchronized void testTwoSeriesDataset()
		{
			final TimeSeriesCollection dataset = new TimeSeriesCollection();
			final TimeSeries series1 = new TimeSeries("name1");
			Date time1 = new Date();
			Date time2 = new Date(time1.getTime() + 60000);
			series1.add(new FixedMillisecond((long) (time1.getTime() / 1d)), 1.0);
			series1.add(new FixedMillisecond((long) (time2.getTime() / 1d)), 2.0);
			final TimeSeries series2 = new TimeSeries("name2");
			series2.add(new FixedMillisecond((long) (time1.getTime() / 1d)), 3.0);
			series2.add(new FixedMillisecond((long) (time2.getTime() / 1d)), 4.0);
			dataset.addSeries(series1);
			dataset.addSeries(series2);
			
			String result = XYPlotUtilities.textMatrix("test", dataset);
			System.out.println(result);
			String expected = "test, name1\n" + 
					df.format(time1) + ", 1.0\n" +  
					df.format(time2) + ", 2.0\n" +
					"test, name2\n" + 
					df.format(time1) + ", 3.0\n" +  
					df.format(time2) +", 4.0\n";
			assertEquals(expected, result);
		}
	}

}

