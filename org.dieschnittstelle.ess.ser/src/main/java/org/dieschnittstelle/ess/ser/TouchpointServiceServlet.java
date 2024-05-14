package org.dieschnittstelle.ess.ser;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;

import java.io.*;

import static org.dieschnittstelle.ess.utils.Utils.show;

public class TouchpointServiceServlet extends HttpServlet {

    protected static Logger logger = org.apache.logging.log4j.LogManager
            .getLogger(TouchpointServiceServlet.class);

    public TouchpointServiceServlet() {
        show("TouchpointServiceServlet: constructor invoked\n");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {

        logger.info("doGet()");

        // we assume here that GET will only be used to return the list of all
        // touchpoints

        // obtain the executor for reading out the touchpoints
        TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext()
                .getAttribute("touchpointCRUD");
        try {
            // set the status
            response.setStatus(HttpServletResponse.SC_OK);
            // obtain the output stream from the response and write the list of
            // touchpoints into the stream
            ObjectOutputStream oos = new ObjectOutputStream(
                    response.getOutputStream());
            // write the object
            oos.writeObject(exec.readAllTouchpoints());
            oos.close();
        } catch (Exception e) {
            String err = "got exception: " + e;
            logger.error(err, e);
            throw new RuntimeException(e);
        }

    }

    /*
     * TODO: SER3 server-side implementation of createNewTouchpoint
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) {

        // assume POST will only be used for touchpoint creation, i.e. there is
        // no need to check the uri that has been used

        // obtain the executor for reading out the touchpoints from the servlet context using the touchpointCRUD attribute
        TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext()
                .getAttribute("touchpointCRUD");


        try {
            // create an ObjectInputStream from the request's input stream
            ObjectInputStream inputStream = new ObjectInputStream(request.getInputStream());

            // read an AbstractTouchpoint object from the stream
            // call the create method on the executor and take its return value
            AbstractTouchpoint abstractTouchpoint = exec.createTouchpoint((AbstractTouchpoint) inputStream.readObject());

            // set the response status as successful, using the appropriate
            // constant from HttpServletResponse
            response.setStatus(HttpServletResponse.SC_CREATED);

            // then write the object to the response's output stream, using a
            // wrapping ObjectOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            // ... and write the object to the stream
            objectOutputStream.writeObject(abstractTouchpoint);
            ByteArrayEntity entity = new ByteArrayEntity(byteArrayOutputStream.toByteArray());
            OutputStream outputStream = response.getOutputStream();
            entity.writeTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /*
     * TODO: SER4 server-side implementation of deleteTouchpoint
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext()
                .getAttribute("touchpointCRUD");
        logger.debug("Executing delete request");

        String uri = request.getRequestURI();
        String[] split = uri.split("/");
        String last = split[split.length - 1];
        long id;

        try {
            id = Long.parseLong(last);
            boolean deleted = exec.deleteTouchpoint(id);
            if (deleted) {
                response.setStatus(HttpStatus.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new RuntimeException(e);
        }
    }
}

