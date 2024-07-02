package org.dieschnittstelle.ess.mip.components.erp.crud.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/pointsOfSale")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RegisterRestClient
public interface PointOfSaleCRUD {

	@POST
	public PointOfSale createPointOfSale(PointOfSale pos);

	@GET
	public List<PointOfSale> readAllPointsOfSale();

	@GET
	@Path("/{posId}")
	public PointOfSale readPointOfSale(@PathParam("posId") long posId);

	@DELETE
	@Path("/{posId}")
	public boolean deletePointOfSale(@PathParam("posId") long posId);

}
