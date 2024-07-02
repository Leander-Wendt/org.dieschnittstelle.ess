package org.dieschnittstelle.ess.utils.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.Config;

/*
 * this class makes the microprofile configuration available to the rest of the application, see https://rieckpil.de/whatis-eclipse-microprofile-config/
 */
public class ConfigurationInjection {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ConfigurationInjection.class);

    @Inject
    private Config config;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        logger.info("got configuration: " + config);
    }

}
