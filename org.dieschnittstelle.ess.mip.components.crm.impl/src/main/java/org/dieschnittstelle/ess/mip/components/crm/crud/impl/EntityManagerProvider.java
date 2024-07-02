package org.dieschnittstelle.ess.mip.components.crm.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Qualifier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * following: https://stackoverflow.com/questions/31374994/how-to-inject-entitymanager-in-cdi-weld
 * but does not work yet. see: https://www.sitepoint.com/cdi-weld-inject-jpa-hibernate-entity-managers/
 */
@ApplicationScoped
public class EntityManagerProvider {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface CRMDataAccessor {

    }

    @Produces
    @CRMDataAccessor
    @PersistenceContext(unitName = "crm_PU")
    private EntityManager em;

}