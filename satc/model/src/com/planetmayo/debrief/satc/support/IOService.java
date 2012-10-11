package com.planetmayo.debrief.satc.support;

import java.io.IOException;
import java.util.List;

public interface IOService
{
	List<String> readLinesFrom(String url) throws IOException;
}
