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
package com.puppycrawl.tools.checkstyle.checks.usage;

import com.puppycrawl.tools.checkstyle.BaseCheckTestCase;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class UnusedParameterCheckTest
    extends BaseCheckTestCase
{
    public void testDefault() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(UnusedParameterCheck.class);
        final String[] expected = {
            "8:57: Unused parameter 'aUnreadPrimitive'.",
            "16:16: Unused parameter 'aUnreadObject'.",
            "29:66: Unused parameter 'aUnreadArray'.",            
        };
        verify(checkConfig, getPath("usage/InputUnusedParameter.java"), expected);
    }
    
    public void testException() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(UnusedParameterCheck.class);
        checkConfig.addAttribute("ignoreCatch", Boolean.FALSE.toString());
        final String[] expected = {
            "8:57: Unused parameter 'aUnreadPrimitive'.",
            "16:16: Unused parameter 'aUnreadObject'.",
            "25:26: Unused parameter 'unreadException'.",
            "29:66: Unused parameter 'aUnreadArray'.",            
        };
        verify(checkConfig, getPath("usage/InputUnusedParameter.java"), expected);
    }
    
    public void testIgnoreFormat() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(UnusedParameterCheck.class);
        checkConfig.addAttribute("ignoreFormat", "Array$");
        final String[] expected = {
            "8:57: Unused parameter 'aUnreadPrimitive'.",
            "16:16: Unused parameter 'aUnreadObject'.",
        };
        verify(checkConfig, getPath("usage/InputUnusedParameter.java"), expected);
    }

    public void testIgnoreNonLocal() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(UnusedParameterCheck.class);
        checkConfig.addAttribute("ignoreNonLocal", "true");
        final String[] expected = {
            "8:57: Unused parameter 'aUnreadPrimitive'.",
            "29:66: Unused parameter 'aUnreadArray'.",            
        };
        verify(checkConfig, getPath("usage/InputUnusedParameter.java"), expected);
    }
}
