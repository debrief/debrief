// **********************************************************************
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

import java.io.*;
import java.net.*;
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
    public URLInputReader(java.net.URL url) throws IOException {
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