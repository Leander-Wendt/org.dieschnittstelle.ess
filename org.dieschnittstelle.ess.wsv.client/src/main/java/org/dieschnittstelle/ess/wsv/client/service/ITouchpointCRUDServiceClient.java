package org.dieschnittstelle.ess.wsv.client.service;

import jakarta.ws.rs.*;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;

import java.util.List;

@Path("/touchpoints")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface ITouchpointCRUDServiceClient {
	
	@GET
	public List<StationaryTouchpoint> readAllTouchpoints();

	@GET
	@Path("/{touchpointId}")
	public StationaryTouchpoint readTouchpoint(@PathParam("touchpointId") long id);

	@POST
	public StationaryTouchpoint createTouchpoint(StationaryTouchpoint touchpoint); 
	
	@DELETE
	@Path("/{touchpointId}")
	public boolean deleteTouchpoint(@PathParam("touchpointId") long id);

	@PUT
	@Path("/{touchpointId}")
	public StationaryTouchpoint updateTouchpoint(@PathParam("touchpointId") long id,StationaryTouchpoint touchpoint);

}
