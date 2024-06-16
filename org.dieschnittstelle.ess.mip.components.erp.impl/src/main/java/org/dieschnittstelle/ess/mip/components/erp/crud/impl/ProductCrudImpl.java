package org.dieschnittstelle.ess.mip.components.erp.crud.impl;


import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;
import java.util.List;

@Transactional
@ApplicationScoped
@Logged
@Alternative
@Priority(Interceptor.Priority.APPLICATION + 10)
public class ProductCrudImpl implements ProductCRUD {

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    EntityManager em;

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        em.persist(prod);
        return prod;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        return em.createQuery("SELECT DISTINCT product FROM AbstractProduct product", AbstractProduct.class).getResultList();
    }

    @Override
    public AbstractProduct updateProduct(AbstractProduct update) {
        em.merge(update);
        return update;
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        return em.find(AbstractProduct.class, productID);
    }

    @Override
    public boolean deleteProduct(long productID) {
        em.remove(em.find(AbstractProduct.class, productID));
        return em.find(AbstractProduct.class, productID) == null;
    }

    @Override
    public List<Campaign> getCampaignsForProduct(long productID) {
        return em.createQuery("SELECT campaign FROM Campaign campaign JOIN campaign.bundles bundle WHERE bundle.product.id = ?1", Campaign.class)
                .setParameter(1, productID).getResultList();
    }
}
