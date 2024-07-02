package org.dieschnittstelle.ess.mip.webapi;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.apache.logging.log4j.Logger;

@ApplicationPath("/api")
public class RESTWebAPIRoot extends Application {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(RESTWebAPIRoot.class);
	
	public RESTWebAPIRoot() {
		logger.info("<constructor>");
	}

// explicit declaration of resource implementation does not seem to be necessary
//	@Override
//	public Set<Class<?>> getClasses() {
//		return new HashSet(Arrays.asList(
//				TouchpointAccessImpl.class,
//				CustomerCRUDImpl.class,
//				CustomerTransactionCRUDImpl.class,
//				CustomerTrackingImpl.class,
//				CampaignTrackingImplSingleton.class,
//				ShoppingCartRESTServiceImpl.class));
//	}

}
