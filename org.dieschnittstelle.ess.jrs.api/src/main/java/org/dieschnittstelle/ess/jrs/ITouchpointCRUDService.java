package org.dieschnittstelle.ess.jrs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;

import java.util.List;

@Path("/touchpoints")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface ITouchpointCRUDService {

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
	StationaryTouchpoint updateTouchpoint(StationaryTouchpoint touchpoint);
		
	/*
	 * TODO JRS1: add a new annotated method for using the updateTouchpoint functionality of TouchpointCRUDExecutor and implement it
	 */
}
