package org.dieschnittstelle.ess.jrs.client.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import org.apache.logging.log4j.Logger;

@Provider
@Consumes({"application/json", "application/*+json", "text/json"})
@Produces({"application/json", "application/*+json", "text/json"})
public class LaissezFairePolymorphicJacksonProvider extends ResteasyJackson2Provider {

    protected static Logger logger = org.apache.logging.log4j.LogManager
            .getLogger(LaissezFairePolymorphicJacksonProvider.class);

    public static class LaissezFairePolymorphicTypeValidator extends PolymorphicTypeValidator.Base
    {
        @Override
        public Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
            return Validity.ALLOWED;
        }

        @Override
        public Validity validateSubClassName(MapperConfig<?> config, JavaType baseType, String subClassName) {
            return Validity.ALLOWED;
        }
    }

    private ObjectMapper mapper;

    public LaissezFairePolymorphicJacksonProvider() {
        super();
        this.mapper = JsonMapper.builder()
                .polymorphicTypeValidator(new LaissezFairePolymorphicTypeValidator())
                .build();
    }

    public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
        logger.info("locateMapper(): will use our own laissez-faire object mapper for handling JsonTypeInfo with fully qualified classnames");
        return this.mapper;
    }

}
