package org.dieschnittstelle.ess.mip.client.apiclients;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import org.dieschnittstelle.ess.jrs.client.jackson.LaissezFairePolymorphicJacksonProvider;

import jakarta.ws.rs.client.*;
import java.io.*;
import java.util.*;

/*
 * Created by master on 17.02.17.
 *
 * creates a rest service proxy for the interface
 *
 * does some checks on the uris and interfaces
 * realised as singleton
 */
public class ServiceProxyFactory {

    public static final String MONOLITHIC_DEPLOYMENT = "ml";
    public static final String MICROSERVICES_DEPLOYMENT = "ms";

    /*
     * this filter logs the raw response body data
     */
    public static class LoggingFilter implements ClientResponseFilter {

        @Override
        public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) throws IOException {
            if (clientResponseContext.hasEntity()) {
                // we need to copy the original input stream because, once read, it will not be available to further processing anymore
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                clientResponseContext.getEntityStream().transferTo(baos);
                // this copy of the stream will be used for logging
                InputStream dataForLogging = new ByteArrayInputStream(baos.toByteArray());
                // this copy will be used for further processing, i.e. it will be re-set on the response context
                InputStream dataForFurtherProcessing = new ByteArrayInputStream(baos.toByteArray());
                clientResponseContext.setEntityStream(dataForFurtherProcessing);

                if (clientResponseContext.getStatus() != HttpStatus.SC_OK && clientResponseContext.getStatus() != HttpStatus.SC_CREATED && clientResponseContext.getStatus() != HttpStatus.SC_ACCEPTED) {
                    logger.error("got response body for status " + clientResponseContext.getStatus() +  ": " + IOUtils.toString(dataForLogging));
                }
                else {
                    logger.info("got response body for status " + clientResponseContext.getStatus() +  ": " + IOUtils.toString(dataForLogging));
                }
            }
        }
    }

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ServiceProxyFactory.class);

    // some custom runtime exception
    public static class ServiceProxyException extends RuntimeException {

        public ServiceProxyException(String msg) {
            super(msg);
        }

        public ServiceProxyException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }

    private static Properties essClientProperties = new Properties();

    // a list of service id prefixes and the baseurls for the services, which will be read from the properties
    private static class ServiceRegistryItem {
        private String serviceIdprefix;
        private ResteasyWebTarget servicebase;

        public ServiceRegistryItem(String serviceIdprefix, ResteasyWebTarget servicebase) {
            this.serviceIdprefix = serviceIdprefix;
            this.servicebase = servicebase;
        }

        public boolean match(Class serviceInterface) {
            return serviceInterface.getName().startsWith(this.serviceIdprefix);
        }

        @Override
        public String toString() {
            return "<" + this.serviceIdprefix + ": " + this.servicebase.getUri() + ">";
        }
    }

    public static final String PROPERTY_WEB_API_BASE_URL_PREFIX = "ess.mip.client.apiclients.baseurl.";

    static {
        try {
            essClientProperties.load(ServiceProxyFactory.class.getClassLoader().getResourceAsStream("ess-mip-client.properties"));
            logger.info("initialise(): loaded properties: " + essClientProperties);
        }
        catch (Exception e) {
            throw new ServiceProxyException("<static initialiser> got exception trying to read client properties: " + e,e);
        }
    }

    // the instance
    private static ServiceProxyFactory instance;

    // public method for initialising the factory
    public static void initialise() {
        initialise(MONOLITHIC_DEPLOYMENT);
    }

    public static void initialise(String deployment) {
        if (instance != null) {
            logger.warn("initialise() was called on ServiceProxyFactory, but there already exists an instance. Will not overwrite it.");
            return;
        }
        instance = new ServiceProxyFactory(deployment);
    }

    // this gives us the instance
    public static ServiceProxyFactory getInstance() {
        if (instance == null) {
            throw new ServiceProxyException("getInstance() was invoked, but no instance has been created yet. Need to call initialise() before");
        }
        return instance;
    }

    // obtain a deployment specific instance (beyond the singleton usage)
    public static ServiceProxyFactory newInstance(String deployment) {
        return new ServiceProxyFactory(deployment);
    }


    // this is the client-side representation of the web api, which gives access to the different services offered via this api
    private List<ServiceRegistryItem> serviceRegistry = new ArrayList<>();

    private ServiceProxyFactory(String deployment) {
        // we check whether polymorphism is handled for products and touchpoints
        if (!AbstractTouchpoint.class.isAnnotationPresent(JsonTypeInfo.class)) {
            throw new ServiceProxyException("access to web api cannot be supported as polymorphism is not handled sufficiently. Check annotations on AbstractTouchpoint! Remember to also restart the server-side application once changes have been made.");
        } else if (!AbstractProduct.class.isAnnotationPresent(JsonTypeInfo.class)) {
            logger.warn("NOTE THAT AbstractProduct might need to be prepared for polymorphism in order for WebAPI access to work overall correctly. Remember to also restart the server-side application once changes have been made.");
        } else {
            logger.info("consistency check of datamodel classes succeeded.");
        }

//        if (useWebAPIAsDefault) {
        System.out.println("\n%%%%%%%%%%%% ServiceProxyFactory: services will be accessed via REST API %%%%%%%%%%%\n\n");
//        }
//        else {
//            System.out.println("\n%%%%%%%%%%%% ServiceProxyFactory: services will be accessed via EJB proxies %%%%%%%%%%%\n\n");
//        }

        try {
            createServiceRegistry(deployment);
            System.out.println("\n%%%%%%%%%%%% ServiceProxyFactory: service registry has been created for deployment " + deployment + " %%%%%%%%%%%\n" + this.serviceRegistry + "\n");
        } catch (Exception e) {
            throw new ServiceProxyException("got exception trying to instantiate proxy factory: " + e, e);
        }

    }

    public <T> T getProxy(Class<T> serviceInterface) {
        return getProxy(serviceInterface, "");
    }

    public <T> T getProxy(Class<T> serviceInterface, String ejbUri) {
        return getProxy(serviceInterface, ejbUri, true);
    }

    /*
     * allow to specify what kind of proxy shall be created
     */
    public <T> T getProxy(Class<T> serviceInterface, String ejbUri, boolean useWebAPI) {
        T proxy;

        try {
            proxy = this.lookupServiceBaseurl(serviceInterface).proxy(serviceInterface);
        } catch (Exception e) {
            throw new ServiceProxyException("got exception trying to create a " + (useWebAPI ? " web service " : " EJB ") + " proxy for interface " + serviceInterface + ": " + e, e);
        }

        logger.info("getProxy(): returning proxy for " + serviceInterface + ": " + proxy);

        return proxy;
    }

    /*
     * this creates the service registry using a single client instance, from which the web targets will be created
     */
    public void createServiceRegistry(String deployment) {
        Client client = ClientBuilder.newBuilder().register(new LoggingFilter()).register(LaissezFairePolymorphicJacksonProvider.class).build();

        // we iterate over the properties and create the registry. Note that more specific base url assignments need
        // to precede less specific ones, therefore we sort the matching property in ascending order of their length
        essClientProperties.stringPropertyNames()
                .stream()
                .filter(prop -> prop.startsWith(PROPERTY_WEB_API_BASE_URL_PREFIX + deployment + "."))
                .sorted(Comparator.comparingInt(String::length).reversed())
                .forEach(prop -> {
                    serviceRegistry.add(new ServiceRegistryItem(prop.substring((PROPERTY_WEB_API_BASE_URL_PREFIX + deployment + ".").length()),((ResteasyWebTarget) client.target(String.valueOf(essClientProperties.getProperty(prop))))));
                });
    }

    public ResteasyWebTarget lookupServiceBaseurl(Class serviceInterface) {
        // we return the first matching item
        for (ServiceRegistryItem registryItem : serviceRegistry) {
            if (registryItem.match(serviceInterface)) {
                return registryItem.servicebase;
            }
        }
        throw new ServiceProxyException("Could not find baseurl for service interface " + serviceInterface + ". No matching baseurl found in service registry: " + serviceRegistry);
    }

}
