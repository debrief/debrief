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
package org.mwc.debrief.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

public class TestPlotXsdFiles extends TestCase
{

	public void testFiles()
	{
		List<File> files = new ArrayList<File>();
		String rootPath = "../org.mwc.cmap.combined.feature/root_installs/sample_data/";
		rootPath = rootPath.replace("/", File.separator);
		File root = new File(rootPath);
		collect(root, files);
		for (File file : files)
		{
			validate(file);
		}
	}

	private void validate(File file)
	{
		System.out.print("Validating " + file.getName() + ": ");

		try
		{
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			String schemaPath = "../org.mwc.debrief.core/schema/debrief_plot.xsd"
					.replace("/", File.separator);
			Source schemaFile = new StreamSource(new File(schemaPath));
			Schema schema = factory.newSchema(schemaFile);

			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(file));
		}
		catch (Exception e)
		{
			System.out.println(" fail. " + e.getMessage());
			return;
		}
		System.out.println(" pass.");
	}

	private void collect(File root, List<File> files)
	{
		File[] entries = root.listFiles();
		for (File file : entries)
		{
			if (file.isFile() && file.getName() != null
					&& file.getName().endsWith(".dpf"))
			{
				files.add(file);
			}
			else if (file.isDirectory())
			{
				collect(file, files);
			}
		}
	}
}
