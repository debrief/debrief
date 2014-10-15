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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
// $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/S57/support/URLInputReader.java,v $
// $RCSfile: URLInputReader.java,v $
// $Revision: 1.1 $
// $Date: 2007/04/27 09:20:02 $
// $Author: ian.mayo $
// 
// **********************************************************************

package MWC.GUI.S57.support;

import java.io.IOException;
import java.net.URL;

import com.bbn.openmap.util.Debug;

/**
 * An InputReader to handle files at a URL.
 */
public class URLInputReader extends StreamInputReader {

    /** Where to go to hook up with a resource. */
    protected URL inputURL = null;

    /**
     * Construct a URLInputReader from a URL.
     */
    public URLInputReader(final java.net.URL url) throws IOException {
        if (Debug.debugging("binaryfile")) {
            Debug.output("URLInputReader created from URL ");
        }
        inputURL = url;
        reopen();
        name = url.getProtocol() + "://" + url.getHost() + url.getFile();
    }

    /**
     * Reset the InputStream to the beginning, by closing the current
     * connection and reopening it.
     */
    public void reopen() throws IOException {
        super.reopen();
        inputStream = inputURL.openStream();
    }
}