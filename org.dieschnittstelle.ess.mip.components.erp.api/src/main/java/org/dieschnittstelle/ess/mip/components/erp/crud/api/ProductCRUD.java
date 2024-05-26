package org.dieschnittstelle.ess.mip.components.erp.crud.api;

import java.util.List;

import jakarta.ws.rs.*;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.core.MediaType;

/*
 * TODO MIP+JPA1/2/5:
 * this interface shall be implemented using an ApplicationScoped CDI bean with an EntityManager.
 * See TouchpointCRUDImpl for an example bean with a similar scope of functionality
 */
@Path("/products")
@RegisterRestClient
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ProductCRUD {

	@POST
	AbstractProduct createProduct(AbstractProduct prod);

	@GET
	List<AbstractProduct> readAllProducts();

	@PUT
	AbstractProduct updateProduct(AbstractProduct update);

	@GET
	@Path("/{id}")
	AbstractProduct readProduct(@PathParam("id") long productID);

	@DELETE
	@Path("/{id}")
	boolean deleteProduct(@PathParam("id") long productID);

	@GET
	@Path("/{id}/campaigns")
	List<Campaign> getCampaignsForProduct(@PathParam("id") long productID);

}
