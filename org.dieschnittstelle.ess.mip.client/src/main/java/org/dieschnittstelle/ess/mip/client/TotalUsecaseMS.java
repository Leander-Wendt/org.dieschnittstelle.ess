package org.dieschnittstelle.ess.mip.client;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.mip.client.apiclients.*;

public class TotalUsecaseMS {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(TotalUsecaseMS.class);

	public static void main(String[] args) {
		// here, we will use ejb proxies for accessing the server-side components
		ServiceProxyFactory.initialise(ServiceProxyFactory.MICROSERVICES_DEPLOYMENT);

		try {
			(new TotalUsecase()).runAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

