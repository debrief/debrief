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

package MWC.GenericData;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GenericData.TimePeriod.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GenericData.TimePeriod.*;
import java.util.Date;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

public class TimePeriodTest {
	@SuppressWarnings("deprecation")
	@Test
	public void testExtent() {
		final HiResDate t1 = new HiResDate(new Date(2016, 3, 3, 12, 00, 00));
		final HiResDate t4 = new HiResDate(new Date(2016, 3, 3, 12, 01, 00));

		final BaseTimePeriod p1 = new BaseTimePeriod(t1, t4);
		assertEquals(60 * 1000, p1.getExtent(), "correct extent");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIntersects() {
		final HiResDate t1 = new HiResDate(new Date(2016, 3, 3));
		final HiResDate t2 = new HiResDate(new Date(2016, 3, 4));
		final HiResDate t3 = new HiResDate(new Date(2016, 3, 5));
		final HiResDate t4 = new HiResDate(new Date(2016, 3, 6));
		final HiResDate t5 = new HiResDate(new Date(2016, 3, 7));

		BaseTimePeriod p1 = new BaseTimePeriod(t1, t4);
		BaseTimePeriod p2 = new BaseTimePeriod(t2, t3);
		TimePeriod overlap = p1.intersects(p2);
		assertEquals(overlap.getStartDTG(), t2, "inner period");
		assertEquals(overlap.getEndDTG(), t3, "inner period");

		p1 = new BaseTimePeriod(t1, t4);
		p2 = new BaseTimePeriod(t2, t3);
		overlap = p2.intersects(p1);
		assertEquals(overlap.getStartDTG(), t2, "inner period");
		assertEquals(overlap.getEndDTG(), t3, "inner period");

		p1 = new BaseTimePeriod(t1, t3);
		p2 = new BaseTimePeriod(t2, t4);
		overlap = p2.intersects(p1);
		assertEquals(overlap.getStartDTG(), t2, "inner period");
		assertEquals(overlap.getEndDTG(), t3, "inner period");

		p1 = new BaseTimePeriod(t1, t5);
		p2 = new BaseTimePeriod(t2, t4);
		overlap = p2.intersects(p1);
		assertEquals(overlap.getStartDTG(), t2, "inner period");
		assertEquals(overlap.getEndDTG(), t4, "inner period");

	}
}
