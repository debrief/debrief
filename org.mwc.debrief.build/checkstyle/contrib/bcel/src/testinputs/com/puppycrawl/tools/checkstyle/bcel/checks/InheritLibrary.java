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
package com.puppycrawl.tools.checkstyle.bcel.checks;

import org.apache.bcel.classfile.AccessFlags ;
/**
 * Test data for checking that fields with the same name as fields in
 * superclasses work, when the superclass is inside an external library.
 */
public abstract class InheritLibrary extends AccessFlags
{
    protected int access_flags;
}
