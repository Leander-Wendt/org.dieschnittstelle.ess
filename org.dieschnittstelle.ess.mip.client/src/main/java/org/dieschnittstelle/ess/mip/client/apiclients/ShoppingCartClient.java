package org.dieschnittstelle.ess.mip.client.apiclients;

import java.util.List;

import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCartService;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCart;
import org.dieschnittstelle.ess.entities.shopping.ShoppingCartItem;

public class ShoppingCartClient implements ShoppingCart {

	private ShoppingCartService serviceProxy;

	// since we have been using the REST service rather than a stateful ejb, we will manage the "session id"
	// (= the id of the ShoppingCart entity) manually. 
	private long shoppingCartEntityId;

	public ShoppingCartClient() throws Exception {

		this.serviceProxy = ServiceProxyFactory.getInstance().getProxy(ShoppingCartService.class);
		// a client will be instantiated for each new shopping cart, i.e. we will obtain a cart id here
		this.shoppingCartEntityId = this.serviceProxy.createNewCart();
	}

	@Override
	public void addItem(ShoppingCartItem product) {
		serviceProxy.addItem(this.shoppingCartEntityId,product);
	}

	public List<ShoppingCartItem> getItems() {
		return serviceProxy.getItems(this.shoppingCartEntityId);
	}

	public long getShoppingCartEntityId() {
		return shoppingCartEntityId;
	}

}
