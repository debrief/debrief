package org.mwc.debrief.dis.listeners.impl;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.TestCase;

public class PerformanceQueue
{
  final Queue<Long> myQue;
  final String myName;
  private long _period;

  public PerformanceQueue(long period, String name)
  {
    myQue = new ConcurrentLinkedQueue<Long>();
    myName = name;
    _period = period;
  }

  public static class TestMe extends TestCase
  {

    public void testStack()
    {
      PerformanceQueue pq = new PerformanceQueue(3000, "test");
      pq.add(1000);
      assertEquals("correct len", 1, pq.size());
      assertEquals("correct freq", 0.0, pq.freqAt(1000));
      pq.add(2000);
      assertEquals("correct freq", 0.002, pq.freqAt(2000));
      assertEquals("correct len", 2, pq.size());
      pq.add(3000);
      assertEquals("correct freq", 0.0015, pq.freqAt(3000));
      assertEquals("correct len", 3, pq.size());
      pq.add(4000);
      assertEquals("correct freq", 0.0015, pq.freqAt(4000));
      assertEquals("correct len", 3, pq.size());
      pq.add(4500);
      assertEquals("correct freq", 0.0016, pq.freqAt(4500));
      assertEquals("correct len", 4, pq.size());
      pq.add(5000);
      assertEquals("correct freq", 0.002, pq.freqAt(5000));
      assertEquals("correct len", 4, pq.size());
      pq.add(6000);
      assertEquals("correct freq", 0.002, pq.freqAt(6000));
      assertEquals("correct len", 4, pq.size());
      pq.add(7000);
      assertEquals("correct freq", 0.0016, pq.freqAt(7000));
      assertEquals("correct len", 4, pq.size());
      pq.add(8000);
      assertEquals("correct freq", 0.0015, pq.freqAt(8000));
      assertEquals("correct len", 3, pq.size());
    }
  }

  public void main(String args[])
  {

  }

  public double freqAt(long time)
  {
    final double res;

    if (myQue.isEmpty())
      return 0;

    // what's the elapsed period:
    long period = time - myQue.peek();

    if (period == 0)
    {
      res = 0;
    }
    else
    {
      res = myQue.size() / (double) period;
    }

    // throw in a flush
    flush(time);

    return res;
  }

  public Object size()
  {
    return myQue.size();
  }

  public void add(long timeNow)
  {
    myQue.add(timeNow);

    flush(timeNow);
  }

  private void flush(long timeNow)
  {
    long _startTime = timeNow - _period;

    // remove any that are too old
    while (!myQue.isEmpty() && myQue.peek() <= _startTime)
    {
      myQue.remove();
    }
  }

  public void clear()
  {
    myQue.clear();
  }
}
