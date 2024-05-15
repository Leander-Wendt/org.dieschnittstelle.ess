package org.dieschnittstelle.ess.jrs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;

import java.util.List;

/*
 * UE JRS2:
 * deklarieren Sie hier Methoden fuer:
 * - die Erstellung eines Produkts
 * - das Auslesen aller Produkte
 * - das Auslesen eines Produkts
 * - die Aktualisierung eines Produkts
 * - das Loeschen eines Produkts
 * und machen Sie diese Methoden mittels JAX-RS Annotationen als WebService verfuegbar
 */

/*
 * TODO JRS3: aendern Sie Argument- und Rueckgabetypen der Methoden von IndividualisedProductItem auf AbstractProduct
 */
@Path("/products")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface IProductCRUDService {

    @POST
    AbstractProduct createProduct(AbstractProduct prod);

    @GET
    List<?> readAllProducts();

    @PUT
    @Path("/{id}")
    AbstractProduct updateProduct(@PathParam("id") long id,
                                  AbstractProduct update);

    @DELETE
    @Path("/{id}")
    boolean deleteProduct(@PathParam("id") long id);

    @GET
    @Path("/{id}")
    AbstractProduct readProduct(@PathParam("id") long id);

}
