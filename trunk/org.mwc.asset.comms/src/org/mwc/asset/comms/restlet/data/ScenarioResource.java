package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Get;

/**
 * The resource associated to a contact.
 */
public interface ScenarioResource {

    @Get
    public Scenario retrieve();
}
