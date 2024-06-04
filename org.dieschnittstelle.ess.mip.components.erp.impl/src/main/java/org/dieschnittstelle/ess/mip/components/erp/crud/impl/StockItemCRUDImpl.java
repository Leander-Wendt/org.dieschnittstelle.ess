package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

@Transactional
@ApplicationScoped
@Logged
public class StockItemCRUDImpl implements StockItemCRUD {

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    EntityManager em;

    @Override
    public StockItem createStockItem(StockItem item) {
        em.persist(item);
        return item;
    }

    @Override
    public StockItem readStockItem(IndividualisedProductItem prod, PointOfSale pos) {
        try {
            return em.createQuery("select item from StockItem item where item.pos.id = ?1 and item.product.id = ?2", StockItem.class)
                    .setParameter(1, pos.getId()).setParameter(2, prod.getId()).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Could not find result, returning null");
            return null;
        }

    }

    @Override
    public StockItem updateStockItem(StockItem item) {
        return em.merge(item);
    }

    @Override
    public List<StockItem> readStockItemsForProduct(IndividualisedProductItem prod) {
        return em.createQuery("select item from StockItem item where item.product = ?1", StockItem.class).setParameter(1, prod).getResultList();
    }

    @Override
    public List<StockItem> readStockItemsForPointOfSale(PointOfSale pos) {
        return em.createQuery("select item from StockItem item where item.pos = ?1", StockItem.class).setParameter(1, pos).getResultList();
    }
}
