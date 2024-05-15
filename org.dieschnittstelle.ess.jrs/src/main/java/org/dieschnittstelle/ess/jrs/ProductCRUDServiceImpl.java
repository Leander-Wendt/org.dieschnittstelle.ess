package org.dieschnittstelle.ess.jrs;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import org.dieschnittstelle.ess.entities.GenericCRUDExecutor;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import java.util.List;

/*
 * TODO JRS2: implementieren Sie hier die im Interface deklarierten Methoden
 */

public class ProductCRUDServiceImpl implements IProductCRUDService {

    private final GenericCRUDExecutor<AbstractProduct> executor;

    public ProductCRUDServiceImpl(@Context ServletContext context) {
        this.executor = (GenericCRUDExecutor<AbstractProduct>) context.getAttribute("productCRUD");
    }

    @Override
    public IndividualisedProductItem createProduct(
            AbstractProduct prod) {
        return (IndividualisedProductItem) executor.createObject(prod);
    }

    @Override
    public List<?> readAllProducts() {
        return executor.readAllObjects();
    }

    @Override
    public AbstractProduct updateProduct(long id,
                                         AbstractProduct update) {
        update.setId(id);
        return executor.updateObject(update);
    }

    @Override
    public boolean deleteProduct(long id) throws NotFoundException{
        if (!executor.deleteObject(id))
            throw new NotFoundException("Object to delete with given id does not exist.");

        return true;
    }

    @Override
    public AbstractProduct readProduct(long id) {
        AbstractProduct result = executor.readObject(id);

        if(result == null)
            throw new NotFoundException("Object to read with given id does not exist.");

        return result;
    }

}
