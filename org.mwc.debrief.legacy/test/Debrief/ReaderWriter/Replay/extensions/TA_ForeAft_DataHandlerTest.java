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

package Debrief.ReaderWriter.Replay.extensions;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.Replay.extensions.TA_ForeAft_DataHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.ReaderWriter.Replay.extensions.TA_ForeAft_DataHandler.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

public class TA_ForeAft_DataHandlerTest {
	//
	private final List<String> _messages = new ArrayList<String>();

	@Test
	public void testImport() throws ParseException {
		final String str = ";TA_FORE_AFT: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" 12.75 3.00\t14.75 10.00";
		final TA_ForeAft_DataHandler ff = new TA_ForeAft_DataHandler() {

			@Override
			protected void storeMeasurement(final String platform_name, final String sensor_name,
					final String folder, final String dataset, final String units, final HiResDate theDate,
					final double measurement) {
				super.storeMeasurement(platform_name, sensor_name, folder, dataset, units, theDate, measurement);
				final String outStr = "stored:" + dataset + " value of:" + measurement;
				_messages.add(outStr);

			}

		};
		ff.readThisLine(str);

		assertEquals(4, _messages.size(), "found items");

	}
}
