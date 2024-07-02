package org.dieschnittstelle.ess.jrs.opi;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.jrs.ITouchpointCRUDService;
import org.dieschnittstelle.ess.jrs.TouchpointCRUDServiceImpl;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.List;

@Path("opi/touchpoints")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TouchpointCRUDServiceOPIImpl {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(TouchpointCRUDServiceOPIImpl.class);

    private ITouchpointCRUDService service;

    public TouchpointCRUDServiceOPIImpl() {

    }

    /**
     * here we will be passed the context parameters by the resteasy framework. Alternatively @Context
     * can  be declared on the respective instance attributes. note that the request context is only
     * declared for illustration purposes, but will not be further used here
     *
     * @param servletContext
     */
    public TouchpointCRUDServiceOPIImpl(@Context ServletContext servletContext, @Context HttpServletRequest request) {
        this.service = new TouchpointCRUDServiceImpl(servletContext,request);
    }

    @GET
    @Operation(operationId = "readAllTouchpoints")
    public List<StationaryTouchpoint> readAllTouchpoints() {
        return (List)this.service.readAllTouchpoints();
    }

    @POST
    @Operation(operationId = "createTouchpoint")
    public StationaryTouchpoint createTouchpoint(StationaryTouchpoint touchpoint) {
        return (StationaryTouchpoint) this.service.createTouchpoint( touchpoint);
    }

    @DELETE
    @Path("/{id}")
    @Operation(operationId = "deleteTouchpoint")
    public boolean deleteTouchpoint(@PathParam("id") long id) {
        return this.service.deleteTouchpoint(id);
    }

    @GET
    @Path("/{id}")
    @Operation(operationId = "readTouchpoint")
    public StationaryTouchpoint readTouchpoint(@PathParam("id") long id) {
        return (StationaryTouchpoint)this.service.readTouchpoint(id);
    }

}
