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
package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.FindMap;

public class FindMapTest {
	@Test
	public void testGetMapDetails() throws DebriefException {
		final String sampleDonorPathFile = Utils.testFolder + File.separator + "FindMap";

		assertEquals(FindMap.getMapDetails(sampleDonorPathFile), new HashMap<String, String>() {
			/**
			 * Known Result
			 */
			private static final long serialVersionUID = -4264437335359313998L;

			{
				put("cx", "6703821");
				put("cy", "4670507");
				put("name", "map");
				put("x", "2486111");
				put("y", "265548");
			}
		});
	}
}
