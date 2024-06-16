package org.dieschnittstelle.ess.mip.components.shopping.cart.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Customer;
import org.dieschnittstelle.ess.entities.crm.CustomerTransaction;
import org.dieschnittstelle.ess.entities.crm.CustomerTransactionShoppingCartItem;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.shopping.ShoppingCartItem;
import org.dieschnittstelle.ess.mip.components.crm.api.CampaignTracking;
import org.dieschnittstelle.ess.mip.components.crm.api.CustomerTracking;
import org.dieschnittstelle.ess.mip.components.crm.api.TouchpointAccess;
import org.dieschnittstelle.ess.mip.components.crm.crud.api.CustomerCRUD;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.mip.components.shopping.api.PurchaseService;
import org.dieschnittstelle.ess.mip.components.shopping.api.ShoppingException;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCart;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCartService;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
@Logged
public class PurchaseServiceImpl implements PurchaseService {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(PurchaseServiceImpl.class);

    private ShoppingCart shoppingCart;

    @Inject
    private CustomerTracking customerTracking;

    @Inject
    private CampaignTracking campaignTracking;

    @Inject
    private CustomerCRUD customerCRUD;

    @Inject
    private TouchpointAccess touchpointAccess;

    @Inject
    private ShoppingCartService shoppingCartService;

    @Inject
    private ProductCRUD productCRUD;

    @Inject
    private StockSystemService stockSystemService;

    private Customer customer;

    private AbstractTouchpoint touchpoint;

    public void verifyCampaigns() throws ShoppingException {
        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException("cannot verify campaigns! No touchpoint has been set!");
        }

        for (ShoppingCartItem item : this.shoppingCart.getItems()) {
            if (item.isCampaign()) {
                int availableCampaigns = this.campaignTracking.existsValidCampaignExecutionAtTouchpoint(
                        item.getErpProductId(), this.touchpoint);
                logger.info("got available campaigns for product " + item.getErpProductId() + ": "
                        + availableCampaigns);

                if (availableCampaigns < item.getUnits()) {
                    throw new ShoppingException("verifyCampaigns() failed for productBundle " + item
                            + " at touchpoint " + this.touchpoint + "! Need " + item.getUnits()
                            + " instances of campaign, but only got: " + availableCampaigns);
                }
            }
        }
    }

    public void purchase() throws ShoppingException {
        logger.info("purchase()");

        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException(
                    "cannot commit shopping session! Either customer or touchpoint has not been set: " + this.customer
                            + "/" + this.touchpoint);
        }

        verifyCampaigns();

        checkAndRemoveProductsFromStock();

        List<ShoppingCartItem> productsInCart = this.shoppingCart.getItems();
        List<CustomerTransactionShoppingCartItem> productsInCartForTransaction = productsInCart
                .stream()
                .map(si -> new CustomerTransactionShoppingCartItem(si.getErpProductId(), si.getUnits(), si.isCampaign()))
                .collect(Collectors.toList());
        CustomerTransaction transaction = new CustomerTransaction(this.customer, this.touchpoint,
                productsInCartForTransaction);
        transaction.setCompleted(true);
        customerTracking.createTransaction(transaction);

        logger.info("purchase(): done.\n");
    }

    private void checkAndRemoveProductsFromStock() {
        for (ShoppingCartItem item : this.shoppingCart.getItems()) {
            AbstractProduct itemProduct = productCRUD.readProduct(item.getErpProductId());

            if (item.isCampaign()) {
                this.campaignTracking.purchaseCampaignAtTouchpoint(item.getErpProductId(), this.touchpoint,
                        item.getUnits());
                Campaign c = (Campaign) itemProduct;

                c.getBundles().forEach(bundle -> {
                    int total = bundle.getUnits() * item.getUnits();

                    int available = stockSystemService.getUnitsOnStock(bundle.getProduct().getId(), touchpoint.getErpPointOfSaleId());
                    if (available >= total) {
                        stockSystemService.removeFromStock(bundle.getProduct().getId(), touchpoint.getErpPointOfSaleId(), total);
                    }
                });
            } else {
                int available = stockSystemService.getUnitsOnStock(itemProduct.getId(), touchpoint.getErpPointOfSaleId());
                if (available >= item.getUnits()) {
                    stockSystemService.removeFromStock(itemProduct.getId(), touchpoint.getErpPointOfSaleId(), item.getUnits());
                }
            }
        }
    }

    @Override
    public void purchaseCartAtTouchpointForCustomer(long shoppingCartId, long touchpointId, long customerId) throws ShoppingException {
        this.customer = customerCRUD.readCustomer(customerId);
        this.touchpoint = touchpointAccess.readTouchpoint(touchpointId);
        this.shoppingCart = new ShoppingCartEntity();
        this.shoppingCartService.getItems(shoppingCartId)
                .forEach(shoppingCartItem -> {
                    this.shoppingCart.addItem(new ShoppingCartItem(shoppingCartItem.getErpProductId(), shoppingCartItem.getUnits(), shoppingCartItem.isCampaign()));
                });
        this.purchase();
    }
}
