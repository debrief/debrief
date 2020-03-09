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

public class UnusedLocalVariableCheckTest
    extends BaseCheckTestCase
{
    public void testDefault() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(UnusedLocalVariableCheck.class);
        final String[] expected = {
            "13:13: Unused local variable 'mUnreadPrimitive'.",
            "24:16: Unused local variable 'unreadObject'.",
            "36:15: Unused local variable 'unreadArray'.",
            "45:13: Unused local variable 'java'.",
        };
        verify(checkConfig, getPath("usage/InputUnusedLocal.java"), expected);
    }
    
    public void testIgnoreFormat() throws Exception
        {
            final DefaultConfiguration checkConfig =
                createCheckConfig(UnusedLocalVariableCheck.class);
            checkConfig.addAttribute("ignoreFormat", "Array$");
            final String[] expected = {
                "13:13: Unused local variable 'mUnreadPrimitive'.",
                "24:16: Unused local variable 'unreadObject'.",
                "45:13: Unused local variable 'java'.",
            };
            verify(checkConfig, getPath("usage/InputUnusedLocal.java"), expected);
        }
}
