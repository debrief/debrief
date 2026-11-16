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



public class TA_Modules_DataHandler extends TA_ForeAft_DataHandler {

	public TA_Modules_DataHandler() {
		super("TA_MODULES", "Acoustic Modules");
	}

	@Override
	protected boolean checkIfLong(final String[] tokens) {
		boolean res = true;
		for (int i = 0; i < 8; i++) {
			final String nextT = tokens[i];
			if (isNull(nextT)) {
				res = false;
				break;
			} else {
				final double val = Double.valueOf(tokens[i]);
				if (val != 0d) {
					res = false;
					break;
				}
			}
		}
		return res;
	}

	@Override
	protected String nameForRow(final int ctr, final boolean isLong) {
		return isLong ? "" + (1 + (ctr - 8) / 2) : "" + (1 + (ctr / 2));
	}
}
