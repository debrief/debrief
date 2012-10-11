package com.planetmayo.debrief.satc_rcp.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.planetmayo.debrief.satc.support.IOService;

public class RCPIOService implements IOService
{

	@Override
	public List<String> readLinesFrom(String url) throws IOException {
		return IOUtils.readLines(new FileInputStream(url));
	}
}
