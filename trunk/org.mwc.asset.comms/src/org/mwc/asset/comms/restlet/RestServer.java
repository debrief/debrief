package org.mwc.asset.comms.restlet;

import java.io.File;

import org.mwc.asset.comms.restlet.test.data.ContactServerResource;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.LocalReference;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

public class RestServer extends   ServerResource
{
  public static void main(String[] args) throws Exception {  
    // Create the HTTP server and listen on port 8182  
    new Server(Protocol.HTTP, 8182, RestServer.class).start();  
 }

 @Get 
 public String toString() {  
    return "hello, world";  
 }

}
