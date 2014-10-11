/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: ian.mayo
 * Date: 25-Nov-2004
 * Time: 16:26:20
 * To change this template use File | Settings | File Templates.
 */
public class HiFreqTimeStepPropertyEditor extends TimeStepPropertyEditor
{
 /** put the items into the lists.  We do it here so that
   * we can over-ride it to provide the hi-res timers
   */
  protected void initialiseLists()
  {
    if (_stringTags == null)
    {

      _stringTags = new String[]
      {
        "10 Micros",
        "50 Micros",
        "100 Micros",
        "1 Milli",
        "5 Milli",
        "10 Millis",
        "100 Millis",
        "1 Sec"};

      _freqs =
        new long[]
        {
          10,
          50,
          100,
          1000,
          5000,
          10000,
          100000,
          1000000
        };
    }
  }
}
