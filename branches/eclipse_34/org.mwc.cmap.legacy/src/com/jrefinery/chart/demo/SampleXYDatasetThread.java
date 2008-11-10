/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * --------------------------
 * SampleXYDatasetThread.java
 * --------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited;
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * $Id: SampleXYDatasetThread.java,v 1.1.1.1 2003/07/17 10:06:35 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 22-Oct-2001 : Version 1 (DG);
 * 07-Nov-2001 : Updated source header (DG);
 *
 */

package com.jrefinery.chart.demo;

/**
 * Implements the runnable interface and updates the SampleXYDataset.  This gives the event
 * notification mechanism in JFreeChart a run to make sure it works.
 *
 * WARNING 1 : There are likely to be problems with accessing datasets from more than one thread,
 * so don't use this code for anything other than the demo!
 * <P>
 * WARNING 2 : Updating the dataset this way 'animates' the chart - but JFreeChart is not designed
 * for the production of animated charts.  For fast animations, you need a chart that draws to
 * a bitmap, in order to optimise the screen updates.  But JFreeChart draws to an abstract
 * drawing surface (Graphics2D), which brings many benefits, but also means that we cannot implement
 * any performance tricks when the screen updates because we cannot guarantee which pixels are
 * rendered by the Graphics2D pipeline...
 *
 * @author DG
 */
public class SampleXYDatasetThread implements Runnable {

    /** The data. */
    private SampleXYDataset data;

    /**
     * Creates a new thread.
     *
     * @param data  the dataset.
     */
    public SampleXYDatasetThread(SampleXYDataset data) {
        this.data = data;
    }

    /**
     * Runs the thread.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
                data.setTranslate(data.getTranslate() + 0.25);
            }
            catch (Exception e) {
                // ignore
            }
      }

    }

}
