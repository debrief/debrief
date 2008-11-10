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
 * --------------------------
 * DatasetChangeListener.java
 * --------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DatasetChangeListener.java,v 1.1.1.1 2003/07/17 10:06:50 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Moved to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Aug-2002 : Now extends EventListener (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

import java.util.EventListener;

/**
 * The interface that must be supported by classes that wish to receive
 * notification of changes to a dataset.
 *
 * @author DG
 */
public interface DatasetChangeListener extends EventListener {

    /**
     * Receives notification of an dataset change event.
     *
     * @param event  information about the event.
     */
    public void datasetChanged(DatasetChangeEvent event);

}
