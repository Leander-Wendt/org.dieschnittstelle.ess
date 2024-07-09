package org.dieschnittstelle.ess.wsv.client.service;

import jakarta.ws.rs.*;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;

import java.util.List;

@Path("/touchpoints")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface ITouchpointCRUDServiceClient {

	// TODO WSV change stationary touchpoint to abstract touchpoint and make it usable
	
	@GET
	List<AbstractTouchpoint> readAllTouchpoints();

	@GET
	@Path("/{touchpointId}")
	AbstractTouchpoint readTouchpoint(@PathParam("touchpointId") long id);

	@POST
	AbstractTouchpoint createTouchpoint(StationaryTouchpoint touchpoint);
	
	@DELETE
	@Path("/{touchpointId}")
	boolean deleteTouchpoint(@PathParam("touchpointId") long id);

	@PUT
	@Path("/{touchpointId}")
	AbstractTouchpoint updateTouchpoint(@PathParam("touchpointId") long id,StationaryTouchpoint touchpoint);

}
