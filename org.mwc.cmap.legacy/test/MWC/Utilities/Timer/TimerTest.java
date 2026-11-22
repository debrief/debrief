/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package MWC.Utilities.Timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class TimerTest {

	@Test
	public void testStopFiresNoFurtherTick() throws InterruptedException {
		final Timer timer = new Timer();
		timer.setDelay(1000);
		final AtomicInteger ticks = new AtomicInteger();
		timer.addTimerListener(e -> ticks.incrementAndGet());

		timer.start();
		Thread.sleep(100);
		// stop part-way through the first delay: stop() interrupts the sleep,
		// which used to fall through and fire one more tick
		timer.stop();
		Thread.sleep(300);

		assertFalse(timer.isRunning(), "stopped");
		assertEquals(0, ticks.get(), "no tick after stop");
	}
}
