package org.dieschnittstelle.ess.mip.client.apiclients;

import java.util.List;

import org.dieschnittstelle.ess.mip.components.crm.api.CrmException;
import org.dieschnittstelle.ess.mip.components.crm.api.TouchpointAccess;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;

public class TouchpointAccessClient implements TouchpointAccess {
	
	private TouchpointAccess serviceProxy;
	
	public TouchpointAccessClient() throws Exception {
		this.serviceProxy = ServiceProxyFactory.getInstance().getProxy(TouchpointAccess.class);
	}
	
	
	public List<AbstractTouchpoint> readAllTouchpoints() {
		return serviceProxy.readAllTouchpoints();
	}

	@Override
	public AbstractTouchpoint readTouchpoint(long id) {
		return null;
	}

	@Override
	public AbstractTouchpoint createTouchpointAndPointOfSale(AbstractTouchpoint touchpoint) throws CrmException {
		AbstractTouchpoint created = serviceProxy.createTouchpointAndPointOfSale(touchpoint);
		touchpoint.setId(created.getId());
		touchpoint.setErpPointOfSaleId(created.getErpPointOfSaleId());
		
		return created;
	}
		
}
