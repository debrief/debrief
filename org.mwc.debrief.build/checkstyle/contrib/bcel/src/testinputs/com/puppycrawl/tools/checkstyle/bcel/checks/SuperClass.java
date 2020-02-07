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

public class SuperClass
{
    // Name reused in subclass
    protected int reusedName;
    // Subclass uses private name, still conflict
    protected int subClassPrivate;
    // This is a private field, subclass does not shadow
    private int superClassPrivate;
    // Test for different data types
    protected String differentType;

    // Hidden
    public static int staticMethod() {
        return 0;
    }
    // Not hidden
    public int nonStaticMethod() {
        return 0;
    }
    // Hidden
    public static int staticMethodSameParamName(int i) {
        return 0;
    }
    // Hidden
    public static int staticMethodSameParamType(int i) {
        return 0;
    }
    // Not hidden
    public static int staticMethodDifferentParamNum(int i, int j) {
        return 0;
    }
    // Not hidden
    public static int staticMethodDifferentParamType(int i) {
        return 0;
    }
    // Not hidden
    public static int staticMethodDifferentObjectType(Integer i) {
        return 0;
    }
    // Not hidden (?)
    public static int staticMethodSuperParamType(Object i) {
        return 0;
    }
}
