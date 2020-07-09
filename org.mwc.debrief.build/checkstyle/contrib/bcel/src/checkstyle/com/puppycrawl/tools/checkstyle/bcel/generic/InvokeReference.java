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
//Tested with BCEL-5.1
//http://jakarta.apache.org/builds/jakarta-bcel/release/v5.1/

package com.puppycrawl.tools.checkstyle.bcel.generic;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Type;


/**
 * Describe class MethodReference
 * @author Rick Giles
 * @version 18-Jun-2003
 */
public class InvokeReference
    extends FieldOrMethodReference
{

    /**
     * @param aInstruction
     * @param aPoolGen
     */
    public InvokeReference(
        InvokeInstruction aInstruction,
        ConstantPoolGen aPoolGen)
    {
        super(aInstruction, aPoolGen);
    }

    /**
     * @return
     */
    public Type[] getArgTypes()
    {
        return ((InvokeInstruction) mInstruction).getArgumentTypes(mPoolGen);
    }
}
