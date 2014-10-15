/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// 
// <copyright>
// 
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/S57/support/Closable.java,v $
// $RCSfile: Closable.java,v $
// $Revision: 1.1 $
// $Date: 2007/04/27 09:20:00 $
// $Author: ian.mayo $
// 
// **********************************************************************

package MWC.GUI.S57.support;

/**
 * Objects that implement this interface can be registered with
 * BinaryFile to have associated file resources closed when file
 * limits are hit.
 */
public interface Closable {

    /**
     * close/reclaim associated resources.
     * 
     * @param done <code>true</code> indicates that this is a
     *        permanent closure. <code>false</code> indicates that
     *        the object may be used again later, as this is only an
     *        attempt to temporarily reclaim resources
     * @return <code>true</code> indicates the object is still
     *         usable. <code>false</code> indicates that the object
     *         is now unusable, and any references to it should be
     *         released so the garbage collector can do its job.
     */
    public boolean close(boolean done);
}