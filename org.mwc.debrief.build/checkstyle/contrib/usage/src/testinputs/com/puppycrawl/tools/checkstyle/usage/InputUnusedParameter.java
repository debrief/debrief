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
package com.puppycrawl.tools.checkstyle.usage;

import java.awt.Rectangle;

/** Test input for unread parameter check */
public class InputUnusedParameter
{
    public InputUnusedParameter(int aReadPrimitive, int aUnreadPrimitive)
    {
        int i = aReadPrimitive;
    }

    public void method(
        String aReadObject,
        Rectangle aRectangle,
        Object aUnreadObject)
    {
        int i = aReadObject.length();

        int j = aRectangle.x;

        try {
            i++;
        }
        catch (Exception unreadException) {
        }
    }

    private void methodArrays(int[] aArray, int[] aArray2, int[] aUnreadArray)
    {
        int i = aArray[0];
        aArray2[0] = 0;
    }

    private int member = 1;
    private void methodSameLocalVariable(int member)
    {
        int x = member; // refers to the param, not the member
    }
}

interface Interface
{
    public void method(int aParam);
}

abstract class AbstractClass
{
    public abstract void method(int aParam);
}
