package org.dieschnittstelle.ess.wsv.client.service;

import jakarta.ws.rs.*;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;

import java.util.List;

@Path("/touchpoints")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface ITouchpointCRUDServiceClient {
	
	@GET
	List<StationaryTouchpoint> readAllTouchpoints();

	@GET
	@Path("/{touchpointId}")
	StationaryTouchpoint readTouchpoint(@PathParam("touchpointId") long id);

	@POST
	StationaryTouchpoint createTouchpoint(StationaryTouchpoint touchpoint);
	
	@DELETE
	@Path("/{touchpointId}")
	boolean deleteTouchpoint(@PathParam("touchpointId") long id);

	@PUT
	@Path("/{touchpointId}")
	StationaryTouchpoint updateTouchpoint(@PathParam("touchpointId") long id,StationaryTouchpoint touchpoint);

}
