package org.mwc.debrief.dis.listeners.impl;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.debrief.dis.listeners.impl.PerformanceQueue.*;
import org.mwc.debrief.dis.listeners.impl.PerformanceQueue.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.jupiter.api.Test;

public class PerformanceQueueTest {

	@Test
	public void testStack() {
		final PerformanceQueue pq = new PerformanceQueue(3000, "test");
		pq.add(1000);
		assertEquals(1, pq.size(), "correct len");
		assertEquals(0.0, pq.freqAt(1000), "correct freq");
		pq.add(2000);
		assertEquals(0.002, pq.freqAt(2000), "correct freq");
		assertEquals(2, pq.size(), "correct len");
		pq.add(3000);
		assertEquals(0.0015, pq.freqAt(3000), "correct freq");
		assertEquals(3, pq.size(), "correct len");
		pq.add(4000);
		assertEquals(0.0015, pq.freqAt(4000), "correct freq");
		assertEquals(3, pq.size(), "correct len");
		pq.add(4500);
		assertEquals(0.0016, pq.freqAt(4500), "correct freq");
		assertEquals(4, pq.size(), "correct len");
		pq.add(5000);
		assertEquals(0.002, pq.freqAt(5000), "correct freq");
		assertEquals(4, pq.size(), "correct len");
		pq.add(6000);
		assertEquals(0.002, pq.freqAt(6000), "correct freq");
		assertEquals(4, pq.size(), "correct len");
		pq.add(7000);
		assertEquals(0.0016, pq.freqAt(7000), "correct freq");
		assertEquals(4, pq.size(), "correct len");
		pq.add(8000);
		assertEquals(0.0015, pq.freqAt(8000), "correct freq");
		assertEquals(3, pq.size(), "correct len");
	}
}
