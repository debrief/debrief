package org.mwc.debrief.core.loaders;

import javax.xml.bind.JAXBContext;

public interface DebriefJaxbContextAware
{
	void setJaxbContext(JAXBContext debriefContext);
}