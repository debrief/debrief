package com.planetmayo.debrief.satc.model.support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import com.planetmayo.debrief.satc.support.IOService;

public class TestIOService implements IOService
{

	@Override
	public List<String> readLinesFrom(String url) throws IOException
	{
		ArrayList<String> list = new ArrayList<String>(500);
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(url)));
		String line;
		while ((line = reader.readLine()) != null) 
		{
			list.add(line);
		}
		reader.close();
		return list;
	}	
}
