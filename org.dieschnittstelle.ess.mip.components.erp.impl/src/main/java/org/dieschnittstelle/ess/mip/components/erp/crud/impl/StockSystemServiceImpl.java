package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

@Transactional
@ApplicationScoped
@Logged
@Alternative
@Priority(Interceptor.Priority.APPLICATION+10)
public class StockSystemServiceImpl implements StockSystemService {

    @Inject
    private ProductCRUD productCRUD;

    @Inject
    private StockSystem stockSystem;

    @Override
    public void addToStock(long productId, long pointOfSaleId, int units) {
        IndividualisedProductItem productItem = (IndividualisedProductItem) productCRUD.readProduct(productId);
        stockSystem.addToStock(productItem, pointOfSaleId, units);
    }

    @Override
    public void removeFromStock(long productId, long pointOfSaleId, int units) {
        IndividualisedProductItem productItem = (IndividualisedProductItem) productCRUD.readProduct(productId);
        stockSystem.removeFromStock(productItem, pointOfSaleId, units);
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        if(pointOfSaleId == 0 ){
            return stockSystem.getAllProductsOnStock();
        }
        return stockSystem.getProductsOnStock(pointOfSaleId);
    }

    @Override
    public int getUnitsOnStock(long productId, long pointOfSaleId) {
        IndividualisedProductItem productItem = (IndividualisedProductItem) productCRUD.readProduct(productId);
        if(pointOfSaleId == 0){
            return stockSystem.getTotalUnitsOnStock(productItem);
        }
        return stockSystem.getUnitsOnStock(productItem, pointOfSaleId);
    }

    @Override
    public List<Long> getPointsOfSale(long productId) {
        IndividualisedProductItem productItem = (IndividualisedProductItem) productCRUD.readProduct(productId);
        return stockSystem.getPointsOfSale(productItem);
    }
}
