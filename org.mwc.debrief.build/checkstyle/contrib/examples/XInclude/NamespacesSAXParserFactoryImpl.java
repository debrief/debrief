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
package com.puppycrawl.tools.checkstyle;

import org.apache.xerces.jaxp.SAXParserFactoryImpl;

/**
 * A parser factory that produces parsers that support XML namespaces. 
 * @author Rick Giles
 * @version May 28, 2004
 */
public class NamespacesSAXParserFactoryImpl extends SAXParserFactoryImpl
{
    /**
     * Constructs a NamespacesSAXParserFactoryImpl. Initializes
     * it to produce parsers that support XML namespaces. 
     */
    public NamespacesSAXParserFactoryImpl()
    {
        super();
        setNamespaceAware(true);
    }
}
