package org.dieschnittstelle.ess.mip.components.crm.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/touchpoints")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RegisterRestClient
public interface TouchpointAccess {

	@POST
	public AbstractTouchpoint createTouchpointAndPointOfSale(AbstractTouchpoint touchpoint) throws CrmException;

	@GET
	public List<AbstractTouchpoint> readAllTouchpoints();

	@GET
	@Path("/{id}")
	public AbstractTouchpoint readTouchpoint(@PathParam("id") long id);
	
}
