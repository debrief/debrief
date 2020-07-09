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

package com.puppycrawl.tools.checkstyle.bcel;

import java.util.Set;

/**
 * Object set visitor for a general set.
 * @author Rick Giles
 */
public interface IObjectSetVisitor
{
    /**
     * Visit a set itself.
     * @param aSet the set.
     */
    void visitSet(Set aSet);

    /**
     * Finish the visit of a set.
     * @param aSet the set.
     */
    void leaveSet(Set aSet);

    /**
     * Visit an object. Normally this is an object of the set.
     * @param aObject the object.
     */
    void visitObject(Object aObject);

    /**
     * Finish the visit an object. Normally this is an object of the set.
     * @param aObject the object.
     */
    void leaveObject(Object aObject);
}
