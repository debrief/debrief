/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package com.mycompany.filters;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.Filter;
import com.puppycrawl.tools.checkstyle.api.Utils;

public class FilesFilter
    extends AutomaticBean
    implements Filter
{
    private RE mFileRegexp;

    public FilesFilter()
        throws RESyntaxException
    {
        setFiles("^$");
    }
    
    public boolean accept(AuditEvent aEvent)
    {
        final String fileName = aEvent.getFileName();
        return ((fileName == null) || !mFileRegexp.match(fileName));
    }

    public void setFiles(String aFilesPattern)
        throws RESyntaxException
    {
        mFileRegexp = Utils.getRE(aFilesPattern);
    }
}
