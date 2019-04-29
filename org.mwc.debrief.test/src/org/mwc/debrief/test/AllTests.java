/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.test;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.ClasspathProperty;
import org.junit.extensions.cpsuite.ClasspathSuite.SuiteTypes;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)  
@ClasspathProperty("classpathsuite.path")
@SuiteTypes({org.junit.extensions.cpsuite.SuiteType.JUNIT38_TEST_CLASSES, 
	org.junit.extensions.cpsuite.SuiteType.TEST_CLASSES})
@ClassnameFilters({"ASSET.*", "Debrief.*", "MWC.*", "org.mwc.asset.*", "org.mwc.cmap.core.*", 
	"org.mwc.cmap.g*", "org.mwc.cmap.p*", "org.mwc.debrief.*"})

public class AllTests {}
