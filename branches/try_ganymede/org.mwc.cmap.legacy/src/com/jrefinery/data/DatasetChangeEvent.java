/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * -----------------------
 * DatasetChangeEvent.java
 * -----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DatasetChangeEvent.java,v 1.1.1.1 2003/07/17 10:06:50 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Move to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 11-Jun-2002 : Separated the event source from the dataset to cover the case where the dataset
 *               is changed to null in the Plot class.  Updated Javadocs (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

/**
 * A change event that encapsulates information about a change to a dataset.
 *
 * @author DG
 */
public class DatasetChangeEvent extends java.util.EventObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		/**
     * The dataset that generated the change event.
     */
    private Dataset data;

    /**
     * Constructs a new event.
     * <P>
     * The source is either the dataset or the Plot class.  The dataset can be
     * null (in this case the source will be the Plot class).
     *
     * @param source  the source of the event.
     * @param data  the dataset that generated the event.
     */
    public DatasetChangeEvent(Object source, Dataset data) {
        super(source);
        this.data = data;
    }

    /**
     * Returns the dataset that generated the event.
     *
     * @return the dataset.
     */
    public Dataset getDataset() {
        return data;
    }

}
