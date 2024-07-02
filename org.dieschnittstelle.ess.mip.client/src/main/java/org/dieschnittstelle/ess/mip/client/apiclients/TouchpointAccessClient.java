package org.dieschnittstelle.ess.mip.client.apiclients;

import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.mip.components.crm.api.CrmException;
import org.dieschnittstelle.ess.mip.components.crm.api.TouchpointAccess;

import java.util.List;

public class TouchpointAccessClient implements TouchpointAccess {
	
	private TouchpointAccess serviceProxy;
	
	public TouchpointAccessClient() {
		this.serviceProxy = ServiceProxyFactory.getInstance().getProxy(TouchpointAccess.class);
	}
	
	
	public List<AbstractTouchpoint> readAllTouchpoints() {
		return serviceProxy.readAllTouchpoints();
	}

	@Override
	public AbstractTouchpoint readTouchpoint(long id) {
		return serviceProxy.readTouchpoint(id);
	}

	@Override
	public AbstractTouchpoint createTouchpointAndPointOfSale(AbstractTouchpoint touchpoint) throws CrmException {
		AbstractTouchpoint created = serviceProxy.createTouchpointAndPointOfSale(touchpoint);
		touchpoint.setId(created.getId());
		touchpoint.setErpPointOfSaleId(created.getErpPointOfSaleId());
		
		return created;
	}
		
}
