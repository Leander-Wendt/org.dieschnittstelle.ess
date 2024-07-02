package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.PointOfSaleCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@ApplicationScoped
@Logged
@Alternative
@Priority(Interceptor.Priority.APPLICATION + 10)
public class StockSystemImpl implements StockSystem {
    @Inject
    StockItemCRUD stockItemCRUD;

    @Inject
    PointOfSaleCRUD pointOfSaleCRUD;


    @Override
    public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        PointOfSale pos = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        StockItem item = stockItemCRUD.readStockItem(product, pos);
        if (item == null) {
            item = new StockItem();
            item.setPos(pos);
            item.setProduct(product);
            item.setUnits(units);
            stockItemCRUD.createStockItem(item);
        } else {
            item.setUnits(item.getUnits() + units);
            stockItemCRUD.updateStockItem(item);
        }
    }

    @Override
    public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        addToStock(product, pointOfSaleId, units * - 1);
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        List<StockItem> items = stockItemCRUD.readStockItemsForPointOfSale(pointOfSaleCRUD.readPointOfSale(pointOfSaleId));
        List<IndividualisedProductItem> productItems = new ArrayList<>();
        items.forEach((item) -> {
            productItems.add(item.getProduct());
        });
        return productItems;
    }

    @Override
    public List<IndividualisedProductItem> getAllProductsOnStock() {
        List<IndividualisedProductItem> allItems = new ArrayList<>();
        List<PointOfSale> pos = pointOfSaleCRUD.readAllPointsOfSale();
        for(PointOfSale p : pos) {
            List<StockItem> items = stockItemCRUD.readStockItemsForPointOfSale(p);
            items.forEach((item) -> {
                allItems.add(item.getProduct());
            });
        }
        return allItems.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
        PointOfSale pos = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        List<StockItem> items = stockItemCRUD.readStockItemsForPointOfSale(pos);
        Optional<StockItem> optional  = items.stream().filter((item) -> item.getProduct().equals(product)).findFirst();
        return optional.map(StockItem::getUnits).orElse(0);
    }

    @Override
    public int getTotalUnitsOnStock(IndividualisedProductItem product) {
        List<StockItem> items =  stockItemCRUD.readStockItemsForProduct(product);
        return items.stream().map(StockItem::getUnits).reduce(0, Integer::sum);
    }

    @Override
    public List<Long> getPointsOfSale(IndividualisedProductItem product) {
        List<StockItem> items = stockItemCRUD.readStockItemsForProduct(product);
        return items.stream().map(item -> item.getPos().getId()).collect(Collectors.toList());
    }
}
