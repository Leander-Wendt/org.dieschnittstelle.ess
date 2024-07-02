package org.dieschnittstelle.ess.mip.client.apiclients;

import java.util.List;

import org.dieschnittstelle.ess.mip.components.crm.api.CampaignTracking;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.CampaignExecution;

public class CampaignTrackingClient implements CampaignTracking {

	private CampaignTracking serviceProxy;
	
	public CampaignTrackingClient() throws Exception {
		serviceProxy = ServiceProxyFactory.getInstance().getProxy(CampaignTracking.class);
	}
	
	@Override
	public void addCampaignExecution(CampaignExecution campaign) {
		serviceProxy.addCampaignExecution(campaign);
	}

	@Override
	public int existsValidCampaignExecutionAtTouchpoint(long erpProductId,
			AbstractTouchpoint tp) {
		return serviceProxy.existsValidCampaignExecutionAtTouchpoint(erpProductId, tp);
	}

	@Override
	public void purchaseCampaignAtTouchpoint(long erpProductId,
			AbstractTouchpoint tp, int units) {
		serviceProxy.purchaseCampaignAtTouchpoint(erpProductId, tp, units);
	}

	@Override
	public List<CampaignExecution> getAllCampaignExecutions() {
		return serviceProxy.getAllCampaignExecutions();
	}

}
