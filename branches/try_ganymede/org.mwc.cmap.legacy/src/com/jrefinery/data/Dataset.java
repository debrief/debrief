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
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ------------
 * Dataset.java
 * ------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Dataset.java,v 1.1.1.1 2003/07/17 10:06:50 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Moved to a new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Changed name to Dataset.java (DG);
 * 17-Nov-2001 : Added getLegendItemCount() and getLegendItemLabels() methods, created
 *               SeriesDataset interface and transferred series related methods out (DG);
 * 22-Jan-2002 : Reconsidered (and removed) the getLegendItemCount() and getLegendItemLabels()
 *               methods...leave this to client code (DG);
 * 27-Sep-2002 : Added get/setDatasetGroup(...) methods (DG);
 *
 */

package com.jrefinery.data;

/**
 * The base interface for data sets.
 * <P>
 * All datasets are required to support the DatasetChangeEvent mechanism by allowing listeners to
 * register and receive notification of any changes to the dataset.
 * <P>
 * In addition, all datasets must belong to one (and only one) DatasetGroup.  The group object
 * maintains a reader-writer lock which provides synchronised access to the datasets in
 * multi-threaded code.
 *
 * @see PieDataset
 * @see SeriesDataset
 *
 * @author DG
 */
public interface Dataset {

    /**
     * Registers an object for notification of changes to the dataset.
     *
     * @param listener  the object to register.
     */
    public void addChangeListener(DatasetChangeListener listener);

    /**
     * Deregisters an object for notification of changes to the dataset.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener);

    /**
     * Returns the dataset group.
     *
     * @return the dataset group.
     */
    public DatasetGroup getGroup();

    /**
     * Sets the dataset group.
     *
     * @param group  the dataset group.
     */
    public void setGroup(DatasetGroup group);

}
