package org.mwc.cmap.xyplot.views;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
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
	
	private static String textMatrix(final String plotTitle,
			final TimeSeriesCollection dataset)
	{
		StringBuffer dataStr = new StringBuffer();
		dataStr.append(plotTitle);
		dataStr.append(", ");
		for (int j = 0; j < dataset.getSeriesCount(); j++) 
		{	
			final TimeSeries series = dataset.getSeries(dataset.getSeriesKey(j));
			dataStr.append(series.getKey().toString());
			dataStr.append(", ");
		}
		int len = dataStr.toString().length();
		dataStr.delete(len-2, len-1);
		dataStr.append("\n");
		
		int itemsCount = Integer.MAX_VALUE;
		for (int i = 0; i < dataset.getSeriesCount(); i++) 
		{
			final TimeSeries series = dataset.getSeries(dataset.getSeriesKey(i));
			final int items = series.getItemCount();
			if (items < itemsCount)
				itemsCount = items;
		}
		
		for(int i = 0; i < itemsCount; i++)
		{
			RegularTimePeriod period = null;
			for (int j = 0; j < dataset.getSeriesCount(); j++) 
			{	
				final TimeSeries series = dataset.getSeries(dataset.getSeriesKey(j));
				if (period == null)
				{
					period = series.getTimePeriod(i);
					dataStr.append(df.format(period.getStart()));
					dataStr.append(", ");
				}
				dataStr.append(series.getDataItem(i).getValue());
				dataStr.append(", ");
			}
			period = null;
			len = dataStr.toString().length();
			dataStr.delete(len-2, len-1);
			dataStr.append("\n");
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
		public void testTwoSeriesDataset()
		{
			final TimeSeriesCollection dataset = new TimeSeriesCollection();
			final TimeSeries series1 = new TimeSeries("name1");
			Date time1 = new Date();
			Date time2 = new Date(time1.getTime() + 5000);
			series1.add(new FixedMillisecond((long) (time1.getTime() / 1000d)), 1.0);
			series1.add(new FixedMillisecond((long) (time2.getTime() / 1000d)), 2.0);
			final TimeSeries series2 = new TimeSeries("name2");
			series2.add(new FixedMillisecond((long) (time1.getTime() / 1000d)), 3.0);
			series2.add(new FixedMillisecond((long) (time2.getTime() / 1000d)), 4.0);
			dataset.addSeries(series1);
			dataset.addSeries(series2);
			
			String result = XYPlotUtilities.textMatrix("test", dataset);
			System.out.println(result);
			String expected = "test, name1 name2 \n" + 
					"17/jan/1970 06:43, 1.0, 3.0 \n" +  
					"17/jan/1970 06:43, 2.0, 4.0 \n";
			assertEquals(expected, result);
		}
	}

}

