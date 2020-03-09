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
package com.puppycrawl.tools.checkstyle.bcel.classfile;

import java.util.Set;

import org.apache.bcel.classfile.FieldOrMethod;

import com.puppycrawl.tools.checkstyle.api.Scope;

/**
 * Utility methods for BCEL classfile package
 * @author Rick Giles
 */
public class Utils
{
    /**
     * Determines whether the declared scope of a field or method is in
     * a set of scopes.
     * @param aFieldOrMethod the field or method to test.
     * @param aScopes the set of scopes to test against.
     * @return true if the declared scope of aFieldOrMethod is in aScopes.
     */
    public static boolean inScope(FieldOrMethod aFieldOrMethod, Set aScopes)
    {
        if (aFieldOrMethod.isPrivate()) {
            return (aScopes.contains(Scope.PRIVATE));
        }
        else if (aFieldOrMethod.isProtected()) {
            return (aScopes.contains(Scope.PROTECTED));
        }
        else if (aFieldOrMethod.isPublic()) {
            return (aScopes.contains(Scope.PUBLIC));
        }
        else {
            return (aScopes.contains(Scope.PACKAGE));
        }
    }


}
