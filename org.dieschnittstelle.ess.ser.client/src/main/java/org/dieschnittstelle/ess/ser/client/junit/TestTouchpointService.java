package org.dieschnittstelle.ess.ser.client.junit;

import java.util.List;

import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Address;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.ser.client.ShowTouchpointService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTouchpointService {

	private ShowTouchpointService client;

	private static List<AbstractTouchpoint> initialTps;
	private static StationaryTouchpoint ORIGINAL_TOUCHPOINT;
	private static StationaryTouchpoint CREATED_TOUCHPOINT;

	@Before
	public void prepareClient() {
		// create the accessor for the service
		client = new ShowTouchpointService();
		client.setStepwise(false);
	}

	@Test
	public void a_create() {
		// read out all touchpoints - this has already been implemented
		initialTps = client.readAllTouchpoints();

		// create a touchpoint
		Address addr = new Address("Luxemburger Strasse", "10", "13353",
				"Berlin");
		ORIGINAL_TOUCHPOINT = new StationaryTouchpoint(-1,
				"BHT Verkaufsstand", addr);


		// create a new touchpoint
		CREATED_TOUCHPOINT = (StationaryTouchpoint) client.createNewTouchpoint(ORIGINAL_TOUCHPOINT);
		List<AbstractTouchpoint> newTps = client.readAllTouchpoints();
		assertNotNull("touchpoint creation returns an object", CREATED_TOUCHPOINT);
		assertEquals("list of touchpoints is extended on creation",
				initialTps.size() + 1, newTps.size());
		CREATED_TOUCHPOINT = (StationaryTouchpoint) getTouchpointFromList(newTps, CREATED_TOUCHPOINT);
		assertTrue("created touchpoint coincides with local copy", ORIGINAL_TOUCHPOINT.getName()
				.equals(CREATED_TOUCHPOINT.getName()));
		assertNotNull("created touchpoint contains embedded address",
				((StationaryTouchpoint) CREATED_TOUCHPOINT).getAddress());
	}

	@Test
	public void b_delete() {
		// delete the new touchpoint
		client.deleteTouchpoint(CREATED_TOUCHPOINT);
		assertEquals("deletion reduces touchpoint list", initialTps.size(),
				client.readAllTouchpoints().size());
	}

	private AbstractTouchpoint getTouchpointFromList(
			List<AbstractTouchpoint> tps, AbstractTouchpoint tp) {
		for (AbstractTouchpoint currentTp : tps) {
			if (currentTp.getId() == tp.getId()) {
				return currentTp;
			}
		}
		return null;
	}

}
