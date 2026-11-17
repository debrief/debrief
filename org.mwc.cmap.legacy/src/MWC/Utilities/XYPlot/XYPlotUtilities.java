/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.Utilities.XYPlot;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.text.DateFormat;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import MWC.Utilities.TextFormatting.GMTDateFormat;

public class XYPlotUtilities {

	static final class TextTransfer implements ClipboardOwner {
		private TextTransfer() {
		}

		@Override
		public void lostOwnership(final Clipboard aClipboard, final Transferable aContents) {
			// do nothing
		}

		/**
		 * Place a String on the clipboard, and make this class the owner of the
		 * Clipboard's contents.
		 */
		public void setClipboardContents(final String aString) {
			final StringSelection stringSelection = new StringSelection(aString);
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		}

	}

	final static DateFormat df = new GMTDateFormat("dd/MMM/yyyy HH:mm");

	public static void copyToClipboard(final String plotTitle, final TimeSeriesCollection dataset) {
		final String dataStr = textMatrix(plotTitle, dataset);
		new TextTransfer().setClipboardContents(dataStr);
	}

	synchronized static String textMatrix(final String plotTitle, final TimeSeriesCollection dataset) {
		final StringBuffer dataStr = new StringBuffer();

		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			final TimeSeries series = dataset.getSeries(dataset.getSeriesKey(i));
			dataStr.append(plotTitle);
			dataStr.append(", ");
			dataStr.append(series.getKey().toString());
			dataStr.append("\n");

			for (int j = 0; j < series.getItemCount(); j++) {
				dataStr.append(df.format(series.getTimePeriod(j).getStart()));
				dataStr.append(", ");
				dataStr.append(series.getDataItem(j).getValue());
				dataStr.append("\n");
			}
		}
		return dataStr.toString();
	}

}
